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
 package fr.amapj.service.services.meslivraisons;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.LongUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.TypEditionSpecifique;
import fr.amapj.model.models.editionspe.emargement.FeuilleEmargementJson;
import fr.amapj.model.models.editionspe.emargement.TypFeuilleEmargement;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.EtatPeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceDate;
import fr.amapj.service.services.edgenerator.excel.emargement.EGFeuilleEmargement;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;

/**
 * Permet la gestion des modeles de contrat
 * 
 *  
 *
 */
public class MesLivraisonsService
{
	private final static Logger logger = LogManager.getLogger();

	public MesLivraisonsService()
	{

	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES LIVRAISONS POUR UN UTILISATEUR

	/**
	 * Permet de charger la liste de tous les livraisons
	 * dans une transaction en lecture
	 */
	@DbRead
	public MesLivraisonsDTO getMesLivraisons(Date dateDebut,Date dateFin,List<RoleList> roles,Long idUtilisateur)
	{
		EntityManager em = TransactionHelper.getEm();

		MesLivraisonsDTO res = new MesLivraisonsDTO();

		Utilisateur user = em.find(Utilisateur.class, idUtilisateur);

		
		// On récupère ensuite la liste de tous les cellules de contrats de cet utilisateur dans cet intervalle
		List<ContratCell> cells = getAllQte(em, dateDebut,dateFin,user);
		
		//
		for (ContratCell cell : cells)
		{
			addCell(cell,res);
		}
		
		// On récupère ensuite la liste de toutes les permanences de cet utilisateur dans cet intervalle
		List<PeriodePermanenceDate> dds = getAllDistributionsForUtilisateur(em, dateDebut,dateFin,user);
		for (PeriodePermanenceDate dd : dds)
		{
			addDistribution(em,dd.datePerm,dd.id,res,dd.periodePermanence);
		}
		
		// On récupère ensuite le planning mensuel si il y en a un
		res.planningMensuel = computePlanningMensuel(em,dateDebut,dateFin,roles);
		
		return res;

	}
	

	public List<PeriodePermanenceDate> getAllDistributionsForUtilisateur(EntityManager em, Date dateDebut, Date dateFin,Utilisateur utilisateur)
	{
		Query q = em.createQuery("select distinct(du.periodePermanenceDate) from PermanenceCell du WHERE " +
				"du.periodePermanenceDate.periodePermanence.etat=:etat and " +
				"du.periodePermanenceDate.datePerm>=:deb and " +
				"du.periodePermanenceDate.datePerm<=:fin and " +
				"du.periodePermanenceUtilisateur.utilisateur=:user " +
				"order by du.periodePermanenceDate.datePerm");
		
		q.setParameter("etat", EtatPeriodePermanence.ACTIF);
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		q.setParameter("user", utilisateur);
		
		List<PeriodePermanenceDate> dds = q.getResultList();
		
		return dds;
	}
	
	private void addDistribution(EntityManager em,Date dateLiv,Long idPeriodePermanenceDate, MesLivraisonsDTO res, PeriodePermanence periodePermanence)
	{
		JourLivraisonsDTO jour = findJour(dateLiv,res);
		if (jour.permanences==null)
		{
			jour.permanences = new ArrayList<JourLivraisonsDTO.InfoPermanence>();
		}
		
		JourLivraisonsDTO.InfoPermanence info = new JourLivraisonsDTO.InfoPermanence();
		info.dateDTO = new PeriodePermanenceService().loadOneDatePermanence(idPeriodePermanenceDate);
		info.periodePermanenceDTO = new PeriodePermanenceService().createSmallPeriodePermanenceDTO(em, periodePermanence);
		jour.permanences.add(info);
	}
	

	
	
	private void addCell(ContratCell cell, MesLivraisonsDTO res)
	{
		JourLivraisonsDTO jour = findJour(cell.getModeleContratDate().getDateLiv(),res);
		ProducteurLivraisonsDTO producteurs = findProducteurLivraison(cell.getModeleContratDate(),cell.getModeleContratDate().getModeleContrat(),jour);
		
		QteProdDTO qteProdDTO = findQteProdDTO(producteurs.produits,cell);
		qteProdDTO.qte = qteProdDTO.qte+cell.getQte();
	}

	private QteProdDTO findQteProdDTO(List<QteProdDTO> produits, ContratCell cell)
	{
		for (QteProdDTO qteProdDTO : produits)
		{
			if (qteProdDTO.idProduit.equals(cell.getModeleContratProduit().getProduit().getId()))
			{
				return qteProdDTO;
			}
		}
		QteProdDTO qteProdDTO = new QteProdDTO();
		qteProdDTO.conditionnementProduit = cell.getModeleContratProduit().getProduit().getConditionnement();
		qteProdDTO.nomProduit = cell.getModeleContratProduit().getProduit().getNom();
		qteProdDTO.idProduit = cell.getModeleContratProduit().getProduit().getId();
		
		produits.add(qteProdDTO);
		
		return qteProdDTO;
	}

	private JourLivraisonsDTO findJour(Date dateLiv, MesLivraisonsDTO res)
	{
		for (JourLivraisonsDTO jour : res.jours)
		{
			if (jour.date.equals(dateLiv))
			{
				return jour;
			}
		}
		
		JourLivraisonsDTO jour = new JourLivraisonsDTO();
		jour.date = dateLiv;
		res.jours.add(jour);
		
		return jour;
		
	}
	
	
	

	private ProducteurLivraisonsDTO findProducteurLivraison(ModeleContratDate modeleContratDate, ModeleContrat modeleContrat, JourLivraisonsDTO jour)
	{
		for (ProducteurLivraisonsDTO producteur : jour.producteurs)
		{
			if (producteur.idModeleContrat.equals(modeleContrat.getId()))
			{
				return producteur;
			}
		}
		ProducteurLivraisonsDTO producteur = new ProducteurLivraisonsDTO();
		producteur.producteur = modeleContrat.getProducteur().nom;
		producteur.modeleContrat = modeleContrat.getNom();
		producteur.idModeleContrat = modeleContrat.getId();
		producteur.idModeleContratDate = modeleContratDate.getId();
		jour.producteurs.add(producteur);
		
		return producteur;
	}

	/**
	 * 
	 */
	private List<ContratCell> getAllQte(EntityManager em, Date dateDebut, Date dateFin, Utilisateur user)
	{
		Query q = em.createQuery("select c from ContratCell c WHERE " +
				"c.modeleContratDate.dateLiv>=:deb AND " +
				"c.modeleContratDate.dateLiv<=:fin and " +
				"c.contrat.utilisateur =:user " +
				"order by c.modeleContratDate.dateLiv, c.contrat.modeleContrat.producteur.id, c.contrat.modeleContrat.id , c.modeleContratProduit.indx");
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		q.setParameter("user", user);
		
		List<ContratCell> prods = q.getResultList();
		return prods;
	}
	
	
	private List<EGFeuilleEmargement> computePlanningMensuel(EntityManager em, Date dateDebut, Date dateFin,List<RoleList> roles)
	{
		List<EGFeuilleEmargement> res = new ArrayList<EGFeuilleEmargement>(); 
		
		if (new EditionSpeService().needPlanningMensuel()==false)
		{
			return res;
		}
		
		// Récupération de la liste des mois
		List<Date> months = getMonth(em,dateDebut,dateFin);
		
		// Récupération de la liste des semaines
		List<Date> weeks = getWeek(em,dateDebut,dateFin);
		
		// Récupération de la liste des editions
		List<EditionSpecifique> editions = new EditionSpeService().getEtiquetteByType(TypEditionSpecifique.FEUILLE_EMARGEMENT);
		
		
		for (EditionSpecifique editionSpecifique : editions)
		{
			if (canAccess(roles,editionSpecifique))
			{	
				// Le nom de l'edition est mis en suffixe uniquement si il y en a plusieurs 
				String suffix = "";
				if (editions.size()!=1)
				{
					suffix = editionSpecifique.nom;
				}
				
				FeuilleEmargementJson planningJson = (FeuilleEmargementJson) new EditionSpeService().load(editionSpecifique.id);
				
				if(planningJson.getTypPlanning()==TypFeuilleEmargement.MENSUEL)
				{
					for (Date month : months)
					{
						EGFeuilleEmargement planningMensuel = new EGFeuilleEmargement(editionSpecifique.getId(), month, suffix);
						res.add(planningMensuel);
					}
				}
				else
				{
					for (Date week : weeks)
					{
						EGFeuilleEmargement planningMensuel = new EGFeuilleEmargement(editionSpecifique.getId(), week, suffix);
						res.add(planningMensuel);
					}
				}
			}
		}
		
		
		return res;
	}
	
	/**
	 * Indique si cet utilisateur a le droit d'accéder à cette édition spécifique
	 * @param u
	 * @param editionSpecifique
	 * @return
	 */
	private boolean canAccess(List<RoleList> roles, EditionSpecifique editionSpecifique)
	{
		FeuilleEmargementJson planningJson = (FeuilleEmargementJson) new EditionSpeService().load(editionSpecifique.id);
		return roles.contains(planningJson.getAccessibleBy());
	}

	/**
	 * Retourne la liste des mois de cette pages mes livraisons
	 * @param dto
	 * @return
	 */
	private List<Date> getMonth(EntityManager em, Date dateDebut, Date dateFin)
	{
		// On extrait toutes les livraisons sur l'intervalle et on en déduit la liste de mois
		Query q = em.createQuery("select distinct(mcd.dateLiv) from ModeleContratDate mcd WHERE " +
				"mcd.dateLiv>=:deb AND " +
				"mcd.dateLiv<=:fin " +
				"order by mcd.dateLiv");
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		
		List<Date> mcds = q.getResultList();
		
		List<Date> res = new ArrayList<Date>();
		
		for (Date mcd : mcds)
		{
			Date month = fr.amapj.common.DateUtils.firstDayInMonth(mcd);
			if (res.contains(month)==false)
			{
				res.add(month);
			}
		}
		return res;
	}
	
	
	/**
	 * Retourne la liste des semaines de cette pages mes livraisons
	 * @param dto
	 * @return
	 */
	private List<Date> getWeek(EntityManager em, Date dateDebut, Date dateFin)
	{
		// On extrait toutes les livraisons sur l'intervalle et on en déduit la liste des semaines
		Query q = em.createQuery("select distinct(mcd.dateLiv) from ModeleContratDate mcd WHERE " +
				"mcd.dateLiv>=:deb AND " +
				"mcd.dateLiv<=:fin " +
				"order by mcd.dateLiv");
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		
		List<Date> mcds = q.getResultList();
		
		List<Date> res = new ArrayList<Date>();
		
		for (Date mcd : mcds)
		{
			Date week = fr.amapj.common.DateUtils.firstMonday(mcd);
			if (res.contains(week)==false)
			{
				res.add(week);
			}
		}
		return res;
	}
	
	
	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES LIVRAISONS DANS VERIFICATION LIVRAISON

	/**
	 * Permet de charger la liste de tous les livraisons
	 * dans une transaction en lecture, pour la feuille d'émargement en mode liste 
	 */
	@DbRead
	public MesLivraisonsDTO getLivraisonFeuilleEmargementListe(Date d,Long idUtilisateur)
	{
		EntityManager em = TransactionHelper.getEm();

		MesLivraisonsDTO res = new MesLivraisonsDTO();

		Utilisateur user = em.find(Utilisateur.class, idUtilisateur);
		
		// On récupère ensuite la liste de tous les cellules de contrats de cet utilisateur dans cet intervalle
		List<ContratCell> cells = getAllQte(em, d,d,user);
		
		//
		for (ContratCell cell : cells)
		{
			addCell(cell,res);
		}
		
		return res;

	}
	
	
	

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES LIVRAISONS POUR UN PRODUCTEUR , ECRAN MES LIVRAISONS

	/**
	 * Retourne la liste des livraisons pour le producteur spécifié 
	 */
	@DbRead
	public MesLivraisonsDTO getLivraisonProducteur(Date dateDebut,Date dateFin,Long idProducteur)
	{
		EntityManager em = TransactionHelper.getEm();
		
		MesLivraisonsDTO res = new MesLivraisonsDTO();

		Producteur producteur = em.find(Producteur.class, idProducteur);
		
		// On récupère ensuite la liste de tous les cellules de contrats de cet utilisateur dans cet intervalle
		List<ContratCell> cells = getAllQte(em, dateDebut,dateFin,producteur);
		
		//
		for (ContratCell cell : cells)
		{
			addCell(cell,res);
		}
		
		return res;

	}
	
	
	/**
	 * 
	 */
	private List<ContratCell> getAllQte(EntityManager em, Date dateDebut, Date dateFin, Producteur producteur)
	{
		Query q = em.createQuery("select c from ContratCell c " +
				"WHERE c.modeleContratDate.dateLiv>=:deb AND " +
				"c.modeleContratDate.dateLiv<=:fin and " +
				"c.contrat.modeleContrat.producteur =:prod " +
				"order by c.modeleContratDate.dateLiv, c.contrat.modeleContrat.producteur.id, c.contrat.modeleContrat.id , c.modeleContratProduit.indx");
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		q.setParameter("prod", producteur);
		
		List<ContratCell> prods = q.getResultList();
		return prods;
	}
		
}
