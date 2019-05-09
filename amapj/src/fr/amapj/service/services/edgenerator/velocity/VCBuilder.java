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

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.velocity.VelocityContext;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import fr.amapj.common.periode.TypPeriode;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.cotisation.PeriodeCotisationUtilisateur;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;


public class VCBuilder
{
	
	
	/**
	 * Permet d'escaper les caracteres HTML  
	 */
	static public String s(String value)
	{
		if (value==null)
		{
			return "";
		}
		return SafeHtmlUtils.htmlEscape(value);
	}
	
	static public void addAmap(VelocityContext ctx)
	{
		ParametresDTO param = new ParametresService().getParametres();
		VCPersonne amap = new VCPersonne();
		
		amap.nom = s(param.nomAmap);
		amap.ville = s(param.villeAmap);
				
		ctx.put("amap", amap);
	}
	
	/**
	 * Si u est null, correspond à la génération d'un contrat vierge 
	 * 
	 * @param ctx
	 * @param u
	 */
	static public void addAmapien(VelocityContext ctx,Utilisateur u)
	{
		VCAmapien amapien = new VCAmapien();
		amapien.load(u);
				
		ctx.put("amapien", amapien);
	}
	
	static public void addReferent(VelocityContext ctx,Utilisateur u)
	{
		VCPersonne referent = new VCPersonne();
		if (u!=null)
		{
			referent.load(u);
		}
				
		ctx.put("referent", referent);	
	}
	
	
	static public void addContactProducteur(VelocityContext ctx,Utilisateur u)
	{
		VCPersonne contactProd = new VCPersonne();
		if (u!=null)
		{
			contactProd.load(u);
		}
				
		ctx.put("contactproducteur", contactProd);	
	}
	
	
	static public void addProducteur(VelocityContext ctx,Producteur p)
	{
		VCProducteur prod = new VCProducteur();
		
		prod.nom = s(p.nom);
		prod.libContrat = s(p.libContrat);
		
		ctx.put("producteur", prod);
	}
	
	/**
	 * Si c est null, correspond à la création d'un document vierge 
	 * 
	 * mc n'est jamais null
	 * 
	 * @param ctx
	 * @param c
	 * @param em
	 */
	static public void addContrat(VelocityContext ctx,ModeleContrat mc,Contrat c,EntityManager em)
	{
		VCContrat cc = new VCContrat();
		cc.load(mc , c, em);
	
		ctx.put("contrat", cc);
	}
	
	/**
	 * 
	 * @param ctx
	 * @param c
	 * @param em
	 */
	static public void addBilanLivraison(VelocityContext ctx,Utilisateur utilisateur,EntityManager em,TypPeriode typPeriode,Date startDate,Date endDate)
	{
		VCBilanLivraison cc = new VCBilanLivraison();
		cc.load(em, utilisateur, typPeriode, startDate, endDate);
	
		ctx.put("livraison", cc);
	}
	
	
	
	static public void addAdhesion(VelocityContext ctx,PeriodeCotisationUtilisateur pcu,EntityManager em)
	{
		VCAdhesion cc = new VCAdhesion();
		cc.load(pcu, em);
	
		ctx.put("adhesion", cc);
	}
	
	
	static public void addDateInfo(VelocityContext ctx)
	{
		VCDate date = new VCDate();
		ctx.put("date", date);
	}
	
	
	
}
