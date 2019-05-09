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
 package fr.amapj.model.models.permanence.periode;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;
import fr.amapj.model.models.fichierbase.Producteur;

@Entity
@Table( uniqueConstraints=
{
   @UniqueConstraint(columnNames={"nom"})
})
public class PeriodePermanence implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(length = 100)
	public String nom;
	
	@NotNull
	@Size(min = 1, max = 255)
	@Column(length = 255)
	public String description;
	
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet de savoir l'état du modele de contrat
	public EtatPeriodePermanence etat = EtatPeriodePermanence.CREATION;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet de savoir la nature de cette période de permanence
    public NaturePeriodePermanence nature;
	
	
	@Temporal(TemporalType.DATE)
	public Date dateFinInscription;
	
	// Delai dans le cas des inscriptins flottantes 
	public int flottantDelai;
	
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet de savoir les controles réalisés lors des inscriptions 
    public RegleInscriptionPeriodePermanence regleInscription;


	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}
	
	
	
}
