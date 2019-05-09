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
 package fr.amapj.service.engine.excelreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.utilisateur.UtilisateurService;

public class ExcelReader
{

	public ExcelReader()
	{

	}

	public List<String[]> readFile(String fileName, int nbCol) throws IOException
	{
		List<String[]> res = new ArrayList<>();

		FileInputStream file = new FileInputStream(new File(fileName));

		HSSFWorkbook workbook = new HSSFWorkbook(file);

		// Get first sheet from the workbook
		HSSFSheet sheet = workbook.getSheetAt(0);

		// Iterate through each rows from first sheet
		Iterator<Row> rowIterator = sheet.iterator();

		DataFormatter df = new DataFormatter(Locale.FRANCE);

		while (rowIterator.hasNext())
		{

			Row row = rowIterator.next();
			String[] strCell = new String[nbCol];

			for (int i = 0; i < strCell.length; i++)
			{
				strCell[i] = getValue(row, i, df);
			}

			res.add(strCell);

		}

		file.close();

		return res;
	}

	private String getValue(Row row, int i, DataFormatter df)
	{
		Cell cell = row.getCell(i);
		if (cell == null)
		{
			return null;
		}

		return df.formatCellValue(cell);

	}

	public static void main(String[] args) throws IOException
	{
		TestTools.init();

		List<String[]> res = new ExcelReader().readFile("adh.xls", 7);

		List<Utilisateur> dtos = new UtilisateurService().getUtilisateurs(true);
		for (Utilisateur utilisateurDTO : dtos)
		{
			String[] line = findLine(utilisateurDTO, res);

			System.out.println("-- "+utilisateurDTO.getNom()+" - "+utilisateurDTO.getPrenom());
			if (line != null)
			{
				String adr = "";
				String ville = "";
				String code = "";

				Pattern ligneEnsPattern = Pattern.compile("(.*)(26...)(.*)");
				Matcher m = ligneEnsPattern.matcher(line[3]);
				boolean b = m.matches();
				if (b == true)
				{
					adr = m.group(1).trim();
					code = m.group(2).trim();
					ville = m.group(3).trim();
				}

				String numTel1 = numTel(line[4]);
				String numTel2 = numTel(line[5]);

				/*System.out.println("adr=" + adr);
				System.out.println("code=" + code);
				System.out.println("ville=" + ville);
				System.out.println("numTel1=" + numTel1);
				System.out.println("numTel2=" + numTel2);*/
				System.out.println("update Utilisateur set libadr1='"+rm(adr)+"', codepostal='"+rm(code)+"', ville='"+rm(ville)+"', numTel1='"+numTel1+"', numTel2='"+numTel2+"' where id="+utilisateurDTO.getId()+";");
			}
			else
			{
				System.out.println("-- NOT FOUND");
			}
		}
	}

	private static String rm(String s)
	{
		return s.replace('\'', '"');
	}

	private static String numTel(String tel)
	{
		if (tel.length() == 0)
		{
			return tel;
		}

		tel = tel.replaceAll(" ", "");
		tel = tel.replaceAll("\\.", "");

		if (tel.startsWith("0"))
		{
			return tel;
		}
		return "0" + tel;
	}

	private static String[] findLine(Utilisateur utilisateurDTO, List<String[]> res)
	{
		for (int i = 9; i < 75; i++)
		{
			String[] line = res.get(i);
			if (isMatching(utilisateurDTO, line))
			{
				return line;
			}
		}
		return null;
	}

	private static boolean isMatching(Utilisateur utilisateurDTO, String[] line)
	{
		if (eq(utilisateurDTO.getNom(), line[1]) && (eq(utilisateurDTO.getPrenom(), line[2])))
		{
			return true;
		}
		return false;
	}

	private static boolean eq(String a, String b)
	{
		return a.trim().equalsIgnoreCase(b.trim());

	}

}
