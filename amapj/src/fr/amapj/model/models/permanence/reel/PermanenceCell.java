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
 package fr.amapj.model.models.permanence.reel;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceDate;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceUtilisateur;
import fr.amapj.model.models.permanence.periode.PermanenceRole;

/**
 * Représente l'affectation d'un utilisateur à une permanence
 *
 */
@Entity
public class PermanenceCell  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@NotNull
	@ManyToOne
	public PeriodePermanenceDate periodePermanenceDate;
	
	// Peut être null
	@ManyToOne
	public PeriodePermanenceUtilisateur periodePermanenceUtilisateur;
	
	@NotNull
	@ManyToOne
	public PermanenceRole permanenceRole;
	
	
	// Date de la notification (envoi du mail) à l'utilisateur 
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNotification;
	
	@NotNull
	// Numéro d'ordre 
	public int indx;

	
	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
	
}
