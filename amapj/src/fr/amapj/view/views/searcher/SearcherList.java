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
 package fr.amapj.view.views.searcher;

import fr.amapj.model.models.editionspe.TypEditionSpecifique;
import fr.amapj.view.engine.searcher.SearcherDefinition;


/**
 * Contient la liste de tous les searchers de l'application 
 *
 */
public class SearcherList 
{
	
	static public SearcherDefinition MODELE_CONTRAT = new SDModeleContrat();
	
	static public SearcherDefinition PRODUCTEUR = new SDProducteur();
	
	static public SearcherDefinition PERIODE_COTISATION = new SDPeriodeCotisation();
	
	static public SearcherDefinition PERIODE_PERMANENCE = new SDPeriodePermanence();
	
	static public SearcherDefinition ETIQUETTE = new SDEditionSpe(TypEditionSpecifique.ETIQUETTE_PRODUCTEUR);
	
	static public SearcherDefinition ENGAGEMENT = new SDEditionSpe(TypEditionSpecifique.CONTRAT_ENGAGEMENT);
	
	static public SearcherDefinition BULLETIN_ADHESION = new SDEditionSpe(TypEditionSpecifique.BULLETIN_ADHESION);
	
	static public SearcherDefinition BILAN_LIVRAISON = new SDEditionSpe(TypEditionSpecifique.BILAN_LIVRAISON);
	
	// Searcher d'un produit lié à un producteur
	// Permet de lister tous les produits d'un producteur donné
	static public SearcherDefinition PRODUIT = new SDProduit();
	
	// Permet de lister tous les produits de la base, sous la forme Producteur - Produit 
	static public SearcherDefinition PRODUIT_ALL = new SDProduitAll();
	
	static public SearcherDefinition UTILISATEUR_SANS_CONTRAT = new SDUtilisateurSansContrat();
	
	static public SearcherDefinition UTILISATEUR_SANS_ADHESION = new SDUtilisateurSansAdhesion();
	
	static public SearcherDefinition UTILISATEUR_ACTIF = new SDUtilisateur();
	
	static public SearcherDefinition UTILISATEUR_TOUS = new SDUtilisateurTous();
	
	static public SearcherDefinition PERIODE_PERMANENCE_ROLE = new SDPeriodePermanenceRole();
	
	
		
}
