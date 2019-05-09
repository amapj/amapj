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
 package fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;


/**
 * Permet la gestion des modeles de contrat
 * 
 *  
 *
 */
public class EGSyntheseContrat  extends AbstractExcelGenerator
{
	
	Long modeleContratId;
	
	public EGSyntheseContrat(Long modeleContratId)
	{
		this.modeleContratId = modeleContratId;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{	
		EGGrilleTool grilleTool = new EGGrilleTool();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		
		// Avec une sous requete, on récupere la liste des produits
		List<ModeleContratProduit> prods = new GestionContratService().getAllProduit(em, mc);
		
		// Avec une sous requete, on obtient la liste de toutes les dates de livraison, trièes par ordre croissant 
		List<ModeleContratDate> dates = new GestionContratService().getAllDates(em, mc);

		// Avec une sous requete, on obtient la liste de tous les utilisateur ayant commandé au moins un produit
		List<Utilisateur> utilisateurs = new MesContratsService().getUtilisateur(em, mc);
		
		// On charge ensuite la liste de tous les contrats pour chaque utilisateur
		Map<Utilisateur, ContratDTO> contrats = grilleTool.loadContrat(em,utilisateurs,mc);

		// Nombre de colonnes fixe à gauche
		int nbColGauche = 3;

		grilleTool.performSheet(et,"SYNTHESE DU CONTRAT","Amap",mc,prods,dates,utilisateurs,nbColGauche,contrats);
	}
	
	

	@Override
	public String getFileName(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		return "synthese-"+mc.getNom();
	}


	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "la synthese des distributions en une page";
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLSX;
	}
	
	

	public static void main(String[] args) throws IOException
	{
		new EGSyntheseContrat(11252L).test(); 
	}

}
