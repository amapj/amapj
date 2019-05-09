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
 package fr.amapj.view.views.gestioncontrat.editorpart;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.model.models.contrat.modele.extendparam.MiseEnFormeGraphique;
import fr.amapj.service.services.gestioncontrat.ExtPModeleContratService;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.fieldlink.ClassicFieldLink;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Permet de modifier les paramètres de mise en forme des modeles de contrat
 * 
 *
 */
public class MiseEnFormeModeleContratEditorPart extends WizardFormPopup
{
	private MiseEnFormeGraphique miseEnForme;
	
	private Long idModeleContrat;
	
	private ModeleContratDTO modelecontratDto;

	public enum Step
	{
		PAIEMENT_STD;
	}

	/**
	 * 
	 */
	public MiseEnFormeModeleContratEditorPart(Long idModeleContrat)
	{
		this.idModeleContrat = idModeleContrat;
		
		setWidth(80);
		popupTitle = "Mise en forme d'un modèle de contrat";
		
		// Chargement de l'objet  à modifier
		modelecontratDto = new GestionContratService().loadModeleContrat(idModeleContrat);
		
		miseEnForme = new ExtPModeleContratService().loadMiseEnFormeGraphique(idModeleContrat);
		
		item = new BeanItem<MiseEnFormeGraphique>(miseEnForme);

	}
	
	@Override
	protected void configure()
	{
		add(Step.PAIEMENT_STD,()->addFieldPaiement());
	}

	
	
	

	private void addFieldPaiement()
	{	
		setStepTitle("Mise en forme du popup de paiement");
	
		if (modelecontratDto.gestionPaiement==GestionPaiement.NON_GERE)
		{
			addLabel("Pas de parametrage possible en mode PAS DE GESTION DES PAIEMENTS", ContentMode.HTML);
		}
		else
		{
		
			addBlocTexteParametrable("Chèques à remettre à .... suivi du nom d'un des référents", "paiementStdLib1");
		
			addBlocTexteParametrable("Une proposition de paiement a été calculée et est affichée ...", "paiementStdLib2");
		}
	}
	
	
	
	
	private void addBlocTexteParametrable(String lib,String field)
	{
		addLabel("Texte \""+lib	+ "\"", ContentMode.HTML);
		
		ClassicFieldLink f1 = new ClassicFieldLink();
		f1.box = addComboEnumField("Modifier ce texte", field+"Modifier",new NotNullValidator());
		f1.ckEditor = addCKEditorFieldForLabel("Remplacer ce texte par", field);
		f1.doLink();
	}
	


	@Override
	protected void performSauvegarder()
	{
		new ExtPModeleContratService().saveMiseEnFormeGraphique(idModeleContrat,miseEnForme);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
