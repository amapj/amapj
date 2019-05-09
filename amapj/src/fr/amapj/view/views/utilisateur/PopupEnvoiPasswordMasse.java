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
 package fr.amapj.view.views.utilisateur;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.TextArea;

import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.service.services.utilisateur.envoimail.EnvoiMailDTO;
import fr.amapj.service.services.utilisateur.envoimail.EnvoiMailUtilisateurDTO;
import fr.amapj.service.services.utilisateur.envoimail.StatusEnvoiMailDTO;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Popup 
 * 
 *
 */
public class PopupEnvoiPasswordMasse extends WizardFormPopup
{

	private EnvoiMailDTO envoiMail;


	public enum Step
	{
		INFO_GENERALES, UTILISATEURS , SAISIE_TEXTE_MAIL , AVERTISSEMENT , RESULTAT;
	}

	/**
	 * 
	 */
	public PopupEnvoiPasswordMasse()
	{
		setWidth(80);
		popupTitle = "Envoi des mots de passe en masse";
		saveButtonTitle = "Quitter";


		// Chargement de l'objet à créer
		envoiMail = new UtilisateurService().getEnvoiMailDTO();
		envoiMail.texteMail = getInitialText();
		
		item = new BeanItem<EnvoiMailDTO>(envoiMail);

	}
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.UTILISATEURS,()->addFieldUtilisateurs());
		add(Step.SAISIE_TEXTE_MAIL,()->addFieldTexteMail());
		add(Step.AVERTISSEMENT,()->addFieldAvertissement());
		add(Step.RESULTAT,()->addFieldResultat());
	}

	private void addFieldInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales.");
		
		String str = 	"Cet outil va vous permettre d'envoyer un mail de bienvenue avec un password et une adresse de connexion</br>"+
						"à tous les utilisateurs qui n'ont pas de mot de passe<br/><br/>"+
						"Sur l'écran suivant, vous allez visualiser la liste des personnes qui vont recevoir ce message <br/><br/>"+
						"Sur l'écran encore suivant, vous pourrez saisir le texte du message qui sera envoyé<br/>";
				
		
		addLabel(str, ContentMode.HTML);
		

	}
	
	

	private void addFieldUtilisateurs()
	{
		// Titre
		setStepTitle("les utilisateurs qui vont recevoir le mail de bienvenue");
		
		//
		CollectionEditor<EnvoiMailUtilisateurDTO> f1 = new CollectionEditor<EnvoiMailUtilisateurDTO>("Liste des utilisateurs", (BeanItem) item, "utilisateurs", EnvoiMailUtilisateurDTO.class);
		f1.addSearcherColumn("idUtilisateur", "Nom de l'utilisateur",FieldType.SEARCHER, null,SearcherList.UTILISATEUR_ACTIF,null);
		f1.addColumn("sendMail","Envoyer un mail",FieldType.CHECK_BOX,true);
		binder.bind(f1, "utilisateurs");
		form.addComponent(f1);

	}
	
	private void addFieldTexteMail()
	{
		// Titre
		setStepTitle("le texte du mail de bienvenue");
		
		//
		TextArea f =  addTextAeraField("Texte du mail", "texteMail");
		f.setMaxLength(20480);
		f.setHeight(10, Unit.CM);
		
	}
	
	
	private void addFieldAvertissement()
	{
		// Titre
		setStepTitle("Etes vous sûr ? ");
		
		int count=0;
		for (EnvoiMailUtilisateurDTO u : envoiMail.utilisateurs)
		{
			if (u.sendMail)
			{
				count++;
			}
		}
		
		String str = 	"Vous allez envoyer "+count+" mails.</br>"+
						"Etes vous sûr de vouloir continuer ?<br/>"+
						"Quand vous allez cliquer sur Etape suivante, les mails seront envoyés<br/>";
				
		
		addLabel(str, ContentMode.HTML);
	}
	
	
	private void addFieldResultat()
	{
		StatusEnvoiMailDTO ret = new UtilisateurService().envoiEmailBienvenue(envoiMail);
		
		
		// Titre
		setStepTitle("Résultats");
		
		String str = 	ret.nbMailOK+" mails ont été envoyés avec succés";
		addLabel(str, ContentMode.HTML);
		
		
		if (ret.erreurs.size()>0)
		{
			str = 	"Il y a eu "+ret.erreurs.size()+" erreurs.</br></br>";
			for (String err : ret.erreurs)
			{
				str = str+err+"</br>";
			}
			addLabel(str, ContentMode.HTML);
		}
		
		previousButton.setEnabled(false);
		
		
	}


	

	@Override
	protected void performSauvegarder()
	{
		
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	private String getInitialText()
	{
		String lineSep="\r\n";
		
		ParametresDTO param = new ParametresService().getParametres();
		
		StringBuffer buf = new StringBuffer();
		buf.append("<h2>"+param.nomAmap+"</h2>");
		buf.append(lineSep);
		buf.append("Bonjour , voici vos identifiants pour vous connecter à l'application WEB de :"+param.nomAmap);
		buf.append(lineSep);
		buf.append(lineSep);
		buf.append("Adresse e mail : #EMAIL#");
		buf.append(lineSep);
		buf.append(lineSep);
		buf.append("Mot de passe : #PASSWORD#");
		buf.append(lineSep);
		buf.append(lineSep);
		buf.append("<a href=\"#LINK#\">Cliquez ici pour accéder à l'application</a>");
		buf.append(lineSep);
		buf.append(lineSep);
		buf.append("Merci de conserver ce lien pour pouvoir vous reconnecter plus tard.");
		buf.append(lineSep);
		buf.append("Si vous souhaitez changer votre mot de passe, vous pourrez le faire en vous " +
				"connectant dans l'application, puis en allant dans le menu \"Mon Compte\"");
		buf.append(lineSep);
		buf.append(lineSep);
		buf.append(lineSep);
		buf.append(lineSep);
		buf.append("Bonne journée à tous !!");
		buf.append(lineSep);
		return buf.toString();
	}
}
