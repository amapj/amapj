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
 package fr.amapj.model.models.remise;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.models.contrat.modele.ModeleContratDatePaiement;

@Entity
@Table( uniqueConstraints=
{
   @UniqueConstraint(columnNames={"datePaiement_id"})
})
public class RemiseProducteur  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreation;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateRemise;
	
	@NotNull
	@ManyToOne
	private ModeleContratDatePaiement datePaiement;
	
	
	// Montant de la remise en centimes
	@NotNull
	private int montant=0;


	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}


	public Date getDateCreation()
	{
		return dateCreation;
	}


	public void setDateCreation(Date dateCreation)
	{
		this.dateCreation = dateCreation;
	}


	public Date getDateRemise()
	{
		return dateRemise;
	}


	public void setDateRemise(Date dateRemise)
	{
		this.dateRemise = dateRemise;
	}


	public ModeleContratDatePaiement getDatePaiement()
	{
		return datePaiement;
	}


	public void setDatePaiement(ModeleContratDatePaiement datePaiement)
	{
		this.datePaiement = datePaiement;
	}


	public int getMontant()
	{
		return montant;
	}


	public void setMontant(int montant)
	{
		this.montant = montant;
	}
	

	

	
}
