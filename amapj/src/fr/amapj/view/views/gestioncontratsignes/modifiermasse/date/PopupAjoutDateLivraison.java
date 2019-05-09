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
 package fr.amapj.view.views.gestioncontratsignes.modifiermasse.date;

import java.util.Date;
import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.service.services.gestioncontrat.DateModeleContratDTO;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontratsigne.update.GestionContratSigneUpdateService;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.CollectionNoDuplicates;
import fr.amapj.view.engine.popup.formpopup.validator.CollectionNotIn;
import fr.amapj.view.engine.popup.formpopup.validator.CollectionSizeValidator;
import fr.amapj.view.engine.popup.formpopup.validator.ColumnNotNull;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.date.PopupDeplacerDateLivraison.Step;

/**
 * Permet d'ajouter des dates 
 */
public class PopupAjoutDateLivraison extends WizardFormPopup
{
	
	private ModeleContratDTO modeleContratDTO;
	
	private List<Date> existingDateLivs;
	
	static public enum Step
	{
		INFO_GENERALES , SAISIE_DATES;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.SAISIE_DATES, ()->saisieDate());
	}
	

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	/**
	 * 
	 */
	public PopupAjoutDateLivraison(Long id)
	{
		super();
		popupTitle = "Ajouter des dates de livraison à un contrat";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		modeleContratDTO = new GestionContratService().loadModeleContrat(id);
		
		// On sauvegarde dans une autre variable la liste des dates existantes
		existingDateLivs = CollectionUtils.select(modeleContratDTO.dateLivs, e->e.dateLiv);
		
		
		// On efface la liste des dates déjà présentes dans le dto  
		modeleContratDTO.dateLivs.clear();
		
		item = new BeanItem<ModeleContratDTO>(modeleContratDTO);
		
	}
	
	
	private void addFieldInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales.");
		
		String str = 	"Cet outil va vous permettre d'ajouter une ou plusieurs dates de livraison, pour tous les adhérents à ce contrat.</br>"+
						"<br/>"+
						"Par contre, cet outil ne modifie pas les quantités commandées. Vous devez ensuite ajouter manuellement les bonnes quantités sur les nouvelles dates.<br/>"+
						"Il faut ensuite également mettre à jour les paiements manuellement.<br/>";
						
										
		
		addLabel(str, ContentMode.HTML);
		

	}
	
	
	
	/**
	 *  
	 */
	private void saisieDate()
	{
		// Titre
		setStepTitle("les dates à ajouter");
		
		//
		IValidator size = new CollectionSizeValidator<DateModeleContratDTO>(1, null);
		IValidator noDuplicates = new CollectionNoDuplicates<DateModeleContratDTO>(e->e.dateLiv);
		IValidator notIn = new CollectionNotIn<DateModeleContratDTO,Date>(e->e.dateLiv,existingDateLivs,e->"Vous ne pouvez pas ajouter la date "+FormatUtils.getStdDate().format(e.dateLiv)+" car c'est déjà une date livraison de ce contrat.");
				
		//
		addCollectionEditorField("Liste des dates", "dateLivs", DateModeleContratDTO.class,size,noDuplicates,notIn);
		addColumn("dateLiv", "Date",FieldType.DATE, null,new ColumnNotNull<DateModeleContratDTO>(e->e.dateLiv));
		
		
	}
	



	protected void performSauvegarder()
	{	
		// Sauvegarde du contrat
		new GestionContratSigneUpdateService().addDates(modeleContratDTO);
	}
	
}
