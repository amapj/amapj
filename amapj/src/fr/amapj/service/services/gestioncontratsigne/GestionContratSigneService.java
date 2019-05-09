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
 package fr.amapj.service.services.gestioncontratsigne;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.DateUtils;
import fr.amapj.common.LongUtils;
import fr.amapj.common.SQLUtils;
import fr.amapj.common.collections.G1D;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratExclude;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.LigneContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontratsigne.InfoBarrerProduitDTO.CellChange;
import fr.amapj.service.services.gestioncontratsigne.InfoBarrerProduitDTO.DiffState;
import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.saisiecontrat.ContratAboManager;

/**
 * Permet la gestion des modeles de contrat
 * 
 * 
 *
 */
public class GestionContratSigneService
{

	public GestionContratSigneService()
	{

	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES CONTRATS SIGNE

	/**
	 * Permet de charger la liste de tous les contrat signes dans une
	 * transaction en lecture
	 */
	@DbRead
	public List<ContratSigneDTO> getAllContratSigne(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);
		List<ContratSigneDTO> res = new ArrayList<ContratSigneDTO>();

		Query q = em.createQuery("select c from Contrat c " + "where c.modeleContrat=:mc " + "order by c.utilisateur.nom, c.utilisateur.prenom");
		q.setParameter("mc", mc);

		List<Contrat> mcs = q.getResultList();

		for (Contrat contrat : mcs)
		{
			ContratSigneDTO mcInfo = createContratSigneInfo(em, contrat);
			res.add(mcInfo);
		}

		return res;

	}

	public ContratSigneDTO createContratSigneInfo(EntityManager em, Contrat contrat)
	{
		ContratSigneDTO info = new ContratSigneDTO();

		info.nomUtilisateur = contrat.getUtilisateur().getNom();
		info.prenomUtilisateur = contrat.getUtilisateur().getPrenom();
		info.idUtilisateur = contrat.getUtilisateur().getId();
		info.idContrat = contrat.getId();
		info.idModeleContrat = contrat.getModeleContrat().getId();
		info.dateCreation = contrat.getDateCreation();
		info.dateModification = contrat.getDateModification();
		info.mntCommande = getMontant(em, contrat);

		info.nbChequePromis = getNbCheque(em, contrat, EtatPaiement.A_FOURNIR);
		info.nbChequeRecus = getNbCheque(em, contrat, EtatPaiement.AMAP);
		info.nbChequeRemis = getNbCheque(em, contrat, EtatPaiement.PRODUCTEUR);

		int mntChequeRemis = getMontantChequeRemis(em, contrat);
		info.mntAvoirInitial = contrat.getMontantAvoir();
		info.mntSolde = info.mntAvoirInitial + mntChequeRemis - info.mntCommande;

		return info;
	}

	private int getMontantChequeRemis(EntityManager em, Contrat contrat)
	{
		Query q = em.createQuery("select sum(p.montant) from Paiement p WHERE p.etat=:etat and p.contrat=:c");
		q.setParameter("c", contrat);
		q.setParameter("etat", EtatPaiement.PRODUCTEUR);

		return LongUtils.toInt(q.getSingleResult());
	}

	private int getNbCheque(EntityManager em, Contrat contrat, EtatPaiement etatPaiement)
	{
		Query q = em.createQuery("select count(p) from Paiement p WHERE p.etat=:etat and p.contrat=:c");
		q.setParameter("etat", etatPaiement);
		q.setParameter("c", contrat);

		return ((Long) q.getSingleResult()).intValue();
	}

	/**
	 * Récupère le montant du contrat en une seule requete
	 * 
	 */
	public int getMontant(EntityManager em, Contrat contrat)
	{
		Query q = em.createQuery("select sum(c.qte * c.modeleContratProduit.prix) from ContratCell c WHERE c.contrat=:contrat");
		q.setParameter("contrat", contrat);
		return SQLUtils.toInt(q.getSingleResult());
	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES CONTRATS MODLEE D'UN PRODUCTEUR

	/**
	 * Permet de charger la liste de tous les modeles de contrats d'un producteur,
	 * actif ou en creation 
	 * 
	 *  Les contrats sont triés avec en tete ceux dont la date de derniere livraison est la plus lointaine 
	 */
	@DbRead
	public List<ModeleContrat> getModeleContratCreationOrActif(Long idProducteur)
	{
		EntityManager em = TransactionHelper.getEm();

		Query q = em.createQuery("select max(mcd.dateLiv),mcd.modeleContrat from ModeleContratDate mcd "
				+ " where mcd.modeleContrat.producteur.id=:id and mcd.modeleContrat.etat !=:etat "
				+ " GROUP BY mcd.modeleContrat ORDER BY max(mcd.dateLiv) desc , mcd.modeleContrat.id desc");
		
		q.setParameter("id", idProducteur);
		q.setParameter("etat", EtatModeleContrat.ARCHIVE);
		
		List<Object[]> mcs = q.getResultList();
		
		return CollectionUtils.select(mcs, e-> (ModeleContrat) e[1]);
		
	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES UTILISATEURS QUI N'ONT PAS DE
	// CONTRAT SUR UN MODELE

	/**
	 * Permet de charger la liste des utilisateurs sans ce contrat dans une
	 * transaction en lecture
	 */
	@DbRead
	public List<Utilisateur> getUtilisateurSansContrat(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);

		Query q = em
				.createQuery("select u from Utilisateur u WHERE NOT EXISTS (select c from Contrat c where c.utilisateur = u and c.modeleContrat=:mc) ORDER BY u.nom,u.prenom");
		q.setParameter("mc", mc);
		List<Utilisateur> us = q.getResultList();
		return us;
	}

	// PARTIE REQUETAGE POUR AVOIR UNIQUEMENT LES INFORMATIONS SUR LES AVOIRS
	/**
	 * Retourne la liste des avoirs
	 * 
	 * @return
	 */
	public List<ContratSigneDTO> getAvoirsInfo(EntityManager em, Long idModeleContrat)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);
		List<ContratSigneDTO> res = new ArrayList<ContratSigneDTO>();

		Query q = em.createQuery("select c from Contrat c " + "where c.modeleContrat=:mc and " + " c.montantAvoir>0 "
				+ "order by c.utilisateur.nom, c.utilisateur.prenom");
		q.setParameter("mc", mc);

		List<Contrat> mcs = q.getResultList();

		for (Contrat contrat : mcs)
		{
			ContratSigneDTO mcInfo = createAvoirInfo(em, contrat);
			res.add(mcInfo);
		}

		return res;

	}

	public ContratSigneDTO createAvoirInfo(EntityManager em, Contrat contrat)
	{
		ContratSigneDTO info = new ContratSigneDTO();

		info.nomUtilisateur = contrat.getUtilisateur().getNom();
		info.prenomUtilisateur = contrat.getUtilisateur().getPrenom();
		info.mntAvoirInitial = contrat.getMontantAvoir();

		return info;
	}

	/*
	 * Gestion des annulations des dates de livraisons (avec barré de la date) 
	 */
	@DbRead
	public AnnulationDateLivraisonDTO getAnnulationDateLivraisonDTO(Long mcId)
	{
		EntityManager em = TransactionHelper.getEm();

		AnnulationDateLivraisonDTO dto = new AnnulationDateLivraisonDTO();
		dto.mcId = mcId;

		ModeleContrat mc = em.find(ModeleContrat.class, mcId);

		List<ModeleContratDate> dates = new GestionContratService().getAllDates(em, mc);

		// La premiere date proposée est la première date du contrat dans le
		// futur
		for (ModeleContratDate modeleContratDate : dates)
		{
			if (modeleContratDate.getDateLiv().after(DateUtils.getDate()))
			{
				dto.dateDebut = modeleContratDate.getDateLiv();
				break;
			}
		}

		// La dernière date proposée est la dernière date du contrat , si on est
		// arrivé à calculer une date de début
		if ((dto.dateDebut != null) && (dates.size() >= 1))
		{
			dto.dateFin = dates.get(dates.size() - 1).getDateLiv();
		}
		return dto;
	}
	
	static public class ResBarrerDate
	{
		// 
		public String msg;
		
		// Liste des dates qu'il faudra barrer
		public List<Long> idModeleContratDates;
	}
	
	

	@DbRead
	public ResBarrerDate getAnnulationInfo(final AnnulationDateLivraisonDTO annulationDto)
	{
		EntityManager em = TransactionHelper.getEm();

		StringBuffer buf = new StringBuffer();

		ModeleContrat mc = em.find(ModeleContrat.class, annulationDto.mcId);

		// On selectionne toutes les dates de livraison
		Query q = em.createQuery("select d from ModeleContratDate d where " + " d.modeleContrat=:mc and " + " d.dateLiv >= :debut and " + " d.dateLiv <= :fin");

		q.setParameter("mc", mc);
		q.setParameter("debut", annulationDto.dateDebut);
		q.setParameter("fin", annulationDto.dateFin);

		List<ModeleContratDate> mcds = q.getResultList();

		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
		buf.append("Les quantités des " + mcds.size() + " dates de livraisons suivantes vont être mises à zéro:<br/>");
		for (ModeleContratDate modeleContratDate : mcds)
		{
			buf.append(" - " + df.format(modeleContratDate.getDateLiv()) + "<br/>");
		}
		buf.append("<br/>");

		q = em.createQuery("select sum(c.qte),c.modeleContratProduit from ContratCell c where " + " c.contrat.modeleContrat=:mc and "
				+ " c.modeleContratDate.dateLiv >= :debut and " + " c.modeleContratDate.dateLiv <= :fin " + " group by c.modeleContratProduit "
				+ " order by c.modeleContratProduit.indx");

		q.setParameter("mc", mc);
		q.setParameter("debut", annulationDto.dateDebut);
		q.setParameter("fin", annulationDto.dateFin);

		List<Object[]> qtes = q.getResultList();
		buf.append("Les quantités suivantes vont être mises à zéro: ( " + qtes.size() + " produits)<br/>");
		for (Object[] qte : qtes)
		{
			Produit prod = ((ModeleContratProduit) qte[1]).getProduit();
			buf.append(" - " + qte[0] + " " + prod.getNom() + " , " + prod.getConditionnement() + "<br/>");
		}
		buf.append("<br/>");

		q = em.createQuery("select distinct(c.contrat.utilisateur) from ContratCell c where " + " c.contrat.modeleContrat=:mc and "
				+ " c.modeleContratDate.dateLiv >= :debut and " + " c.modeleContratDate.dateLiv <= :fin "
				+ " order by c.contrat.utilisateur.nom, c.contrat.utilisateur.prenom");

		q.setParameter("mc", mc);
		q.setParameter("debut", annulationDto.dateDebut);
		q.setParameter("fin", annulationDto.dateFin);

		List<Utilisateur> utilisateurs = q.getResultList();

		buf.append(UtilisateurUtil.getUtilisateurImpactes(utilisateurs));
		
		ResBarrerDate resBarrerDate = new ResBarrerDate();
		resBarrerDate.msg =  buf.toString();
		resBarrerDate.idModeleContratDates = CollectionUtils.select(mcds, e->e.getId());
		
		return resBarrerDate;

	}

	@DbWrite
	public void performAnnulationDateLivraison(AnnulationDateLivraisonDTO annulationDto,ResBarrerDate resBarrerDate)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, annulationDto.mcId);

		// On selectionne toutes les cellules, puis on les supprime
		Query q = em.createQuery("select c from ContratCell c where " + " c.contrat.modeleContrat=:mc and " + " c.modeleContratDate.dateLiv >= :debut and "
				+ " c.modeleContratDate.dateLiv <= :fin");

		q.setParameter("mc", mc);
		q.setParameter("debut", annulationDto.dateDebut);
		q.setParameter("fin", annulationDto.dateFin);

		List<ContratCell> mcs = q.getResultList();
		for (ContratCell contratCell : mcs)
		{
			// On supprime la cellule dans la base devenue inutile
			em.remove(contratCell);
		}
		
		// On barre ensuite les dates 
		ContratDTO contratDTO = new MesContratsService().loadContrat(annulationDto.mcId,null);
		contratDTO.expandExcluded();
		
		for (Long idModeleContratDate : resBarrerDate.idModeleContratDates)
		{
			// On retrouve l'index de la date 
			int index = CollectionUtils.findIndex(contratDTO.contratLigs, e->(e.modeleContratDateId==idModeleContratDate));
			
			// On barre toute la ligne de cet index
			int nbCol = contratDTO.contratColumns.size();
			for (int j = 0; j < nbCol; j++)
			{
				contratDTO.excluded[index][j] = true;
			}
		}
		
		// 
		new GestionContratService().updateDateBarreesModeleContrat(contratDTO);
		
	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES MAILS DES UTILISATEURS QUI ONT
	// UN CONTRAT
	/**
	 * 
	 */
	@DbRead
	public List<String> getAllMails(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);

		Query q = em.createQuery("select c.utilisateur.email from Contrat c WHERE c.modeleContrat=:mc ORDER BY c.utilisateur.nom, c.utilisateur.prenom");
		q.setParameter("mc", mc);
		List<String> mails = q.getResultList();
		return mails;
	}

	/**
	 * 
	 */
	@DbRead
	public List<Utilisateur> getAllUtilisateur(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);

		Query q = em.createQuery("select c.utilisateur from Contrat c WHERE c.modeleContrat=:mc ORDER BY c.utilisateur.nom, c.utilisateur.prenom");
		q.setParameter("mc", mc);
		List<Utilisateur> mails = q.getResultList();
		return mails;
	}

	// Déplacement des dates de livraisons

	@DbRead
	public DeplacerDateLivraisonDTO getDeplacerDateLivraisonDTO(Long mcId)
	{
		DeplacerDateLivraisonDTO dto = new DeplacerDateLivraisonDTO();

		EntityManager em = TransactionHelper.getEm();
		ModeleContrat mc = em.find(ModeleContrat.class, mcId);

		List<ModeleContratDate> mcds = new GestionContratService().getAllDates(em, mc);
		for (ModeleContratDate mcd : mcds)
		{
			DeplacerDateLivraisonDTO.ModifDateLivraisonDTO mdl = new DeplacerDateLivraisonDTO.ModifDateLivraisonDTO();
			mdl.dateLiv = mcd.getDateLiv();
			mdl.idModeleContratDate = mcd.getId();

			dto.dateLivraisonDTOs.add(mdl);
		}

		return dto;
	}

	@DbWrite
	public void performDeplacerDateLivraison(DeplacerDateLivraisonDTO deplacerDto)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContratDate mcd = em.find(ModeleContratDate.class, deplacerDto.selected.idModeleContratDate);
		mcd.setDateLiv(deplacerDto.actualDate);

	}

	/**
	 * Retourne la liste de tous les amapiens concernés par cette date de
	 * livraison
	 */
	@DbRead
	public String getDeplacerInfo(Long idModeleContratDate)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContratDate mcd = em.find(ModeleContratDate.class, idModeleContratDate);

		// On selectionne toutes les dates de livraison
		Query q = em.createQuery("select distinct(c.contrat.utilisateur) from ContratCell c where " + " c.modeleContratDate = :mcd "
				+ " order by c.contrat.utilisateur.nom, c.contrat.utilisateur.prenom");

		q.setParameter("mcd", mcd);
		List<Utilisateur> utilisateurs = q.getResultList();

		return UtilisateurUtil.getUtilisateurImpactes(utilisateurs);
	}

	// Modification d'un prix de produit

	/**
	 * Retourne la liste de tous les amapiens concernés par cette date de
	 * livraison
	 * 
	 * @param annulationDto
	 * @return
	 */
	@DbRead
	public String getModifPrixInfo(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		StringBuffer buf1 = new StringBuffer();

		// On recherche la liste des prix réellement modifiés
		List<Long> mcps = new ArrayList<Long>();
		CurrencyTextFieldConverter ctc = new CurrencyTextFieldConverter();
		for (LigneContratDTO lig : modeleContrat.produits)
		{
			ModeleContratProduit mcp = em.find(ModeleContratProduit.class, lig.idModeleContratProduit);
			if (mcp.getPrix() != lig.prix)
			{
				buf1.append("<li>Le prix du \"" + mcp.getProduit().getNom() + "," + mcp.getProduit().getConditionnement() + "\" passe de "
						+ ctc.convertToString(mcp.getPrix()) + " € à  " + ctc.convertToString(lig.prix) + " €</li>");
				mcps.add(mcp.getId());
			}
		}

		//
		StringBuffer buf = new StringBuffer();
		if (mcps.size() == 0)
		{
			buf.append("Aucun prix n'a été modifié.");
			return buf.toString();
		}

		buf.append("Les prix de " + mcps.size() + " produits ont été modifiés.<br/>Voici le détail des modifications :<ul>");
		buf.append(buf1);
		buf.append("</ul><br/><br/>");

		// On recherches les utilisateurs impactés
		Query q = em.createQuery("select distinct(c.contrat.utilisateur) from ContratCell c where " + " c.modeleContratProduit.id IN :mcps "
				+ " order by c.contrat.utilisateur.nom, c.contrat.utilisateur.prenom");

		q.setParameter("mcps", mcps);
		List<Utilisateur> utilisateurs = q.getResultList();

		buf.append(UtilisateurUtil.getUtilisateurImpactes(utilisateurs));
		return buf.toString();

	}

	@DbWrite
	public void performModifPrix(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		for (LigneContratDTO lig : modeleContrat.produits)
		{
			ModeleContratProduit mcp = em.find(ModeleContratProduit.class, lig.idModeleContratProduit);
			mcp.setPrix(lig.prix);
		}
	}

	// AJOUT DE PRODUITS SUR UN CONTRAT

	/**
	 * Permet de charger la liste des produits hors de ce contrat dans une
	 * transaction en lecture
	 */
	@DbRead
	public List<Produit> getProduitHorsContrat(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);

		Query q = em
				.createQuery("select p from Produit p WHERE p.producteur=:prod and NOT EXISTS (select mcp from ModeleContratProduit mcp where mcp.produit = p and mcp.modeleContrat=:mc) ORDER BY p.nom,p.conditionnement");
		q.setParameter("mc", mc);
		q.setParameter("prod", mc.getProducteur());
		List<Produit> us = q.getResultList();
		return us;
	}

	@DbWrite
	public void performAjoutProduit(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		//
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContrat.id);

		// On calcule l'index le plus grand, qui servira pour numeroter les
		// produits ajoutés
		Query q = em.createQuery("select max(mcp.indx) from ModeleContratProduit mcp WHERE mcp.modeleContrat=:mc");
		q.setParameter("mc", mc);
		int index = SQLUtils.toInt(q.getSingleResult()) + 1;

		// Création de toutes les lignes pour chacun des produits
		for (LigneContratDTO lig : modeleContrat.getProduits())
		{
			ModeleContratProduit mcp = new ModeleContratProduit();
			mcp.setIndx(index);
			mcp.setModeleContrat(mc);
			mcp.setPrix(lig.getPrix().intValue());
			mcp.setProduit(em.find(Produit.class, lig.produitId));

			em.persist(mcp);

			index++;
		}
	}

	// Suppression de produits sur un contrat

	/**
	 * 
	 * 
	 */
	@DbRead
	public String getSuppressProduitInfo(Long idModeleContrat, List<Long> modeleContratProduitsToSuppress)
	{
		EntityManager em = TransactionHelper.getEm();

		StringBuffer buf = new StringBuffer();

		// On affiche la liste des produits supprimés
		buf.append(" " + modeleContratProduitsToSuppress.size() + " produits vont être supprimés.<br/>Voici la liste des produits supprimés :<ul>");
		for (Long idModeleContratProduit : modeleContratProduitsToSuppress)
		{
			ModeleContratProduit mcp = em.find(ModeleContratProduit.class, idModeleContratProduit);
			Produit produit = mcp.getProduit();

			buf.append("<li>" + produit.getNom() + "," + produit.getConditionnement() + "</li>");
		}
		buf.append("</ul><br/>");

		// On recherches les utilisateurs impactés
		Query q = em.createQuery("select distinct(c.contrat.utilisateur) from ContratCell c where " + " c.modeleContratProduit.id IN :ps "
				+ " order by c.contrat.utilisateur.nom, c.contrat.utilisateur.prenom");

		q.setParameter("ps", modeleContratProduitsToSuppress);
		List<Utilisateur> utilisateurs = q.getResultList();

		buf.append(UtilisateurUtil.getUtilisateurImpactes(utilisateurs));
		return buf.toString();
	}

	@DbWrite
	public void performSupressProduit(Long idModeleContrat, List<Long> modeleContratProduitsToSuppress)
	{
		EntityManager em = TransactionHelper.getEm();

		//
		for (Long mcpId : modeleContratProduitsToSuppress)
		{

			ModeleContratProduit mcp = em.find(ModeleContratProduit.class, mcpId);

			// On supprime toutes les commandes de ce produit
			Query q = em.createQuery("select cc from ContratCell cc where cc.modeleContratProduit=:mcp");
			q.setParameter("mcp", mcp);
			List<ContratCell> ccs = q.getResultList();
			for (ContratCell contratCell : ccs)
			{
				em.remove(contratCell);
			}

			// On supprime les dates barrées de ce produit
			q = em.createQuery("select mce from ModeleContratExclude mce where mce.produit=:mcp");
			q.setParameter("mcp", mcp);
			List<ModeleContratExclude> mces = q.getResultList();
			for (ModeleContratExclude mce : mces)
			{
				em.remove(mce);
			}

			// Enfin, on supprime le produit du contrat
			em.remove(mcp);
		}

	}

	//
	@DbWrite
	public void performModifProduitOrdreContrat(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		int index = 0;
		//
		for (LigneContratDTO lig : modeleContrat.getProduits())
		{
			ModeleContratProduit mcp = em.find(ModeleContratProduit.class, lig.idModeleContratProduit);
			mcp.setIndx(index);

			index++;
		}
	}
	
	
	
	/*
	 * Gestion des barrer sur les produits 
	 */
	
	
	static public class ResBarrerProduit
	{
		// 
		public String msg;
		
		// Liste des cellules de contrat qu'il faudra supprimer
		public List<Long> idContratCellToDeletes;
	}
	
	@DbRead
	public ResBarrerProduit getBarrerProduitInfo(ContratDTO newContratDTO)
	{
		EntityManager em = TransactionHelper.getEm();

		StringBuffer buf = new StringBuffer();

		// On charge les anciennes valeurs  
		ContratDTO oldContratDTO = new MesContratsService().loadContrat(newContratDTO.modeleContratId,null);
		
		// On compare les anciennes valeurs et les nouvelles
		InfoBarrerProduitDTO infos = compareGrilleBarre(oldContratDTO.excluded,newContratDTO.excluded,newContratDTO,em);

		// On affiche la liste des produits impactés 
		buf.append(infos.computeStringInfo());
		buf.append("<br/>");
		
		// On recherche la liste des toutes les cellules de contrats qu'il faudra mettre à zéro 
		List<ContratCell> toDelete = getAllContratCellToDelete(infos.cellChanges,em);
		
		// On ajoute les infos sur les quantités mises à zéro 
		buf.append(computeQteMiseAzero(toDelete));
		buf.append("<br/>");

		// On recherche la liste des utilisateurs impactés 
		List<Utilisateur> utilisateurs = CollectionUtils.selectDistinct(toDelete, e->e.getContrat().getUtilisateur());
		CollectionUtils.sort(utilisateurs,e->e.getNom(),e->e.getPrenom());
		
		buf.append(UtilisateurUtil.getUtilisateurImpactes(utilisateurs));
		
		ResBarrerProduit res = new ResBarrerProduit();
		res.msg = buf.toString();
		res.idContratCellToDeletes = CollectionUtils.select(toDelete, e->e.getId());
		
		return res;

	}

	private List<ContratCell> getAllContratCellToDelete(List<CellChange> cellChanges, EntityManager em)
	{
		List<ContratCell> res = new ArrayList<ContratCell>();
		
		for (CellChange cellChange : cellChanges)
		{
			// On recherches les cellules de contrats impactés 
			Query q = em.createQuery("select c from ContratCell c where " + 
										" c.modeleContratProduit.id =:mcpid and " +
										" c.modeleContratDate.id =:mcdid");
			
			q.setParameter("mcpid", cellChange.modeleContratProduit.getId());
			q.setParameter("mcdid", cellChange.modeleContratDate.getId());
			
			res.addAll(q.getResultList());
			
		}
		return res;
	}

	private String computeQteMiseAzero(List<ContratCell> toDelete)
	{
		// On réalise un eclatement 1D des ContratCell par produit 
		G1D<Produit, ContratCell> c1 = new G1D<Produit, ContratCell>();
		
		c1.fill(toDelete);
		c1.groupBy(e->e.getModeleContratProduit().getProduit());
		
		c1.sortLigAdvanced(e->e.getModeleContratProduit().getIndx(),true);
		
		c1.compute();
		
		// On fait ensuite de la chaine a afficher 
		List<Produit> produits = c1.getKeys();
		
		StringBuilder buf = new StringBuilder();
		buf.append("Les quantités suivantes vont être mises à zéro: ( " + produits.size() + " produits)<br/>");
		for (int i = 0; i < produits.size(); i++)
		{
			Produit prod = produits.get(i);
			List<ContratCell> cells = c1.getCell(i);
			int qte = CollectionUtils.accumulateInt(cells, e->e.getQte()); 
		
			buf.append(" - " + qte + " " + prod.getNom() + " , " + prod.getConditionnement() + "<br/>");
		}
		buf.append("<br/>");
		
		return buf.toString();
	}

	private InfoBarrerProduitDTO compareGrilleBarre(boolean[][] oldC, boolean[][] newC, ContratDTO contratDTO, EntityManager em)
	{
		InfoBarrerProduitDTO info = new InfoBarrerProduitDTO();
		
		
		int nbLig = contratDTO.contratLigs.size();
		int nbCol = contratDTO.contratColumns.size();
		
		
		for (int j = 0; j < nbCol; j++)
		{
			ContratColDTO col = contratDTO.contratColumns.get(j);
		
			for (int i = 0; i < nbLig; i++)
			{
				ContratLigDTO lig = contratDTO.contratLigs.get(i);
				processDiffSate(i,j,oldC,newC,info,col,lig,em);
			}
		}
		
		return info;
	}
	
	
	private void processDiffSate(int i, int j, boolean[][] oldC, boolean[][] newC, InfoBarrerProduitDTO info, ContratColDTO col, ContratLigDTO lig, EntityManager em)
	{
		// Calcul de l'ancien status
		boolean oldState = false;
		if (oldC!=null)
		{
			oldState = oldC[i][j];
		}
		
		// Calcul du nouveau status
		boolean newState = newC[i][j];
		
		// Si pas de changement : rien à faire 
		if (oldState==newState)
		{
			return;
		}
		
		ModeleContratDate mcd = em.find(ModeleContratDate.class,lig.modeleContratDateId);
		ModeleContratProduit mcp = em.find(ModeleContratProduit.class,col.modeleContratProduitId);
		
		// Si on a barré un produit non barré avant
		if (newState==true)
		{
			
			info.addCellChange(DiffState.NO_MORE_DISPO,mcd,mcp);
		}
		// Si on a débarré un produit barré avant 
		else
		{
			info.addCellChange(DiffState.NOW_DISPO,mcd,mcp);
		}
	}


	
	
	
	@DbWrite
	public void performBarrerProduitInfo(ResBarrerProduit resBarrerProduit,ContratDTO newContratDTO)
	{
		EntityManager em = TransactionHelper.getEm();
						
		// On efface les cellules de contrat
		for (Long idContratCell : resBarrerProduit.idContratCellToDeletes)
		{
			//
			ContratCell contratCell = em.find(ContratCell.class, idContratCell);
			
			// On supprime la cellule dans la base devenue inutile
			em.remove(contratCell);
		}
		
		// On barre ensuite les cellules sur le contrat vierge 
		new GestionContratService().updateDateBarreesModeleContrat(newContratDTO);
	}
	
	
	// Modification des regeles de gestion des jokers

	/**
	 * Permet de connaitre les impacts de de la modification des règles de gestion des jokers
	 */
	@DbRead
	public String getModifJokerInfo(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		ModeleContrat mc  =em.find(ModeleContrat.class, modeleContrat.id);

		StringBuilder buf = new StringBuilder();
		
		TypedQuery<Contrat> q = em.createQuery("select c from Contrat c  where c.modeleContrat=:mc order by c.utilisateur.nom, c.utilisateur.prenom",Contrat.class);
		q.setParameter("mc", mc);

		List<Contrat> cs = q.getResultList();
		List<Utilisateur> utilisateurs = new ArrayList<Utilisateur>();
		
		for (Contrat c : cs)
		{
			String msg = isJokerConforme(c,modeleContrat);
			if (msg!=null)
			{
				Utilisateur u = c.getUtilisateur();
				buf.append("Le contrat de "+u.nom+" "+u.prenom+" est non conforme :"+msg+"<br/>");
				utilisateurs.add(u);
			}
		}
		
		buf.append("<br/><br/>");
		
		//
		buf.append(UtilisateurUtil.getUtilisateurImpactes(utilisateurs));
		return buf.toString();
	}


	private String isJokerConforme(Contrat c, ModeleContratDTO modeleContrat)
	{
		ContratDTO dto = new MesContratsService().loadContrat(c.getModeleContrat().id, c.getId());
		return new ContratAboManager().isConforme(dto,modeleContrat.jokerNbMin,modeleContrat.jokerNbMax);
	}

	// REQUETAGE DIVERS

	/**
	 * Permet de connaitre le nombre de livraison réelle pour un contrat d'un
	 * amapien donné
	 * 
	 * Exemple : il y a 42 livraisons sur un contrats, mais l'amapien a commandé
	 * uniquement sur 10 livraisons
	 * 
	 */
	@DbRead
	public int getNbLivraisonContrat(Contrat contrat, EntityManager em)
	{
		Query q = em.createQuery("select count(distinct(c.modeleContratDate.id)) from ContratCell c WHERE c.contrat=:cc");
		q.setParameter("cc", contrat);

		return SQLUtils.toInt(q.getSingleResult());
	}

	/**
	 * Permet de connaitre le nombre de produits total pour un contrat d'un
	 * amapien donné
	 * 
	 * 
	 */
	@DbRead
	public int getNbProduitContrat(Contrat contrat, EntityManager em)
	{
		Query q = em.createQuery("select sum(c.qte) from ContratCell c WHERE c.contrat=:cc");
		q.setParameter("cc", contrat);

		return SQLUtils.toInt(q.getSingleResult());
	}

	/**
	 * Vérifie si cet utilisateur posséde ou non un contrat pour ce modele de
	 * contrat
	 * 
	 * @param idModeleContrat
	 * @param userId
	 * @return
	 */
	@DbRead
	public boolean checkIfUserHasContrat(Long idModeleContrat, Long userId)
	{
		EntityManager em = TransactionHelper.getEm();

		Query q = em.createQuery("select count(c) from Contrat c WHERE c.utilisateur.id=:uId and c.modeleContrat.id=:mId");
		q.setParameter("uId", userId);
		q.setParameter("mId", idModeleContrat);

		return SQLUtils.toInt(q.getSingleResult()) > 0;
	}

	

}
