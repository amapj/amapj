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
 package fr.amapj.service.services.edgenerator.odt;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.velocity.VelocityContext;

import fr.amapj.common.VelocityUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.engagement.EngagementJson;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.odt.AbstractOdtGenerator;
import fr.amapj.service.engine.generator.odt.OdtGeneratorTool;
import fr.amapj.service.services.edgenerator.velocity.VCBuilder;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.producteur.ProdUtilisateurDTO;
import fr.amapj.service.services.producteur.ProducteurService;


/**
 * Exemple pour la generation d'un fichier au format ODT
 * 
 */
public class OGExemple extends AbstractOdtGenerator
{
	
	Long modeleContratId;
	
	public OGExemple(Long modeleContratId)
	{
		this.modeleContratId = modeleContratId;
	}
	
	@Override
	public void fillWordFile(EntityManager em,OdtGeneratorTool et)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		EditionSpecifique editionSpecifique = mc.getProducteur().engagement;
		EngagementJson engagement = (EngagementJson)  new EditionSpeService().load(editionSpecifique.id);
		String htmlContent = engagement.getText();
		
		// Avec une sous requete, on obtient la liste de tous les utilisateur ayant commandé au moins un produit
		List<Utilisateur> utilisateurs = new MesContratsService().getUtilisateur(em, mc); 
		for (Utilisateur utilisateur : utilisateurs)
		{
			Contrat c = new MesContratsService().getContrat(modeleContratId, em, utilisateur);
			addOneContrat(em,c,utilisateur,et,htmlContent);
			et.addSautPage();
		}
		
	}

	
	private void addOneContrat(EntityManager em, Contrat c, Utilisateur utilisateur, OdtGeneratorTool et, String htmlContent)
	{
		VelocityContext ctx = generateContext(em,c,utilisateur);		
		String res = VelocityUtils.evaluate(ctx, htmlContent);
		et.addWikiContent(res);
		
		/*java.util.List<String> a = new ArrayList<String>();
		a.add("Item1");
		a.add("Item2");
		et.addBullet(a);*/
	}

	private VelocityContext generateContext(EntityManager em, Contrat c, Utilisateur utilisateur)
	{
		VelocityContext ctx = new VelocityContext();
		
		Producteur producteur = c.getModeleContrat().getProducteur();
		
		VCBuilder.addAmap(ctx);
		VCBuilder.addDateInfo(ctx);
		VCBuilder.addAmapien(ctx, utilisateur);
		VCBuilder.addContrat(ctx, c.getModeleContrat(),c, em);
		VCBuilder.addProducteur(ctx, producteur);
		List<ProdUtilisateurDTO> refs=new ProducteurService().getReferents(em, producteur);
		if (refs.size()>=1)
		{
			ProdUtilisateurDTO ref = refs.get(0);
			Utilisateur r = em.find(Utilisateur.class, ref.idUtilisateur);
			VCBuilder.addReferent(ctx, r);
		}
		
		return ctx;
	}

	@Override
	public String getFileName(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class,modeleContratId);
		return "engagements-"+mc.getNom();
	}

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class,modeleContratId);
		return "la liste des contrats d'engagement";
	}
	


	public static void main(String[] args) throws Exception
	{
		new OGExemple(10011L).test();
	}

}
