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
 package fr.amapj.view.views.parametres;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.RichTextArea;

import fr.amapj.model.models.param.SmtpType;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Permet à un utilisateur de mettre à jour ses coordonnées
 * 
 *
 */
public class PopupSaisieParametres extends WizardFormPopup
{

	private ParametresDTO dto;
	
	// L'utilisateur est il adminFull ? 
	private boolean adminFull;
	
	// L'utilisateur a t'il le droit de modifier l'adresse de l'expéditeur et son mot de passe et son quota d'expédition ? 
	private boolean allowedModifyMailSender;

	public enum Step
	{
		AMAP_INFO , MAIL_INFO , PERMANENCE_INFO , MAIL_PERIODIQUE , GESTION_COTISATION  ;
	}

	/**
	 * 
	 */
	public PopupSaisieParametres(ParametresDTO dto)
	{
		setWidth(80);
		popupTitle = "Modification des paramètres";

		this.dto = dto;
		item = new BeanItem<ParametresDTO>(dto);
		
		adminFull = SessionManager.getSessionParameters().isAdminFull();
		
		// On peut modifier l'expediteur si on est admin full ou si on est avec du gmail
		allowedModifyMailSender = adminFull || (dto.smtpType==SmtpType.GMAIL);

	}
	
	@Override
	protected void configure()
	{
		add(Step.AMAP_INFO,()->addFieldAmapInfo());
		add(Step.MAIL_INFO,()->addFieldMailInfo());
		add(Step.PERMANENCE_INFO,()->addFieldPermanenceInfo());
		add(Step.MAIL_PERIODIQUE,()->addFieldMailPeriodique());
		add(Step.GESTION_COTISATION,()->addFieldGestionCotisation());
	}
	
	

	private void addFieldAmapInfo()
	{
		// Titre
		setStepTitle("identification de l'AMAP");
		
		// 
		addTextField("Nom de l'AMAP", "nomAmap");
		
		addTextField("Ville de l'AMAP", "villeAmap");		
	}
	
	private void addFieldMailInfo()
	{
		// Titre
		setStepTitle("information sur l'envoi des mails");
		
		AbstractField b = addComboEnumField("Type du serveur de mail", "smtpType", new NotNullValidator());
		b.setEnabled(adminFull);
		
		b = addTextField("Adresse mail qui enverra les messages", "sendingMailUsername");
		b.setEnabled(allowedModifyMailSender);

		b = addPasswordTextField("Password de l'adresse mail qui enverra les messages", "sendingMailPassword");
		b.setEnabled(allowedModifyMailSender);
		
		b = addIntegerField("Nombre maximum de mail par jour", "sendingMailNbMax");
		b.setEnabled(adminFull);
		
		b = addTextField("URL de l'application utilisée dans les mails", "url");
		b.setEnabled(adminFull);
		
		addTextField("Adresse mail qui sera en copie de tous les mails envoyés par le logiciel", "mailCopyTo");
		
		addTextField("Adresse mail du destinataire des sauvegardes quotidiennes", "backupReceiver");
		
	}
	
	private void addFieldPermanenceInfo()
	{
		// Titre
		setStepTitle("module \"Gestion des permanences\"");
				
		// Champ 9
		addComboEnumField("Activation du module \"Gestion des permanences\"", "etatPlanningDistribution");
		
		addComboEnumField("Envoi d'un mail de rappel pour les permanences",  "envoiMailRappelPermanence");
		
		addIntegerField("Délai en jours entre la permanence et l'envoi du mail", "delaiMailRappelPermanence");
		
		addTextField("Titre du mail de rappel pour les permanences", "titreMailRappelPermanence");
		
		RichTextArea f =  addRichTextAeraField("Contenu du mail de rappel pour les permanences", "contenuMailRappelPermanence");
		f.setHeight(8, Unit.CM);

	}
	
	private void addFieldMailPeriodique()
	{
		// Titre
		setStepTitle("Envoi d'un mail périodique (tous les mois)");
				
		// Champ 9
		addComboEnumField("Activation de l'envoi d'un mail périodique (tous les mois)",  "envoiMailPeriodique");
		
		addIntegerField("Numéro du jour dans le mois où le mail sera envoyé", "numJourDansMois");
		
		addTextField("Titre du mail périodique", "titreMailPeriodique");
		
		RichTextArea f =  addRichTextAeraField("Contenu du mail périodique", "contenuMailPeriodique");
		f.setHeight(8, Unit.CM);

	}
	
	private void addFieldGestionCotisation()
	{
		// Titre
		setStepTitle("module \"Gestion des cotisations\"");
				
		// Champ 9
		addComboEnumField("Activation du module \"Gestion des cotisations\"",  "etatGestionCotisation");
		
	}
	

	@Override
	protected void performSauvegarder()
	{
		new ParametresService().update(dto);
	}


	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
}
