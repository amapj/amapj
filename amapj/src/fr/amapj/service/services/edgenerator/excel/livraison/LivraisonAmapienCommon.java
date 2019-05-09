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
 package fr.amapj.service.services.edgenerator.excel.livraison;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.FormatUtils;
import fr.amapj.common.collections.G1D;
import fr.amapj.common.collections.G1D.Cell1;
import fr.amapj.common.collections.M2;
import fr.amapj.common.collections.M2.Pair;
import fr.amapj.common.periode.TypPeriode;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceDate;
import fr.amapj.service.services.meslivraisons.MesLivraisonsService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;


/**
 * Partie commune du code entre EGLivraisonAmapien et PGLivraisonAmapien
 */
public class LivraisonAmapienCommon 
{
	
	/**
	 * Réalise la première partie du calcul : liste des dates de livraisons
	 * @return 
	 */
	public List<Pair<Date, List<ContratCell>, List<PeriodePermanenceDate>>> computeListDateLiv(EntityManager em,Utilisateur u,Date startDate,Date endDate)
	{	
		/** PARTIE 1 : les livraisons */
		
		// On recherche tout d'abord la liste des livraisons concernées
		List<ContratCell> cells = getContratCell(em,u,startDate,endDate);
		
		// On réalise une projection 1D de ces livraisons
		// en ligne les dates 
		G1D<Date,ContratCell> c1 = new G1D<>();
		
		// 
		c1.fill(cells);
		c1.groupBy(e->e.getModeleContratDate().dateLiv);
		
		// Pas de tri sur les lignes
		// Pas de tri sur les cellules
		c1.compute();
		
		// On en deduit la liste des blocs pour les livraisons
		List<Cell1<Date, ContratCell>> dateLivs = c1.getFullCells();
		
		
		/** PARTIE 2 : les permanences */
		
		// On recherche d'abord la liste de toutes les permanences
		List<PeriodePermanenceDate> perms = new MesLivraisonsService().getAllDistributionsForUtilisateur(em, startDate, endDate, u);
		

		// On réalise une projection 1D de ces permanences
		// en ligne les dates 
		G1D<Date,PeriodePermanenceDate> c2 = new G1D<>();
		
		// 
		c2.fill(perms);
		c2.groupBy(e->e.datePerm);
		
		// Pas de tri sur les lignes
		// Pas de tri sur les cellules
		c2.compute();
		
		// On en deduit la liste des blocs pour les livraisons
		List<Cell1<Date, PeriodePermanenceDate>> datePerms = c2.getFullCells();
		
		/** PARTIE 3 : On fusionne ensuite ces deux listes */

		 return  M2.merge(dateLivs, e->e.lig, e->e.values , e->new ArrayList<ContratCell>(),
						  datePerms,e->e.lig, e->e.values , e->new ArrayList<PeriodePermanenceDate>()).get();
						 
	}
	

	public String getDescriptionPeriode(TypPeriode typPeriode,Date startDate,Date endDate)
	{
		switch (typPeriode)
		{
		case SEMAINE:
		{
			SimpleDateFormat df = FormatUtils.getFullDate();
			return "la semaine du "+df.format(startDate)+" au "+df.format(endDate);
		}
		
		case MOIS:
		{
			SimpleDateFormat df = FormatUtils.getMoisFullText();
			return "le mois de "+df.format(startDate);
		}
		
		case TRIMESTRE:
		{
			return "le trimestre de "+FormatUtils.formatTrimestreFullText(startDate);
		}
		
		case A_PARTIR_DE:
		{
			SimpleDateFormat df = FormatUtils.getFullDate();
			return "toutes les dates à partir du "+df.format(startDate);
		}	
		
		default:
			throw new AmapjRuntimeException();
		}
	}
	
	
	public String getDescriptionPeriodeTitre(TypPeriode typPeriode,Date startDate,Date endDate)
	{
		switch (typPeriode)
		{
		case SEMAINE:
		{
			SimpleDateFormat df = FormatUtils.getFullDate();
			return ("DE LA SEMAINE DU "+df.format(startDate)+" AU "+df.format(endDate)).toUpperCase();
		}
		
		case MOIS:
		{
			SimpleDateFormat df = FormatUtils.getMoisFullText();
			return ("DU MOIS DE "+df.format(startDate)).toUpperCase();
		}
		
		case TRIMESTRE:
		{
			return ("DU TRIMESTRE DE "+FormatUtils.formatTrimestreFullText(startDate)).toUpperCase();
		}
		
		case A_PARTIR_DE:
		{
			SimpleDateFormat df = FormatUtils.getFullDate();
			return ("POUR TOUTES LES DATES A PARTIR DU "+df.format(startDate)).toUpperCase();
		}	
		
		default:
			throw new AmapjRuntimeException();
		}
	}
	
	
	

	/**
	 * Réalise la deuxième partie du calcul : formatage d'une date de livraison 
	 * @return 
	 */
	public List<Cell1<ModeleContrat, ContratCell>> computeBlocDate(List<ContratCell> contratCells)
	{
		// On réalise une projection 1D de ces livraisons
		// en ligne les modeles de contrats 
		G1D<ModeleContrat,ContratCell> c1 = new G1D<>();
		
		// 
		c1.fill(contratCells);
		c1.groupBy(e->e.getModeleContratDate().modeleContrat);
		
		// Tri par producteur puis par nom de contrat (si plusieurs contrats pour un même producteur : ils se suivent) 
		c1.sortLig(e->e.producteur.nom,true);
		c1.sortLig(e->e.nom,true);

		// tri sur les cellules sur le rang dans le contrat
		c1.sortCell(e->e.getModeleContratProduit().getIndx(), true);
		
		// Calcul des données
		c1.compute();
		
		// On en deduit la liste des blocs à afficher
		List<Cell1<ModeleContrat, ContratCell>> livs = c1.getFullCells();
		
		return livs;
	}
	
	

	/**
	 * Retourne la liste de toutes les livraisons concernées
	 * @param u 
	 */
	private List<ContratCell> getContratCell(EntityManager em, Utilisateur u,Date startDate,Date endDate)
	{
		
		String query = "select c from ContratCell c WHERE c.modeleContratDate.dateLiv >= :d1 ";
		if (endDate!=null)
		{
			query = query + "AND c.modeleContratDate.dateLiv<=:d2 ";
		}
		query = query + " AND c.contrat.utilisateur=:u";
		
		
		TypedQuery<ContratCell> q = em.createQuery(query,ContratCell.class);
				
		q.setParameter("d1",startDate);
		if (endDate!=null)
		{
			q.setParameter("d2",endDate);
		}
		q.setParameter("u",u);
		
		return q.getResultList();
	}
	

	public String getFileName(TypPeriode typPeriode,Date startDate,Date endDate)
	{
		switch (typPeriode)
		{
			case SEMAINE:
				return "semaine-"+FormatUtils.getDateFile().format(startDate);
				
			case MOIS:
				return "mois-"+FormatUtils.getMoisFile().format(startDate);
			
			case TRIMESTRE:
				return "trimestre-"+FormatUtils.formatTrimestreFile(startDate);
				
			case A_PARTIR_DE:
				return "tout-"+FormatUtils.getDateFile().format(startDate);
				
				
			default:
				throw new AmapjRuntimeException();
		}
	}

	/**
	 * Si idUtilisateur !=null : retourne cet utilisateur
	 * 
	 * Si idUtilisateur ==null :retourne la liste des utilisateurs ayant des livraisons sur la periode
	 */
	public List<Utilisateur> getUtilisateurs(EntityManager em, Long idUtilisateur,Date startDate,Date endDate)
	{
		if (idUtilisateur!=null)
		{
			return Arrays.asList(em.find(Utilisateur.class, idUtilisateur));
		}
		
		TypedQuery<Utilisateur> q = em.createQuery("select distinct(c.contrat.utilisateur) from ContratCell c WHERE "
				+ " c.modeleContratDate.dateLiv >= :d1 AND c.modeleContratDate.dateLiv<=:d2 "
				+ " ORDER BY c.contrat.utilisateur.nom , c.contrat.utilisateur.prenom", Utilisateur.class);
		
		q.setParameter("d1",startDate);
		q.setParameter("d2",endDate);
		
		return q.getResultList();
	}
	
	
	public List<String> getInfoPermanence(List<PeriodePermanenceDate> perms)
	{
		List<String> res = new ArrayList<String>();
		for (PeriodePermanenceDate perm : perms)
		{
			res.add(getInfoPermanence(perm));
		}
		return res;
	}
	
	private String getInfoPermanence(PeriodePermanenceDate perm)
	{
		SimpleDateFormat df1 = FormatUtils.getFullDate();
		PeriodePermanenceDateDTO dateDTO = new PeriodePermanenceService().loadOneDatePermanence(perm.id);
		
		String msg = "!! Attention, vous devez réaliser la permanence ce "+df1.format(dateDTO.datePerm)+" ("+perm.periodePermanence.nom+")!! - "+
				"Liste des personnes de permanence : "+dateDTO.getNomInscrit();
		
		return msg;

	}
	
}
