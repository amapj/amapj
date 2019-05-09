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
 package fr.amapj.service.services.utilisateur.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.LongUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.EtatUtilisateur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.access.AccessManagementService;
import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.utilisateur.envoimail.EnvoiMailDTO;
import fr.amapj.service.services.utilisateur.envoimail.EnvoiMailUtilisateurDTO;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;

/**
 * 
 * 
 */
public class UtilisateurUtil
{
	
	/**
	 * Détermine si cet utilisateur a une adresse e mail valide
	 * 
	 * @param u
	 * @return
	 */
	static public boolean canSendMailTo(Utilisateur u)
	{
		return canSendMailTo(u.getEmail());
	}
	
	/**
	 * Détermine si cet utilisateur a une adresse e mail valide
	 * 
	 * @param u
	 * @return
	 */
	static public boolean canSendMailTo(String email)
	{
		if (email==null)
		{
			return false;
		}
		if (email.endsWith("#"))
		{
			return false;
		}
		return true;
	}
	
	
	/**
	 * Convertit une liste d'utilisateurs en une String
	 *  
	 * Exemple : ls = [ "Bob AAA" , "Marc BBBB" , "Paul CCC" ] 
	 * 
	 *  asStringPrenomFirst(ls," et ") =>  "Bob AAA et Marc BBB et Paul CCCC"
	 * 
	 */
	public static String asStringPrenomFirst(List<? extends IUtilisateur> ls,String sep)
	{
		if (ls.size()==0)
		{
			return "";
		}
		
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < ls.size()-1; i++)
		{
			IUtilisateur l = ls.get(i);
			str.append(l.getPrenom()+" "+l.getNom());
			str.append(sep);
		}
		
		IUtilisateur l = ls.get(ls.size()-1);
		str.append(l.getPrenom()+" "+l.getNom());
		return str.toString();
	}
	
	// Méthodes utilitaires sur les modifications en masse

	/**
	 * Retourne la liste des utilisateurs impactés sous un format facilement
	 * utilisable
	 */
	static public String getUtilisateurImpactes(List<Utilisateur> utilisateurs)
	{
		StringBuffer buf = new StringBuffer();
		if (utilisateurs.size() == 0)
		{
			buf.append("Aucun utilisateur n'est impacté par cette modification.<br/>");
			return buf.toString();
		}

		buf.append("Les " + utilisateurs.size() + " utilisateurs suivants sont impactés par cette modification :<br/>");
		for (Utilisateur utilisateur : utilisateurs)
		{
			String warning = "";
			if (UtilisateurUtil.canSendMailTo(utilisateur) == false)
			{
				warning = "<b>Utilisateur sans e mail !</b>";
			}
			buf.append(" - " + utilisateur.getNom() + " " + utilisateur.getPrenom() + warning + "<br/>");
		}
		buf.append("<br/>");

		buf.append("Liste des adresses e-mail :<br/>");
		for (Utilisateur utilisateur : utilisateurs)
		{
			if (UtilisateurUtil.canSendMailTo(utilisateur))
			{
				buf.append(utilisateur.getEmail() + ";");
			}
		}
		return buf.toString();
	}
	
	
	
	/**
	 * Retourne la liste des e mails des utilisateurs en tenant compte des utilisateurs sans e mails 
	 */
	
	static public class EmailInfo
	{
		public int nbUtilisateurAvecEmail;
		
		// Liste des e mails séparés par des ;
		public String utilisateurAvecEmail = "";
		
		public int nbUtilisateurSansEmail;
		
		// Liste des prenom nom séparés par des ;
		public String utilisateurSansEmail = "";
	}
	
	
	static public EmailInfo getEmailsInfos(List<Utilisateur> utilisateurs)
	{
		EmailInfo res = new EmailInfo();
		
		for (Utilisateur utilisateur : utilisateurs)
		{
			
			if (UtilisateurUtil.canSendMailTo(utilisateur))
			{
				res.nbUtilisateurAvecEmail++;
				res.utilisateurAvecEmail = res.utilisateurAvecEmail + utilisateur.getEmail()+";";
			}
			else
			{
				res.nbUtilisateurSansEmail++;
				res.utilisateurSansEmail =  res.utilisateurSansEmail + utilisateur.getNom() + " " + utilisateur.getPrenom()+";";
			}
		}
		return res;
	}
	
	


}
