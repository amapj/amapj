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
 package fr.amapj.service.services.gestioncontrat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.SQLUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratDatePaiement;
import fr.amapj.model.models.contrat.modele.ModeleContratExclude;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.service.engine.tools.DbToDto;
import fr.amapj.service.engine.tools.DtoToDb;
import fr.amapj.service.engine.tools.DtoToDb.ElementToAdd;
import fr.amapj.service.engine.tools.DtoToDb.ElementToUpdate;
import fr.amapj.service.engine.tools.DtoToDb.ListDiff;
import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.service.services.gestioncontratsigne.update.GestionContratSigneUpdateService;
import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.notification.DeleteNotificationService;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.views.gestioncontrat.editorpart.FrequenceLivraison;

/**
 * Permet la gestion des modeles de contrat
 * 
 *  
 * 
 */
public class GestionContratService
{
	
	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES CONTRATS

	/**
	 * Permet de charger la liste de tous les modeles de contrats dans une
	 * transaction en lecture
	 */
	@DbRead
	public List<ModeleContratSummaryDTO> getModeleContratInfo()
	{
		EntityManager em = TransactionHelper.getEm();
		
		Query q = em.createQuery("select mc from ModeleContrat mc WHERE mc.etat!=:etat");
		q.setParameter("etat",EtatModeleContrat.ARCHIVE);
		
		return DbToDto.transform(q, (ModeleContrat mc)->createModeleContratInfo(em, mc));
	}

	public ModeleContratSummaryDTO createModeleContratInfo(EntityManager em, ModeleContrat mc)
	{
		ModeleContratSummaryDTO info = new ModeleContratSummaryDTO();

		
		info.id = mc.getId();
		info.nom = mc.getNom();
		info.nomProducteur = mc.getProducteur().nom;
		info.producteurId = mc.getProducteur().getId();
		info.finInscription = mc.getDateFinInscription();
		info.etat = mc.getEtat();

		// Avec une sous requete, on obtient la liste de toutes les dates de
		// livraison
		List<ModeleContratDate> dates = getAllDates(em, mc);

		info.nbLivraison = dates.size()-getNbDateAnnulees(em,mc);
		
		info.nbInscrits = getNbInscrits(em, mc);

		if (dates.size() >= 1)
		{
			info.dateDebut = dates.get(0).getDateLiv();
			info.dateFin = dates.get(dates.size() - 1).getDateLiv();
		}

		info.nbProduit = getNbProduit(em, mc);

		return info;
	}
	
	
	static public class DateInfo
	{
		public Date dateDebut;
		public Date dateFin;
		public int nbDateLivs;
	}
	
	public DateInfo getDateDebutFin(EntityManager em, ModeleContrat mc)
	{
		DateInfo di = new DateInfo();
		
		Query q = em.createQuery("select min(c.dateLiv),max(c.dateLiv),count(c.dateLiv) from ModeleContratDate c WHERE c.modeleContrat=:mc");
		q.setParameter("mc",mc);
		
		Object[] res = (Object[]) q.getSingleResult();
		
		di.dateDebut = (Date) res[0];
		di.dateFin = (Date) res[1];
		di.nbDateLivs = SQLUtils.toInt(res[2]);
		
		return di;
	}
			
	
	
	/**
	 * Retourne le nombre d'adherent ayant souscrit à ce modele de contrat
	 * 
	 * @return
	 */
	@DbRead
	public int getNbInscrits(Long mcId)
	{
		EntityManager em = TransactionHelper.getEm();
		ModeleContrat mc = em.find(ModeleContrat.class, mcId);
		return getNbInscrits(em, mc);
	}
	

	/**
	 * Retourne le nombre d'adherent ayant souscrit à ce modele de contrat
	 * @param em
	 * @param mc
	 * @return
	 */
	private int getNbInscrits(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select count(c.id) from Contrat c WHERE c.modeleContrat=:mc");
		q.setParameter("mc",mc);
		
		return ((Long) q.getSingleResult()).intValue();
	}

	/**
	 * Retourne le nombre de dates annulées pour un modele de contrat
	 */
	private int getNbDateAnnulees(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select count(p.id) from ModeleContratExclude p WHERE p.modeleContrat=:mc and p.produit is null");
		q.setParameter("mc",mc);
		
		return ((Long) q.getSingleResult()).intValue();
	}

	private int getNbProduit(EntityManager em, ModeleContrat mc)
	{
		return getAllProduit(em, mc).size();
	}

	/**
	 * Retrouve la liste des produits, triés suivant la valeur indx
	 *
	 */
	public List<ModeleContratProduit> getAllProduit(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select mcp from ModeleContratProduit mcp where mcp.modeleContrat=:mc ORDER BY mcp.indx"); 
		q.setParameter("mc", mc);
		
		List<ModeleContratProduit> prods = q.getResultList();
		return prods;
	}

	public List<ModeleContratDate> getAllDates(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select mcd from ModeleContratDate mcd where mcd.modeleContrat=:mc ORDER BY mcd.dateLiv"); 
		q.setParameter("mc", mc);
		
		List<ModeleContratDate> dates = q.getResultList();
		return dates;
	}

	public List<ModeleContratExclude> getAllExcludedDateProduit(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select mce from ModeleContratExclude mce where mce.modeleContrat=:mc"); 
		q.setParameter("mc", mc);

		List<ModeleContratExclude> exclude = q.getResultList();
		return exclude;
	}
	
	
	public List<ModeleContratDatePaiement> getAllDatesPaiements(EntityManager em, ModeleContrat mc)
	{
		// On récupère ensuite la liste de tous les paiements de ce contrat
		Query q = em.createQuery("select p from ModeleContratDatePaiement p WHERE p.modeleContrat=:mc order by p.datePaiement");
		q.setParameter("mc",mc);
		List<ModeleContratDatePaiement> paiements = q.getResultList();
		return paiements;
	}
	
	
	// PARTIE CREATION D'UN MODELE DE CONTRAT
	/**
	 * Permet de pre charger les nouveaux modeles de contrat
	 */
	@DbRead
	public List<LigneContratDTO> getInfoProduitModeleContrat(Long idProducteur)
	{
		EntityManager em = TransactionHelper.getEm();
		
		List<LigneContratDTO> res = new ArrayList<LigneContratDTO>();
		
		Query q = em.createQuery("select p from Produit p " +
				"WHERE p.producteur=:producteur order by p.id");
		q.setParameter("producteur",em.find(Producteur.class, idProducteur));
		List<Produit> prods = q.getResultList();
		for (Produit prod : prods)
		{
			LigneContratDTO l =new LigneContratDTO();
			l.prix = new Integer(0);
			l.produitId = prod.getId();
			l.produitNom = prod.getNom();
			l.produitConditionnement = prod.getConditionnement();
			res.add(l);
		}
		return res;
	}
	

	// PARTIE CHARGEMENT D'UN MODELE DE CONTRAT

	/**
	 * Permet de charger les informations d'un modele contrat dans une
	 * transaction en lecture
	 */
	@DbRead
	public ModeleContratDTO loadModeleContrat(Long id)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, id);

		ModeleContratDTO info = new ModeleContratDTO();
		info.id = mc.getId();
		info.nom = mc.getNom();
		info.description = mc.getDescription();
		info.producteur = mc.getProducteur().getId();
		info.dateFinInscription = mc.getDateFinInscription();
		info.gestionPaiement = mc.getGestionPaiement();
		info.textPaiement = mc.getTextPaiement();
		info.libCheque = mc.getLibCheque();
		info.dateRemiseCheque = mc.getDateRemiseCheque();
		info.nature = mc.nature;
		info.cartePrepayeeDelai = mc.cartePrepayeeDelai;
		info.jokerNbMin = mc.jokerNbMin;
		info.jokerNbMax = mc.jokerNbMax;
		info.jokerAutorise = mc.jokerNbMax!=0 ? ChoixOuiNon.OUI : ChoixOuiNon.NON;
		info.jokerMode = mc.jokerMode;
		info.jokerDelai = mc.jokerDelai;

		// Avec une sous requete, on obtient la liste de toutes les dates de
		// livraison
		List<ModeleContratDate> dates = getAllDates(em, mc);
		for (ModeleContratDate date : dates)
		{
			DateModeleContratDTO dto = new DateModeleContratDTO();
			dto.dateLiv = date.getDateLiv();
			info.dateLivs.add(dto);
		}

		if (dates.size() >= 1)
		{
			info.dateDebut = dates.get(0).getDateLiv();
			info.dateFin = dates.get(dates.size() - 1).getDateLiv();
		}

		// Avec une sous requete, on récupere la liste des produits
		List<ModeleContratProduit> prods = getAllProduit(em, mc);
		for (ModeleContratProduit prod : prods)
		{
			LigneContratDTO lig = new LigneContratDTO();
			lig.idModeleContratProduit = prod.getId();
			lig.produitId = prod.getProduit().getId();
			lig.produitNom = prod.getProduit().getNom();
			lig.produitConditionnement = prod.getProduit().getConditionnement();
			lig.prix = prod.getPrix();

			info.produits.add(lig);
		}

		info.frequence = guessFrequence(dates);
		
		// Avec une sous requete, on récupere la liste des dates de paiements
		List<ModeleContratDatePaiement> datePaiements = getAllDatesPaiements(em, mc);
		if (datePaiements.size() >= 1)
		{
			info.premierCheque = datePaiements.get(0).getDatePaiement();
			info.dernierCheque = datePaiements.get(datePaiements.size()-1).getDatePaiement();
		}
		for (ModeleContratDatePaiement date : datePaiements)
		{
			DatePaiementModeleContratDTO dto = new DatePaiementModeleContratDTO();
			dto.datePaiement = date.getDatePaiement();
			info.datePaiements.add(dto);
		}
		

		return info;
	}

	private FrequenceLivraison guessFrequence(List<ModeleContratDate> dates)
	{
		if ((dates.size() == 0) || dates.size() == 1)
		{
			return FrequenceLivraison.UNE_SEULE_LIVRAISON;
		}
		int delta = DateUtils.getDeltaDay(dates.get(0).getDateLiv(), dates.get(1).getDateLiv());
		if (delta == 7)
		{
			return FrequenceLivraison.UNE_FOIS_PAR_SEMAINE;
		} else if (delta == 14)
		{
			return FrequenceLivraison.QUINZE_JOURS;
		} else
		{
			return FrequenceLivraison.UNE_FOIS_PAR_MOIS;
		}

	}

	// PARTIE SAUVEGARDE D'UN NOUVEAU CONTRAT

	/**
	 * Permet de sauvegarder un nouveau modele de contrat 
	 * 
	 */
	@DbWrite
	public Long saveNewModeleContrat(final ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On charge le producteur
		Producteur p = em.find(Producteur.class, modeleContrat.producteur);

		// Informations d'entete
		ModeleContrat mc = new ModeleContrat();
		mc.setProducteur(p);
		mc.setNom(modeleContrat.nom);
		mc.setDescription(modeleContrat.description);
		mc.setDateFinInscription(modeleContrat.dateFinInscription);
		mc.nature = modeleContrat.nature;
		mc.cartePrepayeeDelai = modeleContrat.cartePrepayeeDelai;
		mc.jokerNbMin = modeleContrat.jokerNbMin;
		mc.jokerNbMax = modeleContrat.jokerNbMax;
		mc.jokerMode = modeleContrat.jokerMode;
		mc.jokerDelai = modeleContrat.jokerDelai;
		
		
		// Informations sur le paiement
		mc.setGestionPaiement(modeleContrat.gestionPaiement);
		mc.setTextPaiement(modeleContrat.textPaiement);
		mc.setDateRemiseCheque(modeleContrat.dateRemiseCheque);
		mc.setLibCheque(modeleContrat.libCheque);
		
		em.persist(mc);

		// Création de toutes les lignes pour chacune des dates
		List<Date> dates = getAllDates(modeleContrat.dateDebut, modeleContrat.dateFin, modeleContrat.frequence,modeleContrat.dateLivs);
		if (dates.size()==0)
		{
			throw new AmapjRuntimeException("Vous ne pouvez pas créer un contrat avec 0 date de livraison");
		}
		
		for (Date date : dates)
		{
			ModeleContratDate md = new ModeleContratDate();
			md.setModeleContrat(mc);
			md.setDateLiv(date);
			em.persist(md);
		}

		// Création de loutes les lignes pour chacun des produits
		List<LigneContratDTO> produits = modeleContrat.getProduits();
		int index = 0;
		for (LigneContratDTO lig : produits)
		{
			ModeleContratProduit mcp = new ModeleContratProduit();
			mcp.setIndx(index);
			mcp.setModeleContrat(mc);
			mcp.setPrix(lig.getPrix().intValue());
			mcp.setProduit(em.find(Produit.class, lig.produitId));

			em.persist(mcp);

			index++;

		}
		
		// Informations de dates de paiement
		if (modeleContrat.gestionPaiement!=GestionPaiement.NON_GERE)
		{
			List<Date> datePaiements = getAllDatePaiements(modeleContrat.premierCheque, modeleContrat.dernierCheque, modeleContrat.frequence,modeleContrat.dateRemiseCheque);
			for (Date datePaiement : datePaiements)
			{
				ModeleContratDatePaiement md = new ModeleContratDatePaiement();
				md.setModeleContrat(mc);
				md.setDatePaiement(datePaiement);
				em.persist(md);
			}
		}
		
		return mc.getId();
	}
	
	private List<Date> getAllDatePaiements(Date premierCheque, Date dernierCheque, FrequenceLivraison frequence, Date dateRemiseCheque)
	{
		List<Date> res = new ArrayList<Date>();

		// Si une seule livraison : c'est fini
		if (frequence.equals(FrequenceLivraison.UNE_SEULE_LIVRAISON))
		{
			int cpt = 0;
			res.add(dateRemiseCheque);
			return res;
		} 
		
		int cpt =0;
				
		while (premierCheque.before(dernierCheque) || premierCheque.equals(dernierCheque))
		{
			cpt++;
			res.add(premierCheque);
			premierCheque = DateUtils.addMonth(premierCheque, 1);

			if (cpt > 1000)
			{
				throw new RuntimeException("Erreur dans la saisie des dates");
			}
		}

		return res;
	}
	
	
	
	
	

	public List<Date> getAllDates(Date dateDebut, Date dateFin, FrequenceLivraison frequence, List<DateModeleContratDTO> dateLivs)
	{
		List<Date> res = new ArrayList<Date>();

		int cpt = 0;
		res.add(dateDebut);

		// Si une seule livraison : c'est fini
		if (frequence.equals(FrequenceLivraison.UNE_SEULE_LIVRAISON))
		{
			return res;
		} 
		// Si la liste a été définie complètement
		else if (frequence.equals(FrequenceLivraison.AUTRE))
		{
			return getAllDatesAutre(dateLivs);
		}
		else if (frequence.equals(FrequenceLivraison.UNE_FOIS_PAR_MOIS))
		{
			return getAllDatesUneFoisParMois(dateDebut, dateFin);
		}

		int delta = 0;
		if (frequence.equals(FrequenceLivraison.UNE_FOIS_PAR_SEMAINE))
		{
			delta = 7;
		} 
		else if (frequence.equals(FrequenceLivraison.QUINZE_JOURS))
		{
			delta = 14;
		}

		dateDebut = DateUtils.addDays(dateDebut, delta);

		while (dateDebut.before(dateFin) || dateDebut.equals(dateFin))
		{
			cpt++;
			res.add(dateDebut);
			dateDebut = DateUtils.addDays(dateDebut, delta);

			if (cpt > 1000)
			{
				throw new AmapjRuntimeException("Erreur dans la saisie des dates");
			}
		}

		return res;
	}

	private List<Date> getAllDatesAutre(List<DateModeleContratDTO> dateLivs)
	{
		List<Date> res = new ArrayList<>();
		for (DateModeleContratDTO dto : dateLivs)
		{
			res.add(dto.dateLiv);
		}
		return res;
	}

	/**
	 * Calcul permettant d'avoir par exemple tous les 1er jeudi du mois
	 */
	private List<Date> getAllDatesUneFoisParMois(Date dateDebut, Date dateFin)
	{
		List<Date> res = new ArrayList<Date>();

		int cpt = 0;
		res.add(dateDebut);

		int rank = DateUtils.getDayOfWeekInMonth(dateDebut);
		int delta = 7;

		dateDebut = DateUtils.addDays(dateDebut, delta);

		while (dateDebut.before(dateFin) || dateDebut.equals(dateFin))
		{
			cpt++;
			if (DateUtils.getDayOfWeekInMonth(dateDebut) == rank)
			{
				res.add(dateDebut);
			}
			dateDebut = DateUtils.addDays(dateDebut, delta);

			if (cpt > 1000)
			{
				throw new RuntimeException("Erreur dans la saisie des dates");
			}
		}

		return res;
	}

	// PARTIE SUPPRESSION

	/**
	 * Permet de supprimer un contrat Ceci est fait dans une transaction en
	 * ecriture
	 */
	@DbWrite
	public void deleteContrat(Long id)  throws UnableToSuppressException
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, id);
		
		int nbInscrits = getNbInscrits(em, mc);
		if (nbInscrits>0)
		{
			String str = "Vous ne pouvez plus supprimer ce contrat<br/>"+
					 "car "+nbInscrits+" adhérents ont déjà souscrits à ce contrat<br/><br/>."+
					 "Si vous souhaitez réellement supprimer ce contrat,<br/>"+
					 "allez tout d'abord dans \"Gestion des contrats signés\", puis vous cliquez sur le bouton \"Supprimer un contrat signé\""+
					 "pour supprimer tous les contrats signés";
			throw new UnableToSuppressException(str);
		}

		suppressAllDatesPaiement(em, mc);
		deleteAllDateBarreesModeleContrat(em, mc);
		new DeleteNotificationService().deleteAllNotificationDoneModeleContrat(em, mc);
		suppressAllDates(em, mc);
		suppressAllProduits(em, mc);

		em.remove(mc);
	}

	private void suppressAllProduits(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select mcp from ModeleContratProduit mcp where mcp.modeleContrat=:mc"); 
		q.setParameter("mc", mc);
		SQLUtils.deleteAll(em, q);
	}

	private void suppressAllDates(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select mcd from ModeleContratDate mcd where mcd.modeleContrat=:mc ORDER BY mcd.dateLiv"); 
		q.setParameter("mc", mc);
		SQLUtils.deleteAll(em, q);
	}
	
	private void suppressAllDatesPaiement(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select d from ModeleContratDatePaiement d WHERE d.modeleContrat=:mc");
		q.setParameter("mc",mc);
		SQLUtils.deleteAll(em, q);
	}

	// PARTIE MISE A JOUR

	/**
	 * Permet de mettre à jour l'etat d'un contrat
	 * 
	 */
	@DbWrite
	public void updateEtat(EtatModeleContrat newValue, Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);
		mc.setEtat(newValue);
	}

	/**
	 * Permet de mettre à jour les elements d'entete d'un contrat
	 * y compris sa nature 
	 * 
	 * @param newValue
	 * @param idModeleContrat
	 */
	@DbWrite
	public void updateEnteteModeleContrat(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContrat.id);
		mc.setDateFinInscription(modeleContrat.dateFinInscription);
		mc.setNom(modeleContrat.nom);
		mc.setDescription(modeleContrat.description);
		mc.cartePrepayeeDelai = modeleContrat.cartePrepayeeDelai;
		mc.nature = modeleContrat.nature;
	}

	/**
	 * Permet de mettre à jour les dates d'un contrat
	 */
	@DbWrite
	public void updateDateModeleContrat(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContrat.id);

		// Calcul de la liste des nouvelles dates 
		List<Date> newList = getAllDates(modeleContrat.dateDebut, modeleContrat.dateFin, modeleContrat.frequence,modeleContrat.dateLivs);
		if (newList.size()==0)
		{
			throw new AmapjRuntimeException("Vous ne pouvez pas créer un contrat avec 0 date de livraison");
		}
		
		// Calcul de la liste des anciennes dates
		List<ModeleContratDate> oldList = getAllDates(em, mc);

		// Calcul de la différence entre les deux listes
		ListDiff<ModeleContratDate, Date, Date> diff = DtoToDb.diffList(oldList, newList, e->e.getDateLiv(),e->e);
		
		// On efface les dates en trop
		GestionContratSigneUpdateService update  = new GestionContratSigneUpdateService();
		for (ModeleContratDate modeleContratDate : diff.toSuppress)
		{
			update.suppressOneDateLiv(modeleContratDate.getId());
		}
		
		// On crée les nouvelles dates 
		for (ElementToAdd<Date> dateLiv : diff.toAdd)
		{
			update.addOneDateLiv(em,dateLiv.dto,mc);
		}
	}



	/**
	 * Perlet la mise à jour des dates barrées d'un contrat dans une transaction
	 * en ecriture
	 */
	@DbWrite
	public void updateDateBarreesModeleContrat(ContratDTO contratDTO)
	{
		EntityManager em = TransactionHelper.getEm();
		 
		
		ModeleContrat mc = em.find(ModeleContrat.class, contratDTO.modeleContratId);

		// On commence par effacer toutes les dates exclues
		deleteAllDateBarreesModeleContrat(em, mc);

		// On recree ensuite toutes les exclusions
		boolean[][] excluded = contratDTO.excluded;
		for (int i = 0; i < contratDTO.contratLigs.size(); i++)
		{
			ContratLigDTO ligDto = contratDTO.contratLigs.get(i);
			if (isFullExcludedLine(excluded, i,contratDTO.contratColumns.size()) == true)
			{
				ModeleContratExclude exclude = new ModeleContratExclude();
				exclude.setModeleContrat(mc);
				exclude.setDate(em.find(ModeleContratDate.class, ligDto.modeleContratDateId));
				exclude.setProduit(null);
				em.persist(exclude);
			} else
			{
				for (int j = 0; j < contratDTO.contratColumns.size(); j++)
				{
					if (excluded[i][j] == true)
					{
						ContratColDTO colDto = contratDTO.contratColumns.get(j);

						ModeleContratExclude exclude = new ModeleContratExclude();
						exclude.setModeleContrat(mc);
						exclude.setDate(em.find(ModeleContratDate.class, ligDto.modeleContratDateId));
						exclude.setProduit(em.find(ModeleContratProduit.class, colDto.modeleContratProduitId));
						em.persist(exclude);
					}
				}
			}
		}
	}

	/**
	 * Return true si toute la ligne est exclue
	 * @param excluded
	 * @param lineNumber
	 * @param lineLength
	 * @return
	 */
	private boolean isFullExcludedLine(boolean[][] excluded, int lineNumber,int lineLength)
	{
		for (int j = 0; j < lineLength; j++)
		{
			if (excluded[lineNumber][j]==false)
			{
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Methode utilitaire permettant de supprimer toutes les dates barrées d'un modele de contrat
	 * @param em
	 * @param mc
	 */
	public void deleteAllDateBarreesModeleContrat(EntityManager em, ModeleContrat mc)
	{
		// On commence par effacer toutes les dates exclues
		List<ModeleContratExclude> excludes = getAllExcludedDateProduit(em, mc);
		for (ModeleContratExclude exclude : excludes)
		{
			em.remove(exclude);
		}
	}
	
	
	/**
	 * Permet la mise à jour des produits d'un contrat dans une transaction
	 * en ecriture
	 *  
	 */
	@DbWrite
	public void updateProduitModeleContrat(final ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContrat.id);

		// Calcul de la liste des anciens produits en base 
		List<ModeleContratProduit> dbList = getAllProduit(em, mc);
				
		// Calcul de la liste des nouveaux produits
		List<LigneContratDTO> dtoList = modeleContrat.getProduits();
		if (dtoList.size()==0)
		{
			throw new AmapjRuntimeException("Vous ne pouvez pas créer un contrat avec 0 produits");
		}
		
		// Calcul de la différence entre les deux listes
		ListDiff<ModeleContratProduit, LigneContratDTO, Long> diff = DtoToDb.diffList(dbList, dtoList, e->e.getProduit().getId(),e->e.produitId);
		
		// On efface les produits en trop
		GestionContratSigneUpdateService update  = new GestionContratSigneUpdateService();
		for (ModeleContratProduit mcp : diff.toSuppress)
		{
			update.suppressOneProduit(mcp.getId());
		}
		
		// On crée les nouveaux produits avec le bon index 
		for (ElementToAdd<LigneContratDTO> toAdd : diff.toAdd)
		{
			update.addOneProduit(em,toAdd.dto.produitId,toAdd.dto.prix,toAdd.index,mc);
		}
		
		// On met à jour les produits existants 
		for (ElementToUpdate<ModeleContratProduit, LigneContratDTO> toUpdate : diff.toUpdate)
		{
			update.updateModeleContratProduit(em,toUpdate.db.getId(),toUpdate.dto.prix,toUpdate.index);
		}
	}

	@DbWrite
	public void updateInfoPaiement(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContrat.id);
		
		// Sauvegarde des info de paiements
		mc.setGestionPaiement(modeleContrat.gestionPaiement);
		mc.setTextPaiement(modeleContrat.textPaiement);
		mc.setDateRemiseCheque(modeleContrat.dateRemiseCheque);
		mc.setLibCheque(modeleContrat.libCheque);

		// Avec une sous requete, on obtient la liste de toutes les dates de
		// paiement  actuellement en base et on les efface
		List<ModeleContratDatePaiement> datesInBase = getAllDatesPaiements(em, mc);
		for (ModeleContratDatePaiement datePaiement : datesInBase)
		{
			em.remove(datePaiement);
		}

		// Création de toutes les lignes pour chacune des dates
		if (modeleContrat.gestionPaiement!=GestionPaiement.NON_GERE)
		{
			List<Date> datePaiements = getAllDatePaiements(modeleContrat.premierCheque, modeleContrat.dernierCheque, modeleContrat.frequence,modeleContrat.dateRemiseCheque);
			for (Date datePaiement : datePaiements)
			{
				ModeleContratDatePaiement md = new ModeleContratDatePaiement();
				md.setModeleContrat(mc);
				md.setDatePaiement(datePaiement);
				em.persist(md);
			}
		}
		
	}
	
	/**
	 * Permet la mise à jour des dates de paiement, c'est à dire la suppression et l'ajout de date 
	 * @param modeleContrat
	 */
	@DbWrite
	public void updateDatePaiement(ModeleContratDTO modeleContrat) throws OnSaveException
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContrat.id);
		
		// Avec une sous requete, on obtient la liste de toutes les dates de
		// paiement  actuellement en base 
		List<ModeleContratDatePaiement> dateInBases = getAllDatesPaiements(em, mc);
		
		// On calcule la liste des dates , en verifiant les doublons et en supprimant la notion d'heure 
		List<Date> dates = extractDates(modeleContrat.datePaiements);
		
		// On cherche les dates à supprimer
		for (ModeleContratDatePaiement dateInBase : dateInBases)
		{
			Date ref = DateUtils.suppressTime(dateInBase.getDatePaiement()); 
					
			// La date en base n'est pas dans la liste des dates
			if (dates.contains(ref)==false)
			{
				int r = getNbPaiementForDate(em, dateInBase);
				if (r>0)
				{
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					String message = "Impossible de supprimer la date de paiement "+df.format(dateInBase.getDatePaiement())+
										" car il y a "+r+" paiements prévus à cette date";
							
					throw new OnSaveException(message);
				}
				
				em.remove(dateInBase);
			}
		}
		
		// On cherche les dates à ajouter
		for (Date date : dates)
		{
			// La date en cours ne fait pas partie des dates en base
			if (contains(dateInBases,date)==false)
			{
				ModeleContratDatePaiement md = new ModeleContratDatePaiement();
				md.setModeleContrat(mc);
				md.setDatePaiement(date);
				em.persist(md);
			}
		}
		
	}
	
	private boolean contains(List<ModeleContratDatePaiement> dateInBases, Date date)
	{
		for (ModeleContratDatePaiement mcdp : dateInBases)
		{
			Date ref = DateUtils.suppressTime(mcdp.getDatePaiement());
			if (ref.equals(date))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Retourne le nombre d'adherent ayant souscrit à ce modele de contrat
	 * @param em
	 * @param mc
	 * @return
	 */
	private int getNbPaiementForDate(EntityManager em, ModeleContratDatePaiement mcdp)
	{
		Query q = em.createQuery("select count(p.id) from Paiement p WHERE p.modeleContratDatePaiement=:mcdp");
		q.setParameter("mcdp",mcdp);
		return ((Long) q.getSingleResult()).intValue();
	}
	
	
	
	private List<Date> extractDates(List<DatePaiementModeleContratDTO> datePaiements) throws OnSaveException
	{
		List<Date> res = new ArrayList<>();
		
		for (DatePaiementModeleContratDTO dto : datePaiements)
		{
			Date date = DateUtils.suppressTime(dto.datePaiement);
			if (res.contains(date)==true)
			{
				throw new OnSaveException("Il y a une date de paiement en doublon");
			}
			res.add(date);
		}
		
		return res;
	}
	
	// MISE A JOUR DES INFORMATIONS JOKERS
	
	@DbWrite
	public void updateJoker(ModeleContratDTO modeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContrat.id);
		
		mc.jokerNbMin = modeleContrat.jokerNbMin;
		mc.jokerNbMax = modeleContrat.jokerNbMax;
		mc.jokerMode = modeleContrat.jokerMode;
		mc.jokerDelai = modeleContrat.jokerDelai;	
	}
	
	

	// MISE A JOUR POUR BASE DE DEMO
	@DbWrite
	public void updateForDemo(DemoDateDTO demoDateDTO)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// Mise à jour des contrats
		Query q = em.createQuery("select mc from ModeleContrat mc");
		List<ModeleContrat> mcs = q.getResultList();
		
		for (ModeleContrat mc : mcs)
		{
			updateForDemo(em, mc,demoDateDTO);
		}
		
		// Mise à jour des mots de passe
		PasswordManager passwordManager = new PasswordManager();
		q = em.createQuery("select u from Utilisateur u order by u.id");
		List<Utilisateur> us = q.getResultList();
		for (Utilisateur u : us)
		{
			passwordManager.setUserPassword(u.getId(), demoDateDTO.password);
		}
	
	}
	
	
	
	private void updateForDemo(EntityManager em, ModeleContrat mc, DemoDateDTO demoDateDTO)
	{
		ModeleContratDTO dto = loadModeleContrat(mc.getId());
		
		dto.dateFinInscription = demoDateDTO.dateFinInscription;
		dto.dateRemiseCheque = demoDateDTO.dateRemiseCheque;
		dto.dateDebut = demoDateDTO.dateDebut;
		dto.dateFin = demoDateDTO.dateFin;
		dto.premierCheque = demoDateDTO.premierCheque;
		dto.dernierCheque = demoDateDTO.dernierCheque;
		
		updateEnteteModeleContrat(dto);
		updateInfoPaiement(dto);
		updateDateModeleContrat(dto);
		
	}

	// Obtenir le montant total commandé pour un contrat
	public int getMontantCommande(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select sum(c.qte*c.modeleContratProduit.prix) " +
				"from ContratCell c " +
				"WHERE c.contrat.modeleContrat=:mc");
		q.setParameter("mc", mc);
		return SQLUtils.toInt(q.getSingleResult());
	}
	
	
	// Obtenir le montant total des avoirs initiaux pour un contrat
	public int getMontantAvoir(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select sum(c.montantAvoir) " +
				"from Contrat c " +
				"WHERE c.modeleContrat=:mc");
		q.setParameter("mc", mc);
		return SQLUtils.toInt(q.getSingleResult());
	}
	
	
	/**
	 * Permet d'obtenir le detail d'un contrat pour l'afficher dans la livraison d'un producteur
	 * 
	 * @return
	 */
	@DbRead
	public String getDetailContrat(Long modeleContratDateId)
	{
		String msg = "";
		Long user = 0L;
		EntityManager em = TransactionHelper.getEm();
				
		ModeleContratDate mcDate = em.find(ModeleContratDate.class, modeleContratDateId);
		
		Query q = em.createQuery("select c from ContratCell c WHERE " +
				"c.modeleContratDate=:mcDate "+
				"order by c.contrat.utilisateur.nom , c.contrat.utilisateur.prenom , c.modeleContratProduit.indx");
		q.setParameter("mcDate", mcDate);
		
		List<ContratCell> cells = q.getResultList();
		for (ContratCell cell : cells)
		{
			int qte =  cell.getQte();
			Utilisateur u = cell.getContrat().getUtilisateur();
			Produit produit = cell.getModeleContratProduit().getProduit();
			
			if (u.getId().equals(user)==false)
			{
				user = u.getId();
				if (msg.length()!=0)
				{
					msg +="</ul>";
				}
				msg += "<b>"+u.getNom()+" "+u.getPrenom()+"</b><ul>";
			}
			msg += "<li>"+qte+" "+produit.getNom()+" , "+produit.getConditionnement()+"</li>";
		}
		
		if (msg.length()!=0)
		{
			msg +="</ul>";
		}
		
		return msg;
	}
	
	
	/**
	 * Retourne la derniere date de livraison d'un contrat
	 * 
	 */
	@DbRead
	public Date getLastDate(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Query q = em.createQuery("select mcd from ModeleContratDate mcd WHERE mcd.modeleContrat.id=:id ORDER BY mcd.dateLiv desc");
		q.setParameter("id",idModeleContrat);
		
		List<ModeleContratDate> dates = q.getResultList();
		
		if (dates.size()==0)
		{
			throw new AmapjRuntimeException("Le contrat "+idModeleContrat+" a aucune date de livraison !!");
		}
		
		return dates.get(0).getDateLiv();
		
	}
	

}
