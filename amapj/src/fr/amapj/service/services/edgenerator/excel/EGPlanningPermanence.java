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
 package fr.amapj.service.services.edgenerator.excel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.common.DateUtils;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.saisiepermanence.PermanenceDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceService;


/**
 * Permet la generation du planning de dsitribution au format Excel 
 * 
 *  
 *
 */
public class EGPlanningPermanence extends AbstractExcelGenerator
{
	
	Date startDate;
	
	public EGPlanningPermanence(Date startDate)
	{
		this.startDate = startDate;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		List<PermanenceDTO> distributionDTOs = new PermanenceService().getAllDistributions();
		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");

		
		// Il y a systèmatiquement 8 colonnes
		et.addSheet("Planning des permanences", 8, 28);
		et.setColumnWidth(0, 2);
		et.setColumnWidth(2, 2);
		et.setColumnWidth(4, 2);
	
				
		et.addRow("Planning de distribution",et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		
		List<List<PermanenceDTO>> lines = cutInThree(distributionDTOs);
		
		
		for (List<PermanenceDTO> line : lines)
		{
			processOneLine(line,et,df);
		}	

	}

	private List<List<PermanenceDTO>> cutInThree(List<PermanenceDTO> distributionDTOs)
	{
		List<List<PermanenceDTO>> res = new ArrayList<>();
		
		List<PermanenceDTO> tmp = new ArrayList<>();
		int size = distributionDTOs.size();
		for (int i = 0; i < size; i++)
		{
			if ( (i!=0) && ((i%3)==0) )
			{
				res.add(tmp);
				tmp = new ArrayList<>();
			}
			tmp.add(distributionDTOs.get(i));
		}
		
		if (tmp.size()!=0)
		{
			res.add(tmp);
		}
		
		return res;
	}

	private void processOneLine(List<PermanenceDTO> line, ExcelGeneratorTool et,SimpleDateFormat df)
	{
	
		// Ligne de titre
		et.addRow();
		int index =1;
		for (PermanenceDTO distributionDTO : line)
		{
			et.setCell(index, df.format(distributionDTO.datePermanence), et.grasCentreBordure);
			index = index +2;
		}
		
		// Ligne avec les noms
		et.addRow();
		index =1;
		int maxLine = 1;
		for (PermanenceDTO distributionDTO : line)
		{
			String str = distributionDTO.getUtilisateurs("\n");
			et.setCell(index, str, et.grasCentreBordure);
			
			maxLine = Math.max(maxLine, distributionDTO.permanenceUtilisateurs.size());
			
			index = index +2;
		}
		et.setRowHeigth(maxLine+1);
		
		// Une ligne vide
		et.addRow();
	}



	@Override
	public String getFileName(EntityManager em)
	{
		return "planning-distribution";
	}

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "le planning des permanences";
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

	public static void main(String[] args) throws IOException
	{
		new EGPlanningPermanence(DateUtils.getDate()).test();
	}

}
