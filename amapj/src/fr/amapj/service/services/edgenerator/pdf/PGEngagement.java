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
	
	private PGEngagementMode mode;
	
	
	public enum PGEngagementMode
	{
		TOUS_LES_CONTRATS,
		
		UN_CONTRAT,
		
		UN_VIERGE,
		
		TOUS_LES_CONTRATS_EN_MODE_TEST,
		
	}
	
	
	/**
	 * 4 modes sont possibles :
	 *  
	 * tous les contrats d'un modele (TOUS_LES_CONTRATS,id,null,null) 
	 * 
	 * un contrat d'un amapien d'un modele (UN_CONTRAT,id,id,null)
	 * 
	 * un vierge d'un modele de contrat (UN_VIERGE,id,null,null)
	 * 
	 * le mode test (TOUS_LES_CONTRATS_EN_MODE_TEST,null,null,fortest) 
	 * 
	 * @param modeleContratId
	 * @param forTest
	 */
	public PGEngagement(PGEngagementMode mode,Long modeleContratId,Long contratId,EngagementJson forTest)
	{
		super(forTest);
		this.mode = mode;
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
		switch (mode)
		{
		case TOUS_LES_CONTRATS:
		case TOUS_LES_CONTRATS_EN_MODE_TEST:
			performAllContratOfModele(em,et,htmlContent);
			break;
			
		case UN_CONTRAT:
			performOneContrat(em,et,htmlContent);
			break;
			
		case UN_VIERGE:
			performOneContratVierge(em,et,htmlContent);
			break;
			
		default:
			throw new AmapjRuntimeException();
		}
	}

	
	private void performOneContratVierge(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		ModeleContrat mc =  em.find(ModeleContrat.class, modeleContratId);
		Producteur producteur = mc.getProducteur();
		
		addOneContrat(em,mc,null,null,et,htmlContent,producteur);
	}
	

	
	private void performOneContrat(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		// 
		Contrat c = em.find(Contrat.class, contratId);
		Utilisateur utilisateur = c.getUtilisateur();
		Producteur producteur = c.getModeleContrat().getProducteur();
		
		//
		if (c.getModeleContrat().getId().equals(modeleContratId)==false)
		{
			throw new AmapjRuntimeException("Incoherence");
		}
		addOneContrat(em,c.getModeleContrat(),c,utilisateur,et,htmlContent,producteur);
	}

	private void performAllContratOfModele(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		ModeleContrat mc =  em.find(ModeleContrat.class, modeleContratId);
		Producteur producteur = mc.getProducteur();
		
		// Avec une sous requete, on obtient la liste de tous les utilisateur ayant commandé au moins un produit
		List<Utilisateur> utilisateurs = new MesContratsService().getUtilisateur(em, mc);
		int nb = utilisateurs.size();
		for (int i = 0; i < nb; i++)
		{
			Utilisateur utilisateur = utilisateurs.get(i);
		
			Contrat c = new MesContratsService().getContrat(mc.getId(), em, utilisateur);
			addOneContrat(em,mc,c,utilisateur,et,htmlContent,producteur);
		
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



	private void addOneContrat(EntityManager em, ModeleContrat mc,Contrat c, Utilisateur utilisateur, PdfGeneratorTool et, String htmlContent,Producteur producteur)
	{
		VelocityContext ctx = generateContext(em,mc,c,utilisateur,producteur);		
		String res = VelocityUtils.evaluate(ctx, htmlContent);
		et.addContent(res);
	}

	/**
	 * 
	 * @param em
	 * @param c peut être null dans le cas de la generation d'un vierge
	 * @param utilisateur peut être null dans le cas de la generation d'un vierge
	 * @param producteur n'est jamais null 
	 * @return
	 */
	private VelocityContext generateContext(EntityManager em, ModeleContrat mc,Contrat c, Utilisateur utilisateur,Producteur producteur)
	{
		VelocityContext ctx = new VelocityContext();
		
		VCBuilder.addAmap(ctx);
		VCBuilder.addDateInfo(ctx);
		VCBuilder.addAmapien(ctx, utilisateur);
		VCBuilder.addContrat(ctx, mc, c, em);
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
		ModeleContrat mc = em.find(ModeleContrat.class,modeleContratId);
		
		switch (mode)
		{
		case TOUS_LES_CONTRATS:
			return "engagements-"+mc.getNom();	

		case TOUS_LES_CONTRATS_EN_MODE_TEST:
			return "test-"+mc.getNom();	
			
		case UN_CONTRAT:
			Utilisateur u = em.find(Contrat.class,contratId).getUtilisateur();
			return "contrat-engagement-"+mc.getNom()+"-"+u.getNom()+" "+u.getPrenom();
			
		case UN_VIERGE:
			return "engagement-vierge-"+mc.getNom();	
			
		default:
			throw new AmapjRuntimeException();
		}		
	}

	@Override
	public String getNameToDisplayStandard(EntityManager em)
	{
		switch (mode)
		{
		case TOUS_LES_CONTRATS:
			return "tous les contrats d'engagement";

		case TOUS_LES_CONTRATS_EN_MODE_TEST:
			return "mode test";
			
		case UN_CONTRAT:
			Contrat c = em.find(Contrat.class,contratId);
			Utilisateur u = c.getUtilisateur();
			return "le contrat d'engagement "+c.getModeleContrat().getNom()+" pour "+u.getNom()+" "+u.getPrenom();
			
		case UN_VIERGE:
			return "un contrat d'engagement vierge";
			
		default:
			throw new AmapjRuntimeException();
		}
	}
	
	
	public static void main(String[] args) throws Exception
	{
		new PGEngagement(PGEngagementMode.TOUS_LES_CONTRATS,10011L,null,null).test();
	}

}
