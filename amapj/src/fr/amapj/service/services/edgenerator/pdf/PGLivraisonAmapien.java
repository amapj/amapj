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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.velocity.VelocityContext;

import fr.amapj.common.VelocityUtils;
import fr.amapj.common.periode.TypPeriode;
import fr.amapj.model.models.editionspe.bilanlivraison.BilanLivraisonJson;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.pdf.AbstractPdfGenerator;
import fr.amapj.service.engine.generator.pdf.PdfGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.livraison.LivraisonAmapienCommon;
import fr.amapj.service.services.edgenerator.velocity.VCBuilder;
import fr.amapj.service.services.editionspe.EditionSpeService;


/**
 * Permet la generation des bilans de livraisons au format PDF
 * 
 */
public class PGLivraisonAmapien extends AbstractPdfGenerator
{
	
	private Date endDate;
	
	private Date startDate;
	
	private Long idUtilisateur;

	private TypPeriode typPeriode;
	
	private Long idEditionSpecifique;
	
	/**
	 * Deux modes sont possibles : 
	 * les livraisons d'un amapien sur une periode (idUtilisateur non null)
	 * les livraisons de tous les amapiens sur une periode (idUtilisateur = null)
	 * 
	 */
	public PGLivraisonAmapien(TypPeriode typPeriode,Date startDate,Date endDate,Long idUtilisateur,Long idEditionSpecifique)
	{
		this.typPeriode = typPeriode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.idUtilisateur = idUtilisateur;
		this.idEditionSpecifique = idEditionSpecifique;
	}
	
	
	@Override
	public void fillPdfFile(EntityManager em,PdfGeneratorTool et)
	{
		BilanLivraisonJson json = (BilanLivraisonJson)  new EditionSpeService().load(idEditionSpecifique);
		
		et.startDocument(json);
		
		List<Utilisateur> utilisateurs = new LivraisonAmapienCommon().getUtilisateurs(em,idUtilisateur,startDate,endDate);
		int nb = utilisateurs.size();
		for (int i = 0; i < utilisateurs.size(); i++)
		{
			Utilisateur utilisateur = utilisateurs.get(i);
			performOneBilan(em,utilisateur,et,json.getText());
			
			if (i!=nb-1)
			{
				et.addSautPage();
			}
		}
		
		// On positionne un message d'avertissement si besoin 
		if(utilisateurs.size()==0)
		{
			et.addContent("<p>Il y a aucune livraison pour aucun utilisateur !! </p>");
		}
	}

	private void performOneBilan(EntityManager em, Utilisateur utilisateur, PdfGeneratorTool et, String htmlContent)
	{
		VelocityContext ctx = generateContext(em, utilisateur);
		String res = VelocityUtils.evaluate(ctx, htmlContent);
		et.addContent(res);
	}

	private VelocityContext generateContext(EntityManager em,  Utilisateur utilisateur)
	{
		VelocityContext ctx = new VelocityContext();
		
		VCBuilder.addAmap(ctx);
		VCBuilder.addDateInfo(ctx);
		VCBuilder.addAmapien(ctx, utilisateur);
		VCBuilder.addBilanLivraison(ctx, utilisateur, em, typPeriode, startDate, endDate);
		
		return ctx;
	
	}

	@Override
	public String getFileName(EntityManager em)
	{
		return new LivraisonAmapienCommon().getFileName(typPeriode, startDate, endDate);
	}
	
	@Override
	public String getNameToDisplay(EntityManager em)
	{
		String nomUtilisateur;
		if (idUtilisateur==null)
		{
			nomUtilisateur = "tous les amapiens";
		}
		else
		{
			Utilisateur u = em.find(Utilisateur.class, idUtilisateur);
			nomUtilisateur = u.nom+" "+u.prenom;		
		}		
		return "Livraisons pour "+nomUtilisateur+" pour "+new LivraisonAmapienCommon().getDescriptionPeriode(typPeriode, startDate, endDate);
	}
}
