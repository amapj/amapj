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
 package fr.amapj.model.models.editionspe.emargement;


/**
 * Paramétrage de l'édition planning mensuel 
 *
 */
public class ParametresProduitsJson 
{


	private Long idProduit;
	
	/**
	 * Titre de la colonne
	 */
	private String titreColonne;
	
	
	// Largeur en mm pour la colonne
	private int largeurColonne;


	public Long getIdProduit()
	{
		return idProduit;
	}


	public void setIdProduit(Long idProduit)
	{
		this.idProduit = idProduit;
	}


	public String getTitreColonne()
	{
		return titreColonne;
	}


	public void setTitreColonne(String titreColonne)
	{
		this.titreColonne = titreColonne;
	}


	public int getLargeurColonne()
	{
		return largeurColonne;
	}


	public void setLargeurColonne(int largeurColonne)
	{
		this.largeurColonne = largeurColonne;
	}
	
}
