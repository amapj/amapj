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
 package fr.amapj.model.models.editionspe;

import fr.amapj.model.engine.Identifiable;


public class AbstractEditionSpeJson implements Identifiable , Imprimable
{

	transient private Long id;

	transient private String nom;
	
	/*
	 * LISTE DES CHAMPS COMMUN A TOUTES LES EDITIONS SPECIFIQUES
	 * CES CHAMPS NE SONT PAS TOUJOURS UTILISES
	 */
	
	//
	private int margeDroite=10;
	
	//
	private int margeGauche=10;
	
	//
	private int margeHaut=10;
	
	//
	private int margeBas=10;
	
	//
	private PageFormat pageFormat = PageFormat.A4_PORTRAIT;
	
	
	public AbstractEditionSpeJson()
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


	public String getNom()
	{
		return nom;
	}


	public void setNom(String nom)
	{
		this.nom = nom;
	}


	public int getMargeDroite()
	{
		return margeDroite;
	}


	public void setMargeDroite(int margeDroite)
	{
		this.margeDroite = margeDroite;
	}


	public int getMargeGauche()
	{
		return margeGauche;
	}


	public void setMargeGauche(int margeGauche)
	{
		this.margeGauche = margeGauche;
	}


	public int getMargeHaut()
	{
		return margeHaut;
	}


	public void setMargeHaut(int margeHaut)
	{
		this.margeHaut = margeHaut;
	}


	public int getMargeBas()
	{
		return margeBas;
	}


	public void setMargeBas(int margeBas)
	{
		this.margeBas = margeBas;
	}


	public PageFormat getPageFormat()
	{
		return pageFormat;
	}


	public void setPageFormat(PageFormat pageFormat)
	{
		this.pageFormat = pageFormat;
	}

	
}
