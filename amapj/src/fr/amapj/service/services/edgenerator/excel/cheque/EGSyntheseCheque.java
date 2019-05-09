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
 package fr.amapj.service.services.edgenerator.excel.cheque;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.common.collections.G1D;
import fr.amapj.common.collections.G1D.Cell1;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.model.models.contrat.reel.Paiement;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;


/**
 * Feuille de de synthese multi contrat des cheques
 * 
 * Affiche tous les chèques à remettre à l'AMAP pour tous les amapiens
 *
 */
public class EGSyntheseCheque extends AbstractExcelGenerator
{
	static public enum Mode 
	{
		// Chèques à remettre à l'AMAP par les amapiens
		CHEQUE_A_REMETTRE , 
		
		// Chèques en possession de l'AMAP, qui seront remis aux producteurs 
		CHEQUE_AMAP , 
		
		// Chèques en possession de l'AMAP, qui seront remis aux producteurs 
		CHEQUE_REMIS_PRODUCTEUR ,
		
		// Tous les chèques, quel que soit leur état 
		TOUS
	}
	
	
	private Long idUtilisateur;
	private Mode mode;
	
	/**
	 * Deux cas sont possibles : 
	 * les chèques d'un amapien  (idUtilisateur non null)
	 * les chèques de tous les amapiens  (idUtilisateur = null)
	 * 
	 * 4  modes sont possibles
	 */
	public EGSyntheseCheque(Mode mode,Long idUtilisateur)
	{
		this.mode = mode;
		this.idUtilisateur = idUtilisateur;
	}

	/**
	 * 
	 */
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		List<Utilisateur> utilisateurs = getUtilisateurs(em,idUtilisateur);
		for (Utilisateur utilisateur : utilisateurs)
		{
			addOneSheet(em,utilisateur,et);
		}
		
		// On positionne un message d'avertissement si besoin 
		if(utilisateurs.size()==0)
		{
			et.addSheet("AMAP", 1, 50);
			et.addRow("Il y a aucun chèque pour aucun utilisateur !!",et.grasGaucheWrappe);
		}
	}
		

	
	
	private void addOneSheet(EntityManager em, Utilisateur u, ExcelGeneratorTool et)
	{
		SimpleDateFormat df1 = FormatUtils.getStdDate();
		SimpleDateFormat df2 = FormatUtils.getTimeStd();
		
		String nomUtilisateur = u.nom+" "+u.prenom;
		
		// Création de la feuille 
		et.addSheet(nomUtilisateur, 1, 100);
		
		// Affichage des informations d'entête
		
		// 3 lignes de titre
		ParametresDTO param = new ParametresService().getParametres();
		et.addRow(param.nomAmap, et.grasCentre);
		
		et.addRow();
		
		et.addRow(getTitre(nomUtilisateur), et.grasGaucheNonWrappe);
		 
		et.addRow("Extrait le "+df2.format(DateUtils.getDate()),et.grasGaucheNonWrappe);
		
		// Une ligne vide
		et.addRow();
		
				
		// On recherche tout d'abord la liste des livraisons concernées
		List<Paiement> cells = getCheque(em,u);
		
		// On réalise une projection 1D de ces cheques , avec en ligne les modeles de contrats 
		G1D<ModeleContrat,Paiement> c1 = new G1D<>();
		
		// 
		c1.fill(cells);
		c1.groupBy(e->e.getContrat().getModeleContrat());
		
		// Tri sur les lignes par nom du modele du contrat
		c1.sortLig(e->e.nom, true);
		
		// Tri sur les cellules par date de paiement
		c1.sortCell(e->e.getModeleContratDatePaiement().getDatePaiement(), true);
		
		// Calcul
		c1.compute();
		
		// On en deduit la liste des blocs à afficher
		List<Cell1<ModeleContrat, Paiement>> modeleContrats = c1.getFullCells();
		
		// Pour chaque bloc, on l'affiche 
		for (Cell1<ModeleContrat, Paiement> cell1 : modeleContrats)
		{
			drawBlocDate(em,et,cell1.lig,cell1.values,df1);
		}	
	}
	


	private void drawBlocDate(EntityManager em, ExcelGeneratorTool et, ModeleContrat mc, List<Paiement> paiements, SimpleDateFormat df1)
	{
		et.addRow(mc.nom+" - "+mc.producteur.nom,et.grasGaucheNonWrappeBordureGray);
		et.addRow();
		
		for (Paiement paiement : paiements)
		{
			String mnt = new CurrencyTextFieldConverter().convertToString(paiement.getMontant())+" €";
			String lib = "1 chèque de "+mnt+" qui sera débité le "+df1.format(paiement.getModeleContratDatePaiement().getDatePaiement())+
						"  - Etat du chèque ="+getEtat(paiement.getEtat());
			et.addRow(lib,et.nonGrasGaucheNonWrappe);
		}
		
		et.addRow();
		
	}


	/**
	 * Calcul du titre du document en fonction du mode choisi
	 */
	private String getTitre(String nomUtilisateur)
	{
		switch (mode)
		{
		case CHEQUE_A_REMETTRE:
			return "Liste des chèques à remettre à l'AMAP par "+nomUtilisateur;

		case CHEQUE_AMAP:
			return "Liste des chèques fournis à l'AMAP par "+nomUtilisateur+" et non remis aux producteurs";
			
		case CHEQUE_REMIS_PRODUCTEUR:
			return "Liste des chèques fournis à l'AMAP par "+nomUtilisateur+" et remis aux producteurs";
			
		case TOUS:
			return "Liste de tous les chèques de "+nomUtilisateur+" (quelque soit leur état)";			

		default:
			throw new AmapjRuntimeException();
		}
	}

	
	
	
	private String getEtat(EtatPaiement etat)
	{
		switch (etat)
		{
		case A_FOURNIR:
			return "Chèque à fournir à l'AMAP";
		
		case AMAP:
			return "Chèque à l'AMAP";
					
		case PRODUCTEUR:
			return "Chèque remis au producteur";

		default:
			throw new AmapjRuntimeException();
		}
		
	}

	private List<Paiement> getCheque(EntityManager em, Utilisateur u)
	{
		String query = "select p from Paiement p WHERE ";
		if (mode!=Mode.TOUS)
		{
			query = query + " p.etat = :etat  AND ";
		}
		query = query + " p.contrat.utilisateur=:u";
		
		TypedQuery<Paiement> q = em.createQuery(query,Paiement.class);
		if (mode!=Mode.TOUS)
		{
			q.setParameter("etat",computeEtatPaiement());
		}		
		q.setParameter("u",u);
		return q.getResultList();
	}
	
	
	private EtatPaiement computeEtatPaiement()
	{
		switch (mode)
		{
		case CHEQUE_A_REMETTRE:
			return EtatPaiement.A_FOURNIR;

		case CHEQUE_AMAP:
			return EtatPaiement.AMAP;
			
		case CHEQUE_REMIS_PRODUCTEUR:
			return EtatPaiement.PRODUCTEUR;
			
		case TOUS:
		default:
			throw new AmapjRuntimeException();
		}
	}

	/**
	 * Si idUtilisateur !=null : retourne cet utilisateur
	 * 
	 * Si idUtilisateur ==null :retourne la liste des utilisateurs ayant des chèques à remettre 
	 */
	public List<Utilisateur> getUtilisateurs(EntityManager em, Long idUtilisateur)
	{
		if (idUtilisateur!=null)
		{
			return Arrays.asList(em.find(Utilisateur.class, idUtilisateur));
		}
		
		String query = "select distinct(p.contrat.utilisateur) from Paiement p ";
		if (mode!=Mode.TOUS)
		{
			query = query + " WHERE p.etat = :etat ";
		}
		query = query + " ORDER BY p.contrat.utilisateur.nom , p.contrat.utilisateur.prenom";
		
		TypedQuery<Utilisateur> q = em.createQuery(query, Utilisateur.class);
		
		if (mode!=Mode.TOUS)
		{
			q.setParameter("etat",computeEtatPaiement());
		}
		
		return q.getResultList();
	}
	


	@Override
	public String getFileName(EntityManager em)
	{
		String nomUtilisateur = "tous les amapiens";
		if (idUtilisateur!=null)
		{
			Utilisateur u = em.find(Utilisateur.class, idUtilisateur);
			nomUtilisateur = u.nom+" "+u.prenom;
		}
		
		String etat;
		switch (mode)
		{
		case CHEQUE_A_REMETTRE:
			etat ="promis";
			break;

		case CHEQUE_AMAP:
			etat ="recus";
			break;
			
		case CHEQUE_REMIS_PRODUCTEUR:
			etat ="remis";
			break;
			
		case TOUS:
			etat ="promis-recus-remis";
			break;
			
		default:
			throw new AmapjRuntimeException();
		}
		
		return "synthese-cheque-"+etat+"-"+nomUtilisateur;
	}
	

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		String nomUtilisateur = "tous les amapiens";
		if (idUtilisateur!=null)
		{
			Utilisateur u = em.find(Utilisateur.class, idUtilisateur);
			nomUtilisateur = u.nom+" "+u.prenom;
		}
		return getTitre(nomUtilisateur);
	}
	

	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

}
