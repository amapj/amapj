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
 package fr.amapj.service.services.permanence.periode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.common.SQLUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.EtatPeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceDate;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceUtilisateur;
import fr.amapj.model.models.permanence.periode.PermanenceRole;
import fr.amapj.model.models.permanence.reel.PermanenceCell;
import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.permanence.role.PermanenceRoleService;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;

/**
 * Permet la gestion des periodes de permanences
 * 
 */
public class PeriodePermanenceService
{
	
	
	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES PERIODES DE PERMANENCE au format SMALL
	
	/**
	 * Permet de charger la liste de tous les producteurs
	 */
	@DbRead
	public List<SmallPeriodePermanenceDTO> getAllPeriodePermanence()
	{
		EntityManager em = TransactionHelper.getEm();
		
		List<SmallPeriodePermanenceDTO> res = new ArrayList<>();
		
		Query q = em.createQuery("select p from PeriodePermanence p");
			
		List<PeriodePermanence> ps = q.getResultList();
		for (PeriodePermanence p : ps)
		{
			SmallPeriodePermanenceDTO dto = createSmallPeriodePermanenceDTO(em,p);
			res.add(dto);
		}
		
		return res;
		
	}

	/**
	 * Chargement partiel d'une periode de permanence (format SMALL)
	 */
	public SmallPeriodePermanenceDTO createSmallPeriodePermanenceDTO(EntityManager em, PeriodePermanence p)
	{
		SmallPeriodePermanenceDTO dto = new SmallPeriodePermanenceDTO();
		
		dto.id = p.id;
		dto.nom = p.nom;
		
		DateInfo di = getDateDebutFin(em, p);
		
		dto.dateDebut = di.dateDebut;
		dto.dateFin = di.dateFin;
		dto.nbDatePerm = di.nbDatePerm;
		dto.etat = p.etat;

		dto.pourcentageInscription = getPourcentageInscription(p,em);
		
		return dto;
	}
	
	
	// CHARGEMENT DETAILLE d'une periode de permanence 
	
	@DbRead
	public PeriodePermanenceDTO loadPeriodePermanenceDTO(Long idPeriodePermanence)
	{
		EntityManager em = TransactionHelper.getEm();
		PeriodePermanence p = em.find(PeriodePermanence.class, idPeriodePermanence);
		
		PeriodePermanenceDTO dto = createPeriodePermanenceDTO(em, p);
		
		// Ajout des informations de date
		Query q = em.createQuery("select c from PeriodePermanenceDate c WHERE c.periodePermanence=:p ORDER BY c.datePerm");
		q.setParameter("p",p);
		List<PeriodePermanenceDate> pds = q.getResultList();
		for (PeriodePermanenceDate pd : pds)
		{
			PeriodePermanenceDateDTO ddto = createPeriodePermanenceDateDTO(pd);
			dto.datePerms.add(ddto);
		}
		
		// Ajout des informations sur les cellules - le tri sur indx permet d'avoir des listes triées correctement par date 
		q = em.createQuery("select c from PermanenceCell c WHERE c.periodePermanenceDate.periodePermanence=:p order by c.indx");
		q.setParameter("p", p);
		List<PermanenceCell> pcs = q.getResultList();
		for (int i = 0; i < pcs.size(); i++)
		{
			PermanenceCell pc = pcs.get(i);
		
			PermanenceCellDTO pcDto = createPermanenceCellDTO(pc);
			
			PeriodePermanenceDateDTO ddto = findPeriodePermanenceDateDTO(dto.datePerms,pc.periodePermanenceDate.id);
			ddto.permanenceCellDTOs.add(pcDto);
		}
		
		// Calcul des libellés
		for (PeriodePermanenceDateDTO ddto : dto.datePerms)
		{
			for (int i = 0; i < ddto.permanenceCellDTOs.size(); i++)
			{
				PermanenceCellDTO pcDto = ddto.permanenceCellDTOs.get(i);
				pcDto.lib = computeLib(pcDto.nomRole, pcDto.idRole, ddto.permanenceCellDTOs, i);
			}
		}
		
		
		
		// Ajout des informations utilisateurs devant s'inscrire 
		q = em.createQuery("select d from PeriodePermanenceUtilisateur d WHERE d.periodePermanence=:p ORDER BY d.utilisateur.nom,d.utilisateur.prenom,d.utilisateur.id");
		q.setParameter("p",p);
		List<PeriodePermanenceUtilisateur> ps = q.getResultList();
		for (PeriodePermanenceUtilisateur ppu : ps)
		{
			PeriodePermanenceUtilisateurDTO adto = createPeriodePermanenceUtilisateurDTO(ppu);
			
			dto.utilisateurs.add(adto);
		}
		
		return dto;
	}
	
	
	
	public PeriodePermanenceDateDTO createPeriodePermanenceDateDTO(PeriodePermanenceDate pd)
	{
		PeriodePermanenceDateDTO ddto = new PeriodePermanenceDateDTO();
		ddto.idPeriodePermanenceDate = pd.id;
		ddto.datePerm = pd.datePerm;
		ddto.nbPlace = pd.nbPlace;
		ddto.regleInscription = pd.periodePermanence.regleInscription;
		
		return ddto;
	}
	
	
	public PermanenceCellDTO createPermanenceCellDTO(PermanenceCell pc)
	{
		PermanenceCellDTO pcDto = new PermanenceCellDTO();
		
		pcDto.idPermanenceCell = pc.id;
		
		if (pc.periodePermanenceUtilisateur!=null)
		{
			Utilisateur u = pc.periodePermanenceUtilisateur.utilisateur;
			
			pcDto.idUtilisateur = u.getId();
			pcDto.nom = u.getNom();
			pcDto.prenom = u.getPrenom();
			pcDto.idPeriodePermanenceUtilisateur = pc.periodePermanenceUtilisateur.id;
		}
		
		pcDto.idRole = pc.permanenceRole.id;
		pcDto.nomRole = pc.permanenceRole.nom;
		pcDto.dateNotification = pc.dateNotification;
		
		pcDto.lib = null; // sera calculé plus tard 
		
		return pcDto;
	}
	
	/**
	 * Callcul du libelle complet du role
	 * @param nomRole
	 * @param permanenceCellDTOs
	 * @return
	 */
	private String computeLib(String nomRole, Long idRole,List<PermanenceCellDTO> permanenceCellDTOs,int i)
	{
		if (isUniqueRole(idRole,permanenceCellDTOs))
		{
			return nomRole;
		}
		else
		{
			return nomRole+" ("+findNbOccurrence(idRole,permanenceCellDTOs,i)+")";
		}
	}

	/**
	 * Permet de determiner le nombre d'occurrence de ce role dans la liste, en prenant en compte 
	 * les elements depuis 0 jusuq'au rang <code>rang</code>
	 */
	private int findNbOccurrence(Long idRole,List<PermanenceCellDTO> permanenceCellDTOs, int rang)
	{
		int nb=0;
		for (int i = 0; i <= rang; i++)
		{
			PermanenceCellDTO pc = permanenceCellDTOs.get(i);
			if (pc.idRole==idRole)
			{
				nb++;
			}
		}
		return nb;
	}

	private boolean isUniqueRole(Long idRole, List<PermanenceCellDTO> permanenceCellDTOs)
	{
		int nb=0;
		for (PermanenceCellDTO pc : permanenceCellDTOs)
		{
			if (pc.idRole==idRole)
			{
				nb++;
			}
		}
		
		return nb==1;
	}

	public PeriodePermanenceUtilisateurDTO createPeriodePermanenceUtilisateurDTO(PeriodePermanenceUtilisateur ppu)
	{
		PeriodePermanenceUtilisateurDTO adto = new PeriodePermanenceUtilisateurDTO();
		
		adto.idPeriodePermanenceUtilisateur = ppu.id;
		adto.idUtilisateur = ppu.utilisateur.getId();
		adto.nom = ppu.utilisateur.getNom();
		adto.prenom = ppu.utilisateur.getPrenom();
		adto.nbParticipation = ppu.nbParticipation;
		
		return adto;
	}
	

	
	/**
	 * Chargement complet  d'une periode de permanence 
	 */
	private PeriodePermanenceDTO createPeriodePermanenceDTO(EntityManager em, PeriodePermanence p)
	{
		PeriodePermanenceDTO dto = new PeriodePermanenceDTO();
		
		dto.id = p.id;
		dto.nom = p.nom;
		dto.description = p.description;
		dto.flottantDelai = p.flottantDelai;
		
		DateInfo di = getDateDebutFin(em, p);
		
		dto.dateDebut = di.dateDebut;
		dto.dateFin = di.dateFin;
		dto.nbDatePerm = di.nbDatePerm;

		dto.nbUtilisateur = getNbPeriodePermanenceUtilisateur(p,em);
		
		dto.dateFinInscription = p.dateFinInscription;
		dto.etat = p.etat;
		dto.nature = p.nature;
		dto.pourcentageInscription = getPourcentageInscription(p,em);
		dto.regleInscription = p.regleInscription;
		
		return dto;
	}
	
	
	private PeriodePermanenceDateDTO findPeriodePermanenceDateDTO(List<PeriodePermanenceDateDTO> datePerms, Long id)
	{
		for (PeriodePermanenceDateDTO detailPeriodePermanenceDTO : datePerms)
		{
			if (detailPeriodePermanenceDTO.idPeriodePermanenceDate.equals(id))
			{
				return detailPeriodePermanenceDTO;
			}
		}
		throw new AmapjRuntimeException();
	}
	
	
	// PARTIE CHARGEMENT D UNE DATE DE PERMANENCE
	
	/**
	 * Permet de charger une date de permanence seulement, et non toute la période 
	 * 
	 * @param idPeriodePermanenceDate
	 * @return
	 */
	@DbRead
	public PeriodePermanenceDateDTO loadOneDatePermanence(Long idPeriodePermanenceDate)
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodePermanenceDate ppd = em.find(PeriodePermanenceDate.class, idPeriodePermanenceDate);
		
		PeriodePermanenceDateDTO dto = createPeriodePermanenceDateDTO(ppd);
		
		// Ajout des informations sur les cellules - le tri sur indx permet d'avoir des listes triées correctement par date - a ne pas enlever 
		Query q = em.createQuery("select c from PermanenceCell c WHERE c.periodePermanenceDate=:ppd order by c.indx");
		q.setParameter("ppd", ppd);
				
		List<PermanenceCell> pcs = q.getResultList();
		for (int i = 0; i < pcs.size(); i++)
		{
			PermanenceCell pc = pcs.get(i);
			
			PermanenceCellDTO pcDto = createPermanenceCellDTO(pc);
			dto.permanenceCellDTOs.add(pcDto);
		}
		
		// Calcul des libellés
		for (int i = 0; i < dto.permanenceCellDTOs.size(); i++)
		{
			PermanenceCellDTO pcDto = dto.permanenceCellDTOs.get(i);
			pcDto.lib = computeLib(pcDto.nomRole, pcDto.idRole, dto.permanenceCellDTOs, i);
		}
		
				
		return dto;
	}
	
	// PARTIE CHARGEMENT D UNE LISTE DE DATE DE PERMANENCE
	
	public List<PeriodePermanenceDateDTO> getAllDistributionsActif(EntityManager em, Date dateDebut, Date dateFin)
	{
		Query q = em.createQuery("select ppd from PeriodePermanenceDate ppd WHERE " +
				"ppd.periodePermanence.etat=:etat and " +
				"ppd.datePerm>=:deb and " +
				"ppd.datePerm<=:fin " +
				"order by ppd.datePerm");
		q.setParameter("etat",EtatPeriodePermanence.ACTIF);
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		
		List<PeriodePermanenceDateDTO> res = new ArrayList<PeriodePermanenceDateDTO>();
		
		List<PeriodePermanenceDate> ppds = q.getResultList();
		for (PeriodePermanenceDate ppd : ppds)
		{
			PeriodePermanenceDateDTO dto = loadOneDatePermanence(ppd.id);
			res.add(dto);
		}
		
		return res;
	}
	
	
	
	
	
	// FONCTIONS UTILITAIRES DE CALCUL 
	
	private int getNbPeriodePermanenceUtilisateur(PeriodePermanence p, EntityManager em)
	{
		Query q = em.createQuery("select count(c) from PeriodePermanenceUtilisateur c WHERE c.periodePermanence=:p");
		q.setParameter("p",p);
		return SQLUtils.toInt(q.getSingleResult());
	}
	
	
	private int getNbInscription(PeriodePermanence p, EntityManager em)
	{
		Query q = em.createQuery("select count(c) from PermanenceCell c WHERE c.periodePermanenceDate.periodePermanence=:p and c.periodePermanenceUtilisateur is not null");
		q.setParameter("p",p);
		int nbInscrits = SQLUtils.toInt(q.getSingleResult());
		
		return nbInscrits;
		
	}	
	
	
	private int getNbTotalPlace(PeriodePermanence p, EntityManager em)
	{
		Query q = em.createQuery("select sum(c.nbPlace) from PeriodePermanenceDate c WHERE c.periodePermanence=:p");
		q.setParameter("p",p);
		int nbPlaces = SQLUtils.toInt(q.getSingleResult());
		return nbPlaces;
	}
	
	
	private int getPourcentageInscription(PeriodePermanence p, EntityManager em)
	{
		int nbPlaces = getNbTotalPlace(p, em);	
		int nbInscrits = getNbInscription(p, em);
		
		if (nbPlaces==0)
		{
			return 100;
		}
		
		return (nbInscrits*100)/nbPlaces;
		
	}


	static public class DateInfo
	{
		public Date dateDebut;
		public Date dateFin;
		public int nbDatePerm;
	}
	
	public DateInfo getDateDebutFin(EntityManager em, PeriodePermanence p)
	{
		DateInfo di = new DateInfo();
		
		Query q = em.createQuery("select min(c.datePerm),max(c.datePerm),count(c.datePerm) from PeriodePermanenceDate c WHERE c.periodePermanence=:p");
		q.setParameter("p",p);
		
		Object[] res = (Object[]) q.getSingleResult();
		
		di.dateDebut = (Date) res[0];
		di.dateFin = (Date) res[1];
		di.nbDatePerm = SQLUtils.toInt(res[2]);
		
		return di;
	}


	

	// PARTIE CREATION DES PERIODES DE PERMANENCE
	@DbWrite
	public Long create(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodePermanence p = new PeriodePermanence();
		
		// Informations d'entete
		p.nom = dto.nom;
		p.description = dto.description;
		p.dateFinInscription = dto.dateFinInscription;
		p.flottantDelai = dto.flottantDelai;
		p.etat = EtatPeriodePermanence.CREATION;
		p.nature = dto.nature;
		p.regleInscription = dto.regleInscription;
		
		em.persist(p);

		// Création de toutes les lignes pour chacune des dates
		if (dto.datePerms.size()==0)
		{
			throw new AmapjRuntimeException("Vous ne pouvez pas créer une periode de permanence avec 0 date de permanence");
		}
		
		// Recuperation du role par defaut 
		PermanenceRole defaultRole = new PermanenceRoleService().getOrCreateDefaultRole(em);
		
		//
		for (PeriodePermanenceDateDTO date : dto.datePerms)
		{
			PeriodePermanenceDate md = new PeriodePermanenceDate();
			md.periodePermanence = p;
			md.datePerm = date.datePerm;
			md.nbPlace = date.nbPlace;
			em.persist(md);
			
			// Création de toutes les cellules de permanences
			for (int i = 0; i < date.nbPlace; i++)
			{
				PermanenceCell pc = new PermanenceCell();
				pc.periodePermanenceDate = md;
				pc.permanenceRole = defaultRole;
				pc.indx = i;
				
				em.persist(pc);
			}
			
			
		}
		

		
		
		// Affcetation des adherents 
		for (PeriodePermanenceUtilisateurDTO detail : dto.utilisateurs)
		{
			Utilisateur utilisateur = em.find(Utilisateur.class, detail.idUtilisateur);
			
			PeriodePermanenceUtilisateur ppu = new PeriodePermanenceUtilisateur();
			ppu.nbParticipation = detail.nbParticipation;
			ppu.utilisateur = utilisateur;
			ppu.periodePermanence = p;
			
			em.persist(ppu);
		}
		
		return p.id;
	}
	
	
	/**
	 * Permet de remplir la liste des dates de permances à partir des infos générales, 
	 * et ensuite l'utilisateur pourra affiner son choix de date 
	 * 
	 */
	public void fillDatePermanence(PeriodePermanenceDTO dto)
	{
		List<Date> dates = getAllDates(dto.dateDebut, dto.dateFin,dto.frequencePermanence);
		
		for (Date date : dates)
		{
			PeriodePermanenceDateDTO d = new PeriodePermanenceDateDTO();
			d.datePerm = date;
			d.nbPlace = dto.nbPlaceParDate;
			
			dto.datePerms.add(d);
		}
		
	}


	private List<Date> getAllDates(Date dateDebut, Date dateFin, FrequencePermanence frequence)
	{
		if (frequence == FrequencePermanence.UNE_FOIS_PAR_MOIS)
		{
			return getAllDatesUneFoisParMois(dateDebut, dateFin);
		}
		
		
		List<Date> res = new ArrayList<Date>();

		int cpt = 0;
		res.add(dateDebut);

	
		int delta = 0;
		if (frequence == FrequencePermanence.UNE_FOIS_PAR_SEMAINE)
		{
			delta = 7;
		} 
		else if (frequence == FrequencePermanence.QUINZE_JOURS)
		{
			delta = 14;
		}

		dateDebut = DateUtils.addDays(dateDebut, delta);

		while (dateDebut.before(dateFin) || dateDebut.equals(dateFin))
		{
			cpt++;
			res.add(dateDebut);
			dateDebut = DateUtils.addDays(dateDebut, delta);

			if (cpt > 1000)
			{
				throw new AmapjRuntimeException("Erreur dans la saisie des dates");
			}
		}

		return res;
	}


	/**
	 * Calcul permettant d'avoir par exemple tous les 1er jeudi du mois
	 */
	private List<Date> getAllDatesUneFoisParMois(Date dateDebut, Date dateFin)
	{
		List<Date> res = new ArrayList<Date>();

		int cpt = 0;
		res.add(dateDebut);

		int rank = DateUtils.getDayOfWeekInMonth(dateDebut);
		int delta = 7;

		dateDebut = DateUtils.addDays(dateDebut, delta);

		while (dateDebut.before(dateFin) || dateDebut.equals(dateFin))
		{
			cpt++;
			if (DateUtils.getDayOfWeekInMonth(dateDebut) == rank)
			{
				res.add(dateDebut);
			}
			dateDebut = DateUtils.addDays(dateDebut, delta);

			if (cpt > 1000)
			{
				throw new RuntimeException("Erreur dans la saisie des dates");
			}
		}

		return res;
	}
	
	

	


	// PARTIE SUPPRESSION

	/**
	 * Permet de supprimer une periode de permanence 
	 */
	@DbWrite
	public void deletePeriodePermanence(Long id)  throws UnableToSuppressException
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodePermanence p = em.find(PeriodePermanence.class, id);
		
		int nbInscrits = getNbInscription(p, em);
		if (nbInscrits>0)
		{
			String str = "Vous ne pouvez plus supprimer cette periode de permanence<br/>"+
					 "car "+nbInscrits+" adhérents sont déjà inscrits<br/><br/>."+
					 "Si vous souhaitez réellement supprimer cette periode de permanence,<br/>"+
					 "allez tout d'abord dans \"Affectation des permanences\", puis vous supprimez les inscriptions";
			throw new UnableToSuppressException(str);
		}

		suppressAllPermanenceCell(em,p);
		suppressAllDates(em, p);
		suppressAllPermanenceUtilisateurs(em, p);
		

		em.remove(p);
	}


	private void suppressAllDates(EntityManager em, PeriodePermanence p)
	{
		Query q = em.createQuery("select d from PeriodePermanenceDate d WHERE d.periodePermanence=:p");
		q.setParameter("p",p);
		SQLUtils.deleteAll(em, q);
	}
	
	private void suppressAllPermanenceUtilisateurs(EntityManager em, PeriodePermanence p)
	{
		Query q = em.createQuery("select d from PeriodePermanenceUtilisateur d WHERE d.periodePermanence=:p");
		q.setParameter("p",p);
		SQLUtils.deleteAll(em, q);
	}
	
	private void suppressAllPermanenceCell(EntityManager em, PeriodePermanence p)
	{
		Query q = em.createQuery("select d from PermanenceCell d WHERE d.periodePermanenceDate.periodePermanence=:p");
		q.setParameter("p",p);
		SQLUtils.deleteAll(em, q);
	}
	
	
	
	// PARTIE AFFECTATION DES UTILISATEURS 
	
	/**
	 */
	@DbRead
	public void fillUtilisateur(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On charge les utilisateurs
		dto.utilisateurs.clear();
		List<Utilisateur> utilisateurs = getAllUtilisateursCotisants(em,dto.idPeriodeCotisation);
		for (Utilisateur utilisateur : utilisateurs)
		{
			PeriodePermanenceUtilisateurDTO e = createAffectAdherentDetailDTO(utilisateur);
			dto.utilisateurs.add(e);
		}
		
	}

	/**
	 * Retourne la liste des utilisateurs cotisants 
	 * 
	 * Si idPeriodeCotisation est null, alors retourne tous les utilisateurs actifs
	 * 
	 * @return
	 */
	public List<Utilisateur> getAllUtilisateursCotisants(EntityManager em,Long idPeriodeCotisation)
	{
		if (idPeriodeCotisation==null)
		{
			return new UtilisateurService().getUtilisateurs(false);
			
		}
		else
		{
			return new GestionCotisationService().getAllUtilisateurAvecAdhesion(idPeriodeCotisation);
		}
	}


	
	@DbRead
	public PeriodePermanenceUtilisateurDTO createAffectAdherentDetailDTO(Long userId)
	{
		EntityManager em = TransactionHelper.getEm();
		Utilisateur utilisateur = em.find(Utilisateur.class, userId);
		return createAffectAdherentDetailDTO(utilisateur);
	}
	
	 
	public PeriodePermanenceUtilisateurDTO createAffectAdherentDetailDTO(Utilisateur utilisateur)
	{
		PeriodePermanenceUtilisateurDTO e = new PeriodePermanenceUtilisateurDTO();
		
		e.idUtilisateur = utilisateur.getId();
		e.nom = utilisateur.getNom();
		e.prenom = utilisateur.getPrenom();
		
		return e;
	}


	
	/**
	 * Permet de calculer le nombre total de place de permanence sur la periode 
	 */
	private int getNbTotalPlace(PeriodePermanenceDTO dto)
	{
		int nbPlaces = 0;
		for (PeriodePermanenceDateDTO d : dto.datePerms)
		{
			nbPlaces = nbPlaces + d.nbPlace;
		}
		return nbPlaces;
	}
	
	/**
	 * Permet de calculer le nombre total de participation  sur la periode 
	 */
	private int getNbTotalParticipation(PeriodePermanenceDTO dto)
	{
		int nbParticipation = 0;
		for (PeriodePermanenceUtilisateurDTO detail : dto.utilisateurs)
		{
			nbParticipation = nbParticipation+detail.nbParticipation;
		}
		return nbParticipation;
	}
	
	
	
	
	public void fillNombreParPersonne(PeriodePermanenceDTO dto)
	{				
		int nbDate = dto.datePerms.size();
		int nbPlaces = getNbTotalPlace(dto);
		
		
		int nbParPersonne = nbPlaces / dto.utilisateurs.size(); 
		
		String message =	"Il y a au total "+nbDate+" dates de permanences<br/>"+
							"et "+dto.utilisateurs.size()+" adhérents participants<br>";
		
		int reliquat = nbPlaces - nbParPersonne*dto.utilisateurs.size();
		
		int borneSup = (nbParPersonne+1)*dto.utilisateurs.size()-nbPlaces;
		
		if (reliquat==0)
		{
			message = message +"Cela fait donc exactement "+nbParPersonne+" participations par personne";
			dto.nbParPersonne = nbParPersonne;
		}
		else
		{
			message = message+"Cela fait donc soit <ul>"+
							"<li>"+nbParPersonne+" participations par personne et il y aura "+reliquat+" permanences avec une personne en moins</li>"+
							"<li>"+(nbParPersonne+1)+" participations par personne et il y aura "+borneSup+" permanences avec une personne en plus</li></ul>";
			dto.nbParPersonne = nbParPersonne+1; 
		}
		
		dto.message = message;
	}

	

	
	// GESTION DE L'ETAT 
	
	@DbWrite
	public void updateEtat(EtatPeriodePermanence newValue, Long idPeriodePermanence)
	{
		EntityManager em = TransactionHelper.getEm();
		PeriodePermanence p = em.find(PeriodePermanence.class, idPeriodePermanence);
		p.etat = newValue;
	}

	
	
	// GESTION DES LOCKS 
	
	
	/**
	 * Permet de locker une date précise avant d'ajouter ou de supprimer des participants 
	 * 
	 * Attention, la transaction em doit être de type écriture 
	 */
	public void lockOneDate(EntityManager em,Long idPeriodePermanenceDate)
	{
		Query q = em.createQuery("select c from PeriodePermanenceDate c WHERE c.id=:ppd");
		q.setParameter("ppd", idPeriodePermanenceDate);
		q.setLockMode(LockModeType.PESSIMISTIC_READ);
		
		List<PeriodePermanenceDate> us = q.getResultList();
		if (us.size()!=1)
		{
			throw new AmapjRuntimeException("Lock impossible size ="+us.size());
		}
	}
	
	
	// PARTIE BILAN D'UNE PERIODE DE PERMANENCE
	
	@DbRead
	public String computeBilan(PeriodePermanenceDTO dto)
	{
		// Nombre de place total 
		int nbPlaceTotal = getNbTotalPlace(dto);
		
		// Nb de participations totales
		int nbParticipations = getNbTotalParticipation(dto);
		
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Nom de la période de permanence :"+dto.nom+"<br/><br/>");
		sb.append("Nombre de dates de permanences : "+dto.datePerms.size()+"<br/>");
		sb.append("Nombre moyen de personnes à chaque date : "+FormatUtils.div2int2digit(nbPlaceTotal, dto.datePerms.size())+"<br/>");
		sb.append("Soit un total de  "+nbPlaceTotal+" places de permanences à occuper.<br/><br/>");
		
		
		sb.append("Nombre de personnes pouvant s'inscrire : "+dto.utilisateurs.size()+"<br/>");
		sb.append("Nombre moyen de participation par personne : "+FormatUtils.div2int2digit(nbParticipations, dto.utilisateurs.size())+"<br/>");
		sb.append("Soit un total de  "+nbParticipations+" participations totales.<br/><br/>");
		
		
		if (nbPlaceTotal==nbParticipations)
		{
			sb.append("Le nombre de participations est égale au nombre de places. Parfait ! <br/><br/>");
		}
		else if (nbPlaceTotal<nbParticipations)
		{
			sb.append("Il y a moins de places que de participations. Toutes les permanences seront réalisées , mais certaines personnes feront moins de permanence que prévu.<br/><br/>");
		}
		else 
		{
			sb.append("Il y a plus de places que de participations. Il manquera des personnes à certaines permanences.<br/><br/>");
		}
		
		// Dans le cas d'une periode deja existante 
		if (dto.id!=null)
		{	
			sb.append("Avancement des inscriptions:<br/>");
			EntityManager em = TransactionHelper.getEm();
			PeriodePermanence p = em.find(PeriodePermanence.class, dto.id);
			int nbInscrits = getNbInscription(p, em);
			sb.append("Il y a pour le moment "+nbInscrits+" inscriptions sur les "+nbPlaceTotal+" places à occuper,<br/>");
			sb.append("soit un pourcentage de couverture de "+FormatUtils.div2int2digit(nbInscrits*100, nbPlaceTotal)+"%.<br/>");
		}
		
		
		return sb.toString();
	}
}
