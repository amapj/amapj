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

import fr.amapj.model.models.permanence.periode.EtatPeriodePermanence;
import fr.amapj.model.models.permanence.periode.NaturePeriodePermanence;
import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;
import fr.amapj.view.engine.tools.TableItem;

/**
 * Description complete d'une periode de permanence
 *
 */
public class PeriodePermanenceDTO implements TableItem
{
	public Long id;
	
	public String nom;
	
	public String description;
	
	public FrequencePermanence frequencePermanence;

	public Date dateDebut;
	
	public Date dateFin;

	public int nbDatePerm;
	
	// Nombre total de personnes sur cette periode 
	public int nbUtilisateur;
	
	public Date dateFinInscription;
	
	public int flottantDelai;
	
	public EtatPeriodePermanence etat;
	
	public NaturePeriodePermanence nature;
	
	public int pourcentageInscription;
	
	public int nbPlaceParDate;
	
	// Liste des dates de cette periode
	public List<PeriodePermanenceDateDTO> datePerms = new ArrayList<PeriodePermanenceDateDTO>();
	
	/* Les 4 champs suivants sont utilisés uniquement pour l'affectation des utilisateurs */
	
	// Peut être null
	public Long idPeriodeCotisation;
		
	public List<PeriodePermanenceUtilisateurDTO> utilisateurs = new ArrayList<>();
		
	public String message;
		
	public int nbParPersonne;
	
	// La liste suivante est utilisée uniquement pour l'affectation des roles
	public List<PeriodePermanenceRoleDTO> roles;
		
	public RegleInscriptionPeriodePermanence regleInscription;

	public Long getIdPeriodeCotisation()
	{
		return idPeriodeCotisation;
	}

	public void setIdPeriodeCotisation(Long idPeriodeCotisation)
	{
		this.idPeriodeCotisation = idPeriodeCotisation;
	}

	public List<PeriodePermanenceUtilisateurDTO> getUtilisateurs()
	{
		return utilisateurs;
	}

	public void setUtilisateurs(List<PeriodePermanenceUtilisateurDTO> utilisateurs)
	{
		this.utilisateurs = utilisateurs;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public int getNbParPersonne()
	{
		return nbParPersonne;
	}

	public void setNbParPersonne(int nbParPersonne)
	{
		this.nbParPersonne = nbParPersonne;
	}

	

	public List<PeriodePermanenceDateDTO> getDatePerms()
	{
		return datePerms;
	}

	public void setDatePerms(List<PeriodePermanenceDateDTO> datePerms)
	{
		this.datePerms = datePerms;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public Date getDateDebut()
	{
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut)
	{
		this.dateDebut = dateDebut;
	}

	public Date getDateFin()
	{
		return dateFin;
	}

	public void setDateFin(Date dateFin)
	{
		this.dateFin = dateFin;
	}



	public int getNbUtilisateur()
	{
		return nbUtilisateur;
	}

	public void setNbUtilisateur(int nbUtilisateur)
	{
		this.nbUtilisateur = nbUtilisateur;
	}


	public EtatPeriodePermanence getEtat()
	{
		return etat;
	}

	public void setEtat(EtatPeriodePermanence etat)
	{
		this.etat = etat;
	}

	public NaturePeriodePermanence getNature()
	{
		return nature;
	}

	public void setNature(NaturePeriodePermanence nature)
	{
		this.nature = nature;
	}

	public int getPourcentageInscription()
	{
		return pourcentageInscription;
	}

	public void setPourcentageInscription(int pourcentageInscription)
	{
		this.pourcentageInscription = pourcentageInscription;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getNbDatePerm()
	{
		return nbDatePerm;
	}

	public void setNbDatePerm(int nbDatePerm)
	{
		this.nbDatePerm = nbDatePerm;
	}

	public Date getDateFinInscription()
	{
		return dateFinInscription;
	}

	public void setDateFinInscription(Date dateFinInscription)
	{
		this.dateFinInscription = dateFinInscription;
	}

	public FrequencePermanence getFrequencePermanence()
	{
		return frequencePermanence;
	}

	public void setFrequencePermanence(FrequencePermanence frequencePermanence)
	{
		this.frequencePermanence = frequencePermanence;
	}

	public int getNbPlaceParDate()
	{
		return nbPlaceParDate;
	}

	public void setNbPlaceParDate(int nbPlaceParDate)
	{
		this.nbPlaceParDate = nbPlaceParDate;
	}

	public int getFlottantDelai()
	{
		return flottantDelai;
	}

	public void setFlottantDelai(int flottantDelai)
	{
		this.flottantDelai = flottantDelai;
	}

	public List<PeriodePermanenceRoleDTO> getRoles()
	{
		return roles;
	}

	public void setRoles(List<PeriodePermanenceRoleDTO> roles)
	{
		this.roles = roles;
	}

	public RegleInscriptionPeriodePermanence getRegleInscription()
	{
		return regleInscription;
	}

	public void setRegleInscription(RegleInscriptionPeriodePermanence regleInscription)
	{
		this.regleInscription = regleInscription;
	}
	
	
	
}
