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

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.common.ObjectUtils;
import fr.amapj.common.ResourceUtils;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.PageFormat;
import fr.amapj.model.models.editionspe.TypEditionSpecifique;
import fr.amapj.model.models.editionspe.emargement.FormatFeuilleEmargement;
import fr.amapj.model.models.editionspe.emargement.ParametresProduitsJson;
import fr.amapj.model.models.editionspe.emargement.FeuilleEmargementJson;
import fr.amapj.model.models.editionspe.emargement.TypFeuilleEmargement;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.service.services.editionspe.EditionSpeDTO;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;
import fr.amapj.view.views.editionspe.bulletinadhesion.BulletinAdhesionEditorPart.Step;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet la saisie des paramétres des feuilles d'émargement 
 * 
 */
public class FeuilleEmargementEditorPart extends WizardFormPopup
{

	private FeuilleEmargementJson etiquetteDTO;

	private boolean create;

	public enum Step
	{
		GENERAL, MARGES , LARGEURS , COLONNES , CUMUL_PRODUCTEUR ;
	}

	/**
	 * 
	 */
	public FeuilleEmargementEditorPart(boolean create,EditionSpeDTO p)
	{
		this.create = create;
		
		setWidth(80);
		
		if (create)
		{
			popupTitle = "Création d'une feuille d'émargement (mensuelle ou hebdomadaire)";
			this.etiquetteDTO = new FeuilleEmargementJson();
			
			// Positionnement des valeurs par défaut 
			this.etiquetteDTO.setFormat(FormatFeuilleEmargement.LISTE);
			
			//
			this.etiquetteDTO.setPageFormat(PageFormat.A4_PAYSAGE);
			
			//
			this.etiquetteDTO.setLgColNom(40);
			this.etiquetteDTO.setLgColPrenom(30);
			this.etiquetteDTO.setLgColPresence(20);
			this.etiquetteDTO.setLgColnumTel1(30);
			
			//
			this.etiquetteDTO.setLgColProduits(40);
			this.etiquetteDTO.setNomDuContrat(ChoixOuiNon.OUI);
			this.etiquetteDTO.setNomDuProducteur(ChoixOuiNon.NON);
			
		}
		else
		{
			popupTitle = "Modification d'une feuille d'émargement (mensuelle ou hebdomadaire)";
			
			
			this.etiquetteDTO = (FeuilleEmargementJson) new EditionSpeService().load(p.id);
			
		}	
			
		item = new BeanItem<FeuilleEmargementJson>(this.etiquetteDTO);

	}
	
	@Override
	protected void configure()
	{
		add(Step.GENERAL,()->addFieldGeneral());
		add(Step.MARGES,()->addFieldMarges());
		add(Step.LARGEURS,()->addFieldLargeur());
		add(Step.COLONNES,()->addFieldColonnes());
		add(Step.CUMUL_PRODUCTEUR,()->addFieldCumulProducteur());
		
	}

	private void addFieldGeneral()
	{
		// Titre
		setStepTitle("les informations générales de la feuille d'émargement");
		
		IValidator uniq = new UniqueInDatabaseValidator(EditionSpecifique.class,"nom",etiquetteDTO.getId());
		
		addTextField("Nom", "nom",uniq);
		
		addComboEnumField("Type de feuille d'émargement", "typPlanning", new NotNullValidator());
		
		addComboEnumField("Format de feuille d'émargement", "format", new NotNullValidator());
		
		Enum[] enumsToExclude = new Enum[] { RoleList.MASTER };
		
		addComboEnumField("Accessible par ", "accessibleBy", enumsToExclude,new NotNullValidator());
		
		String str = "La documentation sur les feuilles d'émargement est disponible ici :<br/>"
				+ "<a href=\"http://amapj.fr/docs_utilisateur_liste_emargement.html\" target=\"_blank\">http://amapj.fr/docs_utilisateur_liste_emargement.html</a>";
		
		addLabel(str, ContentMode.HTML);
		
	}
	
	
	private void addFieldMarges()
	{
		// Titre
		setStepTitle("les informations de marges et de disposition");
		
		addIntegerField("Marge droite (en mm)", "margeDroite");
		addIntegerField("Marge gauche (en mm)", "margeGauche");
		addIntegerField("Marge en haut (en mm)", "margeHaut");
		addIntegerField("Marge en bas (en mm)", "margeBas");
		
		addComboEnumField("Disposition de la page","pageFormat",new NotNullValidator());	
	}

	
	
	
	
	private void addFieldLargeur()
	{
		// Titre
		setStepTitle("les informations de largeur des colonnes Nom, prénom, ..");		
		
		addIntegerField("Largeur (en mm) de la colonne Nom", "lgColNom");
		
		addIntegerField("Largeur (en mm) de la colonne Prénom", "lgColPrenom");
		
		addIntegerField("Largeur (en mm) de la colonne Présence", "lgColPresence");
		
		addIntegerField("Largeur (en mm) de la colonne Tel1", "lgColnumTel1");
		
		addIntegerField("Largeur (en mm) de la colonne Tel2", "lgColnumTel2");
		
		addIntegerField("Largeur (en mm) de la colonne Commentaire", "lgColCommentaire");
		
		addIntegerField("Hauteur (en mm) des lignes (ajustement automatique si 0)", "hauteurLigne");	
	}

	private void addFieldColonnes()
	{
		if (etiquetteDTO.getFormat()==FormatFeuilleEmargement.GRILLE)
		{
			addFieldColonnesGrille();
		}
		else
		{
			addFieldColonnesListe();
		}
	}
	
	private void addFieldColonnesGrille()
	{
		// Titre
		setStepTitle("la description des colonnes (format grille)");
		
		addComboEnumField("Contenu des cellules ", "contenuCellule", new NotNullValidator());
		
		CollectionEditor<ParametresProduitsJson> f1 = new CollectionEditor<ParametresProduitsJson>("Liste des colonnes", (BeanItem) item, "parametresProduits", ParametresProduitsJson.class);
		f1.addSearcherColumn("idProduit","Produit",FieldType.SEARCHER,null,SearcherList.PRODUIT_ALL,null);
		f1.addColumn("titreColonne","Titre de la colonne",FieldType.STRING,"");
		f1.addColumn("largeurColonne","Largeur en mm",FieldType.INTEGER,20);
		binder.bind(f1, "parametresProduits");
		form.addComponent(f1);
	}
	

	private void addFieldColonnesListe()
	{
		// Titre
		setStepTitle("la description des colonnes (format liste)");

		addIntegerField("Largeur (en mm) de la colonne Produits", "lgColProduits");
		
		addComboEnumField("Imprimer le nom du contrat au dessus des produits", "nomDuContrat",  new NotNullValidator());
		
		addComboEnumField("Imprimer le nom du producteur au dessus des produits", "nomDuProducteur",  new NotNullValidator());
		
		addComboEnumField("Imprimer la liste des produits", "detailProduits",  new NotNullValidator());
	}
	
	
	private void addFieldCumulProducteur()
	{
		if (etiquetteDTO.getFormat()==FormatFeuilleEmargement.GRILLE)
		{
			addFieldCumulProducteurGrille();
		}
		else
		{
			addFieldCumulProducteurListe();
		}
	}
	
	private void addFieldCumulProducteurGrille()
	{
		// Titre
		setStepTitle("cumul des quantités producteur");
		
		
		addLabel("Non disponible pour le moment. Appuyer sur continuer", ContentMode.HTML);
		
	}
	

	private void addFieldCumulProducteurListe()
	{
		// Titre
		setStepTitle("cumul des quantités producteur");

		addComboEnumField("Imprimer en tete du document un cumul des quantités livrées pour chaque producteur", "listeAffichageCumulProducteur",  new NotNullValidator());
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
