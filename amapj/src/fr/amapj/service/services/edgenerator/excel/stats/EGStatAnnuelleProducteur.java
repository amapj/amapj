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
 package fr.amapj.service.services.edgenerator.excel.stats;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.SQLUtils;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;


/**
 * Statistiques annuelles sur les producteurs 
 * 
 *  
 *
 */
public class EGStatAnnuelleProducteur extends AbstractExcelGenerator
{
	
	public EGStatAnnuelleProducteur()
	{
		
	}

	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		
		
		List<Integer> annees = getAllAnnees(em);
		List<Producteur> producteurs = getAllProducteurs(em); 
		
			
		
		et.addSheet("Statistiques producteur", 1+annees.size(), 20);
		et.setColumnWidth(0, 40);
		
		et.addRow("Statistiques producteur par année",et.titre);
		et.addRow();
		
		et.addRow();
		et.setCell(0, "Nom du producteur", et.grasGaucheNonWrappeBordure);
		
		for (int i = 0; i < annees.size(); i++)
		{
			et.setCell(i+1, ""+annees.get(i), et.grasCentreBordure);
		}
	
		
		
		for (Producteur producteur : producteurs)
		{
			et.addRow();
			et.setCell(0,producteur.nom,et.grasGaucheNonWrappeBordure);
			
			for (int i = 0; i < annees.size(); i++)
			{
				int mnt = getMontant(producteur,annees.get(i),em);
				et.setCellPrix(i+1, mnt, et.prixCentreBordure);
			}
			
		}
	}
	
	
	

	
	private List<Integer> getAllAnnees(EntityManager em)
	{
		Query q = em.createQuery("select distinct(EXTRACT(YEAR d.dateLiv))   from ModeleContratDate d order by EXTRACT(YEAR d.dateLiv)");
		List<Integer> ps = q.getResultList();
		return ps;
	}
	
	
	private List<Producteur> getAllProducteurs(EntityManager em)
	{
		Query q = em.createQuery("select p from Producteur p order by p.nom");
		List<Producteur> ps = q.getResultList();
		return ps;
	}

	private int getMontant(Producteur producteur, Integer annee, EntityManager em)
	{
		Query q = em.createQuery("select sum(c.qte*c.modeleContratProduit.prix) from ContratCell c "
				+ " WHERE EXTRACT(YEAR c.modeleContratDate.dateLiv)=:an and c.modeleContratDate.modeleContrat.producteur = :p");
		q.setParameter("an", annee);
		q.setParameter("p", producteur);
		return SQLUtils.toInt(q.getSingleResult());
	}


	@Override
	public String getFileName(EntityManager em)
	{
		//ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		return "stat-producteur-";
	}
	

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "les statistiques producteurs par annee";
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

	
	public static void main(String[] args) throws IOException
	{
		new EGStatAnnuelleProducteur().test();
	}

	
}
