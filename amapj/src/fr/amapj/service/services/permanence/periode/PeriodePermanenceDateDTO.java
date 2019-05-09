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

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.CollectionUtils;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;
import fr.amapj.service.services.dbservice.DbService;
import fr.amapj.service.services.utilisateur.UtilisateurService;
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
	
	public RegleInscriptionPeriodePermanence regleInscription;
	
	public PeriodePermanenceDateDTO()
	{
		
	}
	

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
	 * Cette methode verifie si la regle d'inscription RegleInscriptionPeriodePermanence est bien respectée 
	 *  
	 *  Retourne null si tout est OK, sinon retourne un message 
	 * 
	 */
	public String checkRegleInscription()
	{
		switch (regleInscription)
		{
		case UNE_INSCRIPTION_PAR_DATE:
			return checkRegleInscriptionUneInscriptionParDate();

		case MULTIPLE_INSCRIPTION_SUR_ROLE_DIFFERENT:
			return checkRegleInscriptionRoleDifferent();
			
		case TOUT_AUTORISE:
			return null;

		default:
			throw new AmapjRuntimeException();
		}	
	}	
	
	
	private String checkRegleInscriptionUneInscriptionParDate()
	{
		List<PermanenceCellDTO> tmp = new ArrayList<PermanenceCellDTO>();
		for (int i = 0; i < permanenceCellDTOs.size(); i++)
		{
			PermanenceCellDTO lig = permanenceCellDTOs.get(i);
			
			if (lig.idUtilisateur!=null)
			{	
				PermanenceCellDTO alreadyIn = CollectionUtils.findMatching(tmp, e->lig.idUtilisateur.equals(e.idUtilisateur));
				if (alreadyIn!=null)
				{
					String  u = new UtilisateurService().prettyString(alreadyIn.idUtilisateur);
					return "L'utilisateur "+u+" est présent deux fois ou plus.";
				}
				else
				{
					tmp.add(lig);
				}
			}
		}
		return null;
	}
	
	private String checkRegleInscriptionRoleDifferent()
	{
		List<PermanenceCellDTO> tmp = new ArrayList<PermanenceCellDTO>();
		for (int i = 0; i < permanenceCellDTOs.size(); i++)
		{
			PermanenceCellDTO lig = permanenceCellDTOs.get(i);
			
			if (lig.idUtilisateur!=null)
			{	
				PermanenceCellDTO alreadyIn = CollectionUtils.findMatching(tmp, e->(lig.idUtilisateur.equals(e.idUtilisateur) && lig.idRole.equals(e.idRole)));
				if (alreadyIn!=null)
				{
					String  u = new UtilisateurService().prettyString(alreadyIn.idUtilisateur);
					return "L'utilisateur "+u+" est présent deux fois ou plus sur le même rôle.";
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
