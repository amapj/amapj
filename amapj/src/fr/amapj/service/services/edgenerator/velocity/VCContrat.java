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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.common.StringUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.mescontrats.DatePaiementDTO;
import fr.amapj.service.services.mescontrats.InfoPaiementDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.mespaiements.MesPaiementsService;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;


public class VCContrat
{
	public String nom;
	
	public String description;
	
	public String dateDebut;
	
	public String dateFin;
	
	public String dateFinInscription;
	
	public String nbLivraison;
	
	public String saison;
	
	public String libCheque;
	
	public String dateRemiseCheque;
	
	public String nbCheque;
	
	public String tableauDateProduit;
	
	public String tableauDateCheque;
	
	public String montantProduit;
	
	public String montantCheque;
	
	public String montantAvoir;

	public String listeDateProduit;

	public String listeDateProduitCompact;

	public String listeDateCheque;
	
	public String listeDateChequeCompact;
	
	public String amapienNbLivraison;
	
	public String amapienNbProduit;
	
	/**
	 * 
	 * @param mc : n'est jamais null
	 * @param c  : peut être null dans le cas d'un vierge 
	 * @param em
	 */
	public void load(ModeleContrat mc,Contrat c,EntityManager em)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			
		ModeleContratSummaryDTO sum = new GestionContratService().createModeleContratInfo(em, mc);
		ModeleContratDTO dto = new GestionContratService().loadModeleContrat(mc.getId());
		
		nom = s(dto.nom);
		description = s(dto.description);
		dateDebut = df.format(dto.dateDebut);
		dateFin = df.format(dto.dateFin);
		dateFinInscription = "";
		if (dto.dateFinInscription!=null)
		{
			dateFinInscription = df.format(dto.dateFinInscription);
		}
		saison = getSaison(dto.dateDebut,dto.dateFin);
		nbLivraison = ""+sum.nbLivraison;
		libCheque = s(dto.libCheque);
		dateRemiseCheque = "";
		if (dto.dateRemiseCheque!=null)
		{
			dateRemiseCheque = df.format(dto.dateRemiseCheque);
		}
		
		
		
		ContratDTO contratDTO;
		boolean isVierge;
		if (c!=null)
		{
			contratDTO =  new MesContratsService().loadContrat(mc.getId(), c.getId());
			isVierge = false;
		}
		else
		{
			contratDTO =  new MesContratsService().loadContrat(mc.getId(), null);
			isVierge = true;
		}
	
		tableauDateProduit = getTableauDateProduit(em,contratDTO);
		tableauDateCheque = getTableauCheque(em,contratDTO,isVierge);
		
		listeDateProduit = getListeDateProduit(em,contratDTO,false);
		listeDateProduitCompact = getListeDateProduit(em,contratDTO,true);
		
		listeDateCheque = getListeDateCheque(em,contratDTO,false);
		listeDateChequeCompact = getListeDateCheque(em,contratDTO,true);
		
		
		//
		if (c!=null)
		{
			nbCheque = ""+new MesPaiementsService().getNbChequeContrat(c, em);
			
			int montantP = new GestionContratSigneService().getMontant(em, c);
			montantProduit = new CurrencyTextFieldConverter().convertToString(montantP);
			
			int montantC = new MesPaiementsService().getMontantChequeSansAvoir(c, em);
			montantCheque = new CurrencyTextFieldConverter().convertToString(montantC);
		
			int montantA = contratDTO.paiement.avoirInitial;
			montantAvoir = new CurrencyTextFieldConverter().convertToString(montantA);
			
			amapienNbLivraison = ""+new GestionContratSigneService().getNbLivraisonContrat(c, em);
			amapienNbProduit = ""+new GestionContratSigneService().getNbProduitContrat(c, em);
		}
		else
		{
			nbCheque = "";
			montantProduit = "";
			montantCheque = "";
			montantAvoir = "";
			amapienNbLivraison = "";
			amapienNbProduit = "";
		}
		
	}
	
	
	
	
	public String montantProduitTVA(double taux)
	{
		// Si contrat vierge, pas de montant produit 
		if (montantProduit.length()==0)
		{
			return "";
		}
		
		double montant = new Double(montantProduit)*100;
		double mntTVA = montant-montant/(1+taux/100.0);
		
		int mntCentimes = (int) Math.round(mntTVA);
		
		return new CurrencyTextFieldConverter().convertToString(mntCentimes);
	}
	


	/**
	 * Permet d'escaper les caracteres HTML  
	 */
	private String s(String value)
	{
		return VCBuilder.s(value);
	}
	
	
	private String getTableauDateProduit(EntityManager em, ContratDTO contratDTO)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
		StringBuffer buf = new StringBuffer();
		
		//		
		buf.append("<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:100%;table-layout:fixed;text-align:center;\"><tbody>");
		
		// La ligne de titre
		
		buf.append("<tr>");
		
		buf.append("<td>");
		buf.append("<p style=\"margin:0.1em\">DATE</p>");
		buf.append("</td>");
		
		
		List<ContratColDTO> contratColumns = contratDTO.contratColumns;
		for (ContratColDTO contratColDTO : contratColumns)
		{
			buf.append("<td>");
			buf.append("<p style=\"margin:0.1em\">"+s(contratColDTO.nomProduit)+"</p>");
			buf.append("<p style=\"margin:0.1em\"><b>"+new CurrencyTextFieldConverter().convertToString(contratColDTO.prix)+"€ </b></p>");
			buf.append("<p style=\"margin:0.1em\">"+s(contratColDTO.condtionnementProduit)+"</b></p>");
			buf.append("</td>");
		}
		
		buf.append("</tr>");
		
		// Les lignes pour chaque date
		List<ContratLigDTO> contratLigs = contratDTO.contratLigs;
		int nbCol = contratColumns.size();
		int i=0;
		for (ContratLigDTO contratLigDTO : contratLigs)
		{
			buf.append("<tr>");
			
			buf.append("<td>");
			buf.append("<p style=\"margin:0.1em\">"+df.format(contratLigDTO.date)+"</p>");
			buf.append("</td>");
			
			

			for (int j=0;j<nbCol;j++)
			{
				buf.append("<td>");
				buf.append("<p style=\"margin:0.1em\">");
				if (contratDTO.qte[i][j]!=0)
				{
					buf.append(""+contratDTO.qte[i][j]);
				}
				buf.append("</p>");
				buf.append("</td>");
			}
			
			buf.append("</tr>");
			i++;
		}
		
		buf.append("</tbody></table>");
		return buf.toString();
	}

	private String getTableauCheque(EntityManager em, ContratDTO contratDTO,boolean isVierge)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
		StringBuffer buf = new StringBuffer();
		InfoPaiementDTO paiement = contratDTO.paiement;
		
		if (paiement.avoirInitial!=0)
		{
			buf.append("<p>Avoir initial : "+new CurrencyTextFieldConverter().convertToString(paiement.avoirInitial)+" €</p>");
		}
		
		//
		buf.append("<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:100%;\"><tbody>");
		
		// La ligne de titre
		buf.append("<tr>");
		
		buf.append("<td style=\"width:50%\">");
		buf.append("<p style=\"margin:0.1em\">DATE DE DEBIT</p>");
		buf.append("</td>");
		
		buf.append("<td style=\"width:50%\">");
		buf.append("<p style=\"margin:0.1em\">MONTANT</p>");
		buf.append("</td>");
		
		
		
		
		for (DatePaiementDTO date : paiement.datePaiements)
		{
			// Si c'est un vierge : on met toujours la ligne
			// Si c'est un contrat classique : on supprime les lignes sans paiement
			if ( (date.montant!=0) || (isVierge==true))
			{
				buf.append("<tr>");
				
				buf.append("<td style=\"width:30%\">");
				buf.append("<p style=\"margin:0.1em\">"+df.format(date.datePaiement)+"</p>");
				buf.append("</td>");
				
				buf.append("<td style=\"width:30%\">");
				buf.append("<p style=\"margin:0.1em\">");
				if (isVierge==false)
				{
					buf.append(""+new CurrencyTextFieldConverter().convertToString(date.montant)+" €");
				}
				buf.append("</p>");
				buf.append("</td>");
				
				buf.append("</tr>");
			}
		}
		
		buf.append("</tbody></table>");
		return buf.toString();
	}
	
	
	private String getListeDateProduit(EntityManager em, ContratDTO contratDTO,boolean compact)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
		
		CompactorTools ct = new CompactorTools(compact);
		
		List<ContratColDTO> contratColumns = contratDTO.contratColumns;
		List<ContratLigDTO> contratLigs = contratDTO.contratLigs;
		
		// Calcul de la liste des couples (Date) (Liste de produits) pour les compacter
		int nbCol = contratColumns.size();
		int i=0;
		for (ContratLigDTO contratLigDTO : contratLigs)
		{
			String s1 = df.format(contratLigDTO.date);
			List<String> s2 = new ArrayList<String>(); 
			
			for (int j=0;j<nbCol;j++)
			{
				if (contratDTO.qte[i][j]!=0)
				{
					ContratColDTO contratColDTO = contratColumns.get(j);
					StringBuffer buf = new StringBuffer();
				
					buf.append(contratDTO.qte[i][j]+" ");
					buf.append(s(contratColDTO.nomProduit)+" (");
					buf.append(s(contratColDTO.condtionnementProduit));
					buf.append(" - "+new CurrencyTextFieldConverter().convertToString(contratColDTO.prix)+" €)");
					
					s2.add(buf.toString());
				}
			}
			
			ct.addLine(s1, StringUtils.asString(s2, ", "));
			i++;
		}
		
		// Formatage du HTML
		List<String> res = ct.getResult("Le ","Les ", ", ", ": ", "");
		StringBuffer buf = new StringBuffer();		
		buf.append("<ul style=\"margin:0pt;\">");
		for (String s : res)
		{
			buf.append("<li>");
			buf.append(s);
			buf.append("</li>");
		}
		buf.append("</ul>");
		
		return buf.toString();
	}
	
	
	private String getListeDateCheque(EntityManager em, ContratDTO contratDTO,boolean compact)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
		
		CompactorTools ct = new CompactorTools(compact);
		
		// Calcul de la liste des couples (Date) (Montants des cheques) pour les compacter
		for (DatePaiementDTO dp : contratDTO.paiement.datePaiements)
		{
			if (dp.montant!=0)
			{
				String s1 = df.format(dp.datePaiement);
				String s2 = new CurrencyTextFieldConverter().convertToString(dp.montant)+" €";;
				ct.addLine(s1, s2);
			}
		}
		
		// Formatage du HTML
		List<CompactorTools.Item> res = ct.getResult();
		StringBuffer buf = new StringBuffer();		
		buf.append("<ul style=\"margin:0pt;\">");
		if (contratDTO.paiement.avoirInitial!=0)
		{
			buf.append("<li>Avoir initial : "+new CurrencyTextFieldConverter().convertToString(contratDTO.paiement.avoirInitial)+" €</li>");
		}
	
		for (CompactorTools.Item item : res)
		{
			buf.append("<li>");
			
			String str = item.part1s.size()+" chèque";
			if (item.part1s.size()>1)
			{
				str = str+"s";
			}
			str = str +" de <b>"+item.part2+"</b> débité";
			if (item.part1s.size()>1)
			{
				str = str+"s";
			}
			
			str = str +" le";
			if (item.part1s.size()>1)
			{
				str = str+"s";
			}
			str = str +" ";
			
			str = str+StringUtils.asString(item.part1s, ",");
			
			buf.append(str);
			
			buf.append("</li>");
		}
		buf.append("</ul>");
		
		return buf.toString();
	}
	
	
	

	private String getSaison(Date dateDebut, Date dateFin)
	{
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy");
		String s1 = df2.format(dateDebut);
		String s2 = df2.format(dateFin);
		
		if (s1.equals(s2))
		{
			return s1;
		}
		return s1+"-"+s2;
	}
	
	
	
	// Getters and setters pour Velocity 
	
	
	

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDateDebut()
	{
		return dateDebut;
	}

	public void setDateDebut(String dateDebut)
	{
		this.dateDebut = dateDebut;
	}

	public String getDateFin()
	{
		return dateFin;
	}

	public void setDateFin(String dateFin)
	{
		this.dateFin = dateFin;
	}

	public String getNbLivraison()
	{
		return nbLivraison;
	}

	public void setNbLivraison(String nbLivraison)
	{
		this.nbLivraison = nbLivraison;
	}

	public String getSaison()
	{
		return saison;
	}

	public void setSaison(String saison)
	{
		this.saison = saison;
	}

	public String getLibCheque()
	{
		return libCheque;
	}

	public void setLibCheque(String libCheque)
	{
		this.libCheque = libCheque;
	}

	public String getNbCheque()
	{
		return nbCheque;
	}

	public void setNbCheque(String nbCheque)
	{
		this.nbCheque = nbCheque;
	}

	public String getTableauDateProduit()
	{
		return tableauDateProduit;
	}

	public void setTableauDateProduit(String tableauDateProduit)
	{
		this.tableauDateProduit = tableauDateProduit;
	}


	public String getListeDateProduit()
	{
		return listeDateProduit;
	}

	public void setListeDateProduit(String listeDateProduit)
	{
		this.listeDateProduit = listeDateProduit;
	}

	public String getListeDateProduitCompact()
	{
		return listeDateProduitCompact;
	}

	public void setListeDateProduitCompact(String listeDateProduitCompact)
	{
		this.listeDateProduitCompact = listeDateProduitCompact;
	}



	public String getMontantProduit()
	{
		return montantProduit;
	}

	public void setMontantProduit(String montantProduit)
	{
		this.montantProduit = montantProduit;
	}

	public String getMontantCheque()
	{
		return montantCheque;
	}

	public void setMontantCheque(String montantCheque)
	{
		this.montantCheque = montantCheque;
	}

	public String getMontantAvoir()
	{
		return montantAvoir;
	}

	public void setMontantAvoir(String montantAvoir)
	{
		this.montantAvoir = montantAvoir;
	}


	public String getAmapienNbLivraison()
	{
		return amapienNbLivraison;
	}

	public void setAmapienNbLivraison(String amapienNbLivraison)
	{
		this.amapienNbLivraison = amapienNbLivraison;
	}

	public String getAmapienNbProduit()
	{
		return amapienNbProduit;
	}

	public void setAmapienNbProduit(String amapienNbProduit)
	{
		this.amapienNbProduit = amapienNbProduit;
	}

	public String getTableauDateCheque()
	{
		return tableauDateCheque;
	}

	public void setTableauDateCheque(String tableauDateCheque)
	{
		this.tableauDateCheque = tableauDateCheque;
	}

	public String getListeDateCheque()
	{
		return listeDateCheque;
	}

	public void setListeDateCheque(String listeDateCheque)
	{
		this.listeDateCheque = listeDateCheque;
	}

	public String getListeDateChequeCompact()
	{
		return listeDateChequeCompact;
	}

	public void setListeDateChequeCompact(String listeDateChequeCompact)
	{
		this.listeDateChequeCompact = listeDateChequeCompact;
	}

	public String getDateFinInscription()
	{
		return dateFinInscription;
	}

	public void setDateFinInscription(String dateFinInscription)
	{
		this.dateFinInscription = dateFinInscription;
	}

	public String getDateRemiseCheque()
	{
		return dateRemiseCheque;
	}

	public void setDateRemiseCheque(String dateRemiseCheque)
	{
		this.dateRemiseCheque = dateRemiseCheque;
	}
	
	
}
