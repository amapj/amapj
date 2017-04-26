/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang.time.DateUtils;

import fr.amapj.common.LongUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.distribution.DatePermanence;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.TypEditionSpecifique;
import fr.amapj.model.models.editionspe.emargement.FeuilleEmargementJson;
import fr.amapj.model.models.editionspe.emargement.TypFeuilleEmargement;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.access.AccessManagementService;
import fr.amapj.service.services.edgenerator.excel.emargement.EGFeuilleEmargement;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.service.services.saisiepermanence.PermanenceService;

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
	public MesLivraisonsDTO getMesLivraisons(Date d,List<RoleList> roles,Long idUtilisateur)
	{
		EntityManager em = TransactionHelper.getEm();

		MesLivraisonsDTO res = new MesLivraisonsDTO();

		Utilisateur user = em.find(Utilisateur.class, idUtilisateur);

		res.dateDebut = fr.amapj.common.DateUtils.firstMonday(d);
		res.dateFin = DateUtils.addDays(res.dateDebut,6);
		
		// On récupère ensuite la liste de tous les cellules de contrats de cet utilisateur dans cet intervalle
		List<ContratCell> cells = getAllQte(em, res.dateDebut,res.dateFin,user);
		
		//
		for (ContratCell cell : cells)
		{
			addCell(cell,res);
		}
		
		// On récupère ensuite la liste de toutes les permanences de cet utilisateur dans cet intervalle
		List<DatePermanence> dds = getAllDistributionsForUtilisateur(em, res.dateDebut,res.dateFin,user);
		for (DatePermanence dd : dds)
		{
			addDistribution(em,dd,res);
		}
		
		// On récupère ensuite le planning mensuel si il y en a un
		res.planningMensuel = computePlanningMensuel(em,res.dateDebut,res.dateFin,roles);
		
		return res;

	}
	

	private List<DatePermanence> getAllDistributionsForUtilisateur(EntityManager em, Date dateDebut, Date dateFin,Utilisateur utilisateur)
	{
		Query q = em.createQuery("select distinct(du.datePermanence) from DatePermanenceUtilisateur du WHERE " +
				"du.datePermanence.datePermanence>=:deb and " +
				"du.datePermanence.datePermanence<=:fin and " +
				"du.utilisateur=:user " +
				"order by du.datePermanence.datePermanence");
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		q.setParameter("user", utilisateur);
		
		List<DatePermanence> dds = q.getResultList();
		
		return dds;
	}
	
	private void addDistribution(EntityManager em,DatePermanence dd, MesLivraisonsDTO res)
	{
		JourLivraisonsDTO jour = findJour(dd.getDatePermanence(),res);
		jour.distribution = new PermanenceService().createDistributionDTO(em, dd);
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
		
		boolean hasLivraison = hasLivraison(em, dateDebut, dateFin);
		
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
					if (hasLivraison)
					{
						EGFeuilleEmargement planningMensuel = new EGFeuilleEmargement(editionSpecifique.getId(), dateDebut, suffix);
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
	 * Indique si il y a au moins une livraison cette semaine
	 * @param dto
	 * @return
	 */
	private boolean hasLivraison(EntityManager em, Date dateDebut, Date dateFin)
	{
		// On extrait toutes les livraisons sur l'intervalle 
		Query q = em.createQuery("select count(mcd.dateLiv) from ModeleContratDate mcd WHERE " +
				"mcd.dateLiv>=:deb AND " +
				"mcd.dateLiv<=:fin ");
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		
		return LongUtils.toInt(q.getSingleResult())>0;
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
	public MesLivraisonsDTO getLivraisonProducteur(Date d,Long idProducteur)
	{
		EntityManager em = TransactionHelper.getEm();
		
		MesLivraisonsDTO res = new MesLivraisonsDTO();

		Producteur producteur = em.find(Producteur.class, idProducteur);

		res.dateDebut = fr.amapj.common.DateUtils.firstMonday(d);
		res.dateFin = DateUtils.addDays(res.dateDebut,6);
		
		// On récupère ensuite la liste de tous les cellules de contrats de cet utilisateur dans cet intervalle
		List<ContratCell> cells = getAllQte(em, res.dateDebut,res.dateFin,producteur);
		
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
