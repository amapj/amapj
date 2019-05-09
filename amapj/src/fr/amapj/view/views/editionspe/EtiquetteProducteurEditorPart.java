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
 package fr.amapj.view.views.editionspe;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.TextField;

import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.TypEditionSpecifique;
import fr.amapj.model.models.editionspe.etiquette.EtiquetteColJson;
import fr.amapj.model.models.editionspe.etiquette.EtiquetteProducteurJson;
import fr.amapj.service.services.editionspe.EditionSpeDTO;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;
import fr.amapj.view.views.editionspe.bulletinadhesion.BulletinAdhesionEditorPart.Step;

/**
 * Permet la saisie des étiquettes producteurs
 * 
 *
 */
@SuppressWarnings("serial")
public class EtiquetteProducteurEditorPart extends WizardFormPopup
{

	private EtiquetteProducteurJson etiquetteDTO;

	private boolean create;

	public enum Step
	{
		GENERAL, COLONNES , MARGES ;
	}

	/**
	 * 
	 */
	public EtiquetteProducteurEditorPart(boolean create,EditionSpeDTO p)
	{
		this.create = create;
		
		setWidth(80);
		
		if (create)
		{
			popupTitle = "Création d'une étiquette";
			this.etiquetteDTO = new EtiquetteProducteurJson();
		}
		else
		{
			popupTitle = "Modification d'une étiquette";
			
			
			this.etiquetteDTO = (EtiquetteProducteurJson) new EditionSpeService().load(p.id);
			
		}	
		
	
		
		item = new BeanItem<EtiquetteProducteurJson>(this.etiquetteDTO);

	}
	
	@Override
	protected void configure()
	{
		add(Step.GENERAL,()->addFieldGeneral());
		add(Step.COLONNES,()->addFieldColonnes());
		add(Step.MARGES,()->addFieldMarges());
		
	}

	private void addFieldGeneral()
	{
		// Titre
		setStepTitle("les informations générales de l'étiquette");
		
		// Champ 1
		IValidator uniq = new UniqueInDatabaseValidator(EditionSpecifique.class,"nom",etiquetteDTO.getId());
		TextField tf = addTextField("Nom", "nom",uniq);
		
		// Champ 
		addIntegerField("Nb de colonnes", "nbColonne");
		
		addIntegerField("Hauteur d'une ligne (en mm)", "hauteur");
	
	}

	private void addFieldColonnes()
	{
		// Titre
		setStepTitle("la largeur des colonnes en mm");
		
		
		initializeLargeur();
			
	
		CollectionEditor<EtiquetteColJson> f1 = new CollectionEditor<EtiquetteColJson>("Liste des largeurs", (BeanItem) item, "largeurColonnes", EtiquetteColJson.class);
		f1.addColumn("nom","Nom",FieldType.STRING,null);
		f1.addColumn("largeur","Largeur en mm",FieldType.INTEGER,70);
		f1.disableAllButtons();
		binder.bind(f1, "largeurColonnes");
		form.addComponent(f1);
		
	
	}
	
	/**
	 * On fabrique une liste de largeur dont la taille est égale au nombre de colonne
	 * qui a été spécifié, en récupérant si possible les valeurs qui étaient présentes avant 
	 */
	private void initializeLargeur()
	{
		int nbCol = etiquetteDTO.getNbColonne();
		
		List<EtiquetteColJson> res = new ArrayList<>();
		for (int i = 0; i < nbCol; i++)
		{
			EtiquetteColJson dto = createEtiquetteColDto(i,etiquetteDTO.getLargeurColonnes());
			res.add(dto);
		}
		etiquetteDTO.setLargeurColonnes(res);
	}
	


	private EtiquetteColJson createEtiquetteColDto(int i, List<EtiquetteColJson> largeurColonnes)
	{
		EtiquetteColJson dto = new EtiquetteColJson();
		dto.setNom("Largeur de la colonne "+(i+1));
		
		if (i<largeurColonnes.size())
		{
			dto.setLargeur(largeurColonnes.get(i).getLargeur());
		}
		return dto;
	}

	private void addFieldMarges()
	{
		// Titre
		setStepTitle("les marges et les bordures");
		
 
		addIntegerField("Marge droite (en mm)", "margeDroite");
		addIntegerField("Marge gauche (en mm)", "margeGauche");
		addIntegerField("Marge en haut (en mm)", "margeHaut");
		addIntegerField("Marge en bas (en mm)", "margeBas");
		
		addComboEnumField("Dessiner une bordure", "bordure", new NotNullValidator());
		
	}

	

	@Override
	protected void performSauvegarder()
	{
		new EditionSpeService().update(etiquetteDTO, create);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
