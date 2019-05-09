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
 package fr.amapj.service.services.edgenerator.excel.emargement;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import fr.amapj.common.DateUtils;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.emargement.FeuilleEmargementJson;
import fr.amapj.model.models.editionspe.emargement.FormatFeuilleEmargement;
import fr.amapj.model.models.editionspe.emargement.TypFeuilleEmargement;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.editionspe.EditionSpeService;


/**
 * Permet la generation d'une feuille d'émargement (hebdomadaire ou mensuelle)
 * au format grille ou liste 
 */
public class EGFeuilleEmargement extends AbstractExcelGenerator
{
	
	private Long editionSpecifiqueId;
	private Date ref;
	private String suffix;
	
	public EGFeuilleEmargement(Long editionSpecifiqueId,Date ref,String suffix)
	{
		this.suffix = suffix;
		this.editionSpecifiqueId = editionSpecifiqueId;
		this.ref = ref ;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		//
		EditionSpecifique editionSpe = em.find(EditionSpecifique.class, editionSpecifiqueId);
		FeuilleEmargementJson planningJson = (FeuilleEmargementJson) new EditionSpeService().load(editionSpe.id);
		
		LibInfo libInfo = getLibForName(em);

		if (planningJson.getFormat()==FormatFeuilleEmargement.GRILLE)
		{
			new EGFeuilleEmargementGrille().fillExcelFile(em, et,planningJson,libInfo);
		}
		else
		{
			new EGFeuilleEmargementListe().fillExcelFile(em, et,planningJson,libInfo);
		}
	}
	


	@Override
	public String getFileName(EntityManager em)
	{
		LibInfo libInfo = getLibForName(em);
		return libInfo.fileName+suffix;
	}
	

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		LibInfo libInfo = getLibForName(em);
		String str = libInfo.displayName;
		if ( (suffix!=null) && (suffix.length()>0) )
		{
			str = str +" ("+suffix+")";
		}
		return str;
	}
	
	public LibInfo getLibForName(EntityManager em)
	{
		LibInfo res = new LibInfo();
		
		SimpleDateFormat df2 = new SimpleDateFormat("MMMMM yyyy");
		SimpleDateFormat df3 = new SimpleDateFormat("ww");
		SimpleDateFormat df4 = new SimpleDateFormat("MMMMM-yyyy");
		SimpleDateFormat df5 = new SimpleDateFormat("dd-MMMMM");
		
		
		EditionSpecifique editionSpe = em.find(EditionSpecifique.class, editionSpecifiqueId);
		FeuilleEmargementJson planningJson = (FeuilleEmargementJson) new EditionSpeService().load(editionSpe.id);
		
		if (planningJson.getTypPlanning()==TypFeuilleEmargement.MENSUEL)
		{
			res.debut = DateUtils.firstDayInMonth(ref);
			res.fin =  DateUtils.addMonth(res.debut,1);
			res.lib1 = df2.format(res.debut);
			res.lib2 = "mensuelle";
			res.fileName = "emargement-mensuel-"+df4.format(res.debut);
			res.displayName = "la feuille d'émargement mensuelle de "+df2.format(res.debut);
		}
		else
		{
			res.debut = DateUtils.firstMonday(ref);
			res.fin = DateUtils.addDays(res.debut,7);
			res.lib1 = "S"+df3.format(res.debut);
			res.lib2 = "hebdomadaire";
			res.fileName = "emargement-hebdomadaire-"+df5.format(res.debut);
			res.displayName = "la feuille d'émargement hebdomadaire semaine "+df3.format(res.debut);
		}
		
		return res;
	}
	
	static public class LibInfo
	{
		// lib1 contient les dates du planning , lib2 contient "hebdomadaire" ou "mensuel"
		public String lib1;
		public String lib2;
		
		// Contient le nom du fichier
		public String fileName;
		
		// Contient le nom affiché
		public String displayName;
		
		public Date debut;
		public Date fin;
	}
	
	
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}
	
	
	public static void main(String[] args) throws IOException
	{
		Date d = DateUtils.addMonth(DateUtils.getDate(), 1);
		new EGFeuilleEmargement(10301L,d,"").test();
	}
	

}
