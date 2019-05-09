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
 package fr.amapj.service.engine.sudo;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import fr.amapj.common.DateUtils;
import fr.amapj.service.services.appinstance.SudoUtilisateurDTO;


public class SudoManager
{
	
	static private SudoManager mainInstance = new SudoManager();


	public static boolean authenticate(String sudo, Long utilisateurId,String nomInstance,String sessionKey)
	{
		return mainInstance.authenticateNS(sudo, utilisateurId,nomInstance,sessionKey);
	}
	
	public static String addSudoCredential(SudoUtilisateurDTO selected,String nomInstance,String sessionKey)
	{
		return mainInstance.addSudoCredentialNS(selected,nomInstance,sessionKey);
	}

	
	private List<SudoInfo> infos = new ArrayList<>();
	
	
	private String addSudoCredentialNS(SudoUtilisateurDTO selected, String nomInstance, String sessionKey)
	{
		String sudo = generateSudo();
		
		SudoInfo info = new SudoInfo();
		info.creationDate = DateUtils.getDate();
		info.sudoKey = sudo;
		info.utilisateurId = selected.id;
		info.nomInstance = nomInstance;
		info.sessionKey = sessionKey;
		
		infos.add(info);
		
		return sudo;
	}
	
	private String generateSudo()
	{
		try
		{
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

			byte[] salt = new byte[16];
			random.nextBytes(salt);

			Base64 coder = new Base64(true);
			String str= coder.encodeAsString(salt);
			str = str.replace('\r', '0');
			str = str.replace('\n', '0');
			return str;
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Erreur inattendue", e);
		}
	}
	

	private boolean authenticateNS(String sudo, Long utilisateurId, String nomInstance, String sessionKey)
	{
		suppressOldEntries();
		for (SudoInfo info : infos)
		{
			if (	   info.sudoKey.equals(sudo) 
					&& info.utilisateurId.equals(utilisateurId)
					&& info.nomInstance.equals(nomInstance)
					&& info.sessionKey.equals(sessionKey) )
			{
				return true;
			}
		}		
		return false;
	}

	
	/**
	 * Suppression de toutes les entrées présentes de plus de 60 secondes
	 */
	private void suppressOldEntries()
	{
		Date ref = DateUtils.addMinute(DateUtils.getDate(), -1);
		List<SudoInfo> toSuppress = new ArrayList<>();
		
		for (SudoInfo info : infos)
		{
			if (info.creationDate.before(ref))
			{
				toSuppress.add(info);
			}
		}
		infos.removeAll(toSuppress);
	}
	
	
	

}
