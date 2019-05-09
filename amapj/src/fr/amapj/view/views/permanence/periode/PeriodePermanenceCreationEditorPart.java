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
 package fr.amapj.view.views.permanence.periode;

import java.util.ArrayList;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import fr.amapj.model.models.permanence.periode.NaturePeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanence;
import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;
import fr.amapj.service.services.permanence.periode.FrequencePermanence;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.StringLengthValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet uniquement de creer des contrats
 */
public class PeriodePermanenceCreationEditorPart extends WizardFormPopup
{

	protected PeriodePermanenceDTO dto;
	
	private ComplexTableBuilder<PeriodePermanenceUtilisateurDTO> builder;

	static public enum Step
	{
		INFO_GENERALES, DATE_DEBUT_FIN, DETAIL_DATE1 , DETAIL_DATE2 , DATE_FIN_INSCRIPTION , TRANSITION , DEPART_CHOIX_UTILISATEURS,  CHOIX_UTILISATEURS , NB_PARTICIPATION_PAR_UTILISATEURS , AJUSTER , BILAN;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES, ()->drawInfoGenerales(),()->checkInfoGenerales());
		add(Step.DATE_DEBUT_FIN, ()->drawDebutFin(),()->checkDebutFin());
		add(Step.DETAIL_DATE1, ()->drawDetailDate1());
		add(Step.DETAIL_DATE2, ()->drawDetailDate2(),()->checkDetailDate2());
		add(Step.DATE_FIN_INSCRIPTION, ()->drawFinInscription());
		add(Step.TRANSITION, ()->drawTransition());
		add(Step.DEPART_CHOIX_UTILISATEURS,()->addDepartChoixUtilisateurs());
		add(Step.CHOIX_UTILISATEURS,()->addFieldUtilisateurs(),()->checkUtilisateur());
		add(Step.NB_PARTICIPATION_PAR_UTILISATEURS,()->addFieldParticipation(),()->checkFieldParticipation());
		add(Step.AJUSTER,()->addFieldAjuster(),()->checkAjuster());
		add(Step.BILAN,()->addFieldBilan());
		
	}
	
	
	public PeriodePermanenceCreationEditorPart()
	{
		setWidth(80);
		popupTitle = "Création d'une période de permanence";

		dto  = new PeriodePermanenceDTO();
		dto.frequencePermanence = FrequencePermanence.UNE_FOIS_PAR_SEMAINE;
		dto.nature = NaturePeriodePermanence.INSCRIPTION_LIBRE_AVEC_DATE_LIMITE;
		dto.regleInscription = RegleInscriptionPeriodePermanence.UNE_INSCRIPTION_PAR_DATE;
		
		item = new BeanItem<PeriodePermanenceDTO>(dto);

	}
	
	

	private void drawInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales de cette période de permanence");
		
		// Liste des validators
		IValidator len_1_100 = new StringLengthValidator(1, 100);
		IValidator len_1_255 = new StringLengthValidator(1, 255);
		IValidator uniq = new UniqueInDatabaseValidator(PeriodePermanence.class,"nom",null);
		IValidator notNull = new NotNullValidator();
		
		
		
		// 
		addTextField("Nom de la période de permanence", "nom",len_1_100,uniq);

		// 
		addTextField("Description de la période", "description",len_1_255);
		
		//
		addComboEnumField("Nature de la période", "nature",notNull);

	
		addComboEnumField("Fréquence des permanences", "frequencePermanence",notNull);
		
		addIntegerField("Nombre de personnes par permanence", "nbPlaceParDate");
		
		ComboBox box = addComboEnumField("Régle d'inscription sur une date", "regleInscription", notNull);
		box.setWidth("600px");

	}
	
	
	private String checkInfoGenerales()
	{
		if (dto.nbPlaceParDate<1)
		{
			return "Le nombre de personnes par permanence doit être supérieur ou égal à 1";
		}
		return null;
	}	

	private void drawDebutFin()
	{
		// Titre
		setStepTitle("les dates de permanences");
		
		// Liste des validators
		IValidator notNull = new NotNullValidator();

		
		addDateField("Date de la première permanence", "dateDebut",notNull);
		addDateField("Date de la dernière permanence", "dateFin",notNull);
		
	}
	
	
	/**
	 * Retourne null si tout est ok, un message sinon
	 * @return
	 */
	private String checkDebutFin()
	{
		
		if (dto.dateDebut.after(dto.dateFin))
		{
			return "La date de début doit être avant la date de fin ";
		}
		else
		{
			return null;
		}

	}
	
	
	private void drawDetailDate1()
	{
		// Titre
		setStepTitle("explication mise au point des dates de permanences");

		String str = "Sur l'écran suivant, vous allez pouvoir affiner les dates de permanences en ajoutant , en supprimant ou en barrant certaines dates.<br/>"+
					 "Vous allez pouvoir aussi modifier le nombre de présents à certaines permanences si vous le souhaitez<br/>"+
					 "Si vous n'avez de modifications à apporter, il suffira de cliquer sur suivant";
		
		addLabel(str, ContentMode.HTML);
		
	}
	
	
	private void drawDetailDate2()
	{
		// On remplit la liste des dates si elle est vide
		if (dto.datePerms.size()==0)
		{
			new PeriodePermanenceService().fillDatePermanence(dto);
		}
		
		// Titre
		setStepTitle("mise au point des dates de permanences");

		CollectionEditor<PeriodePermanenceDateDTO> f1 = new CollectionEditor<PeriodePermanenceDateDTO>("Dates", (BeanItem) item, "datePerms", PeriodePermanenceDateDTO.class);
		f1.addColumn("datePerm", "Date permanence", FieldType.DATE, null);
		f1.addColumn("nbPlace", "Nb de personnes", FieldType.QTE, null);
		binder.bind(f1, "datePerms");
		form.addComponent(f1);
		
	}
	
	
	/**
	 * Retourne null si tout est ok, un message sinon
	 * @return
	 */
	private String checkDetailDate2()
	{
		if (dto.datePerms.size()==0)
		{
			return "Il faut au moins une date de permanence";
		}
		
		return null;

	}
	
	protected void drawFinInscription()
	{
		if (dto.nature==NaturePeriodePermanence.INSCRIPTION_LIBRE_FLOTTANT)
		{
			//
			dto.dateFinInscription = null;
			
			// Titre
			setStepTitle("Période de permanence sans date limite d'inscription - Délai pour modification des affectations");
			
			addIntegerField("Délai en jour pour modification de son affectation avant permanence", "flottantDelai");
			
			addLabel("Votre période de permanence  est de type SANS date limite d'inscription, c'est à dire que l'adhérent peut modifier ses dates de permanence même après le début de la période.<br/>"
					+ "Ce champ vous permet d'indiquer le délai entre la dernière modification possible et la date de permanence.<br/>"
					+ "Par exemple, si les permanences sont le samedi et si vous mettez 2 dans ce champ, alors l'adhérent pourra alors modifier "
					+ "sa participation à cette permanence jusqu'au mercredi soir minuit", ContentMode.HTML);
		}
		else if (dto.nature==NaturePeriodePermanence.INSCRIPTION_LIBRE_AVEC_DATE_LIMITE)
		{	
			// Titre
			setStepTitle("la date de fin des inscriptions");
			
			IValidator notNull = new NotNullValidator();
			
			// Champ 4
			addDateField("Date de fin des inscriptions", "dateFinInscription",notNull);
			
		}
		else if (dto.nature==NaturePeriodePermanence.INSCRIPTION_NON_LIBRE)
		{
			setStepTitle("Période de permanence avec participation imposée");
			
			addLabel("Cliquer sur continuer ...", ContentMode.HTML);
		}
	}



	private void drawTransition()
	{
		// Titre
		setStepTitle("affectation des personnes");
		
		String str = 	"A ce point, vous avez choisi les dates de votre période de permanence.<br/>Maintenant, vous allez devoir choisir les personnes qui vont participer à ces permanences";
												
		addLabel(str, ContentMode.HTML);

	}

	private void addDepartChoixUtilisateurs()
	{
		// Titre
		setStepTitle("choix du groupe de personnes");
				
		//
		addSearcher("Période de cotisation à prendre en compte ", "idPeriodeCotisation", SearcherList.PERIODE_COTISATION, null);
		
		String str = 	"Le champ précédent permet de préciser les personnes à prendre en compte pour les permanences.</br>"+
				"<br/>"+
				"Si vous saississez une période de cotisation, alors uniquement les amapiens ayant cotisé sur cette période et qui sont ACTIF vous seront proposés.<br/>"+
				"Si vous laissez ce champ vide, alors tous les utilisateurs ACTIF  vous seront proposés.<br/>"+
				"<br/><br/>"+
				"A noter : à l'étape suivante, il vous sera possible de supprimer/ajouter une personne en particulier si vous le souhaitez<br/>";
			
								

		addLabel(str, ContentMode.HTML);

	}
	

	private void addFieldUtilisateurs()
	{
		// Chargement des données
		new PeriodePermanenceService().fillUtilisateur(dto);
		
		// Titre
		setStepTitle("les personnes de permanence");
		
	
		builder = new ComplexTableBuilder<PeriodePermanenceUtilisateurDTO>(dto.utilisateurs);
		builder.setPageLength(14);
		
		builder.addString("Nom", false, 300, e->e.nom);
		builder.addString("Prénom", false, 300,  e->e.prenom);
				
		addComplexTable(builder);
		
		Button b = new Button("Ajouter un adhérent");
		b.addClickListener(e->addAdherent());
		form.addComponent(b);
		
		Button d = new Button("Supprimer un adhérent");
		d.addClickListener(e->delAdherent());
		form.addComponent(d);

		
	}
	

	private void addAdherent()
	{
		PopupSaisieUtilisateur.open(new PopupSaisieUtilisateur(dto,new ArrayList<PeriodePermanenceUtilisateurDTO>()), ()->endAddAdherent());
	}
	
	private void endAddAdherent()
	{
		builder.reload(dto.utilisateurs);
	}
	
	
	private void delAdherent()
	{
		PeriodePermanenceUtilisateurDTO detail = builder.getSelectedLine();
		if (detail!=null)
		{
			dto.utilisateurs.remove(detail);
			builder.reload(dto.utilisateurs);
			Notification.show("Suppression", "Suppression faite", Notification.Type.HUMANIZED_MESSAGE);
		}
		else
		{
			Notification.show("Impossible", "Merci de sélectionner une ligne pour pouvoir lancer la suppression", Notification.Type.HUMANIZED_MESSAGE);
		}
	}
	
	private String checkUtilisateur()
	{
		if (dto.utilisateurs.size()==0)
		{
			return "Il faut au minimum 1 personne";
		}
			
		return null;
	}


	private void addFieldParticipation()
	{
		new PeriodePermanenceService().fillNombreParPersonne(dto);
		
		// Titre
		setStepTitle("le nombre de permanences par personne");
		
		addLabel(dto.message, ContentMode.HTML);
		
		addIntegerField("Nombre de permanence par personne", "nbParPersonne");
		
		String str = "Sur la page, vous pourrez faire des adjustements, pour indiquer que telle personne doit faire plus ou moins de permanences que les autres";
		
		addLabel(str, ContentMode.HTML);
		
	}
	
	private String checkFieldParticipation()
	{
		if (dto.nbParPersonne<1)
		{
			return "Le nombre de permanence par personne doit être supérieur ou égal à 1";
		}
			
		// Chargement des données
		for (PeriodePermanenceUtilisateurDTO detail : dto.utilisateurs)
		{
			detail.nbParticipation = dto.nbParPersonne;
		}
		return null;
	}
	
	
	private void addFieldAjuster()
	{		
		// Titre
		setStepTitle("ajustement");
		
	
		builder = new ComplexTableBuilder<PeriodePermanenceUtilisateurDTO>(dto.utilisateurs);
		builder.setPageLength(14);
		
		builder.addString("Nom", false, 300, e->e.nom);
		builder.addString("Prénom", false, 300,  e->e.prenom);
		builder.addInteger("Nb participation", "nb", true, 100,  e->e.nbParticipation);
				
		addComplexTable(builder);
		
		
	}
	

	private String checkAjuster()
	{
		for (int i = 0; i < dto.utilisateurs.size(); i++)
		{
			PeriodePermanenceUtilisateurDTO lig = dto.utilisateurs.get(i);
			
			// 
			TextField tf = (TextField) builder.getComponent(i, "nb");
			
			if (tf.getConvertedValue()==null)
			{
				return "Il faut saisir une valeur pour "+lig.nom+" "+lig.prenom;
			}
			
			int nb = (Integer) tf.getConvertedValue();
			if (nb<0)
			{
				return "La valeur est négative pour "+lig.nom+" "+lig.prenom;
			}
			
			lig.nbParticipation = nb;
		
		}	
				
		return null;
	}
	
	
	
	
	
	private void addFieldBilan()
	{
		// Titre
		setStepTitle("bilan");
		
		String bilan = new PeriodePermanenceService().computeBilan(dto);
		
		addLabel(bilan, ContentMode.HTML);
		
		addLabel("Cliquer sur Sauvegarder pour conserver cette période de permanence en l'état, ou Annuler pour tout effacer", ContentMode.HTML);
		
	}

	@Override
	protected void performSauvegarder()
	{
		new PeriodePermanenceService().create(dto);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	

	
}
