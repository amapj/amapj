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
 package fr.amapj.model.models.contrat.modele;

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
import fr.amapj.model.models.extendedparam.ExtendedParam;
import fr.amapj.model.models.fichierbase.Producteur;

@Entity
@Table( uniqueConstraints=
{
   @UniqueConstraint(columnNames={"nom"})
})
public class ModeleContrat implements Identifiable
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
	@ManyToOne
	public Producteur producteur;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet de savoir l'état du modele de contrat
	public EtatModeleContrat etat = EtatModeleContrat.CREATION;
	
	@Temporal(TemporalType.DATE)
	public Date dateFinInscription;
	
	
	// Paiement
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet de savoir l'état du modele de contrat
	public GestionPaiement gestionPaiement = GestionPaiement.NON_GERE;
	
	@Size(min = 0, max = 2048)
	@Column(length = 2048)
	// Texte qui sera affiché dans le cas ou il n'y a pas de paiement
	public String textPaiement;
	
	// Libellé du chéque 
	@Size(min = 0, max = 255)
	@Column(length = 255)
	public String libCheque;
	
	// Date de remise des chéques
	@Temporal(TemporalType.DATE)
	public Date dateRemiseCheque;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet de savoir l'état du modele de contrat
	public NatureContrat nature;
	
	// Delai en cas de contrat "carte prépayée" 
	public int cartePrepayeeDelai;
	
	// Parametre de mise en forme graphique
	@ManyToOne
	public ExtendedParam miseEnFormeGraphique;
	
	
	// Gestion des jokers
	public int jokerNbMin = 0;
	
	public int jokerNbMax = 0;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	public JokerMode jokerMode;
	
	public int jokerDelai;
	
	

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

	public Producteur getProducteur()
	{
		return producteur;
	}

	public void setProducteur(Producteur producteur)
	{
		this.producteur = producteur;
	}

	public EtatModeleContrat getEtat()
	{
		return etat;
	}

	public void setEtat(EtatModeleContrat etat)
	{
		this.etat = etat;
	}

	public Date getDateFinInscription()
	{
		return dateFinInscription;
	}

	public void setDateFinInscription(Date dateFinInscription)
	{
		this.dateFinInscription = dateFinInscription;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getLibCheque()
	{
		return libCheque;
	}

	public void setLibCheque(String libCheque)
	{
		this.libCheque = libCheque;
	}

	public Date getDateRemiseCheque()
	{
		return dateRemiseCheque;
	}

	public void setDateRemiseCheque(Date dateRemiseCheque)
	{
		this.dateRemiseCheque = dateRemiseCheque;
	}

	public GestionPaiement getGestionPaiement()
	{
		return gestionPaiement;
	}

	public void setGestionPaiement(GestionPaiement gestionPaiement)
	{
		this.gestionPaiement = gestionPaiement;
	}

	public String getTextPaiement()
	{
		return textPaiement;
	}

	public void setTextPaiement(String textPaiement)
	{
		this.textPaiement = textPaiement;
	}

	
	
	
	
	

}
