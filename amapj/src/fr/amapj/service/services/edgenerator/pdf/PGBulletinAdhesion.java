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
import javax.persistence.Query;

import org.apache.velocity.VelocityContext;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.VelocityUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.cotisation.PeriodeCotisation;
import fr.amapj.model.models.cotisation.PeriodeCotisationUtilisateur;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.AbstractPdfEditionSpeJson;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.adhesion.BulletinAdhesionJson;
import fr.amapj.model.models.editionspe.engagement.EngagementJson;
import fr.amapj.model.models.fichierbase.EtatUtilisateur;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.pdf.PdfGeneratorTool;
import fr.amapj.service.engine.generator.pdf.TestablePdfGenerator;
import fr.amapj.service.services.edgenerator.velocity.VCBuilder;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.producteur.ProdUtilisateurDTO;
import fr.amapj.service.services.producteur.ProducteurService;


/**
 * Permet la generation des bulletins d'adhesion au format PDF
 * 
 */
public class PGBulletinAdhesion extends TestablePdfGenerator
{
	
	//  
	private Long idPeriode;
	
	private Long idPeriodeUtilisateur;
	
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
	public PGBulletinAdhesion(Long idPeriode,Long idPeriodeUtilisateur,BulletinAdhesionJson forTest)
	{
		super(forTest);
		this.idPeriode = idPeriode;
		this.idPeriodeUtilisateur = idPeriodeUtilisateur;
	}
	
	@Override
	public String readDataInTestMode(EntityManager em, AbstractEditionSpeJson forTest)
	{
		BulletinAdhesionJson engJson = (BulletinAdhesionJson) forTest;
		
		if (engJson.idPeriodeCotisation ==null)
		{
			return "<p>Vous devez selectionner une periode pour pouvoir tester !</p>";
		}
		
		idPeriode = engJson.idPeriodeCotisation;
		idPeriodeUtilisateur = null;
		return null;
	}
	
	
	@Override
	public AbstractPdfEditionSpeJson getEditionInNormalMode(EntityManager em)
	{
		PeriodeCotisation pc =  em.find(PeriodeCotisation.class, idPeriode);
		EditionSpecifique editionSpecifique = pc.getBulletinAdhesion();
		AbstractPdfEditionSpeJson bulletin = (AbstractPdfEditionSpeJson)  new EditionSpeService().load(editionSpecifique.id);
		return bulletin;
	}
	
	
	
	@Override
	public void fillPdfFile(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		if (idPeriodeUtilisateur!=null)
		{
			performOneBulletin(em,et,htmlContent);
		}
		else
		{
			performAllBulletins(em,et,htmlContent);
		}	
	}
	

	
	private void performOneBulletin(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		// 
		PeriodeCotisationUtilisateur pcu = em.find(PeriodeCotisationUtilisateur.class, idPeriodeUtilisateur);
		
		//
		if (pcu.getPeriodeCotisation().getId().equals(idPeriode)==false)
		{
			throw new AmapjRuntimeException("Incoherence");
		}
		addOneBulletin(em,pcu,et,htmlContent);
	}

	private void performAllBulletins(EntityManager em, PdfGeneratorTool et, String htmlContent)
	{
		PeriodeCotisation pc =  em.find(PeriodeCotisation.class, idPeriode);
		
		// Avec une sous requete, on obtient la liste de tous les utilisateurs ayant adheré 
		List<PeriodeCotisationUtilisateur> pcus = getAllUtilisateurAvecAdhesion(em,pc);
		int nb = pcus.size();
		for (int i = 0; i < nb; i++)
		{
			PeriodeCotisationUtilisateur pcu = pcus.get(i);
		
			addOneBulletin(em,pcu,et,htmlContent);
		
			if (i!=nb-1)
			{
				et.addSautPage();
			}
		}
		
		// On positionne un message d'avertissement si besoin 
		if(nb==0)
		{
			et.addContent("<p>Aucun utilisateur n'a adhéré !! </p>");
		}
	}



	private List<PeriodeCotisationUtilisateur> getAllUtilisateurAvecAdhesion(EntityManager em,PeriodeCotisation p)
	{
		Query q = em.createQuery("select pu from PeriodeCotisationUtilisateur pu " 
				+ "WHERE pu.periodeCotisation=:p  "
				+ "order by pu.utilisateur.nom, pu.utilisateur.prenom");
		q.setParameter("p",p);
		
		List<PeriodeCotisationUtilisateur> us = q.getResultList();
		return us;
	}

	private void addOneBulletin(EntityManager em, PeriodeCotisationUtilisateur pcu, PdfGeneratorTool et, String htmlContent)
	{
		VelocityContext ctx = generateContext(em,pcu);		
		String res = VelocityUtils.evaluate(ctx, htmlContent);
		et.addContent(res);
	}

	private VelocityContext generateContext(EntityManager em, PeriodeCotisationUtilisateur pcu)
	{
		VelocityContext ctx = new VelocityContext();
		
		Utilisateur utilisateur = pcu.getUtilisateur();
		
		VCBuilder.addAmap(ctx);
		VCBuilder.addDateInfo(ctx);
		VCBuilder.addAmapien(ctx, utilisateur);
		VCBuilder.addAdhesion(ctx, pcu, em);
		
		return ctx;
	}

	@Override
	public String getFileNameStandard(EntityManager em)
	{
		if (idPeriodeUtilisateur==null)
		{
			PeriodeCotisation pc =  em.find(PeriodeCotisation.class, idPeriode);
			return "bulletin-adhesion-"+pc.getNom();	
		}
		else
		{
			PeriodeCotisationUtilisateur pcu =  em.find(PeriodeCotisationUtilisateur.class, idPeriodeUtilisateur);
			Utilisateur u = pcu.getUtilisateur();
			return "bulletin-adhesion-"+pcu.getPeriodeCotisation().getNom()+"-"+u.getNom()+" "+u.getPrenom();
		}
		
	}

	@Override
	public String getNameToDisplayStandard(EntityManager em)
	{
		if (idPeriodeUtilisateur==null)
		{
			PeriodeCotisation pc =  em.find(PeriodeCotisation.class, idPeriode);
			return "la liste des bulletins d'adhésion pour "+pc.getNom();
		}
		else
		{
			PeriodeCotisationUtilisateur pcu =  em.find(PeriodeCotisationUtilisateur.class, idPeriodeUtilisateur);
			Utilisateur u = pcu.getUtilisateur();
			return "le bulletin d'adhesion "+pcu.getPeriodeCotisation().getNom()+" pour "+u.getNom()+" "+u.getPrenom();
		}
	}
	
	
	public static void main(String[] args) throws Exception
	{
		new PGBulletinAdhesion(10011L,null,null).test();
	}

}
