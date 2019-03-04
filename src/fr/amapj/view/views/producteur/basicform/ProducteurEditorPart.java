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
 package fr.amapj.view.views.producteur.basicform;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.TextArea;

import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.service.services.producteur.ProdUtilisateurDTO;
import fr.amapj.service.services.producteur.ProducteurDTO;
import fr.amapj.service.services.producteur.ProducteurService;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.fieldlink.ClassicFieldLink;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * La fiche producteur 
 * 
 *
 */
public class ProducteurEditorPart extends WizardFormPopup
{

	private ProducteurDTO producteurDTO;

	private boolean create;

	public enum Step
	{
		GENERAL, DOCUMENTS , UTILISATEUR , REFERENTS ;
	}

	/**
	 * 
	 */
	public ProducteurEditorPart(boolean create,ProducteurDTO p)
	{
		this.create = create;
		
		setWidth(80);
		setHeight("90%");
		
		if (create)
		{
			popupTitle = "Création d'un producteur";
			this.producteurDTO = new ProducteurDTO();
			
			// Valeur par défaut 
			this.producteurDTO.feuilleDistributionGrille = ChoixOuiNon.OUI;
			this.producteurDTO.feuilleDistributionListe = ChoixOuiNon.NON;
			this.producteurDTO.feuilleDistributionEtiquette  =ChoixOuiNon.NON;
			
			this.producteurDTO.contratEngagement = ChoixOuiNon.NON;
			
			this.producteurDTO.delaiModifContrat = 3;
			
		}
		else
		{
			popupTitle = "Modification d'un producteur";
			this.producteurDTO = p;
		}	
		
	
		
		item = new BeanItem<ProducteurDTO>(this.producteurDTO);

	}
	
	@Override
	protected void configure()
	{
		add(Step.GENERAL,()->addFieldGeneral());
		add(Step.DOCUMENTS,()->addFieldDocuments());
		add(Step.UTILISATEUR,()->addFieldUtilisateur(),()->checkFieldUtilisateur());
		add(Step.REFERENTS,()->addFieldReferents(),()->checkFieldReferents());
	}

	private void addFieldGeneral()
	{
		// Titre
		setStepTitle("les informations générales du producteur");
		
		// Champ 1
		IValidator uniq = new UniqueInDatabaseValidator(Producteur.class,"nom",producteurDTO.id);
		IValidator notNull = new NotNullValidator();
		addTextField("Nom", "nom",uniq,notNull);
		
		TextArea f =  addTextAeraField("Description", "description");
		f.setMaxLength(20480);
		f.setHeight(5, Unit.CM);

	}
	
	
	
	private void addFieldDocuments()
	{
		// Titre
		setStepTitle("les documents de ce producteur");
	
		
		addLabel("<b>La feuille de distribution producteur</b>", ContentMode.HTML);
		
		addComboEnumField("La feuille de distribution contient un onglet avec les produits à livrer en tableau", "feuilleDistributionGrille",  new NotNullValidator());
		
		addComboEnumField("La feuille de distribution contient un onglet avec les produits à livrer en liste", "feuilleDistributionListe",  new NotNullValidator());
		
		
		ClassicFieldLink fieldLink = new ClassicFieldLink();
		
		fieldLink.box = addComboEnumField("La feuille de distribution contient un onglet avec les étiquettes des produits ", "feuilleDistributionEtiquette",  new NotNullValidator());
		
		fieldLink.searcher = addSearcher("Type des étiquettes", "idEtiquette", SearcherList.ETIQUETTE ,null,fieldLink.getValidator());
		
		fieldLink.doLink();
		
		
		
		addLabel("<b>Le contrat d'engagement</b>", ContentMode.HTML);
		
		ClassicFieldLink f2 = new ClassicFieldLink();
		
		f2.box = addComboEnumField("Ce producteur utilise des contrats d'engagement", "contratEngagement",  new NotNullValidator());
		
		f2.searcher = addSearcher("Contrat d'engagement", "idEngagement", SearcherList.ENGAGEMENT ,null,f2.getValidator());
		
		f2.textField = addTextField("Identification du producteur sur le contrat d'engagement", "libContrat");
	
		f2.doLink();
		
		addLabel("<b>L'envoi automatique des feuilles de distribution au producteur</b>", ContentMode.HTML);
		
		addIntegerField("Délai en jours entre l'envoi de la feuille de distribution par mail et la livraison", "delaiModifContrat");
		
		String str = 	"Exemple :<br>" +
						"Si les livraisons ont lieu le jeudi et si vous mettez 3 dans le champ précédent<br>"+
						"alors le producteur recevra le mail avec la feuille de distribution le lundi à 2h00 du matin<br>";
		
		addLabel(str, ContentMode.HTML);
	
	}

	private void addFieldUtilisateur()
	{
		// Titre
		setStepTitle("les noms des producteurs");
		
		addLabel("Vous pouvez laisser cette liste vide dans un premier temps", ContentMode.HTML);
		
		CollectionEditor<ProdUtilisateurDTO> f1 = new CollectionEditor<ProdUtilisateurDTO>("Liste des producteurs", (BeanItem) item, "utilisateurs", ProdUtilisateurDTO.class);
		f1.addSearcherColumn("idUtilisateur", "Nom du producteur",FieldType.SEARCHER, null,SearcherList.UTILISATEUR_ACTIF,null);
		f1.addColumn("etatNotification","Notification par mail",FieldType.CHECK_BOX,true);
		binder.bind(f1, "utilisateurs");
		form.addComponent(f1);
	}
	
	private String checkFieldUtilisateur()
	{
		if (isCorrectFieldUtilisateur()==true)
		{
			return null;
		}
		
		return 	"Il y a des erreurs dans la saisie des noms des producteurs<br/>"+
				"Vous ne devez pas avoir de lignes vides<br/>"+
				"Vous devez bien choisir dans la liste des noms proposés";
	}
	
	private boolean isCorrectFieldUtilisateur()
	{
		List<ProdUtilisateurDTO> us = producteurDTO.utilisateurs;
		for (ProdUtilisateurDTO lig : us)
		{
			if (lig.idUtilisateur==null)
			{
				return false;
			}
		}
		return true;
	}
	
	
	private void addFieldReferents()
	{	
		// Titre
		setStepTitle("les noms des référents");
		
		addLabel("Vous pouvez laisser cette liste vide dans un premier temps", ContentMode.HTML);
		
		CollectionEditor<ProdUtilisateurDTO> f1 = new CollectionEditor<ProdUtilisateurDTO>("Liste des référents", (BeanItem) item, "referents", ProdUtilisateurDTO.class);
		f1.addSearcherColumn("idUtilisateur", "Nom des référents",FieldType.SEARCHER, null,SearcherList.UTILISATEUR_ACTIF,null);
		binder.bind(f1, "referents");
		form.addComponent(f1);
	
	}
	
	private String checkFieldReferents()
	{
		if (isCorrectFieldReferents()==true)
		{
			return null;
		}
		
		return 	"Il y a des erreurs dans la saisie des noms des référents<br/>"+
				"Vous ne devez pas avoir de lignes vides<br/>"+
				"Vous devez bien choisir dans la liste des noms proposés";
	}
	
	
	private boolean isCorrectFieldReferents()
	{
		List<ProdUtilisateurDTO> us = producteurDTO.referents;
		for (ProdUtilisateurDTO lig : us)
		{
			if (lig.idUtilisateur==null)
			{
				return false;
			}
		}
		return true;
	}
	


	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		new ProducteurService().update(producteurDTO, create);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
