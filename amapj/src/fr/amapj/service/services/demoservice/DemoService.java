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
 package fr.amapj.service.services.demoservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import fr.amapj.common.DateUtils;
import fr.amapj.common.StackUtils;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.model.models.contrat.modele.JokerMode;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.model.models.fichierbase.EtatNotification;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.ProducteurReferent;
import fr.amapj.model.models.fichierbase.ProducteurUtilisateur;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.RoleAdmin;
import fr.amapj.model.models.fichierbase.RoleTresorier;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.param.EtatModule;
import fr.amapj.model.models.param.Parametres;
import fr.amapj.model.models.param.SmtpType;
import fr.amapj.model.models.saas.AppInstance;
import fr.amapj.model.models.saas.TypDbExemple;
import fr.amapj.service.services.appinstance.AppInstanceDTO;
import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.LigneContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.view.views.gestioncontrat.editorpart.FrequenceLivraison;

/**
 * Ce service permet de creer des données pour la base de démonstration (création de contrat)
 * 
 * 
 */
public class DemoService
{
	public DemoService()
	{

	}

	@DbWrite
	public Void generateDemoData(AppInstanceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();

		createParamGeneraux(em, dto);
		
		if (dto.typDbExemple==TypDbExemple.BASE_EXEMPLE)
		{
			createUtilisateurs(em,dto.password);
			
			createRoleUtilisateur(em);
	
			createProducteur(em);
	
			createProduit(em);
	
			createContrat(em, dto.dateDebut, dto.dateFin, dto.dateFinInscription);
		}
		else if (dto.typDbExemple==TypDbExemple.BASE_MINIMALE)
		{
			createOneAdminUtilisateur(em,dto);
		}
		else if (dto.typDbExemple==TypDbExemple.BASE_MASTER)
		{
			createOneAppInstance(em,dto);
		} 
		
		return null;
	}



	private void createParamGeneraux(EntityManager em, AppInstanceDTO dto)
	{
		Parametres p = new Parametres();
		
		p.setId(1L);
		
		// Etape 1 - Nom et ville Amap
		p.setNomAmap(dto.nomAmap);
		p.setVilleAmap(dto.villeAmap);
		
		// Etape 2 - Les mails 
		p.setSmtpType(dto.smtpType);
		p.setSendingMailUsername(dto.adrMailSrc);
		p.setSendingMailPassword("");
		p.setSendingMailNbMax(dto.nbMailMax);
		p.setUrl(dto.url);
		p.setMailCopyTo("");
		p.setBackupReceiver("");
		
		// Etape 3
		p.setEtatPlanningDistribution(EtatModule.INACTIF);
		p.setEnvoiMailRappelPermanence(ChoixOuiNon.NON);
		
		// Etape 4
		p.setEnvoiMailPeriodique(ChoixOuiNon.NON);
		
		// Etape 5
		p.setEtatGestionCotisation(EtatModule.INACTIF);
		
		
		em.persist(p);

	}

	private void createUtilisateurs(EntityManager em,String password)
	{
		insertUtilisateur(em, 1051, "TREMBLAY", "Antonin", "antonin.tremblay@example.fr", password);
		insertUtilisateur(em, 1052, "DUPUIS", "Romain", "romain.dupuis@example.fr", password);
		insertUtilisateur(em, 1507, "DUBOIS", "Rémi", "remi.dubois@example.fr", password);
		insertUtilisateur(em, 1508, "GAGNON", "Magali", "magali.gagnon@example.fr", password);
		insertUtilisateur(em, 1509, "ROY", "Gaelle", "gaelle.roy@example.fr", password);
		insertUtilisateur(em, 1510, "CÔTÉ", "Nathalie", "nathalie.côte@example.fr", password);
		insertUtilisateur(em, 1511, "BOUCHARD", "Benjamin", "benjamin.bouchard@example.fr", password);
		insertUtilisateur(em, 1512, "GAUTHIER", "Alex", "alex.gauthier@example.fr", password);
		insertUtilisateur(em, 1513, "MORIN", "Karine", "karine.morin@example.fr", password);
		insertUtilisateur(em, 1514, "LAVOIE", "Arthur", "arthur.lavoie@example.fr", password);
		insertUtilisateur(em, 1515, "FORTIN", "Sophie", "sophie.fortin@example.fr", password);
		insertUtilisateur(em, 1517, "OUELLET", "Mathis", "mathis.ouellet@example.fr", password);
		insertUtilisateur(em, 1518, "PELLETIER", "Matthieu", "matthieu.pelletier@example.fr", password);
		insertUtilisateur(em, 1519, "BÉLANGER", "David", "david.belanger@example.fr", password);
		insertUtilisateur(em, 1520, "LÉVESQUE", "Joelle", "joelle.levesque@example.fr", password);
		insertUtilisateur(em, 1522, "BERGERON", "Nadege", "nadege.bergeron@example.fr", password);
		insertUtilisateur(em, 1601, "LEBLANC", "Jeanne", "jeanne.leblanc@example.fr", password);
		insertUtilisateur(em, 1602, "PAQUETTE", "Emeline", "emeline.paquette@example.fr", password);
		insertUtilisateur(em, 1603, "GIRARD", "Florent", "florent.girard@example.fr", password);
		insertUtilisateur(em, 1604, "SIMARD", "Pascal", "pascal.simard@example.fr", password);
		insertUtilisateur(em, 1651, "BOUCHER", "Charles", "charles.boucher@example.fr", password);
		insertUtilisateur(em, 1652, "CARON", "Jean-Luc", "jean-luc.caron@example.fr", password);
		insertUtilisateur(em, 1653, "BEAULIEU", "Mylène", "mylene.beaulieu@example.fr", password);
		insertUtilisateur(em, 1654, "CLOUTIER", "Nadine", "nadine.cloutier@example.fr", password);
		insertUtilisateur(em, 1655, "DUBÉ", "Marine", "marine.dube@example.fr", password);
		insertUtilisateur(em, 1656, "POIRIER", "Frédéric", "frederic.poirier@example.fr", password);
		insertUtilisateur(em, 1657, "FOURNIER", "Yves", "yves.fournier@example.fr", password);
		insertUtilisateur(em, 1659, "LAPOINTE", "Bruno", "bruno.lapointe@example.fr", password);

	}
	
	
	private void createRoleUtilisateur(EntityManager em)
	{
		RoleTresorier rt = new RoleTresorier();
		rt.setUtilisateur(em.find(Utilisateur.class, new Long(1051)));
		em.persist(rt);
		
		RoleAdmin ra = new RoleAdmin();
		ra.setUtilisateur(em.find(Utilisateur.class, new Long(1052)));
		em.persist(ra);
		
	}

	private void insertUtilisateur(EntityManager em, int id, String nom, String prenom, String email, String password)
	{
		Utilisateur u = new Utilisateur();
		
		u.setId(new Long(id));
		u.setNom(nom);
		u.setPrenom(prenom);
		u.setEmail(email);
		
		em.persist(u);
		
		// Mot de passe 
		new PasswordManager().setUserPassword(u.getId(), password);
		
		
		
		
	}
	
	private void createOneAdminUtilisateur(EntityManager em, AppInstanceDTO dto)
	{
		insertUtilisateur(em, 1052, dto.user1Nom, dto.user1Prenom, dto.user1Email, dto.password);
		
		RoleAdmin ra = new RoleAdmin();
		ra.setUtilisateur(em.find(Utilisateur.class, new Long(1052)));
		em.persist(ra);
		
	}
	
	
	
	private void createOneAppInstance(EntityManager em, AppInstanceDTO dto)
	{
		AppInstance app = new AppInstance();
		
		app.setDateCreation(DateUtils.getDate());
		app.setDbms("hi");
		app.setNomInstance("amap1");
		em.persist(app);
		
	}
	
	

	private void createProducteur(EntityManager em)
	{
		createProducteur(em,3002,"FERME DES CHEVRES",1601,1522);
		createProducteur(em,3011,"EARL LAIT VACHE",1653,1513);
		createProducteur(em,3019,"FERME des BREBIS",1515,1509);
		createProducteur(em,3029,"EARL du PAIN De SUC",1508,1514);
	
	}

	private void createProducteur(EntityManager em, int idProducteur, String nomProducteur, int idUtilisateur, int idReferent)
	{
		Producteur p = new Producteur();
		p.setId(new Long(idProducteur));
		p.nom = nomProducteur;
		p.delaiModifContrat = 3;
		p.feuilleDistributionGrille = ChoixOuiNon.OUI;
		p.feuilleDistributionListe = ChoixOuiNon.NON;
		
		em.persist(p);
		
		ProducteurUtilisateur pu  =new ProducteurUtilisateur();
		pu.setNotification(EtatNotification.SANS_NOTIFICATION_MAIL);
		pu.setProducteur(p);
		pu.setUtilisateur(em.find(Utilisateur.class, new Long(idUtilisateur)));
		
		em.persist(pu);
		
		ProducteurReferent pr = new ProducteurReferent();
		pr.producteur = p;
		pr.referent = em.find(Utilisateur.class, new Long(idReferent));
		pr.notification = EtatNotification.SANS_NOTIFICATION_MAIL;
		
		em.persist(pr);
	}

	private void createProduit(EntityManager em)
	{
		insertProduit(em,3003,"Tomme de chèvre - blanc","la pièce", 3002);
		insertProduit(em,3004,"Tomme de chèvre - crémeux","la pièce", 3002);
		insertProduit(em,3005,"Tomme de chèvre - sec","la pièce", 3002);
		insertProduit(em,3006,"Faisselle","le pot de 500 g", 3002);
		insertProduit(em,3007,"Yaourt","le pot de 140 g", 3002);
		insertProduit(em,3008,"Dessert lacté parfumé","le pot de 140 g", 3002);
		insertProduit(em,3009,"Savon au lait de chèvre","la pièce", 3002);
		insertProduit(em,3010,"Tomme pressée","la pièce de 200/230 g", 3002);
		
		
		insertProduit(em,3012,"Lait","le litre", 3011);
		insertProduit(em,3013,"Yaourt nature","le pot de 500 g", 3011);
		insertProduit(em,3014,"Yaourt nature","le pot de 1 kg", 3011);
		insertProduit(em,3015,"Yaourt aux fruits","le pot de 500 g", 3011);
		insertProduit(em,3016,"Faisselle","le pot de 1 kg", 3011);
		insertProduit(em,3017,"Crème fraiche","le pot de 25 cl", 3011);
		insertProduit(em,3018,"Confiture de lait","le pot de 500 g", 3011);
		
		
		insertProduit(em,3020,"Lait","le litre", 3019);
		insertProduit(em,3023,"Yaourt nature","le pot de 300 g", 3019);
		insertProduit(em,3024,"Yaourt vanille","le pot de 300 g", 3019);
		insertProduit(em,3025,"Tommette","la pièce de 100 g", 3019);
		insertProduit(em,3026,"Tomme pressée","la pièce entière (environ 700 g)", 3019);
		insertProduit(em,3027,"Tomme pressée","la 1/2 pièce (environ 350 g)", 3019);
		insertProduit(em,3028,"Tomme pressée","le 1/4 de pièce (environ 175 g)", 3019);
		insertProduit(em,4340,"Féta","la pièce entière (environ 400 g)", 3019);
		insertProduit(em,4341,"Féta","la 1/2 pièce (environ 200 g)", 3019);
		
		
		insertProduit(em,3030,"Pain de blé","la pièce de 900 g", 3029);
		insertProduit(em,3031,"Pain de blé moulé","la pièce de 900 g", 3029);
		insertProduit(em,3032,"Pain de campagne","la pièce de 900 g", 3029);
		insertProduit(em,3033,"Pain de campagne moulé","la pièce de 900 g ", 3029);
		insertProduit(em,3034,"Pain de seigle","la pièce de 900 g", 3029);
		insertProduit(em,3035,"Pain de seigle moulé","la pièce de 900 g ", 3029);
		
		


	}

	private void insertProduit(EntityManager em, int idProduit, String nom, String cond, int idProducteur)
	{
		Produit p = new Produit();
		
		p.setId(new Long(idProduit));
		p.setNom(nom);
		p.setConditionnement(cond);
		p.setProducteur(em.find(Producteur.class, new Long(idProducteur)));
		
		em.persist(p);
		
	}

	private void createContrat(EntityManager em, Date dateDebut, Date dateFin, Date dateFinInscription)
	{

		createContrat(em, "PRODUITS LAITIERS de VACHE", "lait, yaourts, faisselle, crème fraîche", 3011L, dateFinInscription, dateDebut, dateFin);

		createContrat(em, "PAIN", "pains complet, campagne ou seigle", 3029L, dateFinInscription, dateDebut, dateFin);

		createContrat(em, "PRODUITS LAITIERS de CHEVRE", "fromages, yaourts, faisselles, savons", 3002L, dateFinInscription, dateDebut, dateFin);

		createContrat(em, "PRODUITS LAITIERS de BREBIS", "lait, yaourts, et fromages de brebis", 3019L, dateFinInscription, dateDebut, dateFin);

		setAllContratActifs();

	}

	/**
	 * Positionne tous les contrats actifs
	 */
	private void setAllContratActifs()
	{
		GestionContratService service = new GestionContratService();
		List<ModeleContratSummaryDTO> modeles = service.getModeleContratInfo();
		for (ModeleContratSummaryDTO dto : modeles)
		{
			service.updateEtat(EtatModeleContrat.ACTIF, dto.id);
		}

	}

	private void createContrat(EntityManager em, String nom, String description, Long idProducteur, Date dateFinInscription, Date dateDebut, Date dateFin)
	{
		ModeleContratDTO modeleContrat = new ModeleContratDTO();
		modeleContrat.nom = nom;
		modeleContrat.description = description;
		modeleContrat.producteur = idProducteur;
		modeleContrat.dateFinInscription = dateFinInscription;
		modeleContrat.frequence = FrequenceLivraison.UNE_FOIS_PAR_SEMAINE;
		modeleContrat.gestionPaiement = GestionPaiement.GESTION_STANDARD;
		modeleContrat.nature = NatureContrat.LIBRE;
		modeleContrat.jokerMode = JokerMode.INSCRIPTION;

		modeleContrat.dateDebut = dateDebut;
		modeleContrat.dateFin = dateFin;

		modeleContrat.produits = getProduits(idProducteur.intValue(), em);

		modeleContrat.libCheque = em.find(Producteur.class, idProducteur).nom.toLowerCase();
		modeleContrat.dateRemiseCheque = dateFinInscription;
		modeleContrat.premierCheque = DateUtils.firstDayInMonth(modeleContrat.dateDebut);
		modeleContrat.dernierCheque = DateUtils.firstDayInMonth(modeleContrat.dateFin);

		new GestionContratService().saveNewModeleContrat(modeleContrat);

	}

	private List<LigneContratDTO> getProduits(int idProducteur, EntityManager em)
	{
		List<LigneContratDTO> res = new ArrayList<>();

		switch (idProducteur)
		{
		// VACHE
		case 3011:
			add(res, em, 120, 3012);
			add(res, em, 200, 3013);
			add(res, em, 390, 3014);
			add(res, em, 250, 3015);
			add(res, em, 340, 3016);
			add(res, em, 205, 3017);
			break;

		// PAIN
		case 3029:
			add(res, em, 400, 3030);
			add(res, em, 400, 3031);
			add(res, em, 400, 3032);
			add(res, em, 400, 3033);
			add(res, em, 400, 3034);
			add(res, em, 400, 3035);
			break;
		// CHEVRE
		case 3002:

			add(res, em, 130, 3003);
			add(res, em, 130, 3004);
			add(res, em, 130, 3005);
			add(res, em, 240, 3006);
			add(res, em, 70, 3007);
			add(res, em, 80, 3008);
			add(res, em, 350, 3009);
			add(res, em, 440, 3010);
			break;
		// BREBIS
		case 3019:
			add(res, em, 350, 3020);
			add(res, em, 250, 3023);
			add(res, em, 270, 3024);
			add(res, em, 160, 3025);
			add(res, em, 1500, 3026);
			add(res, em, 750, 3027);
			add(res, em, 375, 3028);
			add(res, em, 1000, 4340);
			add(res, em, 500, 4341);
			break;

		default:
			break;
		}

		return res;
	}

	private void add(List<LigneContratDTO> res, EntityManager em, int prix, int idProduit)
	{
		LigneContratDTO dto = new LigneContratDTO();

		dto.prix = prix;
		dto.produitId = new Long(idProduit);

		res.add(dto);

	}

	public static void main(String[] args) throws ParseException
	{
		try
		{
			TestTools.init();

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");

			System.out.println("Debut de la generation ...");
			
			AppInstanceDTO dto = new AppInstanceDTO();
			dto.nomAmap = "AMAP1";
			dto.villeAmap = "VILLE";
			dto.url = "http://monamap";
			dto.dateDebut = df.parse("24/07/14");
			dto.dateFin = df.parse("25/09/14");
			dto.dateFinInscription = df.parse("17/09/14");
			
			new DemoService().generateDemoData(dto);
			System.out.println("Fin de la generation.");
		} 
		catch (ConstraintViolationException e)
		{
			e.printStackTrace();
			System.out.println(StackUtils.getConstraints(e));
			
		}
	}

}
