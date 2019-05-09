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



/**
 * Bean permettant l'edition des modeles de contrats
 *
 */
public class LigneContratDTO
{
	public Long idModeleContratProduit;
	
	public Long produitId;
	
	public String produitNom;
	
	public String produitConditionnement;
	
	public Integer prix;
	
	public Long getProduitId()
	{
		return produitId;
	}

	public void setProduitId(Long produitId)
	{
		this.produitId = produitId;
	}

	public Integer getPrix()
	{
		return prix;
	}

	public void setPrix(Integer prix)
	{
		this.prix = prix;
	}

	public String getProduitNom()
	{
		return produitNom;
	}

	public void setProduitNom(String produitNom)
	{
		this.produitNom = produitNom;
	}

	public Long getIdModeleContratProduit()
	{
		return idModeleContratProduit;
	}

	public void setIdModeleContratProduit(Long idModeleContratProduit)
	{
		this.idModeleContratProduit = idModeleContratProduit;
	}
	
}
