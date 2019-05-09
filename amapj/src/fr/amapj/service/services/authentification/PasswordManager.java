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
 package fr.amapj.service.services.authentification;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.UI;

import fr.amapj.common.DateUtils;
import fr.amapj.common.RandomUtils;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.model.models.fichierbase.EtatUtilisateur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.saas.TypLog;
import fr.amapj.service.engine.sudo.SudoManager;
import fr.amapj.service.services.access.AccessManagementService;
import fr.amapj.service.services.appinstance.LogAccessDTO;
import fr.amapj.service.services.logview.LogViewService;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.service.services.session.SessionParameters;
import fr.amapj.view.engine.ui.AmapJLogManager;


public class PasswordManager
{
	private static final Logger logger = LogManager.getLogger();
	
	public static final AuthentificationCounter authentificationCounter = new AuthentificationCounter();
	
	public PasswordEncryptionService passwordEncryptionService = new PasswordEncryptionService();
	
	public PasswordManager()
	{
		
	}
	
	
	/**
	 * Permet de verifier le user password
	 * Ceci est vérifié dans une transaction en lecture
	 * 
	 * Retourne null si tout est ok, une explication sur l'erreur sinon
	 */
	@DbRead
	public String checkUser(String email,String password, String sudo)
	{
		EntityManager em = TransactionHelper.getEm();
		
		if (((password==null) || password.equals("")) && (sudo==null) )
		{
			return "Vous n'avez pas saisi le mot de passe";
		}
		
		Utilisateur u = findUser(email, em);
		
		if (u==null)
		{
			authentificationCounter.addUnknow();
			return "Adresse e-mail ou mot de passe incorrect";
		}
		
		if (u.getEtatUtilisateur()==EtatUtilisateur.INACTIF)
		{
			return "Votre compte a été désactivé car vous n'êtes plus membre de l'AMAP.";
		}
		
		String msg = checkCredential(u,password,sudo);
		
		// Récupération d'un ensemble d'information
		String ip = UI.getCurrent().getPage().getWebBrowser().getAddress();
		String browser = SessionManager.getAgentName(UI.getCurrent());
		String dbName = DbUtil.getCurrentDb().getDbName();
		
		// Si authentification incorrect
		if (msg!=null)
		{
			authentificationCounter.addBadPassword();
			logger.info("Authentification en echec pour ip={} browser={} dbName={} msg={}",ip,browser,dbName,msg);
			return msg;
		}
		
		
		// Si password ok :		
		
		// On mémorise l'accès dans la base de données du master
		LogAccessDTO logAccessDTO = new LogViewService().saveAccess(u.getNom(),u.getPrenom(),u.getId(),ip,browser,dbName,TypLog.USER,(sudo!=null));
						
		// On sauveagrde les paramètres de session
		SessionParameters p = new SessionParameters();
		p.userId = u.getId();
		p.userRole = new AccessManagementService().getUserRole(u,em);
		p.userNom = u.getNom();
		p.userPrenom = u.getPrenom();
		p.userEmail = email;
		p.dateConnexion = logAccessDTO.dateIn;
		p.logId = logAccessDTO.id;
		p.isSudo = (sudo!=null);
		p.logFileName = logAccessDTO.logFileName;
		SessionManager.setSessionParameters(p);
		
		
		
		
		return null;
	}
	
	
	private String checkCredential(Utilisateur u, String password, String sudo)
	{
		if (sudo!=null)
		{
			return checkCredentialBySudo(u,sudo);
		}
		else
		{
			return checkCredentialByPassword(u,password);
		}
	}


	private String checkCredentialBySudo(Utilisateur u, String sudo)
	{
		String nomInstance = DbUtil.getCurrentDb().getDbName();
		String session = UI.getCurrent().getSession().getCsrfToken();
		
		if (SudoManager.authenticate(sudo,u.getId(),nomInstance,session)==false)
		{
			return "Incorrect";
		}
		else
		{
			return null;
		}
	}


	private String checkCredentialByPassword(Utilisateur u, String password)
	{
		byte[] encryptedPassword = toByteArray(u.getPassword());
		byte[] salt = toByteArray(u.getSalt());

		
		// Verification du password
		if (passwordEncryptionService.authenticate(password, encryptedPassword, salt)==false)
		{
			return "Adresse e-mail ou mot de passe incorrect";
		}
		else
		{
			return null;
		}
	}


	/**
	 * Retrouve l'utilisateur avec cet e-mail
	 * Retourne null si non trouvé ou autre problème
	 */
	private Utilisateur findUser(String email,EntityManager em)
	{	
		if ((email==null) || email.equals(""))
		{
			return null;
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Utilisateur> cq = cb.createQuery(Utilisateur.class);
		Root<Utilisateur> root = cq.from(Utilisateur.class);
		
		// On ajoute la condition where 
		cq.where(cb.equal(root.get(Utilisateur.P.EMAIL.prop()),email));
		
		List<Utilisateur> us = em.createQuery(cq).getResultList();
		if (us.size()==0)
		{
			return null;
		}
		
		if (us.size()>1)
		{
			logger.warn("Il y a plusieurs utilisateurs avec l'adresse "+email);
			return null;
		}

		return us.get(0);
	}
	
	
	/**
	 * Permet de changer le password
	 * Ceci est fait dans une transaction en ecriture  
	 */
	@DbWrite
	public boolean setUserPassword(final Long userId,final String clearPassword)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Utilisateur r = em.find(Utilisateur.class, userId);
		if (r==null)
		{
			logger.warn("Impossible de retrouver l'utilisateur avec l'id "+userId);
			return false;
		}
		
		if (r.getSalt()==null)
		{
			r.setSalt(fromByteArray(passwordEncryptionService.generateSalt()));
		}
		
		byte[] salt = toByteArray(r.getSalt());
		byte[] encryptedPass = passwordEncryptionService.getEncryptedPassword(clearPassword, salt);
		r.setPassword(fromByteArray(encryptedPass));
		
		// A chaque changement du mot de passe on supprime la ré initilisation par mail
		r.setResetPasswordDate(null);
		r.setResetPasswordSalt(null);
		
		return true;
	
	}
	

	/**
	 * Permet de transformer une chaine base 64 en tableau de byte
	 * @param password
	 * @return
	 */
	private byte[] toByteArray(String base64str)
	{
		return Base64.decodeBase64(base64str.getBytes());
	}
	
	/**
	 * Permet de transformer un tableau de byte  en une chaine en base 64  
	 * @param password
	 * @return
	 */
	private String fromByteArray(byte[] flux)
	{
		return new String(Base64.encodeBase64(flux));
	}

	
	

	@DbWrite
	public String sendMailForResetPassword(final String email)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Utilisateur u = findUser(email, em);
		
		if (u==null)
		{
			return "Votre adresse e mail est inconnue";
		}
		
		if (u.getEtatUtilisateur()==EtatUtilisateur.INACTIF)
		{
			return "Votre compte a été désactivé car vous n'êtes plus membre de l'AMAP.";
		}
		
		// Impossible pour le master de faire une recuperation de mot de passe 
		List<RoleList> rs = new AccessManagementService().getUserRole(u, em);
		if (rs.contains(RoleList.MASTER))
		{
			return "Impossible";
		}
		
		
		u.setResetPasswordDate(DateUtils.getDate());
		// Génère une clé pour le reset du password , de 20 caractères en minuscules
		u.setResetPasswordSalt(RandomUtils.generatePasswordMin(20));

		ParametresDTO parametresDTO = new ParametresService().getParametres(); 
		
		String link = parametresDTO.getUrl()+"?resetPassword="+u.getResetPasswordSalt();
		
		StringBuffer buf = new StringBuffer();
		buf.append("<h2>"+parametresDTO.nomAmap+"</h2>");
		buf.append("<br/>");
		buf.append("Vous avez demandé la ré initialisation de votre mot de passe");
		buf.append("<br/>");
		buf.append("Merci de cliquer sur le lien ci dessous pour saisir votre nouveau mot de passe");
		buf.append("<br/>");
		buf.append("<br/>");
		buf.append("<a href=\""+link+"\">Cliquez ici pour changer votre mot de passe</a>");
		buf.append("<br/>");
		buf.append("<br/>");
		buf.append("Si vous n'avez pas demandé à changer de mot de passe, merci de ne pas tenir compte de ce mail");
		buf.append("<br/>");
		
		new MailerService().sendHtmlMail(new MailerMessage(email, "Changement de votre mot de passe", buf.toString()));
		
		return null;
		
	}
	

	
	/**
	 * Retrouve l'utilisateur avec ce resetPasswordSald
	 * Retourne null si non trouvé ou autre problème
	 */
	@DbRead
	public Utilisateur findUserWithResetPassword(String resetPasswordSalt)
	{
		EntityManager em = TransactionHelper.getEm();
		
		if ((resetPasswordSalt==null) || resetPasswordSalt.equals(""))
		{
			return null;
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Utilisateur> cq = cb.createQuery(Utilisateur.class);
		Root<Utilisateur> root = cq.from(Utilisateur.class);
		
		// On ajoute la condition where 
		cq.where(cb.equal(root.get(Utilisateur.P.RESETPASSWORDSALT.prop()),resetPasswordSalt));
		
		List<Utilisateur> us = em.createQuery(cq).getResultList();
		if (us.size()==0)
		{
			return null;
		}
		
		if (us.size()>1)
		{
			logger.warn("Il y a plusieurs utilisateurs avec le salt "+resetPasswordSalt);
			return null;
		}

		Utilisateur u =  us.get(0);
		
		if (u.getEtatUtilisateur()==EtatUtilisateur.INACTIF)
		{
			return null;
		}
		
		return u;
	}
	
	
	/**
	 * Permet de signifier la deconnexion d'un utilisateur
	 * 
	 */
	public void disconnect()
	{
		SessionParameters p = SessionManager.getSessionParameters();
		if (p==null)
		{
			return ;
		}
		
		logger.info("Déconnexion réussie pour {} {} {}",p.userNom,p.userPrenom,p.userId);
		
		SessionManager.setSessionParameters(null);
		new LogViewService().endAccess(p.logId,p.getNbError());
		AmapJLogManager.endLog(true,p.logFileName);
		
		
	}
	
	


	public static void main(String[] args)
	{
		TestTools.init();
		new PasswordManager().setUserPassword(new Long(1052), "a");
		//new PasswordManager().setUserPassword(new Long(1052), "e");
		
		//String str = new PasswordManager().generateResetPaswordSalt();
		//System.out.println("str="+str);
	}
	
	
}
