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

import java.util.ArrayList;
import java.util.List;

import fr.amapj.model.models.acces.RoleList;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.PageFormat;
import fr.amapj.model.models.param.ChoixOuiNon;

/**
 * Paramétrage de l'édition planning mensuel 
 *
 */
public class FeuilleEmargementJson extends AbstractEditionSpeJson
{
	/** PARTIE GENERIQUE **/

	// Largeur en mm pour la colonne Nom
	private int lgColNom;
	
	// Largeur en mm pour la colonne Prenom
	private int lgColPrenom;

	// Largeur en mm pour la colonne Presence
	private int lgColPresence;

	// Largeur en mm pour la colonne Telephone 1
	private int lgColnumTel1;
	
	// Largeur en mm pour la colonne Telephone 2
	private int lgColnumTel2;

	
	// Largeur en mm pour la colonne Commentaire
	private int lgColCommentaire;
	
	// Hauteur en mm pour les lignes (ajustement automatique si 0)
	private int hauteurLigne;
	
	
	// Role pouvant accéder à ce planning 
	private RoleList accessibleBy = RoleList.ADHERENT;
	
	//
	private TypFeuilleEmargement typPlanning = TypFeuilleEmargement.MENSUEL;
	
	//
	private FormatFeuilleEmargement format = FormatFeuilleEmargement.GRILLE;

	
	/** PARTIE SPECIQUE AU MODE GRILLE **/ 
	
	// Contenu des cellules (par défaut, c'est une croix)
	private ContenuCellule contenuCellule = ContenuCellule.CROIX;
	
	private List<ParametresProduitsJson> parametresProduits = new ArrayList<ParametresProduitsJson>();

	
	/** PARTIE SPECIQUE AU MODE LISTE **/
	
	// Largeur en mm pour la colonne Produits
	private int lgColProduits = 70;
	
	private ChoixOuiNon nomDuContrat = ChoixOuiNon.OUI;
	
	private ChoixOuiNon nomDuProducteur = ChoixOuiNon.NON;
	
	private ChoixOuiNon detailProduits = ChoixOuiNon.OUI;
	
	// Permet l'affichage d'un cumul des quantités par producteur en tete du document 
	private ChoixOuiNon listeAffichageCumulProducteur = ChoixOuiNon.NON;
	
	
	public FeuilleEmargementJson()
	{
		setPageFormat(PageFormat.A4_PAYSAGE);
	}
	
	

	public List<ParametresProduitsJson> getParametresProduits()
	{
		return parametresProduits;
	}


	public void setParametresProduits(List<ParametresProduitsJson> parametresProduits)
	{
		this.parametresProduits = parametresProduits;
	}


	public int getLgColNom()
	{
		return lgColNom;
	}


	public void setLgColNom(int lgColNom)
	{
		this.lgColNom = lgColNom;
	}


	public int getLgColPrenom()
	{
		return lgColPrenom;
	}


	public void setLgColPrenom(int lgColPrenom)
	{
		this.lgColPrenom = lgColPrenom;
	}


	public int getLgColPresence()
	{
		return lgColPresence;
	}


	public void setLgColPresence(int lgColPresence)
	{
		this.lgColPresence = lgColPresence;
	}


	public int getLgColnumTel1()
	{
		return lgColnumTel1;
	}


	public void setLgColnumTel1(int lgColnumTel1)
	{
		this.lgColnumTel1 = lgColnumTel1;
	}


	public RoleList getAccessibleBy()
	{
		return accessibleBy;
	}


	public void setAccessibleBy(RoleList accessibleBy)
	{
		this.accessibleBy = accessibleBy;
	}


	public TypFeuilleEmargement getTypPlanning()
	{
		return typPlanning;
	}


	public void setTypPlanning(TypFeuilleEmargement typPlanning)
	{
		this.typPlanning = typPlanning;
	}


	public int getLgColCommentaire() 
	{
		return lgColCommentaire;
	}


	public void setLgColCommentaire(int lgColCommentaire) 
	{
		this.lgColCommentaire = lgColCommentaire;
	}


	public ContenuCellule getContenuCellule() 
	{
		return contenuCellule;
	}


	public void setContenuCellule(ContenuCellule contenuCellule) 
	{
		this.contenuCellule = contenuCellule;
	}


	public int getHauteurLigne() 
	{
		return hauteurLigne;
	}


	public void setHauteurLigne(int hauteurLigne) 
	{
		this.hauteurLigne = hauteurLigne;
	}


	public int getLgColnumTel2()
	{
		return lgColnumTel2;
	}


	public void setLgColnumTel2(int lgColnumTel2)
	{
		this.lgColnumTel2 = lgColnumTel2;
	}


	public FormatFeuilleEmargement getFormat()
	{
		return format;
	}


	public void setFormat(FormatFeuilleEmargement format)
	{
		this.format = format;
	}


	public int getLgColProduits()
	{
		return lgColProduits;
	}


	public void setLgColProduits(int lgColProduits)
	{
		this.lgColProduits = lgColProduits;
	}


	public ChoixOuiNon getNomDuContrat()
	{
		return nomDuContrat;
	}


	public void setNomDuContrat(ChoixOuiNon nomDuContrat)
	{
		this.nomDuContrat = nomDuContrat;
	}


	public ChoixOuiNon getNomDuProducteur()
	{
		return nomDuProducteur;
	}


	public void setNomDuProducteur(ChoixOuiNon nomDuProducteur)
	{
		this.nomDuProducteur = nomDuProducteur;
	}



	public ChoixOuiNon getDetailProduits()
	{
		return detailProduits;
	}

	public void setDetailProduits(ChoixOuiNon detailProduits)
	{
		this.detailProduits = detailProduits;
	}



	public ChoixOuiNon getListeAffichageCumulProducteur()
	{
		return listeAffichageCumulProducteur;
	}



	public void setListeAffichageCumulProducteur(ChoixOuiNon listeAffichageCumulProducteur)
	{
		this.listeAffichageCumulProducteur = listeAffichageCumulProducteur;
	}
	
	
	
	
}
