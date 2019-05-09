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
 package fr.amapj.model.models.contrat.reel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;
import fr.amapj.model.models.contrat.modele.ModeleContratDatePaiement;
import fr.amapj.model.models.remise.RemiseProducteur;

/**
 * Correspond à un chéque
 *
 */
@Entity
public class Paiement  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@ManyToOne
	private Contrat contrat;
	
	@NotNull
	@ManyToOne
	private ModeleContratDatePaiement modeleContratDatePaiement;

	// Montant du paiement en centimes
	@NotNull
	private int montant;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet de savoir l'état du modele de contrat
    private EtatPaiement etat = EtatPaiement.A_FOURNIR;
	
	@ManyToOne
	private RemiseProducteur remise;
	

	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String commentaire1;

	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String commentaire2;
	
	
	public enum P implements Mdm
	{
		ID("id") , CONTRAT("contrat") ,  MODELECONTRATDATEPAIEMENT("modeleContratDatePaiement") , MONTANT("montant") ;
		
		private String propertyId;   
		   
	    P(String propertyId) 
	    {
	        this.propertyId = propertyId;
	    }
	    public String prop() 
	    { 
	    	return propertyId; 
	    }
		
	} ;
	

	public Paiement()
	{
		
	}
	

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Contrat getContrat()
	{
		return contrat;
	}

	public void setContrat(Contrat contrat)
	{
		this.contrat = contrat;
	}

	public ModeleContratDatePaiement getModeleContratDatePaiement()
	{
		return modeleContratDatePaiement;
	}

	public void setModeleContratDatePaiement(ModeleContratDatePaiement modeleContratDatePaiement)
	{
		this.modeleContratDatePaiement = modeleContratDatePaiement;
	}

	public int getMontant()
	{
		return montant;
	}

	public void setMontant(int montant)
	{
		this.montant = montant;
	}

	public EtatPaiement getEtat()
	{
		return etat;
	}

	public void setEtat(EtatPaiement etat)
	{
		this.etat = etat;
	}

	public RemiseProducteur getRemise()
	{
		return remise;
	}

	public void setRemise(RemiseProducteur remise)
	{
		this.remise = remise;
	}


	public String getCommentaire1()
	{
		return commentaire1;
	}

	public void setCommentaire1(String commentaire1)
	{
		this.commentaire1 = commentaire1;
	}

	public String getCommentaire2()
	{
		return commentaire2;
	}

	public void setCommentaire2(String commentaire2)
	{
		this.commentaire2 = commentaire2;
	}
}
