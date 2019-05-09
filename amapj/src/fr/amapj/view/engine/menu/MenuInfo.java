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
 package fr.amapj.view.engine.menu;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.model.models.acces.RoleList;
import fr.amapj.service.services.parametres.ParamEcranDTO;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.views.advanced.devtools.DevToolsView;
import fr.amapj.view.views.advanced.maintenance.MaintenanceView;
import fr.amapj.view.views.advanced.supervision.SupervisionView;
import fr.amapj.view.views.appinstance.AppInstanceListPart;
import fr.amapj.view.views.archivage.ArchivageContratListPart;
import fr.amapj.view.views.compte.MonCompteView;
import fr.amapj.view.views.contratsamapien.ContratsAmapienListPart;
import fr.amapj.view.views.cotisation.bilan.BilanCotisationView;
import fr.amapj.view.views.cotisation.reception.ReceptionCotisationView;
import fr.amapj.view.views.droits.DroitsAdministrateurListPart;
import fr.amapj.view.views.droits.DroitsTresorierListPart;
import fr.amapj.view.views.editionspe.EditionSpeListPart;
import fr.amapj.view.views.gestioncontrat.listpart.GestionContratListPart;
import fr.amapj.view.views.gestioncontratsignes.GestionContratSignesListPart;
import fr.amapj.view.views.historiquecontrats.HistoriqueContratsView;
import fr.amapj.view.views.historiquepaiements.HistoriquePaiementsView;
import fr.amapj.view.views.importdonnees.ImportDonneesView;
import fr.amapj.view.views.listeadherents.ListeAdherentsView;
import fr.amapj.view.views.listeproducteurreferent.ListeProducteurReferentView;
import fr.amapj.view.views.livraisonamapien.LivraisonAmapienView;
import fr.amapj.view.views.logview.LogView;
import fr.amapj.view.views.logview.StatAccessView;
import fr.amapj.view.views.mescontrats.MesContratsView;
import fr.amapj.view.views.meslivraisons.MesLivraisonsView;
import fr.amapj.view.views.mespaiements.MesPaiementsView;
import fr.amapj.view.views.parametres.ParametresView;
import fr.amapj.view.views.permanence.detailperiode.DetailPeriodePermanenceListPart;
import fr.amapj.view.views.permanence.mespermanences.MesPermanencesView;
import fr.amapj.view.views.permanence.periode.PeriodePermanenceListPart;
import fr.amapj.view.views.permanence.permanencerole.PermanenceRoleListPart;
import fr.amapj.view.views.producteur.basicform.ProducteurListPart;
import fr.amapj.view.views.producteur.contrats.ProducteurContratListPart;
import fr.amapj.view.views.producteur.livraison.ProducteurLivraisonsView;
import fr.amapj.view.views.produit.ProduitListPart;
import fr.amapj.view.views.receptioncheque.ReceptionChequeListPart;
import fr.amapj.view.views.remiseproducteur.RemiseProducteurListPart;
import fr.amapj.view.views.sendmail.SendMailView;
import fr.amapj.view.views.suiviacces.SuiviAccesView;
import fr.amapj.view.views.synthesemulticontrat.SyntheseMultiContratView;
import fr.amapj.view.views.tableaudebord.TableauDeBordView;
import fr.amapj.view.views.utilisateur.UtilisateurListPart;

/**
 * Contient la description de chaque menu
 *
 */
public class MenuInfo 
{
	static private MenuInfo mainInstance;
	
	static public MenuInfo getInstance()
	{
		if (mainInstance==null)
		{
			mainInstance = new MenuInfo();
		}
		return mainInstance;
	}
	
	
	private List<MenuDescription> menus;
	
	private MenuInfo()
	{
		menus = new ArrayList<MenuDescription>();
		
		
		menus.add(new MenuDescription(MenuList.MES_CONTRATS,MesContratsView.class));
		menus.add(new MenuDescription(MenuList.MES_LIVRAISONS,  MesLivraisonsView.class));
		menus.add(new MenuDescription(MenuList.MES_PAIEMENTS,  MesPaiementsView.class));
		menus.add(new MenuDescription(MenuList.MON_COMPTE,  MonCompteView.class));
		menus.add(new MenuDescription(MenuList.LISTE_PRODUCTEUR_REFERENT,  ListeProducteurReferentView.class));
		menus.add(new MenuDescription(MenuList.LISTE_ADHERENTS,  ListeAdherentsView.class));
		menus.add(new MenuDescription(MenuList.MES_PERMANENCES,  MesPermanencesView.class , RoleList.ADHERENT , ModuleList.PLANNING_DISTRIBUTION));
		
		
		// Partie historique
		menus.add(new MenuDescription(MenuList.HISTORIQUE_CONTRATS, HistoriqueContratsView.class).setCategorie("HISTORIQUE"));
		menus.add(new MenuDescription(MenuList.HISTORIQUE_PAIEMENTS,  HistoriquePaiementsView.class));
		
		// Partie producteurs
		menus.add(new MenuDescription(MenuList.LIVRAISONS_PRODUCTEUR, ProducteurLivraisonsView.class, RoleList.PRODUCTEUR ).setCategorie("PRODUCTEUR"));
		menus.add(new MenuDescription(MenuList.CONTRATS_PRODUCTEUR, ProducteurContratListPart.class , RoleList.PRODUCTEUR));
		
		// Partie référents
		menus.add(new MenuDescription(MenuList.GESTION_CONTRAT, GestionContratListPart.class , RoleList.REFERENT ).setCategorie("REFERENT"));
		menus.add(new MenuDescription(MenuList.GESTION_CONTRAT_SIGNES,  GestionContratSignesListPart.class , RoleList.REFERENT ));
		menus.add(new MenuDescription(MenuList.RECEPTION_CHEQUES, ReceptionChequeListPart.class, RoleList.REFERENT));
		menus.add(new MenuDescription(MenuList.REMISE_PRODUCTEUR, RemiseProducteurListPart.class, RoleList.REFERENT));
		menus.add(new MenuDescription(MenuList.PRODUIT, ProduitListPart.class , RoleList.REFERENT));
		menus.add(new MenuDescription(MenuList.CONTRATS_AMAPIEN,  ContratsAmapienListPart.class , RoleList.REFERENT ));
		menus.add(new MenuDescription(MenuList.LIVRAISON_AMAPIEN,  LivraisonAmapienView.class , RoleList.REFERENT ));
		menus.add(new MenuDescription(MenuList.CONTRAT_ARCHIVE,  ArchivageContratListPart.class , RoleList.REFERENT ));
		menus.add(new MenuDescription(MenuList.SYNTHESE_MULTI_CONTRAT,  SyntheseMultiContratView.class , RoleList.REFERENT ));
		
		// Partie permanence
		menus.add(new MenuDescription(MenuList.PERIODE_PERMANENCE, PeriodePermanenceListPart.class , RoleList.REFERENT , ModuleList.PLANNING_DISTRIBUTION ).setCategorie("PERMANENCES"));
		menus.add(new MenuDescription(MenuList.DETAIL_PERIODE_PERMANENCE, DetailPeriodePermanenceListPart.class , RoleList.REFERENT , ModuleList.PLANNING_DISTRIBUTION ));
		menus.add(new MenuDescription(MenuList.ROLE_PERMANENCE, PermanenceRoleListPart.class , RoleList.REFERENT , ModuleList.PLANNING_DISTRIBUTION ));
		
		// Partie tésorier
		menus.add(new MenuDescription(MenuList.UTILISATEUR, UtilisateurListPart.class, RoleList.TRESORIER ).setCategorie("TRESORIER"));
		menus.add(new MenuDescription(MenuList.PRODUCTEUR, ProducteurListPart.class, RoleList.TRESORIER));
		menus.add(new MenuDescription(MenuList.TABLEAU_DE_BORD, TableauDeBordView.class, RoleList.TRESORIER));
		menus.add(new MenuDescription(MenuList.BILAN_COTISATION, BilanCotisationView.class, RoleList.TRESORIER , ModuleList.GESTION_COTISATION));
		menus.add(new MenuDescription(MenuList.RECEPTION_COTISATION, ReceptionCotisationView.class, RoleList.TRESORIER , ModuleList.GESTION_COTISATION));
		menus.add(new MenuDescription(MenuList.IMPORT_DONNEES, ImportDonneesView.class, RoleList.TRESORIER));
		menus.add(new MenuDescription(MenuList.LISTE_TRESORIER, DroitsTresorierListPart.class, RoleList.TRESORIER));
		menus.add(new MenuDescription(MenuList.ETIQUETTE, EditionSpeListPart.class, RoleList.TRESORIER ));
		
		// Partie adminitrateur
		menus.add(new MenuDescription(MenuList.PARAMETRES, ParametresView.class, RoleList.ADMIN).setCategorie("ADMIN"));
		menus.add(new MenuDescription(MenuList.LISTE_ADMIN, DroitsAdministrateurListPart.class, RoleList.ADMIN));
		menus.add(new MenuDescription(MenuList.MAINTENANCE, MaintenanceView.class, RoleList.ADMIN));
		menus.add(new MenuDescription(MenuList.ENVOI_MAIL, SendMailView.class, RoleList.ADMIN));
		
		
		// Partie master
		menus.add(new MenuDescription(MenuList.LISTE_APP_INSTANCE, AppInstanceListPart.class, RoleList.MASTER).setCategorie("MASTER"));
		menus.add(new MenuDescription(MenuList.SUIVI_ACCES, SuiviAccesView.class, RoleList.MASTER));	
		menus.add(new MenuDescription(MenuList.VISU_LOG, LogView.class, RoleList.MASTER));
		menus.add(new MenuDescription(MenuList.STAT_ACCES, StatAccessView.class, RoleList.MASTER));
		menus.add(new MenuDescription(MenuList.SUPERVISION, SupervisionView.class, RoleList.MASTER));
		menus.add(new MenuDescription(MenuList.OUTILS_DEV, DevToolsView.class, RoleList.MASTER));
	}
	
	/**
	 * Retourne la liste des menus accessibles par l'utilisateur courant
	 * 
	 */
	public List<MenuDescription> getMenu()
	{
		ParametresDTO param = new ParametresService().getParametres();
		List<ParamEcranDTO> dtos = new ParametresService().getAllParamEcranDTO();
		List<MenuDescription> res = new ArrayList<MenuDescription>();
		List<RoleList> roles = SessionManager.getSessionParameters().userRole;
		
		for (MenuDescription mn : menus)
		{
			if ( mn.hasRole(roles) && mn.hasModule(param) && mn.complyParamEcan(roles,dtos)) 
			{
				res.add(mn);
			}
		}
		return res;
	}
}
