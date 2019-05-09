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
 package fr.amapj.view.views.contratsamapien;

import java.util.Date;
import java.util.List;

import com.vaadin.ui.Table.Align;

import fr.amapj.common.DateUtils;
import fr.amapj.common.periode.TypPeriode;
import fr.amapj.model.engine.IdentifiableUtil;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.access.AccessManagementService;
import fr.amapj.service.services.contratsamapien.AmapienContratDTO;
import fr.amapj.service.services.contratsamapien.AmapienContratsService;
import fr.amapj.service.services.dbservice.DbService;
import fr.amapj.service.services.edgenerator.excel.EGBilanAdhesion;
import fr.amapj.service.services.edgenerator.excel.cheque.EGSyntheseCheque;
import fr.amapj.service.services.edgenerator.excel.cheque.EGSyntheseCheque.Mode;
import fr.amapj.service.services.edgenerator.excel.livraison.EGLivraisonAmapien;
import fr.amapj.service.services.edgenerator.pdf.PGBulletinAdhesion;
import fr.amapj.service.services.gestioncontratsigne.ContratSigneDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.cascadingpopup.CInfo;
import fr.amapj.view.engine.popup.cascadingpopup.CascadingData;
import fr.amapj.view.engine.popup.cascadingpopup.CascadingPopup;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.tools.DateTimeToStringConverter;
import fr.amapj.view.engine.tools.DateToStringConverter;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.common.utilisateurselector.UtilisateurSelectorPart;
import fr.amapj.view.views.gestioncontratsignes.PopupSaisieUtilisateur;
import fr.amapj.view.views.gestioncontratsignes.GestionContratSignesListPart.AjouterData;
import fr.amapj.view.views.receptioncheque.ReceptionChequeEditorPart;
import fr.amapj.view.views.saisiecontrat.SaisieContrat;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;


/**
 * Gestion des contrats d'un amapien en particulier 
 *
 */
@SuppressWarnings("serial")
public class ContratsAmapienListPart extends StandardListPart<AmapienContratDTO> implements PopupSuppressionListener
{

	private UtilisateurSelectorPart utilisateurSelector;
	
	private List<Producteur> allowedProducteurs;	
	
	public ContratsAmapienListPart()
	{
		super(AmapienContratDTO.class,false);
		allowedProducteurs = new AccessManagementService().getAccessLivraisonProducteur(SessionManager.getUserRoles(),SessionManager.getUserId());
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des contrats d'un amapien";
	}
	
	@Override
	protected void addSelectorComponent()
	{
		utilisateurSelector = new UtilisateurSelectorPart(this);
		addComponent(utilisateurSelector.getChoixUtilisateurComponent());
	}


	@Override
	protected void drawButton() 
	{
		addButton("Ajouter nouveau contrat", ButtonType.ALWAYS, ()->handleAjouter());
		addButton("Visualiser", ButtonType.EDIT_MODE, ()->handleVisualiser());
		addButton("Modifier quantités", ButtonType.EDIT_MODE, ()->handleModifier());
		addButton("Réceptionner chèques",ButtonType.EDIT_MODE,()->handleReceptionCheque());
		addButton("Modifier chéques", ButtonType.EDIT_MODE, ()->handleModifierCheque());
		addButton("Supprimer contrat", ButtonType.EDIT_MODE, ()->handleSupprimer());
		addButton("Télécharger ...", ButtonType.ALWAYS, ()->handleTelecharger());

		addSearchField("Rechercher par nom");
	}


	@Override
	protected void drawTable() 
	{
		// Gestion de la liste des colonnes visibles
		cdesTable.setVisibleColumns("nomProducteur" , "nomContrat" ,"dateDebut" , "dateFin" , "dateCreation" , "dateModification" , "montant");
		
		cdesTable.setColumnHeader("nomProducteur","Producteur");
		cdesTable.setColumnHeader("nomContrat","Contrat");
		cdesTable.setColumnHeader("dateDebut","Première livraison");
		cdesTable.setColumnHeader("dateFin","Dernière livraison");
		cdesTable.setColumnHeader("dateCreation","Date création");
		cdesTable.setColumnHeader("dateModification","Date modification");
		cdesTable.setColumnHeader("montant","Montant (en €)");
		cdesTable.setColumnAlignment("montant",Align.RIGHT);
		
		//
		cdesTable.setConverter("dateCreation", new DateTimeToStringConverter());
		cdesTable.setConverter("dateModification", new DateTimeToStringConverter());
		cdesTable.setConverter("dateDebut", new DateToStringConverter());
		cdesTable.setConverter("dateFin", new DateToStringConverter());
		cdesTable.setConverter("montant", new CurrencyTextFieldConverter());
		
	}



	@Override
	protected List<AmapienContratDTO> getLines() 
	{
		Long idUser = utilisateurSelector.getUtilisateurId();
		if (idUser==null)
		{
			return null;
		}
		return new AmapienContratsService().getAllContratsDTO(idUser);
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "dateFin" ,"nomProducteur" };
	}
	
	@Override
	protected boolean[] getSortAsc()
	{
		return new boolean[] { false ,  true};
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nomContrat" , "nomProducteur"  };
	}
	

	private void handleVisualiser()
	{
		AmapienContratDTO dto = getSelectedLine();
		String message = "Visualisation du contrat de "+dto.prenomUtilisateur+" "+dto.nomUtilisateur;
		Long idUtilisateur = utilisateurSelector.getUtilisateurId();
		
		SaisieContrat.saisieContrat(dto.idModeleContrat,dto.idContrat,idUtilisateur,message,ModeSaisie.READ_ONLY,this);
	}

	protected void handleModifier()
	{
		AmapienContratDTO dto = getSelectedLine();
		String message = "Contrat de "+dto.prenomUtilisateur+" "+dto.nomUtilisateur;
		Long idUtilisateur = utilisateurSelector.getUtilisateurId();
		
		SaisieContrat.saisieContrat(dto.idModeleContrat,dto.idContrat,idUtilisateur,message,ModeSaisie.QTE_SEUL,this);
	}

	protected void handleSupprimer()
	{
		AmapienContratDTO dto = getSelectedLine();
		
		String text = "Etes vous sûr de vouloir supprimer le contrat de "+dto.prenomUtilisateur+" "+dto.nomUtilisateur+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.idContrat);
		SuppressionPopup.open(confirmPopup, this);			
	}
	
	
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new MesContratsService().deleteContrat(idItemToSuppress);
	}	
	
	/**
	 * Retourne true si l'utilisateur courant a le droit de manipuler ce contrat
	 * @return
	 */
	@Override
	protected boolean isEditAllowed()
	{
		AmapienContratDTO dto = getSelectedLine();
		if (dto==null)
		{
			return false;
		}
		
		return IdentifiableUtil.contains(allowedProducteurs, dto.idProducteur);
	}
	
	
	private void handleAjouter()
	{
		AjouterData data= new AjouterData();
		data.userId = utilisateurSelector.getUtilisateurId();
		
		CascadingPopup cascading = new CascadingPopup(this,data);
		
		CInfo info = new CInfo();
		info.popup = new PopupSaisieProducteurContrat(data);
		info.onSuccess = ()->successSaisieContrat(data);
		
		cascading.start(info);
	}
		
	private CInfo successSaisieContrat(AjouterData data)
	{
		Long userId = data.userId;
		String message = "Contrat de "+new UtilisateurService().prettyString(userId);
					
		SaisieContrat.saisieContrat(data.idModeleContrat,null,userId,message,ModeSaisie.QTE_CHEQUE_REFERENT,this);
		
		return null;
		
	}

	public class AjouterData extends CascadingData
	{
		Long idModeleContrat;
		Long userId;
	}
	
	
	private void handleModifierCheque()
	{
		AmapienContratDTO dto = getSelectedLine();
		
		String message = "Modification des chèques de "+dto.prenomUtilisateur+" "+dto.nomUtilisateur;
	
		SaisieContrat.saisieContrat(dto.idModeleContrat,dto.idContrat,dto.idUtilisateur,message,ModeSaisie.CHEQUE_SEUL,this);
		
	}
	
	private void handleReceptionCheque()
	{
		AmapienContratDTO dto = getSelectedLine();
		CorePopup.open(new ReceptionChequeEditorPart(dto.idContrat,dto.nomUtilisateur,dto.prenomUtilisateur),this);
	}
	
	private void handleTelecharger()
	{
		Long idUtilisateur = utilisateurSelector.getUtilisateurId();
		
		TelechargerPopup popup = new TelechargerPopup("Informations d'un amapien",80);
	
		Date startDate = DateUtils.getDateWithNoTime();
		popup.addGenerator(new EGLivraisonAmapien(TypPeriode.A_PARTIR_DE, startDate, null, idUtilisateur));
		
		popup.addSeparator();
		popup.addGenerator(new EGSyntheseCheque(Mode.CHEQUE_A_REMETTRE,idUtilisateur));
		popup.addGenerator(new EGSyntheseCheque(Mode.CHEQUE_AMAP,idUtilisateur));
		popup.addGenerator(new EGSyntheseCheque(Mode.CHEQUE_REMIS_PRODUCTEUR,idUtilisateur));
		popup.addGenerator(new EGSyntheseCheque(Mode.TOUS,idUtilisateur));
		
		
		CorePopup.open(popup,this);	
	}
	
}
