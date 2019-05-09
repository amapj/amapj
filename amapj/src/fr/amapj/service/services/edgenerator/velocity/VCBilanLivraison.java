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
 package fr.amapj.service.services.edgenerator.velocity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.CellStyle;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.common.ResourceUtils;
import fr.amapj.common.collections.G1D.Cell1;
import fr.amapj.common.collections.M2.Pair;
import fr.amapj.common.periode.TypPeriode;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceDate;
import fr.amapj.service.services.edgenerator.excel.livraison.LivraisonAmapienCommon;
import fr.amapj.view.views.editionspe.bilanlivraison.BilanLivraisonEditorPart;
import fr.amapj.view.views.editionspe.engagement.EngagementEditorPart;


public class VCBilanLivraison
{
	public String nomPeriode;
	
	public String nomPeriodeTitre;
	
	public String tableauDateProduit;
	
		
	/**
	 * 
	 */
	public void load(EntityManager em,Utilisateur utilisateur,TypPeriode typPeriode,Date startDate,Date endDate)
	{
		nomPeriode = s(new LivraisonAmapienCommon().getDescriptionPeriode(typPeriode, startDate, endDate));
		nomPeriodeTitre = s(new LivraisonAmapienCommon().getDescriptionPeriodeTitre(typPeriode, startDate, endDate));
		tableauDateProduit = computeTableauDateProduit(em, utilisateur, typPeriode, startDate, endDate);		
	}
	
	
	
	
	public String computeTableauDateProduit(EntityManager em,Utilisateur utilisateur,TypPeriode typPeriode,Date startDate,Date endDate)
	{
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat df = FormatUtils.getFullDate();
		
		// On ajoute les styles TODO ajouter dans le head ? 
		sb.append(ResourceUtils.toStringClass(BilanLivraisonEditorPart.class, "template/styles.html"));
		
		// On démarre la table
		sb.append("<table id=\"bilan_livraison\">");
		
		// On ajoute la premiere ligne
		sb.append("<tr><th class=\"date\">Date de livraison</th><th>Produits</th></tr>");
		
		
		// On calcule la liste des blocs à afficher
		List<Pair<Date, List<ContratCell>, List<PeriodePermanenceDate>>> dateLivs = new LivraisonAmapienCommon().computeListDateLiv(em, utilisateur, startDate, endDate);
				
		// Pour chaque bloc, on l'affiche 
		for (Pair<Date, List<ContratCell>, List<PeriodePermanenceDate>> dateLiv : dateLivs)
		{
			drawBlocDate(dateLiv.key,dateLiv.v1,dateLiv.v2,df,sb);
		}
		
		// On termine la table
		sb.append("</table>");
		
		return sb.toString();
	}
	


	private void drawBlocDate(Date lig, List<ContratCell> values, List<PeriodePermanenceDate> perms, SimpleDateFormat df, StringBuilder buf)
	{
		//
		buf.append("<tr>");

		// Calcul du libelle de la colonne date
		String libColDate = df.format(lig);
		if (perms.size()!=0)
		{
			libColDate = "<b/>"+libColDate+"<br/><br/>PERMANENCE</b>";
		}
		buf.append("<td class=\"date\">");
		buf.append("<p style=\"margin:0.1em\">"+libColDate+"</p>");
		buf.append("</td>");	
		
		// Calcul du libellé de la colonne produits
		
		// Les produits
		String libProduit = getListeProduit(values);
		
		// Les permanences
		String libPermanence = CollectionUtils.asString(new LivraisonAmapienCommon().getInfoPermanence(perms),"",e->"<p style=\"margin:0.1em\"><b>"+e+"</b></p>");
		
		buf.append("<td>");
		buf.append(libProduit+libPermanence);
		buf.append("</td>");
		
		//
		buf.append("</tr>");

	}

	
	
	private String getListeProduit(List<ContratCell> values)
	{
		StringBuilder buf  = new StringBuilder();
		
		// On calcule la liste des contrats à afficher
		List<Cell1<ModeleContrat, ContratCell>> livs = new LivraisonAmapienCommon().computeBlocDate(values);
		
		for (Cell1<ModeleContrat, ContratCell> liv : livs)
		{
			// Nom du contrat 
			buf.append("<p style=\"margin:0.1em\">"+s(liv.lig.getNom())+"</p>");
			buf.append("<ul>");
			
			// Liste de produits commandés pour ce contrat
			for (ContratCell cell : liv.values)
			{
				Produit p = cell.getModeleContratProduit().getProduit();
				String content = cell.getQte()+" x "+p.getNom()+" , "+p.getConditionnement();
				buf.append("<li>"+s(content)+"</li>");
			}
		
			// 
			buf.append("</ul>");
		}
		
		return buf.toString();
	}
	

	/**
	 * Permet d'escaper les caracteres HTML  
	 */
	private String s(String value)
	{
		return VCBuilder.s(value);
	}
	
	
	
	// Getters and setters pour Velocity 
	

	public String getNomPeriode()
	{
		return nomPeriode;
	}

	public void setNomPeriode(String nomPeriode)
	{
		this.nomPeriode = nomPeriode;
	}

	public String getTableauDateProduit()
	{
		return tableauDateProduit;
	}

	public void setTableauDateProduit(String tableauDateProduit)
	{
		this.tableauDateProduit = tableauDateProduit;
	}

	public String getNomPeriodeTitre()
	{
		return nomPeriodeTitre;
	}

	public void setNomPeriodeTitre(String nomPeriodeTitre)
	{
		this.nomPeriodeTitre = nomPeriodeTitre;
	}
	
}
