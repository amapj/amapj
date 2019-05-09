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
 package fr.amapj.model.engine;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.vaadin.ui.AbstractSelect;

import fr.amapj.model.models.editionspe.EditionSpecifique;


/**
 * Utilitaires sur les Identifiables
 *
 */
@SuppressWarnings("serial")
public class IdentifiableUtil
{
	
	/**
	 * Retourne true si la liste contient un élement avec l'id en paramètre 
	 * 
	 */
	static public boolean contains(List<? extends Identifiable> ls, Long id)
	{
		for (Identifiable identifiable : ls)
		{
			if (identifiable.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	/**
	 * Permet de créer un nouvel identifiable à partir de sa classe 
	 * @return
	 */
	static public Identifiable createNewObject(Class<? extends Identifiable> clazz)
	{
		try
		{
			return clazz.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException("", e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException("", e);
		}
	}

	/**
	 * Si id est null, retourne null
	 * sinon charge l'élement indiqué 
	 */
	static public <T extends Identifiable> T findIdentifiableFromId(Class<T> clazz, Long id, EntityManager em)
	{
		if (id==null)
		{
			return null;
		}
		return em.find(clazz, id);
	}

	/**
	 * Retourne l'id de l'élement séctionné dans un container contenant une liste d'objet identifiable 
	 */
	static public Long getIdOfSelectedItem(AbstractSelect beanTable)
	{
		if (beanTable.getValue()==null)
		{
			return null;
		}
		
		return (Long) beanTable.getItem(beanTable.getValue()).getItemProperty("id").getValue();
	}

	/**
	 * 
	 * Recherche par id, à garder pour faire des recherches par d'autres colonnes 
	 * 
	 */
	static public <T extends Identifiable> T pourInfofindIdentifiableFromIdOldStyle(Class<T> clazz, Long id, EntityManager em)
	{
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> root = cq.from(clazz);

		// On ajoute la condition where
		cq.where(cb.equal(root.get("id"), id));

		List<T> us = em.createQuery(cq).getResultList();
		if (us.size() != 1)
		{
			throw new RuntimeException("Impossible de charger l'objet avec id=" + id);
		}
		return us.get(0);
	}

	/**
	 * If name follows method naming conventions, convert the name to spaced
	 * upper case text. For example, convert "firstName" to "First Name"
	 * 
	 * @param propertyId
	 * @return the formatted caption string
	 */
	public static String createCaptionByPropertyId(Object propertyId)
	{
		String name = propertyId.toString();
		if (name.length() > 0)
		{

			int dotLocation = name.lastIndexOf('.');
			if (dotLocation > 0 && dotLocation < name.length() - 1)
			{
				name = name.substring(dotLocation + 1);
			}
			if (name.indexOf(' ') < 0 && name.charAt(0) == Character.toLowerCase(name.charAt(0)) && name.charAt(0) != Character.toUpperCase(name.charAt(0)))
			{
				StringBuffer out = new StringBuffer();
				out.append(Character.toUpperCase(name.charAt(0)));
				int i = 1;

				while (i < name.length())
				{
					int j = i;
					for (; j < name.length(); j++)
					{
						char c = name.charAt(j);
						if (Character.toLowerCase(c) != c && Character.toUpperCase(c) == c)
						{
							break;
						}
					}
					if (j == name.length())
					{
						out.append(name.substring(i));
					}
					else
					{
						out.append(name.substring(i, j));
						out.append(" " + name.charAt(j));
					}
					i = j + 1;
				}

				name = out.toString();
			}
		}
		return name;
	}




	/**
	 * retourne null si identifiable, sinon retourne son id
	 * @param identifiable
	 * @return
	 */
	public static Long getId(Identifiable identifiable)
	{
		if (identifiable==null)
		{
			return null;
		}
		return identifiable.getId();
	}

}
