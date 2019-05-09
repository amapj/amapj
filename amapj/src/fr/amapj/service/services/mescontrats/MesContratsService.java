/*
 *  Copyright 2013-2018 Emmanuel BRUN (contact@amapj.fr)
 * 
 *  This file is part of AmapJ.
 *  
 *  AmapJ is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  AmapJ is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with AmapJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
 package fr.amapj.service.services.mescontrats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.DateUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratDatePaiement;
import fr.amapj.model.models.contrat.modele.ModeleContratExclude;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.model.models.contrat.reel.Paiement;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.remise.RemiseProducteur;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.producteur.ProducteurService;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.saisiecontrat.ContratAboManager;

/**
 * Permet la gestion des contrats
 * 
 *  
 *
 */
public class MesContratsService
{
	private final static Logger logger = LogManager.getLogger();

	public MesContratsService()
	{

	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES CONTRATS

	/**
	 * Retourne la liste contrats pour l'utilisateur courant
	 */
	@DbRead
	public MesContratsDTO getMesContrats(Long userId)
	{
		Date now =DateUtils.getDate();
		EntityManager em = TransactionHelper.getEm();
	
		MesContratsDTO res = new MesContratsDTO();

		Utilisateur user = em.find(Utilisateur.class, userId);
		Date d = DateUtils.suppressTime(now);

		// On récupère d'abord la liste de tous les modeles de contrats de type ABO et LIBRE 
		// disponibles (cad à l'état ACTIF et dont la date limite n'est pas passée) 
		Query q = em.createQuery("select mc from ModeleContrat mc " +
				"WHERE mc.etat=:etat and mc.dateFinInscription>=:d and (mc.nature=:t1 or mc.nature=:t2) " +
				"order by mc.dateFinInscription asc , mc.nom , mc.id");
		q.setParameter("etat",EtatModeleContrat.ACTIF);
		q.setParameter("t1",NatureContrat.ABONNEMENT);
		q.setParameter("t2",NatureContrat.LIBRE);
		q.setParameter("d",d);
		
		List<ModeleContrat> mcs1 = q.getResultList();
		
		// On récupère ensuite la liste de tous les modeles de contrats de type CARTE_PREPAYEE 
		// disponibles (cad à l'état ACTIF et dont il reste au moins une date de livraison dans le futur)  
		q = em.createQuery("select distinct(mcd.modeleContrat) from ModeleContratDate mcd " +
				"WHERE mcd.modeleContrat.etat=:etat and mcd.modeleContrat.nature=:t1 " +
				"and mcd.dateLiv>=:d "+
				"order by  mcd.modeleContrat.nom ,  mcd.modeleContrat.id");
		q.setParameter("etat",EtatModeleContrat.ACTIF);
		q.setParameter("t1",NatureContrat.CARTE_PREPAYEE);
		q.setParameter("d",d);
		
		List<ModeleContrat> mcs2 = q.getResultList();
		
		//
		List<ModeleContrat> mcs = new ArrayList<ModeleContrat>();
		mcs.addAll(mcs1);
		mcs.addAll(mcs2);

		// On récupère ensuite la liste de tous les contrats de cet utilisateur
		Query q2 = em.createQuery("select c from Contrat c " +
				"WHERE c.utilisateur=:u " +
				"order by c.modeleContrat.dateFinInscription asc , c.modeleContrat.nom , c.modeleContrat.id");
		q2.setParameter("u",user);
		List<Contrat> contrats = q2.getResultList();

		computeNewContrat(em, res, mcs, contrats, now);

		computeExistingContrat(em, res, contrats, now);
		
		// On ajoute ensuite les informations sur l'adhesion
		new GestionCotisationService().computeAdhesionInfo(em,res,user);

		return res;

	}


	private void computeNewContrat(EntityManager em, MesContratsDTO res, List<ModeleContrat> mcs, List<Contrat> contrats, Date now)
	{
		MesCartesPrepayeesService service = new MesCartesPrepayeesService();
		
		for (ModeleContrat mc : mcs)
		{
			if (isReallyNew(mc, contrats))
			{
				// On calcule les informations prépayées 
				CartePrepayeeDTO cartePrepayee = service.computeCartePrepayee(mc, em, now);
				
				if (isEligibleForNewContrat(cartePrepayee))
				{
					
					// Appel du service modele de contrat pour avoir toutes les
					// infos sur ce modele de contrat
					ModeleContratSummaryDTO summaryDTO = new GestionContratService().createModeleContratInfo(em, mc);
	
					ContratDTO dto = new ContratDTO();
					dto.contratId = null;
					dto.modeleContratId = mc.getId();
					dto.nom = mc.getNom();
					dto.description = mc.getDescription();
					dto.nomProducteur = mc.getProducteur().nom;
					dto.dateFinInscription = mc.getDateFinInscription();
					dto.nbLivraison = summaryDTO.nbLivraison;
					dto.dateDebut = summaryDTO.dateDebut;
					dto.dateFin = summaryDTO.dateFin;
					dto.nature = mc.nature;
					dto.isModifiable = null;
					dto.isSupprimable = null;
					dto.isJoker = null;
					dto.cartePrepayee = cartePrepayee;
					
					res.newContrats.add(dto);
				}
			}
		}
	}

	private boolean isReallyNew(ModeleContrat mc, List<Contrat> contrats)
	{
		for (Contrat contrat : contrats)
		{
			if (mc.getId().equals(contrat.getModeleContrat().getId()))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Permet de filtrer les cartes prepayées sur lesquelles il n'est plus possible de s'inscrire
	 * 
	 */
	private boolean isEligibleForNewContrat(CartePrepayeeDTO cartePrepayee)
	{
		if (cartePrepayee==null)
		{
			return true;
		}
		return cartePrepayee.nbLigModifiable>0;
	}
	

	private void computeExistingContrat(EntityManager em, MesContratsDTO res, List<Contrat> contrats,Date now)
	{
		MesCartesPrepayeesService service = new MesCartesPrepayeesService();
		ContratStatusService statusService = new ContratStatusService();
		
		for (Contrat contrat : contrats)
		{
			// 
			ModeleContrat mc = contrat.getModeleContrat();
			
			// On calcule les informations sur les cartes prepayées et s'il est modifiable 
			CartePrepayeeDTO cartePrepayeeDTO = service.computeCartePrepayee(mc,em,now);
			boolean isModifiable = statusService.isModifiable(mc,em,cartePrepayeeDTO,now);
			
			if (statusService.isHistorique(contrat,em,now,isModifiable)==false)
			{
				// Appel du service modele de contrat pour avoir toutes les infos
				// sur ce modele de contrat
				ModeleContratSummaryDTO summaryDTO = new GestionContratService().createModeleContratInfo(em, mc);
	
				ContratDTO dto = new ContratDTO();
				dto.contratId = contrat.getId();
				dto.modeleContratId = mc.getId();
				dto.nom = contrat.getModeleContrat().getNom();
				dto.description = contrat.getModeleContrat().getDescription();
				dto.nomProducteur = contrat.getModeleContrat().getProducteur().nom;
				dto.dateFinInscription = mc.getDateFinInscription();
				dto.nbLivraison = summaryDTO.nbLivraison;
				dto.dateDebut = summaryDTO.dateDebut;
				dto.dateFin = summaryDTO.dateFin;
				dto.nature = mc.nature; 
				dto.isModifiable = isModifiable;
				dto.isSupprimable = statusService.isSupprimable(contrat,em,cartePrepayeeDTO,now,isModifiable);
				dto.isJoker = new ContratAboManager().hasJokerButton(mc,isModifiable);
				dto.cartePrepayee = cartePrepayeeDTO;
				
	
				res.existingContrats.add(dto);
			}
		}
	}


	
	// PARTIE CHARGEMENT D'UN CONTRAT A PARTIR D'UN MODELE DE CONTRAT

	/**
	 * Permet de charger les informations complete d'un modele contrat
	 * dans une transaction en lecture
	 */
	@DbRead
	public ContratDTO loadContrat(Long modeleContratId, Long contratId)
	{
		EntityManager em = TransactionHelper.getEm();
		Date now = DateUtils.getDate();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);

		ContratDTO dto = new ContratDTO();
		dto.contratId = null;
		Contrat contrat = null;
		if (contratId != null)
		{
			dto.contratId = contratId;
			contrat = em.find(Contrat.class, contratId);
		}

		dto.modeleContratId = mc.getId();
		dto.nom = mc.getNom();
		dto.description = mc.getDescription();
		dto.nomProducteur = mc.getProducteur().nom;
		dto.dateFinInscription = mc.getDateFinInscription();
		dto.nature = mc.nature;
		dto.jokerNbMin = mc.jokerNbMin;
		dto.jokerNbMax = mc.jokerNbMax;
		dto.jokerMode = mc.jokerMode;
		dto.jokerDelai = mc.jokerDelai;
		
		// On calcule les informations sur les cartes prepayées et s'il est modifiable 
		CartePrepayeeDTO cartePrepayeeDTO = new MesCartesPrepayeesService().computeCartePrepayee(mc,em,now);
		ContratStatusService statusService = new ContratStatusService();
		
		boolean isModifiable = statusService.isModifiable(mc,em,cartePrepayeeDTO,now);
		dto.isModifiable = isModifiable;
		dto.isSupprimable = statusService.isSupprimable(contrat, em, cartePrepayeeDTO, now, isModifiable);
		dto.isJoker = null;
		dto.cartePrepayee = cartePrepayeeDTO;

		// Avec une sous requete, on récupere la liste des produits
		List<ModeleContratProduit> prods = new GestionContratService().getAllProduit(em, mc);
		for (ModeleContratProduit prod : prods)
		{
			ContratColDTO col = new ContratColDTO();
			col.modeleContratProduitId = prod.getId();
			col.nomProduit = prod.getProduit().getNom();
			col.condtionnementProduit = prod.getProduit().getConditionnement();
			col.prix = prod.getPrix();
			col.j = dto.contratColumns.size();

			dto.contratColumns.add(col);
		}

		// Avec une sous requete, on obtient la liste de toutes les dates de
		// livraison
		List<ModeleContratDate> dates = new GestionContratService().getAllDates(em, mc);
		for (ModeleContratDate date : dates)
		{
			ContratLigDTO lig = new ContratLigDTO();
			lig.date = date.getDateLiv();
			lig.modeleContratDateId = date.getId();
			lig.i = dto.contratLigs.size();
			
			dto.contratLigs.add(lig);
		}
		
		// Avec une sous requete on recupere la liste des dates ou des produits
		// qui sont exclus si il y en a 
		List<ModeleContratExclude> excludeds = new GestionContratService().getAllExcludedDateProduit(em, mc);
		if (excludeds.size()!=0)
		{
			dto.excluded = new boolean[dto.contratLigs.size()][dto.contratColumns.size()];
			for (int i = 0; i < dto.contratLigs.size(); i++)
			{
				for (int j = 0; j < dto.contratColumns.size(); j++)
				{
					dto.excluded[i][j] = false ;
				}
			}
			for (ModeleContratExclude exclude : excludeds)
			{
				insertExcluded(dto,exclude.getDate().getId(),exclude.getProduit());
			}
		}
		
		
		//
		dto.qte = new int[dto.contratLigs.size()][dto.contratColumns.size()];

		// Si on est en modification d'un contrat existant, on récupère les
		// valeurs déjà saisies
		if (contratId != null)
		{
			Contrat c = em.find(Contrat.class, contratId);
			List<ContratCell> qtes = getAllQte(em, c);
			for (ContratCell qte : qtes)
			{
				insert(dto, qte.getModeleContratDate().getId(), qte.getModeleContratProduit().getId(), qte.getQte());
			}
		}
		
		// On récupère les informations liées au paiement
		dto.paiement = new InfoPaiementDTO();
		dto.paiement.gestionPaiement = mc.getGestionPaiement();
		dto.paiement.textPaiement = mc.getTextPaiement();
		dto.paiement.libCheque = mc.getLibCheque();
		dto.paiement.referentsRemiseCheque = new ProducteurService().getReferents(em, mc.getProducteur());
		// Si on est en modification d'un contrat existant, on récupère l'avoir
		if (contratId != null)
		{
			Contrat c = em.find(Contrat.class, contratId);
			dto.paiement.avoirInitial = c.getMontantAvoir();
		}
		
		// On récupère la liste ordonnée des dates de paiements depuis le modele de contrat
		List<ModeleContratDatePaiement> datePaiements = new GestionContratService().getAllDatesPaiements(em, mc);
		for (ModeleContratDatePaiement date : datePaiements)
		{
			DatePaiementDTO lig = new DatePaiementDTO();
			lig.datePaiement = date.getDatePaiement();
			lig.idModeleContratDatePaiement = date.getId();
			lig.montant = 0;
			lig.etatPaiement = EtatPaiement.A_FOURNIR;
			lig.idPaiement = null;
			
			// Si on est en modification d'un contrat existant, on récupère les  montants déjà saisis
			Paiement paiement = getPaiement(date, contratId, em);
			if (paiement!=null)
			{
				lig.idPaiement = paiement.getId();
				lig.montant = paiement.getMontant();
				lig.etatPaiement = paiement.getEtat();
			}
			
			dto.paiement.datePaiements.add(lig);
		}
		

		return dto;

	}
	
	

	/**
	 * Permet de retrouver un paiement à partir du ModeleContratDatePaiement mcdp et du contrat
	 * 
	 * contratId peut etre null, retourne alors null
	 * 
	 * @param mcdp
	 * @param c
	 * @param em
	 * @return
	 */
	public Paiement getPaiement(ModeleContratDatePaiement mcdp,Long contratId,EntityManager em)
	{
		if (contratId==null)
		{
			return null;
		}
		Contrat c = em.find(Contrat.class, contratId);
		
		Query q = em.createQuery("select p from Paiement p WHERE p.contrat=:c and p.modeleContratDatePaiement =:mcdp ");
		q.setParameter("c",c);
		q.setParameter("mcdp",mcdp);
		
		List<Paiement> paiements = q.getResultList();
		if (paiements.size()==0)
		{
			return null;
		}
		else if (paiements.size()==1)
		{
			return paiements.get(0);
		}
		else
		{
			throw new RuntimeException("Il y a "+paiements.size()+" paiements");
		}
		
	}
	

	private void insertExcluded(ContratDTO dto, Long modeleContratDateId, ModeleContratProduit modeleContratProduit)
	{
		if (modeleContratProduit==null)
		{
			int lig = findLigIndex(dto, modeleContratDateId);
			removeLigne(dto,lig);
		}
		else
		{
			int lig = findLigIndex(dto, modeleContratDateId);
			int col = findColIndex(dto, modeleContratProduit.getId());
			dto.excluded[lig][col]  = true;
		}
	}

	
	private void removeLigne(ContratDTO dto, int lig)
	{
		for (int j = 0; j < dto.contratColumns.size(); j++)
		{
			dto.excluded[lig][j] = true ;
		}
	}

	private void insert(ContratDTO dto, Long modeleContratDateId, Long modeleContratProduitId, int qte)
	{
		int lig = findLigIndex(dto, modeleContratDateId);
		int col = findColIndex(dto, modeleContratProduitId);

		dto.qte[lig][col] = qte;
	}

	private int findLigIndex(ContratDTO dto, Long modeleContratDateId)
	{
		int index = 0;
		for (ContratLigDTO lig : dto.contratLigs)
		{
			if (lig.modeleContratDateId.equals(modeleContratDateId))
			{
				return index;
			}
			index++;
		}
		throw new RuntimeException("Erreur inattendue");
	}

	private int findColIndex(ContratDTO dto, Long modeleContratProduitId)
	{
		int index = 0;
		for (ContratColDTO col : dto.contratColumns)
		{
			if (col.modeleContratProduitId.equals(modeleContratProduitId))
			{
				return index;
			}
			index++;
		}
		throw new RuntimeException("Erreur inattendue");
	}


	/**
	 * 
	 */
	public List<ContratCell> getAllQte(EntityManager em, Contrat c)
	{
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<ContratCell> cq = cb.createQuery(ContratCell.class);
		Root<ContratCell> root = cq.from(ContratCell.class);

		// On ajoute la condition where
		cq.where(cb.equal(root.get(ContratCell.P.CONTRAT.prop()), c));

		List<ContratCell> prods = em.createQuery(cq).getResultList();
		return prods;
	}

	// PARTIE SAUVEGARDE D'UN NOUVEAU CONTRAT

	/**
	 * Permet de sauvegarder un nouveau contrat
	 * Ceci est fait dans une transaction en ecriture  
	 */
	@DbWrite
	public Long saveNewContrat(ContratDTO contratDTO,Long userId) throws OnSaveException
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On vérifie d'abord que le contrat n'est pas vide
		if (contratDTO.isEmpty())
		{
			throw new OnSaveException("Il est impossible de sauvegarder un contrat vide");
		}
		
		ModeleContrat mc = em.find(ModeleContrat.class, contratDTO.modeleContratId);

		// Chargement ou création du contrat
		Contrat c = null;
		if (contratDTO.contratId==null)
		{
			c = new Contrat();
			c.setDateCreation(DateUtils.getDate());
			c.setModeleContrat(mc);
			c.setUtilisateur(em.find(Utilisateur.class, userId));
			em.persist(c);
		}
		else
		{
			c = em.find(Contrat.class, contratDTO.contratId);
			c.setDateModification(DateUtils.getDate());
		}

		// Création ou modification de toutes les lignes quantités
		
		// Chargement des lignes depuis la base de données sous la forme d'une matrice
		ContratCell[][] matrix = getAllQteAsMatrix(em, c,contratDTO);
		
		// Ensuite on balaye chacune des cases pour agir si besoin 
		int nbLigs = contratDTO.contratLigs.size();
		int nbCols = contratDTO.contratColumns.size();
		for (int i = 0; i < nbLigs; i++)
		{
			for (int j = 0; j < nbCols; j++)
			{
				ContratCell dbCell = matrix[i][j];
				ContratColDTO colDto = contratDTO.contratColumns.get(j);
				ContratLigDTO ligDto = contratDTO.contratLigs.get(i);
				int qteDto = contratDTO.qte[i][j];
				
				updateCellInDb(em,dbCell,qteDto,ligDto,colDto,c);
			}
		}
		
		
		// Création ou modification de toutes les lignes paiements
		for (DatePaiementDTO datePaiementDTO : contratDTO.paiement.datePaiements)
		{
			ModeleContratDatePaiement mcdp = em.find(ModeleContratDatePaiement.class, datePaiementDTO.idModeleContratDatePaiement);
			Paiement p = null;
			if (datePaiementDTO.idPaiement!=null)
			{
				p = em.find(Paiement.class, datePaiementDTO.idPaiement);
			}
			insertPaiement(p,datePaiementDTO,c,mcdp,em);
		}	
		
		//
		return c.getId();
	}
	
	private void insertPaiement(Paiement p, DatePaiementDTO datePaiementDTO,Contrat c,ModeleContratDatePaiement mcdp,EntityManager em) throws OnSaveException
	{
		if (p==null)
		{
			if (datePaiementDTO.montant==0)
			{
				// Rien à faire
			}
			else
			{
				// On vérifie que la remise n'a pas été faite pour cette date
				checkRemiseNonFaite(mcdp,em);
				
				// On crée la cellule dans la base
				p = new Paiement();
				p.setContrat(c);
				p.setModeleContratDatePaiement(mcdp);
				p.setMontant(datePaiementDTO.montant);
				em.persist(p);
			}
		}
		else	
		{
			if (datePaiementDTO.montant==0)
			{
				// On enleve la cellule dans la base
				em.remove(p);
			}
			else
			{
				// On met à jour la cellule dans la base
				p.setMontant(datePaiementDTO.montant);	
			}
		}
		
	}

	private void checkRemiseNonFaite(ModeleContratDatePaiement mcdp, EntityManager em) throws OnSaveException
	{
		Query q = em.createQuery("select r from RemiseProducteur r WHERE r.datePaiement=:mcdp");
		q.setParameter("mcdp",mcdp);
		
		
		List<RemiseProducteur> rps = q.getResultList();
		if (rps.size()>0)
		{
			SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");
			throw new OnSaveException("La remise des chèques  a été faite au producteur pour le mois de "+df.format(mcdp.getDatePaiement())+". Vous ne devez donc pas mettre de paiement pour cette date");
		}
		
	}

	private void updateCellInDb(EntityManager em, ContratCell dbCell, int qteDto, ContratLigDTO ligDto, ContratColDTO colDto,Contrat c)
	{
		if (qteDto==0)
		{
			if (dbCell==null)
			{
				// Rien à faire
			}
			else
			{
				// On supprime la cellule dans la base devenue inutile
				em.remove(dbCell);
			}
		}
		else
		{
			if (dbCell==null)
			{
				// On crée la cellule dans la base 
				ContratCell cl = new ContratCell();
				cl.setContrat(c);
				cl.setModeleContratDate(em.find(ModeleContratDate.class, ligDto.modeleContratDateId));
				cl.setModeleContratProduit(em.find(ModeleContratProduit.class, colDto.modeleContratProduitId));
				cl.setQte(qteDto);
				em.persist(cl);
			}
			else
			{
				// On met a jour la cellule dans la base 
				dbCell.setQte(qteDto);
			}
		}
		
	}

	private ContratCell[][] getAllQteAsMatrix(EntityManager em, Contrat c, ContratDTO dto)
	{
		
		ContratCell[][] res = new ContratCell[dto.contratLigs.size()][dto.contratColumns.size()];
		
		List<ContratCell> ligs = getAllQte(em, c);
		for (ContratCell lig : ligs)
		{
			int i = getIndexLig(lig,dto);
			int j = getIndexCol(lig,dto);
			res[i][j] = lig;
		}
		
		
		return res;
	}

	private int getIndexLig(ContratCell lig, ContratDTO contratDTO)
	{
		int i = 0;
		for (ContratLigDTO dto : contratDTO.contratLigs)
		{
			if (dto.modeleContratDateId.equals(lig.getModeleContratDate().getId()))
			{
				return i;
			}
			i++;
		}
		throw new RuntimeException("Erreur inattendue");
	}

	private int getIndexCol(ContratCell lig, ContratDTO contratDTO)
	{
		int j = 0;
		for (ContratColDTO dto : contratDTO.contratColumns)
		{
			if (dto.modeleContratProduitId.equals(lig.getModeleContratProduit().getId()))
			{
				return j;
			}
			j++;
		}
		throw new RuntimeException("Erreur inattendue");
	}



	// PARTIE SUPPRESSION D'UN CONTRAT

	/**
	 * Permet de supprimer un contrat
	 * Ceci est fait dans une transaction en ecriture  
	 */
	@DbWrite
	public void deleteContrat(Long contratId)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Contrat c = em.find(Contrat.class, contratId);
		List<ContratCell> qtes = getAllQte(em, c);
		for (ContratCell contratLig : qtes)
		{
			em.remove(contratLig);
		}
		
		List<Paiement> ps = getAllPaiements(em, c);
		for (Paiement paiement : ps)
		{
			if (paiement.getEtat().equals(EtatPaiement.A_FOURNIR))
			{
				em.remove(paiement);
			}
			else
			{
				String str = "Il existe un paiement de "+new CurrencyTextFieldConverter().convertToString(paiement.getMontant())+" €";
				if (paiement.getEtat().equals(EtatPaiement.AMAP))
				{
					str = str+" qui est receptionné à l'AMAP. Il faut rendre le chèque à l'AMAPIEN ,"
							+ "modifier l'état du chèque dans Réception des chèques , et vous pourrez ensuite supprimer le contrat";  
				}
				else
				{
					str = str+" qui a été donné au producteur (remise). Il n'est plus possible supprimer le contrat.";
				}
				
				throw new UnableToSuppressException(str);
			}
		}
		
		em.remove(c);

	}

	
	/**
	 * Retourne la liste de tous les paiements pour un contrat particulier, tri par date
	 * @param em
	 * @param c
	 * @return
	 */
	private List<Paiement> getAllPaiements(EntityManager em, Contrat c)
	{
		// On récupère ensuite la liste de tous les paiements de cet utilisateur
		Query q = em.createQuery("select p from Paiement p WHERE p.contrat=:c order by p.modeleContratDatePaiement.datePaiement");
		q.setParameter("c",c);
		List<Paiement> paiements = q.getResultList();
		return paiements;
	}
	
	
	
	/**
	 * Permet de retrouver la liste de tous les utilisateurs ayant un contrat sur ce modele de contrat
	 * 
	 */
	public List<Utilisateur> getUtilisateur(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select u from Utilisateur u WHERE EXISTS (select c from Contrat c where c.utilisateur = u and c.modeleContrat=:mc) ORDER BY u.nom,u.prenom");
		q.setParameter("mc",mc);
		List<Utilisateur> us = q.getResultList();
		return us;
	}
	
	
	
	// PARTIE GESTION DES AVOIRS
	
	/**
	 * Permet de sauvegarder un avoir
	 * Ceci est fait dans une transaction en ecriture  
	 */
	@DbWrite
	public void saveAvoirInitial(final Long idContrat, final int mntAvoir)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Contrat c = em.find(Contrat.class, idContrat);
		c.setMontantAvoir(mntAvoir);
	}
	
	// RECHERCHE D'UN CONTRAT
	
	/**
	 * Permet de retrouver un contrat à partir du modele et 
	 * de l'utilisateur 
	 * 
	 * Retourne une exception s'il n'y a pas un et un seul  
	 * 
	 * @param modeleContratId
	 * @param em
	 * @param utilisateur
	 * @return
	 */
	public Contrat getContrat(Long modeleContratId, EntityManager em, Utilisateur utilisateur)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		Query q = em.createQuery("select c from Contrat c where c.utilisateur =:u and c.modeleContrat=:mc");
		q.setParameter("mc",mc);
		q.setParameter("u",utilisateur);
		
		List<Contrat> cs = q.getResultList();
		if (cs.size()!=1)
		{
			throw new RuntimeException("Erreur inattendue pour "+utilisateur.getNom()+utilisateur.getPrenom());
		}
		
		return cs.get(0);
	}
	
	
	
}
