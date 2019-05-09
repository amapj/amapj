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
 package fr.amapj.service.services.gestioncontrat;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.model.models.contrat.modele.JokerMode;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.view.views.gestioncontrat.editorpart.FrequenceLivraison;

/**
 * Bean permettant l'edition des modeles de contrats
 *
 */
public class ModeleContratDTO
{
	public Long id;
	
	public String nom;
	
	public String description;

	public Long producteur;
	
	public Date dateFinInscription;
	
	public int cartePrepayeeDelai;
	
	public FrequenceLivraison frequence;
	
	public GestionPaiement gestionPaiement;
	
	public NatureContrat nature;
	
	public Date dateDebut;
	
	public Date dateFin;
	
	public String libCheque;
	
	public Date dateRemiseCheque;
	
	public Date premierCheque;
	
	public Date dernierCheque;
	
	public String textPaiement;
	
	public ChoixOuiNon jokerAutorise;
	
	public int jokerNbMin = 0;
	
	public int jokerNbMax = 0;
	
	public JokerMode jokerMode;
	
	public int jokerDelai;
	
	
	public List<DateModeleContratDTO> dateLivs = new ArrayList<DateModeleContratDTO>();

	public List<LigneContratDTO> produits = new ArrayList<LigneContratDTO>();
	
	public List<DatePaiementModeleContratDTO> datePaiements = new ArrayList<DatePaiementModeleContratDTO>();

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

	public Long getProducteur()
	{
		return producteur;
	}

	public void setProducteur(Long producteur)
	{
		this.producteur = producteur;
	}

	public Date getDateFinInscription()
	{
		return dateFinInscription;
	}

	public void setDateFinInscription(Date dateFinInscription)
	{
		this.dateFinInscription = dateFinInscription;
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

	public List<LigneContratDTO> getProduits()
	{
		return produits;
	}

	public void setProduits(List<LigneContratDTO> produits)
	{
		this.produits = produits;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public FrequenceLivraison getFrequence()
	{
		return frequence;
	}

	public void setFrequence(FrequenceLivraison frequence)
	{
		this.frequence = frequence;
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

	public Date getPremierCheque()
	{
		return premierCheque;
	}

	public void setPremierCheque(Date premierCheque)
	{
		this.premierCheque = premierCheque;
	}

	public Date getDernierCheque()
	{
		return dernierCheque;
	}

	public void setDernierCheque(Date dernierCheque)
	{
		this.dernierCheque = dernierCheque;
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

	public List<DateModeleContratDTO> getDateLivs()
	{
		return dateLivs;
	}

	public void setDateLivs(List<DateModeleContratDTO> dateLivs)
	{
		this.dateLivs = dateLivs;
	}

	public List<DatePaiementModeleContratDTO> getDatePaiements()
	{
		return datePaiements;
	}

	public void setDatePaiements(List<DatePaiementModeleContratDTO> datePaiements)
	{
		this.datePaiements = datePaiements;
	}

	public NatureContrat getNature()
	{
		return nature;
	}

	public void setNature(NatureContrat nature)
	{
		this.nature = nature;
	}

	public int getCartePrepayeeDelai()
	{
		return cartePrepayeeDelai;
	}

	public void setCartePrepayeeDelai(int cartePrepayeeDelai)
	{
		this.cartePrepayeeDelai = cartePrepayeeDelai;
	}

	public ChoixOuiNon getJokerAutorise()
	{
		return jokerAutorise;
	}

	public void setJokerAutorise(ChoixOuiNon jokerAutorise)
	{
		this.jokerAutorise = jokerAutorise;
	}

	public int getJokerNbMin()
	{
		return jokerNbMin;
	}

	public void setJokerNbMin(int jokerNbMin)
	{
		this.jokerNbMin = jokerNbMin;
	}

	public int getJokerNbMax()
	{
		return jokerNbMax;
	}

	public void setJokerNbMax(int jokerNbMax)
	{
		this.jokerNbMax = jokerNbMax;
	}

	public JokerMode getJokerMode()
	{
		return jokerMode;
	}

	public void setJokerMode(JokerMode jokerMode)
	{
		this.jokerMode = jokerMode;
	}

	public int getJokerDelai()
	{
		return jokerDelai;
	}

	public void setJokerDelai(int jokerDelai)
	{
		this.jokerDelai = jokerDelai;
	}

	
	
	
	
}
