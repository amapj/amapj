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
 package fr.amapj.service.services.advanced.maintenance;

import java.io.IOException;
import java.io.InputStream;

import javax.persistence.EntityManager;

import org.apache.poi.util.IOUtils;

import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;

/**
 * Service pour la maintenance de base 
 *
 */
public class MaintenanceService
{
	
	/**
	 * Permet de vider le cache de la base
	 * Ceci est fait dans une transaction en ecriture  
	 * obligatoire après requete SQL manuelle
	 */
	@DbWrite
	public void resetDatabaseCache()
	{
		EntityManager em = TransactionHelper.getEm();
		em.getEntityManagerFactory().getCache().evictAll();
	}

	
	/**
	 * Lit lenuméro de la version
	 * @return
	 */
	public String getVersion()
	{
		try
		{
			InputStream in = this.getClass().getResourceAsStream("/amapj_version.txt");
			byte[] bs = IOUtils.toByteArray(in);
			return new String(bs);
		} 
		catch (IOException e)
		{
			return "error";
		}
	}
	
}
