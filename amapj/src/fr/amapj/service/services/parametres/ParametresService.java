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
 package fr.amapj.service.services.parametres;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.model.models.param.Parametres;
import fr.amapj.model.models.param.paramecran.PEListeAdherent;
import fr.amapj.model.models.param.paramecran.common.AbstractParamEcran;
import fr.amapj.model.models.param.paramecran.common.ParamEcran;
import fr.amapj.model.models.param.paramecran.common.ParamEcranConverter;
import fr.amapj.service.services.parametres.paramecran.PEListeAdherentDTO;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.menu.MenuList;

/**
 * 
 * 
 */
public class ParametresService
{
	
	static private Long ID_PARAM = new Long(1);
	
	// PARTIE REQUETAGE 
	
	/**
	 * Permet de charger les paramètres
	 */
	@DbRead
	public ParametresDTO getParametres()
	{
		EntityManager em = TransactionHelper.getEm();
		
		Parametres param = em.find(Parametres.class, ID_PARAM);
		
		if (param==null)
		{
			throw new RuntimeException("Il faut insérer les paramètres généraux dans la base");
		}
		
		ParametresDTO dto = new ParametresDTO();
		dto.nomAmap = param.getNomAmap();
		dto.villeAmap = param.getVilleAmap();
		dto.smtpType = param.getSmtpType();
		dto.sendingMailUsername = param.getSendingMailUsername();
		dto.sendingMailPassword = param.getSendingMailPassword();
		dto.sendingMailNbMax = param.getSendingMailNbMax();
		dto.mailCopyTo = param.getMailCopyTo();
		dto.url = param.getUrl();
		dto.backupReceiver = param.getBackupReceiver();
		
		dto.etatPlanningDistribution = param.getEtatPlanningDistribution();
		dto.etatGestionCotisation = param.getEtatGestionCotisation();
		dto.delaiMailRappelPermanence = param.getDelaiMailRappelPermanence();
		dto.envoiMailRappelPermanence = param.getEnvoiMailRappelPermanence();
		dto.titreMailRappelPermanence = param.getTitreMailRappelPermanence();
		dto.contenuMailRappelPermanence = param.getContenuMailRappelPermanence();
		
		dto.envoiMailPeriodique = param.getEnvoiMailPeriodique();
		dto.numJourDansMois = param.getNumJourDansMois();
		dto.titreMailPeriodique = param.getTitreMailPeriodique();
		dto.contenuMailPeriodique = param.getContenuMailPeriodique();
			
		// Champs calculés
		dto.serviceMailActif = false;
		if ((param.getSendingMailUsername()!=null) && (param.getSendingMailUsername().length()>0))
		{
			dto.serviceMailActif = true;
		}
		
		return dto;
		
	}




	// PARTIE MISE A JOUR 

	
	@DbWrite
	public void update(final ParametresDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Parametres param = em.find(Parametres.class, ID_PARAM);
		
		param.setNomAmap(dto.nomAmap);
		param.setVilleAmap(dto.villeAmap);
		param.setSmtpType(dto.smtpType);
		param.setSendingMailUsername(dto.sendingMailUsername);
		param.setSendingMailPassword(dto.sendingMailPassword);
		param.setSendingMailNbMax(dto.sendingMailNbMax);
		param.setMailCopyTo(dto.mailCopyTo);
		param.setUrl(dto.url);
		param.setBackupReceiver(dto.backupReceiver);
		
		param.setEtatPlanningDistribution(dto.etatPlanningDistribution);
		param.setEtatGestionCotisation(dto.etatGestionCotisation);
		param.setDelaiMailRappelPermanence(dto.delaiMailRappelPermanence);
		param.setEnvoiMailRappelPermanence(dto.envoiMailRappelPermanence);
		param.setTitreMailRappelPermanence(dto.titreMailRappelPermanence);
		param.setContenuMailRappelPermanence(dto.contenuMailRappelPermanence);
		
		param.setEnvoiMailPeriodique(dto.envoiMailPeriodique);
		param.setNumJourDansMois(dto.numJourDansMois);
		param.setTitreMailPeriodique(dto.titreMailPeriodique);
		param.setContenuMailPeriodique(dto.contenuMailPeriodique);
		
	}
	
	
	// PARTIE REQUETAGE POUR AVOIR LA LISTE DU PARAMETRAGE DE CHAQUE ECRAN
	

	/**
	 * Permet de charger la liste de tous les parametrages ecrans
	 */
	@DbRead
	public List<ParamEcranDTO> getAllParamEcranDTO()
	{
		EntityManager em = TransactionHelper.getEm();

		List<ParamEcranDTO> res = new ArrayList<>();

		Query q = em.createQuery("select p from ParamEcran p");

		List<ParamEcran> ps = q.getResultList();
		for (ParamEcran p : ps)
		{
			ParamEcranDTO dto = createParamEcranDTO(em, p);
			res.add(dto);
		}

		return res;

	}

	public ParamEcranDTO createParamEcranDTO(EntityManager em, ParamEcran p)
	{
		ParamEcranDTO dto = new ParamEcranDTO();

		dto.id = p.getId();
		dto.menu = p.getMenu();
		dto.content = p.getContent();

		return dto;
	}

	

	// PARTIE MISE A JOUR 
	@DbWrite
	public void update(AbstractParamEcran abstractParamEcran)
	{
		EntityManager em = TransactionHelper.getEm();
		
		boolean create = abstractParamEcran.getId()==null;
		
		ParamEcranDTO dto = ParamEcranConverter.save(abstractParamEcran);

		ParamEcran p;

		if (create)
		{
			p = new ParamEcran();
			p.setMenu(dto.menu);
		} 
		else
		{
			p = em.find(ParamEcran.class, dto.id);
		}

		p.setContent(dto.content);
		

		if (create)
		{
			em.persist(p);
		}

	}


	/**
	 * Permet de charger le parametrage d'un écran 
	 * dans le but de l'utiliser fonctionnellement
	 */
	@DbRead
	public AbstractParamEcran loadParamEcran(MenuList menuList)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ParamEcranDTO p = getParamEcranDTO(menuList,em);

		AbstractParamEcran pe;
		if (p!=null)
		{
			pe = ParamEcranConverter.load(p);
		}
		else
		{
			pe = ParamEcranConverter.getNew(menuList);
		}
		return pe;
	}
	
	
	/**
	 * Permet de charger le parametrage d'un écran particulier, sous la forme d'un DTO
	 * @param em 
	 */
	private ParamEcranDTO getParamEcranDTO(MenuList menu, EntityManager em)
	{
		Query q = em.createQuery("select p from ParamEcran p where p.menu=:m ");
		q.setParameter("m", menu);

		List<ParamEcran> ps = q.getResultList();
		
		if (ps.size()==0)
		{
			return null;
		}
		else if (ps.size()==1)
		{
			return createParamEcranDTO(em, ps.get(0));
		}
		else
		{
			throw new AmapjRuntimeException("Erreur : il y a deux param ecrans pour "+menu);
		}
	}

	
	
	
	/**
	 * Permet de charger le parametrage de l'écran liste adhérent
	 * dans le but de l'utiliser fonctionnellement
	 * 
	 * Cas specifique à cet écran, dans le cas normal utiliser la methode 
	 * public AbstractParamEcran loadParamEcran(MenuList menuList)
	 */
	public PEListeAdherentDTO getPEListeAdherentDTO()
	{
		PEListeAdherent pe = (PEListeAdherent) loadParamEcran(MenuList.LISTE_ADHERENTS);
		
		List<RoleList> roles = SessionManager.getSessionParameters().userRole;
		
		PEListeAdherentDTO ret = new PEListeAdherentDTO();
		ret.canAccessEmail = roles.contains(pe.canAccessEmail);
		ret.canAccessTel1 = roles.contains(pe.canAccessTel1);
		ret.canAccessTel2 = roles.contains(pe.canAccessTel2);
		ret.canAccessAdress = roles.contains(pe.canAccessAdress);	
		
		return ret;
	}
		
}
