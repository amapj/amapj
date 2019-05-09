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
 package fr.amapj.service.services.edgenerator.excel;

import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.logview.LogViewService;
import fr.amapj.service.services.logview.StatInstanceDTO;


/**
 * Permet la generation des statistiques
 * 
 *  
 *
 */
public class EGStatAccess extends AbstractExcelGenerator
{
	
		
	public EGStatAccess()
	{
	
	}

	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		et.addSheet("Stat", 10, 20);
		
		List<StatInstanceDTO> dtos = new LogViewService().getStatInstance();
		
		et.addRow("Statistiques",et.titre);
		et.addRow("",et.grasGaucheNonWrappe);
		
		et.addRow();
		et.setCell(0, "Nom", et.grasCentreBordureGray);
		et.setCell(1, "Visites Mois-1", et.grasCentreBordureGray);
		et.setCell(2, "Visiteurs Mois-1", et.grasCentreBordureGray);
		et.setCell(3, "Visites Mois-2", et.grasCentreBordureGray);
		et.setCell(4, "Visiteurs Mois-2", et.grasCentreBordureGray);
		et.setCell(5, "Visites Mois-3", et.grasCentreBordureGray);
		et.setCell(6, "Visiteurs Mois-3", et.grasCentreBordureGray);
		et.setCell(7, "", et.grasCentreBordureGray);
		et.setCell(8, "Error user Mois-1", et.grasCentreBordureGray);
		et.setCell(9, "Error demon Mois-1", et.grasCentreBordureGray);
	
		for (StatInstanceDTO statInstanceDTO : dtos)
		{
			addRow(et,statInstanceDTO);
		}
		
	}


	private void addRow(ExcelGeneratorTool et, StatInstanceDTO dto)
	{
		et.addRow();
		et.setCell(0, dto.nomInstance, et.nonGrasGaucheBordure);
		et.setCellQte(1, dto.detail[0].nbAccess, et.nonGrasCentreBordure);
		et.setCellQte(2, dto.detail[0].nbVisiteur, et.nonGrasCentreBordure);
		et.setCellQte(3, dto.detail[1].nbAccess, et.nonGrasCentreBordure);
		et.setCellQte(4, dto.detail[1].nbVisiteur, et.nonGrasCentreBordure);
		et.setCellQte(5, dto.detail[2].nbAccess, et.nonGrasCentreBordure);
		et.setCellQte(6, dto.detail[2].nbVisiteur, et.nonGrasCentreBordure);
		et.setCellQte(8, dto.erreurUser, et.nonGrasCentreBordure);
		et.setCellQte(9, dto.erreurDemon, et.nonGrasCentreBordure);
		
	}

	@Override
	public String getFileName(EntityManager em)
	{
		return "statistiques-globales";
	}
	

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "les statistiques d'accès";
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

}
