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
 package fr.amapj.model.models.editionspe.etiquette;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.param.ChoixOuiNon;


public class EtiquetteProducteurJson extends AbstractEditionSpeJson
{
	
	// Nombre de colonnes de l'étiquette
	private int nbColonne;
	
	// Largeur en mm pour chaque colonne
	private List<EtiquetteColJson> largeurColonnes = new ArrayList<EtiquetteColJson>();
	
	// Hauteur en mm pour toutes les lignes
	private int hauteur;
	
	// 
	private ChoixOuiNon bordure = ChoixOuiNon.OUI; 


	public int getNbColonne()
	{
		return nbColonne;
	}


	public void setNbColonne(int nbColonne)
	{
		this.nbColonne = nbColonne;
	}


	public List<EtiquetteColJson> getLargeurColonnes()
	{
		return largeurColonnes;
	}


	public void setLargeurColonnes(List<EtiquetteColJson> largeurColonnes)
	{
		this.largeurColonnes = largeurColonnes;
	}


	public int getHauteur()
	{
		return hauteur;
	}


	public void setHauteur(int hauteur)
	{
		this.hauteur = hauteur;
	}

	public ChoixOuiNon getBordure()
	{
		return bordure;
	}


	public void setBordure(ChoixOuiNon bordure)
	{
		this.bordure = bordure;
	}


	public static void main(String[] args)
	{
		EtiquetteProducteurJson et = new EtiquetteProducteurJson();
		et.hauteur = 15;
		et.setId(18L);
		et.largeurColonnes = new ArrayList<EtiquetteColJson>();
		EtiquetteColJson col1 = new EtiquetteColJson();
		col1.setLargeur(5);
		et.largeurColonnes.add(col1);
		EtiquetteColJson col2 = new EtiquetteColJson();
		col2.setLargeur(6);
		et.largeurColonnes.add(col2);
		et.setMargeBas(12);
		et.setMargeDroite(13);
		et.setNom("toto");
		
		Gson gson = new Gson();
		String str = gson.toJson(et);
		System.out.println(str);
		
		String str2 = "{\"nbColonne\":0,\"largeurColonnes\":[{\"largeur\":5},{\"largeur\":6}],\"hauteur\":16,\"margeDroite\":13,\"margeGauche\":0,\"margeHaut\":0,\"margeBas\":12}";
		EtiquetteProducteurJson et2 = gson.fromJson(str2, EtiquetteProducteurJson.class);
		
		System.out.println("et2="+et2.hauteur);
	}


	
}
