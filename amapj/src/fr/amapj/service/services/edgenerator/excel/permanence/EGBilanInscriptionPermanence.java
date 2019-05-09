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
 package fr.amapj.service.services.edgenerator.excel.permanence;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.Row;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.DateUtils;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.PeriodePermanence;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelCellAutoSize;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil.EmailInfo;


/**
 * Permet de faire un bilan des inscriptions à une période de permanence
 *  
 *
 */
public class EGBilanInscriptionPermanence extends AbstractExcelGenerator
{
	private Long idPeriodePermanence;
		
	public EGBilanInscriptionPermanence(Long idPeriodePermanence)
	{
		this.idPeriodePermanence = idPeriodePermanence;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		PeriodePermanenceDTO dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence);
		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");

		List<UtilisateurInfo> alls = CollectionUtils.convert(dto.utilisateurs, e->createUtilisateurInfo(e, dto,em));
		List<UtilisateurInfo> retards = CollectionUtils.filter(alls, e->e.nbParticipationSouhaite-e.nbParticipationRelle>0);
		List<UtilisateurInfo> oks = CollectionUtils.filter(alls, e->e.nbParticipationSouhaite-e.nbParticipationRelle<=0);
		
		addSheet(retards,et,dto,"En retard" , "Liste des utilisateurs en retard pour s'inscrire",true);
		
		addSheet(oks,et,dto,"Inscription ok","Liste des utilisateurs inscrits correctement",false);
		
		addSheet(alls,et,dto,"bilan global","Bilan global de l'inscription des utilisateurs",false);
		
	}

	private UtilisateurInfo createUtilisateurInfo(PeriodePermanenceUtilisateurDTO utilisateurInfo, PeriodePermanenceDTO dto,EntityManager em)
	{
		UtilisateurInfo ui = new UtilisateurInfo();
		
		ui.utilisateur = em.find(Utilisateur.class, utilisateurInfo.idUtilisateur);
		
		ui.nbParticipationSouhaite = utilisateurInfo.nbParticipation;
		ui.nbParticipationRelle = CollectionUtils.count(dto.datePerms, e->e.isInscrit(utilisateurInfo.idUtilisateur));
		
		return ui;
	}


	private void addSheet(List<UtilisateurInfo> oks, ExcelGeneratorTool et, PeriodePermanenceDTO dto, String nomFeuille, String titre, boolean displayMail)
	{
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");	
		
		et.addSheet(nomFeuille, 6, 20);
		et.setColumnWidth(2, 40);
		et.setColumnWidth(3, 10);
		et.setColumnWidth(4, 10);
		et.setColumnWidth(5, 10);
		
		// Ligne de titre
		et.addRow("Période de permanence "+dto.nom,et.grasGaucheNonWrappe);
		et.addRow(titre,et.grasGaucheNonWrappe);
		et.addRow("Extrait le "+df1.format(DateUtils.getDate()),et.grasGaucheNonWrappe);
			
		// Ligne vide
		et.addRow();
		

		// Ligne de Nom Prenom Email ... 
		et.addRow();
		et.setCell(0, "Nom", et.grasGaucheNonWrappeBordure);
		et.setCell(1, "Prénom", et.grasGaucheNonWrappeBordure);
		et.setCell(2, "E mail", et.grasGaucheNonWrappeBordure);
		et.setCell(3, "Souhaité", et.grasCentreBordure);
		et.setCell(4, "Réel", et.grasCentreBordure);
		et.setCell(5, "Delta", et.grasCentreBordure);
	
		for (UtilisateurInfo u : oks)
		{
			et.addRow();
			
			et.setCell(0, u.utilisateur.getNom(), et.grasGaucheNonWrappeBordure);
			et.setCell(1, u.utilisateur.getPrenom(), et.nonGrasGaucheBordure);
			et.setCell(2, u.utilisateur.getEmail(), et.nonGrasGaucheBordure);
			et.setCellQte(3, u.nbParticipationSouhaite,et.grasCentreBordure);
			et.setCellQte(4, u.nbParticipationRelle,et.grasCentreBordure);
			et.setCellQte(5, u.nbParticipationSouhaite-u.nbParticipationRelle,et.grasCentreBordure);
		}
		
		if (displayMail)
		{
			et.addRow();
			et.addRow("Liste des e mails des utilisateurs concernés :",et.grasGaucheNonWrappe);
			et.addRow();
		
			ExcelCellAutoSize as = new ExcelCellAutoSize(5);
			EmailInfo email = UtilisateurUtil.getEmailsInfos(CollectionUtils.select(oks, e->e.utilisateur));
			
			Row currentRow = et.addRow();
			et.setCell(0, email.utilisateurAvecEmail, et.nonGrasGaucheBordure);
			et.mergeCellsRight(0, 6);
			
			int size = (int) (110 * 5.5) ; // 110 caractères , 1 caractère = 5.5 point 
			as.addCell(size, "Arial", 10);
			as.addLine(email.utilisateurAvecEmail);
			
			as.autosize(currentRow);
			
			if (email.nbUtilisateurSansEmail!=0)
			{
				et.addRow();
				et.addRow("Attention : il y a "+email.nbUtilisateurSansEmail+" utilisateurs sans e mail : ",et.grasGaucheNonWrappe);
				et.addRow();
				
				et.addRow();
				et.setCell(0, email.utilisateurSansEmail, et.nonGrasGaucheBordure);
				et.mergeCellsRight(0, 6);
			}
		}
		
		et.addRow();
		et.addRow();
		
		et.addRow();
		et.setCell(0, "Souhaité : ", et.grasGaucheNonWrappe);
		et.setCell(1, "Nombre de permanence que l'amapien doit faire", et.nonGrasGaucheNonWrappe);
		
		et.addRow();
		et.setCell(0, "Réel : ", et.grasGaucheNonWrappe);
		et.setCell(1, "Nombre de permanence où l'amapien est inscrit", et.nonGrasGaucheNonWrappe);
		
		et.addRow();
		et.setCell(0, "Delta : ", et.grasGaucheNonWrappe);
		et.setCell(1, "Souhaité - Réel", et.nonGrasGaucheNonWrappe);
		
		
		
	}

	

	@Override
	public String getFileName(EntityManager em)
	{
		PeriodePermanence pp = em.find(PeriodePermanence.class, idPeriodePermanence);
		return "bilan-permanence-"+pp.nom;
	}

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		PeriodePermanence pp = em.find(PeriodePermanence.class, idPeriodePermanence);
		return "le bilan des inscriptions à la permanence "+pp.nom;
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

	
	static public class UtilisateurInfo 
	{
		public Utilisateur utilisateur;
		
		public int nbParticipationSouhaite;
		
		public int nbParticipationRelle;
		
	}
	
	
	
	
	public static void main(String[] args) throws IOException
	{
		new EGBilanInscriptionPermanence(1L).test();
	}

}
