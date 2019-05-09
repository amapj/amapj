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
 package fr.amapj.view.views.parametres.paramecran;

import java.util.Arrays;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;

import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.param.paramecran.ChoixImpressionBilanLivraison;
import fr.amapj.model.models.param.paramecran.PEMesLivraisons;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.fieldlink.FieldLink;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet la saisie des paramètres de l'écran "mes livraisons"
 * 
 */
public class PEMesLivraisonsEditorPart extends WizardFormPopup
{
	private PEMesLivraisons pe;

	public enum Step
	{
		GENERALITES , IMPRESSION_PAGE , IMPRESSION_MENSUEL ,IMPRESSION_TRIMESTRE ;
	}

	/**
	 * 
	 */
	public PEMesLivraisonsEditorPart()
	{
		pe = (PEMesLivraisons) new ParametresService().loadParamEcran(MenuList.MES_LIVRAISONS);
		
		setWidth(80);
		popupTitle = "Paramètrage de l'écran \""+pe.getMenu().getTitle()+"\"";
		
		item = new BeanItem<PEMesLivraisons>(this.pe);

	}
	
	@Override
	protected void configure()
	{
		add(Step.GENERALITES,()->addGeneralites());
		add(Step.IMPRESSION_PAGE,()->addFieldImpressionPageCourante());
		add(Step.IMPRESSION_MENSUEL,()->addFieldImpressionMensuel());
		add(Step.IMPRESSION_TRIMESTRE,()->addFieldImpressionTrimestre());
	
	}

	private void addGeneralites()
	{
		// Titre
		setStepTitle("Généralités");
		
		addComboEnumField("Mode d'affichage des livraisons", "modeAffichage",  new NotNullValidator());
			
		
	}
	
	private void addFieldImpressionPageCourante()
	{
		// Titre
		setStepTitle("Impression des bilans de livraison de la page courante");
		
		ComboBox b1 = addComboEnumField("Il est possible d'imprimer les livraisons affichées sur la page courante", "pageCouranteImpressionRecap",  new NotNullValidator());
		
		FieldLink f1 = new FieldLink(validatorManager,Arrays.asList(ChoixOuiNon.OUI),b1);
			
		ComboBox b2 = addComboEnumField("Format du fichier", "pageCouranteFormat",  f1.getValidator());
		
		FieldLink f2 = new FieldLink(validatorManager,Arrays.asList(ChoixImpressionBilanLivraison.PDF,ChoixImpressionBilanLivraison.TABLEUR_ET_PDF),b2);
		f2.setParent(f1);
		f2.addField(addSearcher("Contenu du fichier PDF", "pageCourantePdfEditionId", SearcherList.BILAN_LIVRAISON ,null,f2.getValidator()));

		// Un seul doLink() sur le pere est suffisant 
		f1.doLink();
	}

	
	
	
	
	private void addFieldImpressionMensuel()
	{
		// Titre
		setStepTitle("Impression des bilans mensuels de livraison");
		
		ComboBox b1 = addComboEnumField("Il est possible d'imprimer des bilans mensuels des livraisons", "mensuelImpressionRecap",  new NotNullValidator());
		
		FieldLink f1 = new FieldLink(validatorManager,Arrays.asList(ChoixOuiNon.OUI),b1);
		
		f1.addField(addIntegerField("Les bilans mensuels sont disponibles x jours avant leur date de début.  x = ", "mensuelNbJourAvant"));
		
		f1.addField(addIntegerField("Les bilans mensuels sont disponibles y jours après leur date de fin.  y = ", "mensuelNbJourApres"));
			
		ComboBox b2 = addComboEnumField("Format du fichier", "mensuelFormat",  f1.getValidator());
		
		FieldLink f2 = new FieldLink(validatorManager,Arrays.asList(ChoixImpressionBilanLivraison.PDF,ChoixImpressionBilanLivraison.TABLEUR_ET_PDF),b2);
		f2.setParent(f1);
		f2.addField(addSearcher("Contenu du fichier PDF", "mensuelPdfEditionId", SearcherList.BILAN_LIVRAISON ,null,f2.getValidator()));
	
	
		// Un seul doLink() sur le pere est suffisant 
		f1.doLink();
		
	}
	
	
	private void addFieldImpressionTrimestre()
	{
		// Titre
		setStepTitle("Impression des bilans trimestriels de livraison");
		
		ComboBox b1 = addComboEnumField("Il est possible d'imprimer des bilans trimestriels des livraisons", "trimestreImpressionRecap",  new NotNullValidator());
		
		FieldLink f1 = new FieldLink(validatorManager,Arrays.asList(ChoixOuiNon.OUI),b1);
	
		f1.addField(addIntegerField("Les bilans trimestriels sont disponibles x jours avant leur date de début.  x = ", "trimestreNbJourAvant"));
		
		f1.addField(addIntegerField("Les bilans trimestriels sont disponibles y jours après leur date de fin.  y = ", "trimestreNbJourApres"));
		
		ComboBox b2 = addComboEnumField("Format du fichier", "trimestreFormat",  f1.getValidator());
		
		FieldLink f2 = new FieldLink(validatorManager,Arrays.asList(ChoixImpressionBilanLivraison.PDF,ChoixImpressionBilanLivraison.TABLEUR_ET_PDF),b2);
		f2.setParent(f1);
		f2.addField(addSearcher("Contenu du fichier PDF", "trimestrePdfEditionId", SearcherList.BILAN_LIVRAISON ,null,f2.getValidator()));
		
	
		
		// Un seul doLink() sur le pere est suffisant 
		f1.doLink();
		
	}
	

	@Override
	protected void performSauvegarder()
	{
		new ParametresService().update(pe);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
