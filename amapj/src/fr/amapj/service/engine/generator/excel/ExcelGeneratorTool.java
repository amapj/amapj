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
 package fr.amapj.service.engine.generator.excel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;

/**
 * Permet la génération facile des fichiers excel
 * 
 * Dans ce système, il est nécessaire de fixer dès le départ le nombre de colonnes dans la feuille
 * 
 *  
 *
 */
public class ExcelGeneratorTool
{
	Workbook wb;
	Sheet sheet;
	Row currentRow;
	
	
	Font fontNonGras;
	Font fontGras;
	Font fontGrasBlue;
	Font fontGrasHaut;
	Font fontGrasPetit;
	
	public CellStyle grasGaucheNonWrappe;
	public CellStyle grasGaucheNonWrappeColor;
	public CellStyle grasGaucheNonWrappeBordure;
	public CellStyle grasGaucheNonWrappeBordureGray;
	
	public CellStyle grasGaucheWrappe;
	public CellStyle grasGaucheWrappeColor;
	public CellStyle grasGaucheWrappeBordure;
	public CellStyle grasGaucheWrappeBordureGray;
	
	public CellStyle grasCentre;
	public CellStyle grasCentreBordure;
	public CellStyle grasCentreBordureColor;
	public CellStyle grasCentreBordureColorPetit;
	public CellStyle grasCentreBordureGray;
	
	
	public CellStyle nonGrasCentreBordure;
	public CellStyle nonGrasCentreBordureDiagonal;
	public CellStyle nonGrasCentreBordureDiagonalColor;
	public CellStyle nonGrasCentreBordureColor;
	public CellStyle nonGrasCentreBordureGray;
	public CellStyle nonGrasGaucheBordure;
	public CellStyle nongrasGaucheWrappe;
	public CellStyle nonGrasGaucheNonWrappe;
	public CellStyle nonGrasGaucheBordureGray;
	
	
	
	
	public CellStyle prixCentreBordure;
	public CellStyle prixCentreBordureColor;
	public CellStyle titre;
	
	boolean firstLine = true;
	
	
	int nbColMax;
	

	public ExcelGeneratorTool(ExcelFormat format)
	{
		if (format==ExcelFormat.XLS)
		{
			wb = new HSSFWorkbook();
		}
		else
		{
			wb = new XSSFWorkbook();
		}
		initializeFont();
		initializeStyle();
	}


	/**
	 * On utilise en tout 5 fontes 
	 *  ->une Arial taille 10 gras noir
	 *  ->une Arial taille 10 non gras noir
	 *  ->une Arial taille 10 gras bleue
	 *  ->une Arial taille 12 gras noir 
	 *  ->une Arial taille 7 gras noir
	 */
	private void initializeFont()
	{
		// Création des différentes fontes
		fontNonGras = wb.createFont();
		fontNonGras.setFontHeightInPoints((short) 10);
		fontNonGras.setFontName("Arial");
		fontNonGras.setColor(IndexedColors.BLACK.getIndex());
		fontNonGras.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		fontNonGras.setItalic(false);

		fontGras = wb.createFont();
		fontGras.setFontHeightInPoints((short) 10);
		fontGras.setFontName("Arial");
		fontGras.setColor(IndexedColors.BLACK.getIndex());
		fontGras.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontGras.setItalic(false);

		fontGrasBlue = wb.createFont();
		fontGrasBlue.setFontHeightInPoints((short) 10);
		fontGrasBlue.setFontName("Arial");
		fontGrasBlue.setColor(IndexedColors.BLUE.getIndex());
		fontGrasBlue.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontGrasBlue.setItalic(false);

		fontGrasHaut = wb.createFont();
		fontGrasHaut.setFontHeightInPoints((short) 12);
		fontGrasHaut.setFontName("Arial");
		fontGrasHaut.setColor(IndexedColors.BLACK.getIndex());
		fontGrasHaut.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontGrasHaut.setItalic(false);
		
		fontGrasPetit = wb.createFont();
		fontGrasPetit.setFontHeightInPoints((short) 7);
		fontGrasPetit.setFontName("Arial");
		fontGrasPetit.setColor(IndexedColors.BLACK.getIndex());
		fontGrasPetit.setBoldweight(Font.BOLDWEIGHT_BOLD);
		fontGrasPetit.setItalic(false);

	}

	
	
	/**
	 * On utilise en tout 12 styles
	 */
	private  void initializeStyle()
	{

		// Création des styles
		grasGaucheNonWrappe = wb.createCellStyle();
		grasGaucheNonWrappe.setAlignment(CellStyle.ALIGN_LEFT);
		grasGaucheNonWrappe.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		grasGaucheNonWrappe.setFont(fontGras);
		grasGaucheNonWrappe.setWrapText(false);
		beWhite(grasGaucheNonWrappe);

		grasGaucheNonWrappeColor = wb.createCellStyle();
		grasGaucheNonWrappeColor.cloneStyleFrom(grasGaucheNonWrappe);
		beOrange(grasGaucheNonWrappeColor);

		grasGaucheNonWrappeBordure = wb.createCellStyle();
		grasGaucheNonWrappeBordure.setAlignment(CellStyle.ALIGN_LEFT);
		grasGaucheNonWrappeBordure.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		grasGaucheNonWrappeBordure.setFont(fontGras);
		grasGaucheNonWrappeBordure.setWrapText(false);
		addBorderedStyle(grasGaucheNonWrappeBordure);
		beWhite(grasGaucheNonWrappeBordure);
		
		grasGaucheNonWrappeBordureGray = duplicate(grasGaucheNonWrappeBordure);
		beGray(grasGaucheNonWrappeBordureGray);
		
		
		
		// 
		
		grasGaucheWrappe = duplicate(grasGaucheNonWrappe);
		grasGaucheWrappe.setWrapText(true);
		
		grasGaucheWrappeColor = duplicate(grasGaucheNonWrappeColor);
		grasGaucheWrappeColor.setWrapText(true);
		
		grasGaucheWrappeBordure = duplicate(grasGaucheNonWrappeBordure);
		grasGaucheWrappeBordure.setWrapText(true);
		
		grasGaucheWrappeBordureGray = duplicate(grasGaucheNonWrappeBordureGray);
		grasGaucheWrappeBordureGray.setWrapText(true);
		
		
		//
		
		grasCentre = wb.createCellStyle();
		grasCentre.setAlignment(CellStyle.ALIGN_CENTER);
		grasCentre.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		grasCentre.setFont(fontGras);
		grasCentre.setWrapText(true);
		beWhite(grasCentre);
		
		grasCentreBordure = duplicate(grasCentre);
		addBorderedStyle(grasCentreBordure);

		grasCentreBordureColor = duplicate(grasCentreBordure);
		beOrange(grasCentreBordureColor);
		
		grasCentreBordureColorPetit = duplicate(grasCentreBordureColor);
		grasCentreBordureColorPetit.setFont(fontGrasPetit);
		
		grasCentreBordureGray = duplicate(grasCentreBordure);
		beGray(grasCentreBordureGray);
		
		

		nonGrasCentreBordure = wb.createCellStyle();
		nonGrasCentreBordure.setAlignment(CellStyle.ALIGN_CENTER);
		nonGrasCentreBordure.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		nonGrasCentreBordure.setFont(fontNonGras);
		addBorderedStyle(nonGrasCentreBordure);
		nonGrasCentreBordure.setWrapText(true);
		beWhite(nonGrasCentreBordure);

		nonGrasCentreBordureDiagonal = duplicate(nonGrasCentreBordure);
		addDiagonalBorder(nonGrasCentreBordureDiagonal);
		
		nonGrasCentreBordureDiagonalColor = duplicate(nonGrasCentreBordureDiagonal);
		beOrange(nonGrasCentreBordureDiagonalColor);
		
		nonGrasCentreBordureColor = duplicate(nonGrasCentreBordure);
		beOrange(nonGrasCentreBordureColor);
		
		nonGrasCentreBordureGray = duplicate(nonGrasCentreBordure);
		beGray(nonGrasCentreBordureGray);
		

		nonGrasGaucheBordure = wb.createCellStyle();
		nonGrasGaucheBordure.setAlignment(CellStyle.ALIGN_LEFT);
		nonGrasGaucheBordure.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		nonGrasGaucheBordure.setFont(fontNonGras);
		addBorderedStyle(nonGrasGaucheBordure);
		nonGrasGaucheBordure.setWrapText(true);
		beWhite(nonGrasGaucheBordure);
		
		
		nonGrasGaucheBordureGray = duplicate(nonGrasGaucheBordure);
		beGray(nonGrasGaucheBordureGray);
		
		

		prixCentreBordure = wb.createCellStyle();
		prixCentreBordure.setAlignment(CellStyle.ALIGN_CENTER);
		prixCentreBordure.setFont(fontGrasBlue);
		addBorderedStyle(prixCentreBordure);
		prixCentreBordure.setWrapText(true);
		DataFormat df = wb.createDataFormat();
		prixCentreBordure.setDataFormat(df.getFormat("#,##0.00€"));
		beWhite(prixCentreBordure);

		prixCentreBordureColor = duplicate(prixCentreBordure);
		beOrange(prixCentreBordureColor);
		
	    
	    titre = wb.createCellStyle(); 
	    titre.setAlignment(CellStyle.ALIGN_CENTER);
	    titre.setFont(fontGrasHaut);
	    titre.setWrapText(false);
	 	beWhite(titre);
	 	
	 	
	 	nongrasGaucheWrappe = wb.createCellStyle(); 
		nongrasGaucheWrappe.setAlignment(CellStyle.ALIGN_LEFT);
		nongrasGaucheWrappe.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		nongrasGaucheWrappe.setFont(fontNonGras);
		nongrasGaucheWrappe.setWrapText(true);
		beWhite(nongrasGaucheWrappe);
		
		nonGrasGaucheNonWrappe = duplicate(nongrasGaucheWrappe);
		nonGrasGaucheNonWrappe.setWrapText(false);

	}
	
	private CellStyle duplicate(CellStyle style)
	{
		CellStyle ret = wb.createCellStyle();
		ret.cloneStyleFrom(style);
		return ret;
	}


	private void beWhite(CellStyle style)
	{
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFillForegroundColor(IndexedColors.WHITE.getIndex());

	}

	private void beOrange(CellStyle style)
	{
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());

	}
	
	private void beGray(CellStyle style)
	{
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		if (wb instanceof HSSFWorkbook )
		{
			HSSFPalette palette = ((HSSFWorkbook )wb ).getCustomPalette();
		    palette.setColorAtIndex(HSSFColor.LAVENDER.index,(byte) 0xF0, (byte)0xF0,(byte) 0xF0);
		    style.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
		}
		else
		{
			((XSSFCellStyle) style).setFillForegroundColor(new XSSFColor(new java.awt.Color(0xF0,0xF0,0xF0)));
		}
	}
	

	private void addBorderedStyle(CellStyle style)
	{
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
	}
	
	
	/**
	 * Permet d'ajouter une croix (deux diagnonales) sur la cellule
	 * 
	 * Voir la classe DiagonalBorder pour plus d'explications sur HSSFWorkbook
	 * 
	 * Voir 
	 * https://stackoverflow.com/questions/39529042/apache-poi-how-to-add-diagonal-border
	 * pour plus d'explications sur XSSFWorkbook
	 */
	private void addDiagonalBorder(CellStyle style)
	{
		short lineStyle = CellStyle.BORDER_THIN;
		
		try
		{
			if (wb instanceof HSSFWorkbook )
			{
				Field f = HSSFCellStyle.class.getDeclaredField("_format");
				f.setAccessible(true);
				
				ExtendedFormatRecord efr = (ExtendedFormatRecord) f.get(style);
				
				efr.setIndentNotParentBorder(true);
				efr.setDiag((short)3);
				
				//   
				efr.setAdtlDiag((short) 64);
				efr.setAdtlDiagLineStyle(lineStyle);
			}
			else
			{
				Method m = XSSFCellStyle.class.getDeclaredMethod("getCTBorder");
				m.setAccessible(true);
				
				Field f1 = XSSFCellStyle.class.getDeclaredField("_stylesSource");
				f1.setAccessible(true);
				
				Field f2 = XSSFCellStyle.class.getDeclaredField("_theme");
				f2.setAccessible(true);
				
				
				CTBorder ct = (CTBorder) m.invoke(style);
				CTXf _cellXf = ( (XSSFCellStyle) style).getCoreXf();
				StylesTable _stylesSource = (StylesTable) f1.get(style);
				ThemesTable _theme = (ThemesTable) f2.get(style);
				
				
				CTBorderPr pr = ct.isSetDiagonal() ? ct.getDiagonal() : ct.addNewDiagonal();
				if (lineStyle == BorderFormatting.BORDER_NONE)
				{
					ct.unsetDiagonal();
				} else
				{
					ct.setDiagonalDown(true);
					ct.setDiagonalUp(true);
					pr.setStyle(STBorderStyle.Enum.forInt(lineStyle + 1));
				}
				int idx = _stylesSource.putBorder(new XSSFCellBorder(ct, _theme));
				_cellXf.setBorderId(idx);
				_cellXf.setApplyBorder(true);
			}
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			throw new AmapjRuntimeException(e);
		}
	}
	
	
	
	
	/**
	 * Permet de créer une feuille, en indiquant le nombre de colonnes
	 * et la taille de chaque colonne en caractères
	 * 
	 * 
	 * @param sheetName
	 * @param nbCol
	 * @param colWidth
	 * @return
	 */
	public void addSheet(String sheetName,int nbCol,int colWidth)
	{
		sheetName = WorkbookUtil.createSafeSheetName(sheetName);
		sheet = wb.createSheet(sheetName);
		currentRow = null;
		firstLine = true;
		
		this.nbColMax = nbCol;

		
		for (int i = 0; i < nbCol; i++)
		{
			//
			setColumnWidth(i, colWidth);

			// Style par defaut pour toutes les colonnes
			sheet.setDefaultColumnStyle(i, grasGaucheNonWrappe);
		}
		
		// Les marges par défaut sont 1 cm de chaque côté
		setMargin(10, 10, 10, 10);
		
		// On est systématiquement en mode portrait et en A4
		sheet.getPrintSetup().setLandscape(false);
		sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); 
	}
	
	/**
	 * Permet de passer la feuille en mode Paysage (à la place de Portrait)
	 */
	public void setModePaysage()
	{
		sheet.getPrintSetup().setLandscape(true);
	}
	
	/**
	 * Les paramètres sont en mm
	 */
	public void setMargin(int left, int right, int top , int bottom)
	{
		// c est la valeur de 1 mm en inches
		double c = 0.039370;
		sheet.setMargin(Sheet.LeftMargin, c*left);
		sheet.setMargin(Sheet.RightMargin, c*right);
		sheet.setMargin(Sheet.TopMargin, c*top);
		sheet.setMargin(Sheet.BottomMargin, c*bottom);
	}
	
	
	public void setMargin(AbstractEditionSpeJson json)
	{
		setMargin(json.getMargeGauche(),json.getMargeDroite(),json.getMargeHaut(),json.getMargeBas());
	}
	
	public void setPageFormat(AbstractEditionSpeJson json)
	{
		switch (json.getPageFormat())
		{
		case A3_PAYSAGE:
			sheet.getPrintSetup().setLandscape(true);
			sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A3_PAPERSIZE); 
			break;

		case A4_PAYSAGE:
			sheet.getPrintSetup().setLandscape(true);
			sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); 
			break;
			

		case A3_PORTRAIT:
			sheet.getPrintSetup().setLandscape(false);
			sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A3_PAPERSIZE); 
			break;

		case A4_PORTRAIT:
			sheet.getPrintSetup().setLandscape(false);
			sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); 
			break;	
			
		default:
			throw new AmapjRuntimeException();
		}
	}
	
	public void setMarginAndPageFormat(AbstractEditionSpeJson json)
	{
		setMargin(json);
		setPageFormat(json);
	}
	
	
	/**
	 * Permet d'ajuster la page en largeur à une page pile 
	 * en modifiant la largeur de chaque colonne
	 */
	public void adjustSheetForOnePage()
	{
		// Par essais successifs, on arrive à cette valeur, avec 1 cm de marge à droite et gauche 
		int PAGE_WIDTH = 26500;
		
		int nbTotal=0;
		for (int i = 0; i < nbColMax; i++)
		{
			nbTotal = nbTotal+sheet.getColumnWidth(i);
		}
		
		if (nbTotal<=PAGE_WIDTH)
		{
			return;
		}
		
		for (int i = 0; i < nbColMax; i++)
		{
			int newWidth = (sheet.getColumnWidth(i)*PAGE_WIDTH)/nbTotal;
			sheet.setColumnWidth(i, newWidth);
		}
	}
	
	/**
	 * Permet de déplacer la page courante à l'index spécifié
	 */
	public void setSheetFirst()
	{
		int nb = wb.getNumberOfSheets();
		
		// On construit la liste des noms des feuilles dans le bon ordre
		List<String> sheetName = new ArrayList<>();
		sheetName.add(sheet.getSheetName());
		for (int i = 0; i < nb-1; i++)
		{
			Sheet s = wb.getSheetAt(i);
			sheetName.add(s.getSheetName());
		}
		
		// On applique ensuite à chaque feuille son nouveau numero d'ordre
		for (int i = 0; i < nb; i++)
		{
			wb.setSheetOrder(sheetName.get(i),i);
			Sheet s = wb.getSheetAt(i);
			if (i==0)
			{
				s.setSelected(true);
			}
			else
			{
				s.setSelected(false);
			}
		}
	}

	
	
	/**
	 * Permet de fixer la hauteur de la ligne en nombre de ligne de texte avec fonte 12
	 * @param nbLine
	 */
	public void setRowHeigth(int nbLine)
	{
		
		currentRow.setHeight( (short) (20*12*nbLine));
	}
	
	/**
	 * Permet de fixer la hauteur de la ligne en mm
	 * @param nbLine
	 */
	public void setRowHeigthInMm(int mm)
	{
		
		currentRow.setHeight( (short) (56.7*mm));
	}
	
	/**
	 * Permet de fixer la hauteur de la ligne à UNDEFINED
	 * @param nbLine
	 * 
	 * TODO a voir si on conserve 
	 */
	public void setRowHeigthUndefined()
	{
		
		currentRow.setHeight((short)0);
		currentRow.setRowStyle(grasGaucheWrappeBordure);
	}
	
	
	/**
	 * Permet d'ajouter une ligne, avec le bon nombre de celulles
	 */
	public Row addRow()
	{		
		int index = sheet.getLastRowNum();
		if (firstLine)
		{
			index=-1;
			firstLine=false;
		}
		
			
		Row row = sheet.createRow(index+1);
		
		for (int i = 0; i < nbColMax; i++)
		{
			Cell cell = row.createCell(i);
			cell.setCellStyle(grasGaucheNonWrappe);
		}
		
		currentRow = row;
		
		return row;
	}

	

	/**
	 * Permet d'ajouter une ligne avec la première cellule contenant 
	 * le style indiqué et le texte 
	 * 
	 * @param text
	 * @param style
	 */
	public void addRow(String text, CellStyle style)
	{
		Row row= addRow();
		Cell cell = row.getCell(0);
		cell.setCellStyle(style);
		cell.setCellValue(text);
	}
	
	/**
	 * Permet de merger les cellules par rapport à la ligne courante, 
	 * vers le droite, et d'une longueur nbCol
	 * 
	 * @param nbRow
	 * @param firstCol
	 * @param lastCol
	 */
	public void mergeCellsRight(int numCol,int nbCol)
	{
		checkNumCol(numCol);
		checkNumCol(numCol+nbCol-1);
		
		int lastRow = currentRow.getRowNum();
		int firstRow = lastRow;
		mergeCells(firstRow, lastRow, numCol, numCol+nbCol-1);
	}

	
	/**
	 * Permet de merger les cellules par rapport à la ligne courante, 
	 * vers le haut, et d'une hauteur de nbRow
	 * 
	 * @param nbRow
	 * @param firstCol
	 * @param lastCol
	 */
	public void mergeCellsUp(int numCol,int nbRow)
	{
		checkNumCol(numCol);
		
		mergeCellsUp(numCol,numCol,nbRow);
	}
	
	/**
	 * Permet de merger les cellules par rapport à la ligne courante, 
	 * vers le haut, et d'une hauteur de nbRow
	 * 
	 * @param nbRow
	 * @param firstCol
	 * @param lastCol
	 */
	public void mergeCellsUp(int firstCol, int lastCol,int nbRow )
	{
		checkNumCol(firstCol);	
		checkNumCol(lastCol);
		
		int lastRow = currentRow.getRowNum();
		int firstRow = lastRow-nbRow+1;
		mergeCells(firstRow, lastRow, firstCol, lastCol);
	}
	
	
	/**
	 * Merge des cellules
	 * 
	 * Le style de la cellule en haut à gauche est copié sur toutes les autres cellules 
	 * 
	 * @param firstRow
	 * @param lastRow
	 * @param firstCol
	 * @param lastCol
	 */
	public void mergeCells(int firstRow, int lastRow, int firstCol, int lastCol)
	{
		CellStyle style = sheet.getRow(firstRow).getCell(firstCol).getCellStyle();
		
		for (int numRow = firstRow; numRow <= lastRow; numRow++)
		{
			Row row = sheet.getRow(numRow);	
			for (int numCol = firstCol; numCol <= lastCol; numCol++)
			{
				row.getCell(numCol).setCellStyle(style);
			}
		}
		
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
	}

	
	public void createEmptyCell(Row row, int firstCol, int lastCol)
	{
		for (int i = firstCol; i <= lastCol; i++)
		{
			row.createCell(i);
		}
	}
	
	
	// Gestion de la couleur
	public CellStyle switchColor(CellStyle style, int i)
	{
		if ((i%2)==0)
		{
			return style;
		}
		else
		{
			if (style==grasCentreBordure)
			{
				return grasCentreBordureColor;
			}
			else if (style==prixCentreBordure)
			{
				return prixCentreBordureColor;
			}
			else if (style == nonGrasCentreBordure)
			{
				return nonGrasCentreBordureColor;
			}
			else if (style == nonGrasCentreBordureDiagonal)
			{
				return nonGrasCentreBordureDiagonalColor;
			}
			else
			{
				throw new RuntimeException("erreur de programme");
			}
		}
	}
	
	
	// Gestion du grisé
	public CellStyle switchGray(CellStyle style, int i)
	{
		if ((i%2)==0)
		{
			return style;
		}
		else
		{
			if (style==grasGaucheNonWrappeBordure)
			{
				return grasGaucheNonWrappeBordureGray;
			}
			else if (style==nonGrasGaucheBordure)
			{
				return nonGrasGaucheBordureGray;
			}
			else if (style == grasCentreBordure)
			{
				return grasCentreBordureGray;
			}
			else if (style == nonGrasCentreBordure)
			{
				return nonGrasCentreBordureGray;
			}
			else if (style == grasGaucheWrappeBordure)
			{
				return grasGaucheWrappeBordureGray;
			}
			else
			{
				throw new RuntimeException("erreur de programme");
			}
		}
	}




	
	
	/**
	 * Retourne true si la ligne doit être colorée
	 * @param i
	 * @return
	 */
	private boolean isColored(int i, int nbProd)
	{
		int numDate = i / nbProd;
		if ((numDate % 2) == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private void colorize(Row row, int nbDates, int nbProd)
	{
		for (int i = 0; i < nbDates / 2; i++)
		{
			int index = 3 + nbProd + i * 2 * nbProd;
			for (int j = 0; j < nbProd; j++)
			{
				Cell cell = row.getCell(j + index);
				if (cell != null)
				{
					CellStyle st = cell.getCellStyle();
					beOrange(st);
					cell.setCellStyle(st);

				}
			}

		}

	}



	

	/**
	 * index : 1 based !!!
	 * 
	 */
	private void set(Row row, int index, String str)
	{
		row.createCell(index - 1).setCellValue(str);
	}

	private void setFormula(Row row, int index, String str)
	{
		row.createCell(index - 1).setCellFormula(str);
	}

	
	
	
	public Workbook getWb()
	{
		// Recalcul des formules
		/*if (wb instanceof XSSFWorkbook)
		{
			XSSFFormulaEvaluator.evaluateAllFormulaCells( (XSSFWorkbook) wb);
		}
		else
		{
			HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
		}
		*/
		HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
		
		return wb;
	}
	
	
	// Opérations sur les colonnes
	
	/**
	 * Permet de positionner une largeur en nombre de caractères
	 * @param width
	 */
	public void setColumnWidth(int numCol,int width)
	{
		sheet.setColumnWidth(numCol, width*256);
	}
	
	/**
	 * Permet de positionner une largeur en nombre de mm
	 * @param width
	 */
	public void setColumnWidthInMm(int numCol,int widthInMm)
	{
		sheet.setColumnWidth(numCol, (int) (widthInMm*130.8) );
	}


	/**
	 * Permet de connaitre une largeur en nombre de points
	 * 
	 *  Mesuré sur Libre Office : 50 caractères <=> 9.79 cm <=> 275 points 
	 *  donc 1 caractère = 5.5 points
	 *  
	 *  256/5.5 => 46.5454 
	 *  
	 *  On devrait donc utiliser cette valeur, mais on constate que ca ne fonctionne pas
	 *  Experimentalement, on voit que 48.0 est ok
	 *  
	 *  
	 *  On constate un offset de 5 points entre la largeur de la colonne et la largeur disponible pour le texte 
	 */
	public int getColumnWidthInPoints(int numCol)
	{
		return (int) (sheet.getColumnWidth(numCol)/48.0-5);
	}
	
	
	/**
	 * Permet de connaitre une largeur en nombre de points pour x cellules fusionnes consecutives
	 *  
	 */
	public int getColumnWidthInPointsForMergedCell(int numCol,int nbCol)
	{
		int res = 0;
		for (int i = 0; i < nbCol; i++)
		{
			res = res +sheet.getColumnWidth(numCol+i);
		}

		return (int) (res/48.0-5);
	}

	
	  
	
	
	// Opérations sur les cellules


	/**
	 * Permet de positionner le contenu d'une cellule avec un texte
	 * @param numCol
	 * @param text
	 */
	public void setCell(int numCol, String text,CellStyle style)
	{
		Cell cell = currentRow.getCell(numCol);
		cell.setCellValue(text);
		cell.setCellStyle(style);
	}
	
	/**
	 * Permet de positionner N cellules identiques à la suite
	 */
	public void setNCell(int numCol, int nbCell,String text,CellStyle style)
	{
		for (int i = numCol; i < numCol+nbCell; i++)
		{
			setCell(i, text, style);
		}
	}


	public void setCellPrix(int numCol, int montant,CellStyle style)
	{
		Cell cell = currentRow.getCell(numCol);
		
		if (montant==0)
		{
			// Ne rien faire 
		}
		else
		{
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(( (double) montant)/100.0);
		}
		
		cell.setCellStyle(style);
	}
	
	
	public void setCellQte(int numCol, int qte,CellStyle style)
	{
		checkNumCol(numCol);
		Cell cell = currentRow.getCell(numCol);
		
		
		if (qte==0)
		{
			// Ne rien faire 
		}
		else
		{
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((double) qte);
		}
		
		cell.setCellStyle(style);
	}
	
	
	
	
	private void checkNumCol(int numCol)
	{
		if (numCol<0)
		{
			throw new RuntimeException("Vous essayez d'accèder à la colonne "+numCol+" : les nombres négatifs sont interdits ");
		}
		else if (numCol>=this.nbColMax)
		{
			throw new RuntimeException("La feuille possède "+this.nbColMax+" colonnes et vous essayez d'accèder à la colonne "+numCol);
		}
		
	}


	// PARTIE SUR LES SOMMES
	
	/**
	 * Permet d'indiquer que cette celule est la somme de X cellules de la même ligne.
	 *
	 * Les cellules a sommer sont obligatoirement contigues
	 * 
	 */
	public void setCellSumInRow(int numCol,int firstCellOfTheSum, int nbCellToSum,CellStyle style)
	{
		String formula = "SUM(" + getCellLabel(firstCellOfTheSum) + ":" + getCellLabel(firstCellOfTheSum+nbCellToSum) + ")";

		setCellFormula(numCol, formula, style);
	}
	
	
	/**
	 *Permet d'indiquer que cette celule est la somme de X cellules de la même ligne.
	 *
	 * Les cellules a sommer ne sont pas forcément contigues, elles sont séparés de <code>stepBetweenCellToSum</code> cases
	 * 
	 *
	 */
	public void setCellSumInRow(int numCol,int firstCellOfTheSum, int stepBetweenCellToSum,int nbCellToSum,int[] additionalCells,CellStyle style)
	{
		String formula = asSumString(additionalCells);
		if (formula.length()!=0)
		{
			formula=formula+"+";
		}
		
		for (int i = 0; i < nbCellToSum; i++)
		{
			formula=formula+getCellLabel(firstCellOfTheSum+stepBetweenCellToSum*i);
			if (i!=nbCellToSum-1)
			{
				formula=formula+"+";
			}
		}
		setCellFormula(numCol, formula, style);
	}
	
	
	/**
	 * Permet d'indiquer que cette celule est la somme de X cellules de la même colonne, en dessous de la cellule courante
	 *
	 * Les cellules a sommer sont obligatoirement contigues
	 * 
	 * 
	 * deltaVertical =1 si ca demarre juste en dessous
	 * deltaVertical =2 si il y a une case d'espace 
	 * 
	 */
	public void setCellSumInColDown(int numCol,int deltaVertical, int nbCellToSum,CellStyle style)
	{
		int firstNumRow = currentRow.getRowNum()+deltaVertical;
		int lastNumRow = firstNumRow+nbCellToSum-1;
		
		String formula = "SUM(" + getCellLabel(firstNumRow,numCol) + ":" + getCellLabel(lastNumRow,numCol) + ")";

		setCellFormula(numCol, formula, style);
	}
	
	/**
	 * Permet d'indiquer que cette celule est la somme de X cellules de la même colonne, en dessus de la cellule courante
	 *
	 * Les cellules a sommer sont obligatoirement contigues
	 * 
	 * 
	 * deltaVertical =1 si ca demarre juste en dessus
	 * deltaVertical =2 si il y a une case d'espace 
	 * 
	 */
	public void setCellSumInColUp(int numCol,int deltaVertical, int nbCellToSum,CellStyle style)
	{
		if (nbCellToSum<=0)
		{
			setCellFormula(numCol, "0", style);
			return;
		}
		
		int firstNumRow = currentRow.getRowNum()-deltaVertical-nbCellToSum+1;;
		int lastNumRow = firstNumRow+nbCellToSum-1;
		
		String formula = "SUM(" + getCellLabel(firstNumRow,numCol) + ":" + getCellLabel(lastNumRow,numCol) + ")";

		setCellFormula(numCol, formula, style);
	}
	
	
	// PARTIE SUMPROD
	
	
	/**
	 * Permet d'indiquer que cette celule est la somme du produit de X cellules de la même ligne avec une autre ligne fixe
	 *
	 * Les cellules a sommer et multiplier sont obligatoirement contigues
	 * 
	 * rowIndex représente l'index de la ligne qu va être multiplié avant de faire la multiplication 
	 * rowIndex est 0-based 
	 * 
	 */
	public void setCellSumProdInRow(int numCol,int firstCellOfTheSum, int nbCellToSum,int rowIndex, CellStyle style)
	{
		String formula = 	"SUMPRODUCT(" 
							+ getCellLabel(rowIndex,firstCellOfTheSum) + ":" + getCellLabel(rowIndex,firstCellOfTheSum+nbCellToSum) 
							+ ","
							+ getCellLabel(firstCellOfTheSum) + ":" + getCellLabel(firstCellOfTheSum+nbCellToSum) + ")";

		setCellFormula(numCol, formula, style);
	}
	
	
	
	// PARTIE FORMULES
	
	
	
	/**
	 *Permet d'indiquer que cette celule une formule
	 * 
	 *
	 */
	public void setCellFormula(int numCol,String formula,CellStyle style)
	{
		Cell cell = currentRow.getCell(numCol);
		
		cell.setCellFormula(formula);
		cell.setCellStyle(style);
	}
	
	/**
	 * Permet de créer une formule simple sur une ligne, en disant 
	 * que la cellule numCol est égale à la somme d'autres cellules de la ligne
	 * et la la soutracation d'autres cellules de la ligne
	 */
	public void setCellBasicFormulaInRow(int numCol, int[] cellsToAdd, int[] cellsToSubstract, CellStyle style)
	{
		String sum=asSumString(cellsToAdd);
		String sub=asSumString(cellsToSubstract);
		
		String formula=sum;
		if (sub.length()!=0)
		{
			formula = formula+"-("+sub+")";
		}
		setCellFormula(numCol, formula, style);
	}
	
	
	private String asSumString(int[] cells)
	{
		if ( (cells==null) || (cells.length==0) )
		{
			return "";
		}
		
		String formula="";
		for (int i = 0; i < cells.length; i++)
		{
			int cell = cells[i];	
			formula=formula+getCellLabel(cell);
			if (i!=cells.length-1)
			{
				formula=formula+"+";
			}
		}
		return formula;
	}
	
	
	
	// GESTION DES LABELS
	
	
	/**
	 * Retourne le label de la cellule indiqué, par exemple A9
	 * 
	 * ligIndex et colIndex sont 0-based
	 */
	public String getCellLabel(int ligIndex, int colIndex)
	{
		return CellReference.convertNumToColString(colIndex) + (ligIndex+1);
	}
	
	
	/**
	 * Retourne le label de la cellule indiqué, par exemple A9
	 * 
	 * @param row
	 * @param colIndex
	 * @return
	 */
	public String getCellLabel(Row row, int colIndex)
	{
		return CellReference.convertNumToColString(colIndex) + (row.getRowNum()+1);
	}

	/**
	 * Retourne le label de la cellule indiqué, par exemple A9, sur la ligne courante 
	 * 
	 * @param row
	 * @param colIndex
	 * @return
	 */
	public String getCellLabel(int colIndex)
	{
		return getCellLabel(currentRow,colIndex);
	}

	
	
	/**
	 * Cacher les colonnes
	 */

	public void setColHidden(int columnIndex, boolean hidden)
	{
		sheet.setColumnHidden(columnIndex, hidden);
	}

	
	/**
	 * Permet d'indiquer que toutes les lignes de l1 à l2 seront imprimés sur toutes les pages
	 * 
	 *  Attention : l1 et l2 sont 1-based index 
	 */
	public void setRepeatingRow(int l1,int l2)
	{
		sheet.setRepeatingRows(CellRangeAddress.valueOf(l1+":"+l2));
	}
	
	
}
