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
 package fr.amapj.model.models.cotisation;

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
import fr.amapj.model.models.fichierbase.Utilisateur;

@Entity
@Table( uniqueConstraints=
{
   @UniqueConstraint(columnNames={"periodeCotisation_id" , "utilisateur_id"})
})
public class PeriodeCotisationUtilisateur  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@ManyToOne
	private PeriodeCotisation periodeCotisation;
	
	@NotNull
	@ManyToOne
	private Utilisateur utilisateur;
	
	// Date d'adhesion par l'amapien
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdhesion;
	
	// Date de reception du chèque par le tresorier
	@Temporal(TemporalType.DATE)
	private Date dateReceptionCheque;

	// Montant de de l'adhesion en centimes
	@NotNull
	private int montantAdhesion=0;
	
	// Etat du paiement
	@NotNull
	private EtatPaiementAdhesion etatPaiementAdhesion= EtatPaiementAdhesion.A_FOURNIR;
	
	// Etat du paiement
	@NotNull
	private TypePaiementAdhesion typePaiementAdhesion= TypePaiementAdhesion.CHEQUE;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public PeriodeCotisation getPeriodeCotisation()
	{
		return periodeCotisation;
	}

	public void setPeriodeCotisation(PeriodeCotisation periodeCotisation)
	{
		this.periodeCotisation = periodeCotisation;
	}

	public Utilisateur getUtilisateur()
	{
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur)
	{
		this.utilisateur = utilisateur;
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
	
	
	
	
	
	
}
