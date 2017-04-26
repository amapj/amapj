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
 package fr.amapj.service.services.edgenerator.pdf;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.velocity.VelocityContext;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.VelocityUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.AbstractPdfEditionSpeJson;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.engagement.EngagementJson;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.pdf.PdfGeneratorTool;
import fr.amapj.service.engine.generator.pdf.TestablePdfGenerator;
import fr.amapj.service.services.edgenerator.velocity.VCBuilder;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.producteur.ProdUtilisateurDTO;
import fr.amapj.service.services.producteur.ProducteurService;


/**
 * Permet la generation des engagements au format PDF
 * 
 */
public class PGEngagement extends TestablePdfGenerator
{
	
	//  
	private Long modeleContratId;
	
	private Long contratId;
	
	/**
	 * Trois modes sont possibles : 
	 * tous les contrats d'un modele (id,null,null) 
	 * 
	 * un contrat d'un amapien d'un modele (id,id,null)
	 * 
	 * le mode test (null,null,fortest) 
	 * 
	 * @param modeleContratId
	 * @param forTest
	 */
	public PGEngagement(Long modeleContratId,Long contratId,EngagementJson forTest)
	{
		super(forTest);
		this.modeleContratId = modeleContratId;
		this.contratId = contratId;
	}
	
	@Override
	public String readDataInTestMode(EntityManager em, AbstractEditionSpeJson forTest)
	{
		EngagementJson engJson = (EngagementJson) forTest;
		
		if (engJson.idModeleContrat ==null)
		{
			return "<p>Vous devez selectionner un contrat pour pouvoir tester !</p>";
		}
		
		modeleContratId = engJson.idModeleContrat;
		contratId = null;
		return null;
	}
	
	
	@Override
	public AbstractPdfEditionSpeJson getEditionInNormalMode(EntityManager em)
	{
		ModeleContrat mc =  em.find(ModeleContrat.class, modeleContratId);
		EditionSpecifique editionSpecifique = mc.getProducteur().engagement;
		EngagementJson engagement = (EngagementJson)  new EditionSpeService().load(editionSpecifique.id);
		return engagement;
	}
	
	
	
	@Override
	public void fillPdfFile(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		if (contratId!=null)
		{
			performOneContrat(em,et,htmlContent);
		}
		else
		{
			performAllContratOfModele(em,et,htmlContent);
		}	
	}
	

	
	private void performOneContrat(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		// 
		Contrat c = em.find(Contrat.class, contratId);
		Utilisateur utilisateur = c.getUtilisateur();
		
		//
		if (c.getModeleContrat().getId().equals(modeleContratId)==false)
		{
			throw new AmapjRuntimeException("Incoherence");
		}
		addOneContrat(em,c,utilisateur,et,htmlContent);
	}

	private void performAllContratOfModele(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		ModeleContrat mc =  em.find(ModeleContrat.class, modeleContratId);
		
		// Avec une sous requete, on obtient la liste de tous les utilisateur ayant commandé au moins un produit
		List<Utilisateur> utilisateurs = new MesContratsService().getUtilisateur(em, mc);
		int nb = utilisateurs.size();
		for (int i = 0; i < nb; i++)
		{
			Utilisateur utilisateur = utilisateurs.get(i);
		
			Contrat c = new MesContratsService().getContrat(mc.getId(), em, utilisateur);
			addOneContrat(em,c,utilisateur,et,htmlContent);
		
			if (i!=nb-1)
			{
				et.addSautPage();
			}
		}
		
		// On positionne un message d'avertissement si besoin 
		if(nb==0)
		{
			et.addContent("<p>Aucun utilisateur n'a souscrit à ce contrat !! </p>");
		}
	}



	private void addOneContrat(EntityManager em, Contrat c, Utilisateur utilisateur, PdfGeneratorTool et, String htmlContent)
	{
		VelocityContext ctx = generateContext(em,c,utilisateur);		
		String res = VelocityUtils.evaluate(ctx, htmlContent);
		et.addContent(res);
	}

	private VelocityContext generateContext(EntityManager em, Contrat c, Utilisateur utilisateur)
	{
		VelocityContext ctx = new VelocityContext();
		
		Producteur producteur = c.getModeleContrat().getProducteur();
		
		VCBuilder.addAmap(ctx);
		VCBuilder.addDateInfo(ctx);
		VCBuilder.addAmapien(ctx, utilisateur);
		VCBuilder.addContrat(ctx, c, em);
		VCBuilder.addProducteur(ctx, producteur);
		List<ProdUtilisateurDTO> refs=new ProducteurService().getReferents(em, producteur);
		if (refs.size()>=1)
		{
			ProdUtilisateurDTO ref = refs.get(0);
			Utilisateur r = em.find(Utilisateur.class, ref.idUtilisateur);
			VCBuilder.addReferent(ctx, r);
		}
		else
		{
			VCBuilder.addReferent(ctx, null);
		}
		
		List<ProdUtilisateurDTO> contactProds=new ProducteurService().getUtilisateur(em, producteur);
		if (contactProds.size()>=1)
		{
			ProdUtilisateurDTO contactProd = contactProds.get(0);
			Utilisateur r = em.find(Utilisateur.class, contactProd.idUtilisateur);
			VCBuilder.addContactProducteur(ctx, r);
		}
		else
		{
			VCBuilder.addContactProducteur(ctx, null);
		}
		
		
		return ctx;
	}

	@Override
	public String getFileNameStandard(EntityManager em)
	{
		if (contratId==null)
		{
			ModeleContrat mc = em.find(ModeleContrat.class,modeleContratId);
			return "engagements-"+mc.getNom();	
		}
		else
		{
			Contrat c = em.find(Contrat.class,contratId);
			Utilisateur u = c.getUtilisateur();
			return "contrat-engagement-"+c.getModeleContrat().getNom()+"-"+u.getNom()+" "+u.getPrenom();
		}
		
	}

	@Override
	public String getNameToDisplayStandard(EntityManager em)
	{
		if (contratId==null)
		{
			return "tous les contrats d'engagement";
		}
		else
		{
			Contrat c = em.find(Contrat.class,contratId);
			Utilisateur u = c.getUtilisateur();
			return "le contrat d'engagement "+c.getModeleContrat().getNom()+" pour "+u.getNom()+" "+u.getPrenom();
		}
	}
	
	
	public static void main(String[] args) throws Exception
	{
		new PGEngagement(10011L,null,null).test();
	}

}
