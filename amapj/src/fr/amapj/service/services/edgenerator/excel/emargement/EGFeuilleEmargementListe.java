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
 package fr.amapj.service.services.edgenerator.excel.emargement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.poi.ss.usermodel.Row;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.StringUtils;
import fr.amapj.model.models.editionspe.emargement.ContenuCellule;
import fr.amapj.model.models.editionspe.emargement.FeuilleEmargementJson;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.service.engine.generator.excel.ExcelCellAutoSize;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.emargement.EGFeuilleEmargement.LibInfo;
import fr.amapj.service.services.meslivraisons.MesLivraisonsDTO;
import fr.amapj.service.services.meslivraisons.MesLivraisonsService;
import fr.amapj.service.services.meslivraisons.ProducteurLivraisonsDTO;
import fr.amapj.service.services.meslivraisons.QteProdDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceService;


/**
 * Permet la generation d'une feuille d'émargement (hebdomadaire ou mensuelle)
 * au format liste
 */
public class EGFeuilleEmargementListe
{
	private static final char BULLET_CHARACTER = '\u2022';
	
	public EGFeuilleEmargementListe()
	{
	}
	
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et,FeuilleEmargementJson planningJson, LibInfo libInfo)
	{
		//
		SimpleDateFormat df = new SimpleDateFormat("dd MMMMM");
		
		
		// Recherche de toutes les colonnes du document 
		Entete entete = getEntetePlanning(em,planningJson,libInfo);
		
		// Recherche de tous les utilisateurs du document
		List<Utilisateur> utilisateurs = getUtilisateur(em,libInfo,planningJson);
		
		
		// Les colonnes en + sont le nom, prenom et telephone1 et telephone 2 et commentaire
		int nbCol =  entete.dateCols.size()*2+5;
		et.addSheet("Feuille émargement "+libInfo.lib2+" "+libInfo.lib1, nbCol, 25);
		et.setMarginAndPageFormat(planningJson);
		
		// Ecriture de la ligne des dates
		et.addRow();
		et.setCell(0, "DISTRIBUTIONS "+libInfo.lib1.toUpperCase(), et.grasCentreBordure);
		et.mergeCellsRight(0, 2);
		
		int index = 2;
		for (DateColonne dateCol : entete.dateCols)
		{
			et.setCell(index, df.format(dateCol.date), et.grasCentreBordure);
			et.mergeCellsRight(index, 2);
			index = index + 2;
		}
		
		et.setCell(index, "Téléphone 1 ", et.grasCentreBordure);
		index++;
		et.setCell(index, "Téléphone 2 ", et.grasCentreBordure);
		index++;
		et.setCell(index, "Commentaire ", et.grasCentreBordure);
		
		// Ecriture de la ligne des responsables de la distribution
		et.addRow();
		et.setRowHeigth(3);
		et.setCell(1, "Responsable de distribution", et.nongrasGaucheWrappe);
		
		index = 2;
		for (DateColonne dateCol : entete.dateCols)
		{
			et.setCell(index, dateCol.permanence, et.nonGrasCentreBordure);
			et.mergeCellsRight(index, 2);
			index = index + 2;
		}
		
		et.setCell(index, "", et.grasCentreBordure);
		index++;
		et.setCell(index, "", et.grasCentreBordure);
		
		// Ecriture de la ligne avec les noms des produits
		// et positionnement de toutes les largeurs
		et.addRow();
		et.setCell(0, "Nom", et.grasCentreBordure);
		et.setColumnWidthInMm(0, planningJson.getLgColNom());
		
		et.setCell(1, "Prénom", et.grasCentreBordure);
		et.setColumnWidthInMm(1, planningJson.getLgColPrenom());
		
		index = 2;
		for (DateColonne prodCol : entete.dateCols)
		{
			// Colonne 1 avec les produits
			et.setCell(index, "Produits", et.grasCentreBordure);
			et.setColumnWidthInMm(index, planningJson.getLgColProduits());
			index++;
			
			// Colonne 2 avec la signature
			et.setCell(index, "Présence", et.grasCentreBordureColorPetit);
			et.setColumnWidthInMm(index, planningJson.getLgColPresence());
			index++;
		}
		
		et.setCell(index, "", et.grasCentreBordure);
		et.mergeCellsUp(index, 3);
		et.setColumnWidthInMm(index, planningJson.getLgColnumTel1());
		
		index++;
		et.setCell(index, "", et.grasCentreBordure);
		et.mergeCellsUp(index, 3);
		et.setColumnWidthInMm(index, planningJson.getLgColnumTel2());
		
		
		index++;
		et.setCell(index, "", et.grasCentreBordure);
		et.mergeCellsUp(index, 3);
		et.setColumnWidthInMm(index, planningJson.getLgColCommentaire());
		
		
		
		//
		int numLigne = 0;
		for (Utilisateur utilisateur : utilisateurs)
		{
			addRowUtilisateur(et,utilisateur,em,entete,numLigne,planningJson.getContenuCellule(),planningJson.getHauteurLigne(),planningJson);
			numLigne++;
		}
		
	}
	
	

	private void addRowUtilisateur(ExcelGeneratorTool et, Utilisateur utilisateur, EntityManager em, Entete entete,int numLigne, ContenuCellule contenuCellule, int hauteurLigne,FeuilleEmargementJson feuilleEmargementJson)
	{
		ExcelCellAutoSize as = new ExcelCellAutoSize(5);
		
		Row currentRow = et.addRow();
		
		et.setCell(0, utilisateur.getNom(), et.switchGray(et.grasGaucheWrappeBordure,numLigne));
		et.setCell(1, utilisateur.getPrenom(), et.switchGray(et.nonGrasGaucheBordure,numLigne));
		
		int index = 2;
		for (DateColonne prodCol : entete.dateCols)
		{
			String listeProduits = getListeProduit(prodCol,utilisateur,feuilleEmargementJson);
			
			// Colonne 1 avec les produits
			et.setCell(index, listeProduits, et.switchGray(et.nonGrasGaucheBordure,numLigne));
			
			as.addCell(et.getColumnWidthInPoints(index), "Arial", 10);
			as.addLine(listeProduits);
			
			index++;
			
			// Colonne 2 avec la signature
			et.setCell(index, "", et.grasCentreBordureColor);
			index++;
		}
		
		
		// Numéro de telephone 1
		et.setCell(index, utilisateur.getNumTel1(), et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Numéro de telephone 2
		index++;
		et.setCell(index, utilisateur.getNumTel2(), et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Commentaire
		index++;
		et.setCell(index, "", et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Calcul de la hauteur de ligne optimale 
		as.autosize(currentRow);
	}
	

	

	private String getListeProduit(DateColonne prodCol, Utilisateur utilisateur,FeuilleEmargementJson feuilleEmargementJson)
	{
		MesLivraisonsDTO dto = new MesLivraisonsService().getLivraisonFeuilleEmargementListe(prodCol.date, utilisateur.getId());
		
		if (dto.jours.size()==0)
		{
			// L'amapien n' a rien commandé à cette date 
			return "";
		}
		else if (dto.jours.size()==1)
		{
			return getListeProduits(dto.jours.get(0).producteurs,feuilleEmargementJson);
		}
		else
		{
			// Impossible d'avoir deux dates de livraisons le meme jour ! 
			throw new AmapjRuntimeException();
		}
	}
		
	private String getListeProduits(List<ProducteurLivraisonsDTO> producteurs,FeuilleEmargementJson feuilleEmargementJson)
	{
		StringBuffer buf = new StringBuffer();
		for (ProducteurLivraisonsDTO producteurLiv : producteurs)
		{
			if (feuilleEmargementJson.getNomDuContrat()==ChoixOuiNon.OUI)
			{
				buf.append(producteurLiv.modeleContrat);
				buf.append("\n");
			}
			
			if (feuilleEmargementJson.getNomDuProducteur()==ChoixOuiNon.OUI)
			{
				buf.append(producteurLiv.producteur);
				buf.append("\n");
			}
			
			for (QteProdDTO cell : producteurLiv.produits)
			{
				String content = cell.qte+" "+cell.nomProduit+" , "+cell.conditionnementProduit;
				buf.append(" "+BULLET_CHARACTER+" "+content+"\n");
			}
		}	
		
		// On supprime le dernier /n
		return StringUtils.removeLast(buf.toString(), "\n");
	}

	

	/**
	 * Retourne la liste de tous les utilisateurs qui ont commandé au moins un produit  dans la periode concernée 
	 * 
	 * @param em
	 * @return
	 */
	private List<Utilisateur> getUtilisateur(EntityManager em,LibInfo libInfo, FeuilleEmargementJson planningJson)
	{
		// 
		Query q = em.createQuery("select distinct(c.contrat.utilisateur) from ContratCell c WHERE "
				+ " c.modeleContratDate.dateLiv >= :d1 AND c.modeleContratDate.dateLiv<:d2  "
				+ " ORDER BY c.contrat.utilisateur.nom , c.contrat.utilisateur.prenom");
		
		q.setParameter("d1",libInfo.debut);
		q.setParameter("d2",libInfo.fin);
		
		List<Utilisateur> us = q.getResultList();
		return us;
	}

	/**
	 * Calcul de l'entête du planning mensuel
	 * @param em
	 * @param planningJson
	 * @return
	 */
	private Entete getEntetePlanning(EntityManager em, FeuilleEmargementJson planningJson,LibInfo libInfo)
	{
		Entete entete = new Entete();
		
		// Recherche des distributions dans ce mois
		List<PermanenceDTO> permanenceDTOs = getPermanence(em,libInfo);
		
		// On recherche toutes les dates de livraisons sur ce mois, ordonnées par ordre croissant
		List<Date> dateLivs = getDateLivs(em,libInfo);
		
		// 
		for (Date dateLiv : dateLivs)
		{
			DateColonne dateCol = new DateColonne();
			dateCol.date = dateLiv;
			dateCol.permanence = findPermanence(permanenceDTOs,dateLiv);
			
			entete.dateCols.add(dateCol);
		}
		
		// 
		return entete;
	}
	
	private List<PermanenceDTO> getPermanence(EntityManager em,LibInfo libInfo)
	{
		return new PermanenceService().getAllDistributions(em, libInfo.debut, libInfo.fin);
	}
	
	
	private List<Date> getDateLivs(EntityManager em,LibInfo libInfo)
	{
		Query q = em.createQuery("select distinct(mcd.dateLiv) from ModeleContratDate mcd WHERE mcd.dateLiv >= :d1 AND mcd.dateLiv<:d2 ORDER BY mcd.dateLiv");
		
		q.setParameter("d1",libInfo.debut);
		q.setParameter("d2",libInfo.fin);
		
		List<Date> us = q.getResultList();
		return us;
	}
	
	
	
	/**
	 * Calcul des informations de permanences
	 * @param permanenceDTOs
	 * @param dateLiv
	 * @return
	 */
	private String findPermanence(List<PermanenceDTO> permanenceDTOs, Date dateLiv)
	{
		for (PermanenceDTO permanenceDTO : permanenceDTOs)
		{
			if (permanenceDTO.datePermanence.equals(dateLiv))
			{
				return permanenceDTO.getUtilisateurs("\n");
			}
		}
		
		return "";
	}
	
	
	static public class DateColonne
	{
		public Date date;
		
		public String permanence;
	}
	
	
	static public class Entete
	{
		public List<DateColonne> dateCols = new ArrayList<>();
		
	}
}
