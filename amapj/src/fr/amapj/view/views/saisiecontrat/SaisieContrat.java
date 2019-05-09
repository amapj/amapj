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
 package fr.amapj.view.views.saisiecontrat;

import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.cascadingpopup.CInfo;
import fr.amapj.view.engine.popup.cascadingpopup.CascadingData;
import fr.amapj.view.engine.popup.cascadingpopup.CascadingPopup;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;

public class SaisieContrat
{
	private SaisieContratData data;
	private CascadingPopup cascading;
	
	public SaisieContrat(SaisieContratData data,PopupListener listener)
	{
		super();
		this.data = data;
		cascading = new CascadingPopup(listener,data);
	}

	public void doSaisie()
	{
		// On verifie d'abord si tout est OK 
		String msg = checkInitialCondition();
		if (msg!=null)
		{
			MessagePopup p = new MessagePopup("Impossible de continuer",ContentMode.HTML,ColorStyle.RED,msg);
			CInfo info = new CInfo(p,null);
			cascading.start(info);
			return;
		}	
		
		
		// Si cheque seul : on passe directement à la suite
		if (data.modeSaisie==ModeSaisie.CHEQUE_SEUL)
		{
			cascading.start(getPopupPaiement());
			return;
		}
		
		
		ContratPopupType popupType = computeContratPopupType();	
		
		CorePopup popup = null;
		switch (popupType)
		{
		case POPUP_ABO:
			popup = new PopupSaisieQteContratPanier(data); 
			break;
			
		case POPUP_LIBRE:
			popup = new PopupSaisieQteContrat(data);
			break;
			
		case POPUP_CARTE_PREPAYEE:
			popup = new PopupSaisieQteCartePrepayee(data);
			break;

		default:
			throw new AmapjRuntimeException();
		}
				
		CInfo info = new CInfo(popup,()->endOfSaisieQte());
		cascading.start(info);
	}
	
	
	private String checkInitialCondition()
	{
		// Si saisie standard et que le contrat n'est plus modifiable
		// Ceci arrive si : à 23h59 l'utilisateur affiche l'écran "Mes contrats"
		// à 00:01 il clique sur Modifier - le bouton est bien là mais il n'a plus le droit de modifier 
		if ( (data.modeSaisie==ModeSaisie.STANDARD) && (data.contratDTO.isModifiable==false) )
		{
			return "Il est trop tard pour modifier ce contrat ou pour s'inscrire.";
		}
		
		// Si Saisie standard et Abonnement et si le contrat n'est pas régulier : l'utilisateur n'aurait pas du avoir le droit de le modifier
		if ( (data.modeSaisie==ModeSaisie.STANDARD) && (data.contratDTO.nature==NatureContrat.ABONNEMENT) && (new ContratAboManager().isRegulier(data.contratDTO)==false) )
		{
			return "Vous ne pouvez pas modifier ce contrat <br/> car il a été saisi par le référent";
		}
		
		return null;
	}


	public enum ContratPopupType
	{
		POPUP_ABO ,
		POPUP_LIBRE,
		POPUP_CARTE_PREPAYEE,
	}

	/**
	 * Détermine si ce contrat doit être saisi avec le popup Abonnement ou le popup Libre ou le popup CartePrepayée
	 *  
	 * @return
	 */
	private ContratPopupType computeContratPopupType()
	{
		// Si c'est un contrat libre : on utilise le popup LIBRE
		if (data.contratDTO.nature==NatureContrat.LIBRE)
		{
			return ContratPopupType.POPUP_LIBRE;
		}
		
		// Si c'est un contrat carte prépayée : ca depend de l'utilisateur 
		if (data.contratDTO.nature==NatureContrat.CARTE_PREPAYEE)
		{
			return computeCartePrepayee();
		}
		
		// Si c'est un contrat abonnement : ca depend de l'utilisateur 
		if (data.contratDTO.nature==NatureContrat.ABONNEMENT)
		{
			return computeAbonnement();
		}
		
		throw new AmapjRuntimeException();
	
	}
	
	
	private ContratPopupType computeCartePrepayee()
	{			
		switch (data.modeSaisie)
		{
			// Pour l'adherent 
			case STANDARD:
				return ContratPopupType.POPUP_CARTE_PREPAYEE;
				
			// Pour le test : on voit tout (sinon, ceci pose probleme : quand le contrat est terminé, on ne peut plus le visualiser)
			case FOR_TEST:
				return ContratPopupType.POPUP_LIBRE;
				
			case READ_ONLY:
				// Quand on visualise : on voit tout  
				return ContratPopupType.POPUP_LIBRE;
				
					
			// Le référent a toujours le droit de modifier toutes les quantités à toutes les dates 
			// Par exemple pour les corrections  
			case QTE_SEUL:
			case CHEQUE_SEUL :
			case QTE_CHEQUE_REFERENT:
				return ContratPopupType.POPUP_LIBRE;
		}
		throw new AmapjRuntimeException();
	}
	
	
	
	private ContratPopupType computeAbonnement()
	{	
		// Sinon, dans le cas du contrat ABO, ca depend de l'utilisateur
		boolean isRegulier = new ContratAboManager().isRegulier(data.contratDTO);
		
		switch (data.modeSaisie)
		{
			// Pour l'adherent 
			case STANDARD:
			case FOR_TEST:
				return ContratPopupType.POPUP_ABO;
				
			case READ_ONLY:
				// Quand on visualise : si le contrat est regulier, on se met en mode abo , sinon en mode libre 
				if (isRegulier)
				{
					return ContratPopupType.POPUP_ABO;
				}
				else
				{
					return ContratPopupType.POPUP_LIBRE;
				}
					
			// Le référent a toujours le droit de modifier toutes les quantités à toutes les dates 
			// Par exemple pour les utilisateurs qui commencent en cours d'année 
			case QTE_SEUL:
			case CHEQUE_SEUL :
			case QTE_CHEQUE_REFERENT:
				return ContratPopupType.POPUP_LIBRE;
		}
		throw new AmapjRuntimeException();
	}
	

	/**
	 * On a fini de saisir les quantités
	 * @return
	 */
	private CInfo endOfSaisieQte()
	{
		// Si que saisie des quantités : on sauvegarde tout de suite et on arrete 
		if (data.modeSaisie==ModeSaisie.QTE_SEUL)
		{
			try
			{
				new MesContratsService().saveNewContrat(data.contratDTO,data.userId);
			} 
			catch (OnSaveException e)
			{
				e.showInNewDialogBox();
			}
			return null;
		}
		
		 
		 // Sinon on continue avec le popup de paiement 
		return getPopupPaiement();
	
	}


	/**
	 * Retourne le popup à utiliser pour le paiement 
	 * @return
	 */
	protected CInfo getPopupPaiement()
	{
		if (data.contratDTO.paiement.gestionPaiement==GestionPaiement.GESTION_STANDARD)
		{
			return new CInfo(new PopupSaisiePaiement(data));
		}
		else
		{
			return new CInfo(new PopupInfoPaiement(data));
		}
	}
	

	/**
	 * Permet de lancer le cyle de saisie d'un contrat, avec les quantités et les reglements
	 * 
	 * @param contratDTO
	 * @param readOnly
	 * @param forTest
	 * @param userId
	 * @param messageSpecifique
	 */
	static public void saisieContrat(Long idModeleContrat,Long idContrat, Long userId, String messageSpecifique,ModeSaisie modeSaisie,PopupListener listener)
	{
		// Rechargement du contrat 
		ContratDTO contratDTO = new MesContratsService().loadContrat(idModeleContrat,idContrat);
		
		// Lancement de la saisie 
		SaisieContratData data = new SaisieContratData(contratDTO, userId, messageSpecifique,modeSaisie);
		SaisieContrat saisieContrat = new SaisieContrat(data, listener);
		saisieContrat.doSaisie();
	}
	
	public enum ModeSaisie
	{
		// Saisie standard faite par l'utilisateur
		STANDARD,
		
		// Mode test, correspond à la saisie standard faite par l'utilisateur 
		FOR_TEST,
		
		// Visualisation des informations du contrat 
		READ_ONLY,
		
		// Saisie de la quantité seule, pour correction par le referent
		QTE_SEUL,
		
		// Saisie des chéques seuls, pour correction par le referent
		CHEQUE_SEUL ,
		
		// Saisie de la quantité et des cheques , pour le referent qui saisit à la main le contrat des amapiens
		QTE_CHEQUE_REFERENT
		
	}
	
	
	public static class SaisieContratData extends CascadingData
	{
		public ContratDTO contratDTO;
		public Long userId;
		public String messageSpecifique;
		public ModeSaisie modeSaisie;
				
		public SaisieContratData(ContratDTO contratDTO, Long userId, String messageSpecifique,ModeSaisie modeSaisie)
		{
			
			this.contratDTO = contratDTO;
			this.userId = userId;
			this.messageSpecifique = messageSpecifique;
			this.modeSaisie = modeSaisie;
		}
		
		
	}

}
