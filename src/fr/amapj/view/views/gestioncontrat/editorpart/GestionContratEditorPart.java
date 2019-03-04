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
 package fr.amapj.view.views.gestioncontrat.editorpart;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

import fr.amapj.common.DateUtils;
import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.service.services.gestioncontrat.DateModeleContratDTO;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.LigneContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.produit.ProduitService;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.CollectionNoDuplicates;
import fr.amapj.view.engine.popup.formpopup.validator.CollectionSizeValidator;
import fr.amapj.view.engine.popup.formpopup.validator.ColumnNotNull;
import fr.amapj.view.engine.popup.formpopup.validator.DateRangeValidator;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.StringLengthValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;
import fr.amapj.view.engine.popup.formpopup.validator.ValidatorHolder;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet uniquement de creer des contrats
 */
public class GestionContratEditorPart extends WizardFormPopup
{

	protected ModeleContratDTO modeleContrat;

	private boolean creerAPartirDeMode;
	
	private Searcher prod;
	
	private List<Producteur> allowedProducteurs;

	static public enum Step
	{
		INFO_GENERALES, DATE_LIVRAISON, DATE_FIN_INSCRIPTION , CHOIX_PRODUITS, TYPE_PAIEMENT , DETAIL_PAIEMENT;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES, ()->drawInfoGenerales(),()->checkInfoGenerales());
		add(Step.DATE_LIVRAISON, ()->drawDateLivraison(),()->checkDateLivraison());
		add(Step.DATE_FIN_INSCRIPTION, ()->drawFinInscription());
		add(Step.CHOIX_PRODUITS, ()->drawChoixProduits());
		add(Step.TYPE_PAIEMENT , ()->drawTypePaiement());
		add(Step.DETAIL_PAIEMENT , ()->drawDetailPaiement());
	}
	
	
	public GestionContratEditorPart()
	{
	}

	/**
	 * 
	 */
	public GestionContratEditorPart(Long id,List<Producteur> allowedProducteurs)
	{
		this.allowedProducteurs = allowedProducteurs;
		
		setWidth(80);
		popupTitle = "Création d'un contrat";

		// Chargement de l'objet à créer
		// Si id est non null, alors on se sert de ce contenu pour précharger
		// les champs
		if (id == null)
		{
			modeleContrat = new ModeleContratDTO();
			modeleContrat.frequence = FrequenceLivraison.UNE_FOIS_PAR_SEMAINE;
			modeleContrat.gestionPaiement = GestionPaiement.NON_GERE;
			modeleContrat.nature = NatureContrat.LIBRE;
			creerAPartirDeMode = false;
		}
		else
		{
			modeleContrat = new GestionContratService().loadModeleContrat(id);
			modeleContrat.nom = modeleContrat.nom + "(Copie)";
			modeleContrat.id = null;
			modeleContrat.dateLivs.clear();
			creerAPartirDeMode = true;
		}
		item = new BeanItem<ModeleContratDTO>(modeleContrat);

	}
	
	

	private void drawInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales");
		
		// Liste des validators
		IValidator len_1_100 = new StringLengthValidator(1, 100);
		IValidator len_1_255 = new StringLengthValidator(1, 255);
		IValidator uniq = new UniqueInDatabaseValidator(ModeleContrat.class,"nom",null);
		IValidator notNull = new NotNullValidator();
		IValidator prodValidator = new ProducteurAvecProduitValidator();
		
		
		
		// Champ 1
		addTextField("Nom du contrat", "nom",len_1_100,uniq);


		// Champ 2
		addTextField("Description du contrat", "description",len_1_255);
		
		//
		addComboEnumField("Nature du contrat", "nature",notNull);

		// Champ 3
		prod = addSearcher("Producteur", "producteur", SearcherList.PRODUCTEUR, allowedProducteurs,notNull,prodValidator);
		// On ne peut pas changer le producteur quand on crée à partir d'un
		// autre contrat
		if (creerAPartirDeMode == true)
		{
			prod.setEnabled(false);
		}
		//
		addComboEnumField("Fréquence des livraisons", "frequence",notNull);

	}
	
	private String checkInfoGenerales()
	{
		if ((modeleContrat.nature==NatureContrat.CARTE_PREPAYEE) && (modeleContrat.frequence==FrequenceLivraison.UNE_SEULE_LIVRAISON))
		{
			return "Il n'est pas possible de faire un contrat Carte prépayée avec une seule date de livraison";
		}
		return null;
	}
	

	private void drawDateLivraison()
	{
		// Titre
		setStepTitle("les dates de livraison");
		
		// Liste des validators
		IValidator notNull = new NotNullValidator();

		
		if (modeleContrat.frequence==FrequenceLivraison.UNE_SEULE_LIVRAISON)
		{
			addDateField("Date de la livraison", "dateDebut",notNull);
		}
		else if (modeleContrat.frequence==FrequenceLivraison.AUTRE)
		{
			IValidator size = new CollectionSizeValidator<DateModeleContratDTO>(1, null);
			IValidator noDuplicates = new CollectionNoDuplicates<DateModeleContratDTO>(e->e.dateLiv);
								
			//
			addCollectionEditorField("Liste des dates", "dateLivs", DateModeleContratDTO.class,size,noDuplicates);
			addColumn("dateLiv", "Date",FieldType.DATE, null,new ColumnNotNull<DateModeleContratDTO>(e->e.dateLiv));			
		}
		else
		{
			addDateField("Date de la première livraison", "dateDebut",notNull);
			addDateField("Date de la dernière livraison", "dateFin",notNull);
		}
	}
	
	
	/**
	 * Retourne null si tout est ok, un message sinon
	 * @return
	 */
	private String checkDateLivraison()
	{
		if (modeleContrat.frequence==FrequenceLivraison.UNE_SEULE_LIVRAISON)
		{
			// C'est toujours bon 
			return null;
		}
		else if (modeleContrat.frequence==FrequenceLivraison.AUTRE)
		{
			// C'est toujours bon 
			return null;
		}
		else
		{
			if (modeleContrat.dateDebut.after(modeleContrat.dateFin))
			{
				return "La date de début doit être avant la date de fin ";
			}
			else
			{
				return null;
			}
		}
	}
	
	
	
	
	protected void drawFinInscription()
	{
		if (modeleContrat.nature==NatureContrat.CARTE_PREPAYEE)
		{
			//
			modeleContrat.dateFinInscription = null;
			
			// Titre
			setStepTitle("Contrat Carte prépayée - Délai pour modification du contrat");
			
			addIntegerField("Délai en jour pour modification du contrat avant livraison", "cartePrepayeeDelai");
			
			addLabel("Votre contrat est de type Carte prepayée, c'est à dire que l'adhérent peut modifier le contrat même après le début des livraisons.<br/>"
					+ "Ce champ vous permet d'indiquer le délai entre la dernière modification possible et la livraison<br/>"
					+ "Par exemple, si les livraisons sont le samedi et si vous mettez 2 dans ce champ, alors l'adhérent pourra alors modifier "
					+ "son contrat pour cette livraison jusqu'au mercredi soir minuit", ContentMode.HTML);
		}
		else
		{	
			// Titre
			setStepTitle("la date de fin des inscriptions");
			
			IValidator notNull = new NotNullValidator();
			Date firstLiv = getFirstLiv();
			IValidator dateRange = new DateRangeValidator(null, firstLiv);
			
			// Champ 4
			addDateField("Date de fin des inscriptions", "dateFinInscription",notNull,dateRange);
			
			addLabel("Cette date doit obligatoirement être avant la date de la première livraison", ContentMode.HTML);
		}
	}
	
	
	
	/**
	 * Calcule la date de la première livraison
	 */
	private Date getFirstLiv()
	{
		if (modeleContrat.frequence==FrequenceLivraison.AUTRE)
		{
			// On retourne la plus petite date saisie
			Collections.sort(modeleContrat.dateLivs,(p1,p2)->p1.dateLiv.compareTo(p2.dateLiv));
			return modeleContrat.dateLivs.get(0).dateLiv;
		}
		else
		{
			return modeleContrat.dateDebut;
		}
	}


	private void drawChoixProduits()
	{
		// Si liste vide
		Long idProducteur = (Long) prod.getConvertedValue();
		if (modeleContrat.produits.size()==0 && idProducteur!=null)
		{
			modeleContrat.produits.addAll(new GestionContratService().getInfoProduitModeleContrat(idProducteur));
		}
		
		
		// Titre
		setStepTitle("la liste des produits et des prix");
				
		// 
		
		IValidator size = new CollectionSizeValidator<LigneContratDTO>(1, null);
		IValidator noDuplicates = new CollectionNoDuplicates<LigneContratDTO>(e->e.produitId,e->new ProduitService().prettyString(e.produitId));
							
		//
		addCollectionEditorField("Produits", "produits", LigneContratDTO.class,size,noDuplicates);
		
		addColumnSearcher("produitId", "Nom du produit",FieldType.SEARCHER, null,SearcherList.PRODUIT,prod,new ColumnNotNull<LigneContratDTO>(e->e.produitId));
		addColumn("prix", "Prix du produit", FieldType.CURRENCY, null,new ColumnNotNull<LigneContratDTO>(e->e.prix));	
		
	}


	
	
	

	private void drawTypePaiement()
	{
		if (modeleContrat.nature==NatureContrat.CARTE_PREPAYEE)
		{
			// Titre
			setStepTitle("Contrat Carte prépayée - gestion du paiement");
						
			addLabel("Votre contrat est de type Carte prépayée, il n'y a pas de gestion des paiements possible pour le moment. Vous pouvez juste définir un message avec les indications sur le paiement sur la page suivante.", ContentMode.HTML);
			
			modeleContrat.gestionPaiement = GestionPaiement.NON_GERE;
		}
		else
		{		
			setStepTitle("genéralités sur le paiement");
			
			IValidator notNull = new NotNullValidator();
			
			addComboEnumField("Gestion des paiements", "gestionPaiement",notNull);
		}
	}
	
	
	
	private void drawDetailPaiement()
	{
		setStepTitle("les informations sur le paiement");
		
		// Liste des validators
		IValidator notNull = new NotNullValidator();
		IValidator len_0_2048 = new StringLengthValidator(0, 2048);
		IValidator len_0_255 = new StringLengthValidator(0, 255);

		
		if (modeleContrat.gestionPaiement==GestionPaiement.GESTION_STANDARD)
		{	
			addTextField("Ordre du chèque", "libCheque",len_0_255);
			
			if (modeleContrat.frequence==FrequenceLivraison.UNE_SEULE_LIVRAISON)
			{
				PopupDateField p = addDateField("Date de remise du chèque", "dateRemiseCheque",notNull);
				p.setValue(modeleContrat.dateDebut);
			}
			else
			{
				PopupDateField p = addDateField("Date de remise des chèques", "dateRemiseCheque",notNull);
				p.setValue(modeleContrat.dateFinInscription);
				
				p = addDateField("Date du premier paiement", "premierCheque",notNull);
				p.setValue(proposeDatePremierPaiement());
				
				p = addDateField("Date du dernier paiement", "dernierCheque",notNull);
				p.setValue(proposeDateDernierPaiement()); 
			}
		}
		else
		{
			TextField f = (TextField) addTextField("Texte affiché dans la fenêtre paiement", "textPaiement",len_0_2048);
			f.setMaxLength(2048);
			f.setHeight(5, Unit.CM);
		}
	}

	

	private Date proposeDatePremierPaiement()
	{
		if (modeleContrat.dateDebut!=null)
		{
			return DateUtils.firstDayInMonth(modeleContrat.dateDebut); 
		}
		
		if (modeleContrat.dateLivs.size()>0)
		{
			return DateUtils.firstDayInMonth(modeleContrat.dateLivs.get(0).dateLiv);
		}
		
		return null;
	}
	
	
	private Date proposeDateDernierPaiement()
	{
		if (modeleContrat.dateFin!=null)
		{
			return DateUtils.firstDayInMonth(modeleContrat.dateFin); 
		}
		
		if (modeleContrat.dateLivs.size()>0)
		{
			return DateUtils.firstDayInMonth(modeleContrat.dateLivs.get(modeleContrat.dateLivs.size()-1).dateLiv);
		}
		
		return null;
	}
	
	

	@Override
	protected void performSauvegarder()
	{
		new GestionContratService().saveNewModeleContrat(modeleContrat);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	/**
	 * Validateur qui vérifie que le producteur posséde au moins un produit
	 */
	public class ProducteurAvecProduitValidator implements IValidator
	{

		@Override
		public void performValidate(Object value, ValidatorHolder a)
		{
			Long p = (Long) value;
			if (p!=null)
			{
				List<LigneContratDTO> ligs = new GestionContratService().getInfoProduitModeleContrat(p);
				if (ligs.size()==0)
				{
					a.addMessage("Ce producteur ne posséde pas de produits.");
					a.addMessage("Pour pouvoir créer un contrat pour ce producteur");
					a.addMessage("Vous devez d'abord aller dans le menu \"Gestion des produits\",");
					a.addMessage("et indiquer la liste des produits faits par ce producteur.");
				}
			}
		}

		@Override
		public boolean canCheckOnFly()
		{
			return true;
		}
		
		@Override
		public AbstractField[] revalidateOnChangeOf()
		{
			return null;
		}
	}
	
	
}
