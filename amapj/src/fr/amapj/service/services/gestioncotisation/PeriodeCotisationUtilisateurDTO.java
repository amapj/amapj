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
 package fr.amapj.service.services.gestioncotisation;

import java.util.Date;

import fr.amapj.model.models.cotisation.EtatPaiementAdhesion;
import fr.amapj.model.models.cotisation.TypePaiementAdhesion;
import fr.amapj.view.engine.tools.TableItem;

/**
 * Permet la gestion des cotisations des utilisateurs
 * 
 */
public class PeriodeCotisationUtilisateurDTO implements TableItem
{
	public Long id;
	
	public Long idPeriodeCotisation;
	
	public Long idUtilisateur;
	
	public String nomUtilisateur;
	
	public String prenomUtilisateur;
	
	// Date d'adhesion par l'amapien
	public Date dateAdhesion;
	
	// Date de reception du chèque par le tresorier
	public Date dateReceptionCheque;
	
	public int montantAdhesion=0;
	
	public EtatPaiementAdhesion etatPaiementAdhesion;
	
	public TypePaiementAdhesion typePaiementAdhesion;
	

		

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getIdPeriodeCotisation()
	{
		return idPeriodeCotisation;
	}

	public void setIdPeriodeCotisation(Long idPeriodeCotisation)
	{
		this.idPeriodeCotisation = idPeriodeCotisation;
	}

	public Long getIdUtilisateur()
	{
		return idUtilisateur;
	}

	public void setIdUtilisateur(Long idUtilisateur)
	{
		this.idUtilisateur = idUtilisateur;
	}

	public Date getDateAdhesion()
	{
		return dateAdhesion;
	}

	public void setDateAdhesion(Date dateAdhesion)
	{
		this.dateAdhesion = dateAdhesion;
	}

	public Date getDateReceptionCheque()
	{
		return dateReceptionCheque;
	}

	public void setDateReceptionCheque(Date dateReceptionCheque)
	{
		this.dateReceptionCheque = dateReceptionCheque;
	}

	public int getMontantAdhesion()
	{
		return montantAdhesion;
	}

	public void setMontantAdhesion(int montantAdhesion)
	{
		this.montantAdhesion = montantAdhesion;
	}

	public EtatPaiementAdhesion getEtatPaiementAdhesion()
	{
		return etatPaiementAdhesion;
	}

	public void setEtatPaiementAdhesion(EtatPaiementAdhesion etatPaiementAdhesion)
	{
		this.etatPaiementAdhesion = etatPaiementAdhesion;
	}

	public TypePaiementAdhesion getTypePaiementAdhesion()
	{
		return typePaiementAdhesion;
	}

	public void setTypePaiementAdhesion(TypePaiementAdhesion typePaiementAdhesion)
	{
		this.typePaiementAdhesion = typePaiementAdhesion;
	}

	public String getNomUtilisateur()
	{
		return nomUtilisateur;
	}

	public void setNomUtilisateur(String nomUtilisateur)
	{
		this.nomUtilisateur = nomUtilisateur;
	}

	public String getPrenomUtilisateur()
	{
		return prenomUtilisateur;
	}

	public void setPrenomUtilisateur(String prenomUtilisateur)
	{
		this.prenomUtilisateur = prenomUtilisateur;
	}
	
	
	
	
	
	

}
