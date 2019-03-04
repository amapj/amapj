/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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

import fr.amapj.common.CollectionUtils;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.dbservice.DbService;
import fr.amapj.view.engine.tools.TableItem;

/**
 * Gestion des dates d'une période de permanence
 *
 */
public class PeriodePermanenceDateDTO implements TableItem
{
	public Long idPeriodePermanenceDate;
	
	public Date datePerm;
	
	public int nbPlace;
	
	// Deatil de chaque cellule ou place de permanence
	public List<PermanenceCellDTO> permanenceCellDTOs = new ArrayList<PermanenceCellDTO>();
	

	public Date getDatePerm()
	{
		return datePerm;
	}

	public void setDatePerm(Date datePerm)
	{
		this.datePerm = datePerm;
	}

	public int getNbPlace()
	{
		return nbPlace;
	}

	public void setNbPlace(int nbPlace)
	{
		this.nbPlace = nbPlace;
	}
	
	//
	// CHAMPS CALCULES
	
	public int getNbInscrit()
	{
		int nb=0;
		for (PermanenceCellDTO pc : permanenceCellDTOs)
		{
			if (pc.idUtilisateur!=null)
			{
				nb++;
			}
		}
		return nb;
	}

	public void setNbInscrit(int nbPersonneInscrit)
	{
		//
	}

	public String getComplet()
	{
		if (getNbInscrit()>=nbPlace)
		{
			return "OUI";
		}
		return "NON";
	}

	public void setComplet(String complet)
	{
		// 
	}

	public String getNomInscrit()
	{
		return getNomInscrit(", ");
	}
	
	public void setNomInscrit(String s)
	{
		//
	}
	
	
	public String getNomInscrit(String sep)
	{
		List<PermanenceCellDTO> tmp = new ArrayList<PermanenceCellDTO>();
		for (PermanenceCellDTO pc : permanenceCellDTOs)
		{
			if (pc.idUtilisateur!=null)
			{
				tmp.add(pc);
			}
		}
		
		return CollectionUtils.asString(tmp, sep , e->e.nom+" "+e.prenom);
	}

	
	/**
	 * Permet de savoir si cet utilisateur est inscrit sur cette date 
	 * 
	 */
	public boolean isInscrit(Long idUtilisateur)
	{
		for (PermanenceCellDTO pc : permanenceCellDTOs)
		{
			if (pc.idUtilisateur==idUtilisateur)
			{
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Permet de savoir si cet date est complete  
	 * 
	 */
	public boolean isDateComplete()
	{
		return getNbInscrit()>=nbPlace;
	}
	

	/**
	 * Cette methode verifie si une personne est présente deux fois ou plus dans la liste des inscrits 
	 * 
	 */
	public String findDoublons()
	{
		List<PermanenceCellDTO> tmp = new ArrayList<PermanenceCellDTO>();
		for (int i = 0; i < permanenceCellDTOs.size(); i++)
		{
			PermanenceCellDTO lig = permanenceCellDTOs.get(i);
			
			if (lig.idUtilisateur!=null)
			{
				PermanenceCellDTO alreadyIn = CollectionUtils.findMatching(tmp, e->e.idUtilisateur==lig.idUtilisateur);
				if (alreadyIn!=null)
				{
					Utilisateur u = (Utilisateur) new DbService().getOneElement(Utilisateur.class, alreadyIn.idUtilisateur);
					return "L'utilisateur "+u.getNom()+" "+u.getPrenom()+" est présent deux fois ou plus.";
				}
				else
				{
					tmp.add(lig);
				}
			}
		}
		return null;
	}

	
	
	
	
	
	
	
	@Override
	public Long getId()
	{
		return idPeriodePermanenceDate; 
	}

	public List<PermanenceCellDTO> getPermanenceCellDTOs()
	{
		return permanenceCellDTOs;
	}

	public void setPermanenceCellDTOs(List<PermanenceCellDTO> permanenceCellDTOs)
	{
		this.permanenceCellDTOs = permanenceCellDTOs;
	}

	
		
	
	

}
