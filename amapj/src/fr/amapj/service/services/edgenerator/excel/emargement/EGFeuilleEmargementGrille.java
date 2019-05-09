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
 package fr.amapj.service.services.edgenerator.excel.emargement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.LongUtils;
import fr.amapj.common.StringUtils;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.editionspe.emargement.ContenuCellule;
import fr.amapj.model.models.editionspe.emargement.FeuilleEmargementJson;
import fr.amapj.model.models.editionspe.emargement.ParametresProduitsJson;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.emargement.EGFeuilleEmargement.LibInfo;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;


/**
 * Permet la generation d'une feuille d'émargement (hebdomadaire ou mensuelle)
 * au format grille
 */
public class EGFeuilleEmargementGrille
{
	
	public EGFeuilleEmargementGrille()
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
		int nbCol =  entete.prodCols.size()+5;
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
			et.mergeCellsRight(index, dateCol.nbColProduit);
			index = index + dateCol.nbColProduit;
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
			et.mergeCellsRight(index, dateCol.nbColProduit);
			index = index + dateCol.nbColProduit;
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
		for (ProduitColonne prodCol : entete.prodCols)
		{
			if (prodCol.idProduit!=null)
			{
				et.setCell(index, prodCol.nomColonne, et.grasCentreBordure);
				et.setColumnWidthInMm(index, prodCol.largeurColonne);
			}
			else
			{
				et.setCell(index, prodCol.nomColonne, et.grasCentreBordureColorPetit);
				et.setColumnWidthInMm(index, planningJson.getLgColPresence());
			}
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
		
		// On indique ensuite que les 3 premières lignes sont à garder sur chaque page à l'impression 
		et.setRepeatingRow(1, 3);
		
		//
		int numLigne = 0;
		for (Utilisateur utilisateur : utilisateurs)
		{
			addRowUtilisateur(et,utilisateur,em,entete,numLigne,libInfo,planningJson.getContenuCellule(),planningJson.getHauteurLigne());
			numLigne++;
		}
		
	}
	
	

	private void addRowUtilisateur(ExcelGeneratorTool et, Utilisateur utilisateur, EntityManager em, Entete entete,int numLigne,LibInfo libInfo, ContenuCellule contenuCellule, int hauteurLigne)
	{
		int[] qtes = getQte(utilisateur, em, entete.prodCols,libInfo);
		
		et.addRow();
		if (hauteurLigne>0)
		{
			et.setRowHeigthInMm(hauteurLigne);
		}
		et.setCell(0, utilisateur.getNom(), et.switchGray(et.grasGaucheWrappeBordure,numLigne));
		et.setCell(1, utilisateur.getPrenom(), et.switchGray(et.nonGrasGaucheBordure,numLigne));
		
		int index = 2;
		for (int i = 0; i < qtes.length; i++)
		{
			
			ProduitColonne prodCol = entete.prodCols.get(i);
			
			if (prodCol.idProduit!=null)
			{
				if (contenuCellule==ContenuCellule.QUANTITE)
				{
					et.setCellQte(index, qtes[i], et.switchGray(et.grasCentreBordure,numLigne));
				}
				else
				{
					String str = qtes[i]>0 ? "X" : " ";
					et.setCell(index, str, et.switchGray(et.grasCentreBordure,numLigne));	
				}
			}
			else
			{
				// Colonne présence
				et.setCell(index, "", et.grasCentreBordureColor);
			}
				
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
		
	}
	

	private int[] getQte(Utilisateur u,EntityManager em,List<ProduitColonne> prodCols,LibInfo libInfo)
	{
		Query q = em.createQuery("select c from ContratCell c WHERE "
				+ " c.modeleContratDate.dateLiv >= :d1 AND c.modeleContratDate.dateLiv<:d2 AND "
				+ "c.contrat.utilisateur=:u");
		
		q.setParameter("d1",libInfo.debut);
		q.setParameter("d2",libInfo.fin);
		q.setParameter("u",u);
		
		int[] res = new int[prodCols.size()];
		List<ContratCell> ccs = q.getResultList();
		for (ContratCell cc : ccs)
		{
			int index = findIndex(prodCols,cc);
			if (index!=-1)
			{
				res[index]=res[index]+cc.getQte();
			}
		}
		
		return res;
	}
	
	

	private int findIndex(List<ProduitColonne> prodCols, ContratCell cc)
	{
		int s = prodCols.size();
		for (int i = 0; i < s; i++)
		{
			ProduitColonne produitColonne= prodCols.get(i);
			
			if (	(produitColonne.idProduit!=null) 
				&&  (produitColonne.idProduit.contains(cc.getModeleContratProduit().getProduit().getId())) 
				&&	(cc.getModeleContratDate().getDateLiv().equals(produitColonne.dateColonne.date))    )
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Retourne la liste de tous les utilisateurs qui ont commandé au moins une fois dans le mois
	 * un des prdouits dans la liste des produits paramètrés
	 * @param em
	 * @return
	 */
	private List<Utilisateur> getUtilisateur(EntityManager em,LibInfo libInfo, FeuilleEmargementJson planningJson)
	{
		// On crée la liste de produits concernés 
		List<Long> ids = new ArrayList<Long>();
		for (ParametresProduitsJson pp : planningJson.getParametresProduits())
		{
			ids.add(pp.getIdProduit());
		}
		
		// On fait la requete
		Query q = em.createQuery("select distinct(c.contrat.utilisateur) from ContratCell c WHERE "
				+ " c.modeleContratDate.dateLiv >= :d1 AND c.modeleContratDate.dateLiv<:d2 AND c.modeleContratProduit.produit.id IN :ids "
				+ " ORDER BY c.contrat.utilisateur.nom , c.contrat.utilisateur.prenom");
		
		q.setParameter("d1",libInfo.debut);
		q.setParameter("d2",libInfo.fin);
		q.setParameter("ids",ids);
		
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
		List<PeriodePermanenceDateDTO> permanenceDTOs = getPermanence(em,libInfo);
		
		// On recherche toutes les dates de livraisons sur ce mois, ordonnées par ordre croissant
		List<Date> dateLivs = getDateLivs(em,libInfo);
		
		// Pour chaque date, on fait la liste des produits concernés
		for (Date dateLiv : dateLivs)
		{
			findProduits(em,entete,dateLiv,planningJson,permanenceDTOs);
		}
		
		// 
		return entete;
	}
	
	private List<PeriodePermanenceDateDTO> getPermanence(EntityManager em,LibInfo libInfo)
	{
		return new PeriodePermanenceService().getAllDistributionsActif(em, libInfo.debut, libInfo.fin);
	}
	
	
	private List<Date> getDateLivs(EntityManager em,LibInfo libInfo)
	{
		Query q = em.createQuery("select distinct(mcd.dateLiv) from ModeleContratDate mcd WHERE mcd.dateLiv >= :d1 AND mcd.dateLiv<:d2 ORDER BY mcd.dateLiv");
		
		q.setParameter("d1",libInfo.debut);
		q.setParameter("d2",libInfo.fin);
		
		List<Date> us = q.getResultList();
		return us;
	}
	
	private void findProduits(EntityManager em, Entete entete, Date dateLiv, FeuilleEmargementJson planningJson, List<PeriodePermanenceDateDTO> permanenceDTOs)
	{
		List<ProduitColonne> cols = computeProdCol(em,dateLiv,planningJson);
		
		// Si il n'y a pas de produit pour cette date, on retourne sans rien ajouter à l'entete
		if (cols.size()==0)
		{
			return ;
		}
		
		
		// Ajout de la colonne présence
		ProduitColonne prodCol = new ProduitColonne();
		prodCol.nomColonne = "Présence";
		cols.add(prodCol);
		
		//
		DateColonne dateCol = new DateColonne();
		dateCol.date = dateLiv;
		dateCol.nbColProduit = cols.size();
		dateCol.permanence = findPermanence(permanenceDTOs,dateLiv);
		
		//
		for (ProduitColonne produitColonne : cols)
		{
			produitColonne.dateColonne = dateCol;
		}
				
		entete.prodCols.addAll(cols);
		entete.dateCols.add(dateCol);
		
	}

	private List<ProduitColonne> computeProdCol(EntityManager em, Date dateLiv, FeuilleEmargementJson planningJson)
	{
		List<ProduitColonne> res = new ArrayList<>();
		ProduitColonne previous = null;
		for (ParametresProduitsJson prodJson : planningJson.getParametresProduits())
		{
			Produit p = em.find(Produit.class, prodJson.getIdProduit());
			ProduitColonne prodCol = constructProdCol(em,p,dateLiv,prodJson,previous);
			if (prodCol!=null)
			{
				previous = prodCol;
				res.add(prodCol);
			}
		}
		return res;
	}
	
	
	private ProduitColonne constructProdCol(EntityManager em, Produit p, Date dateLiv, ParametresProduitsJson prodJson, ProduitColonne previous)
	{
		Query q = em.createQuery("select count(c) from ContratCell c WHERE "
				+ " c.modeleContratProduit.produit=:p AND "
				+ " c.modeleContratDate.dateLiv=:d ");
		q.setParameter("p", p);
		q.setParameter("d", dateLiv);

		// Si pas de commande pour ce produit à cette date : on le supprime
		if(LongUtils.toInt(q.getSingleResult())==0)
		{
			return null;
		}
		
		// Si la colonne précédente a le même nom que celle que l'on va créer, alors on fusionne les deux
		if ((previous!=null) && (StringUtils.equals(previous.nomColonne, prodJson.getTitreColonne())))
		{
			previous.idProduit.add(p.getId());
			return null;
		}
		
		
		
		ProduitColonne prodCol = new ProduitColonne();
		prodCol.idProduit = new ArrayList<Long>(); 
		prodCol.idProduit.add(p.getId());
		prodCol.largeurColonne = prodJson.getLargeurColonne();
		prodCol.nomColonne = prodJson.getTitreColonne();
		
		return prodCol;
	}
	
	
	/**
	 * Calcul des informations de permanences
	 * @param permanenceDTOs
	 * @param dateLiv
	 * @return
	 */
	private String findPermanence(List<PeriodePermanenceDateDTO> permanenceDTOs, Date dateLiv)
	{
		for (PeriodePermanenceDateDTO permanenceDTO : permanenceDTOs)
		{
			if (permanenceDTO.datePerm.equals(dateLiv))
			{
				return permanenceDTO.getNomInscrit("\n");
			}
		}
		
		return "";
	}
	
	
	/**
	 * Un "ProduitColonne" correspond à n produits (par exemple les oeufs par 6 et les oeufs par 12)
	 *
	 */
	static public class ProduitColonne
	{
		public String nomColonne;
		
		public int largeurColonne;
		
		// est égal à null pour la colonne Présence
		public List<Long> idProduit;
		
		public DateColonne dateColonne;
		

	}
	
	static public class DateColonne
	{
		public Date date;
		
		// Nombre de colonne produit pour cette date donnée
		public int nbColProduit;
		
		public String permanence;
	}
	
	
	static public class Entete
	{
		public List<ProduitColonne> prodCols = new ArrayList<>();
		
		public List<DateColonne> dateCols = new ArrayList<>();
		
	}
}
