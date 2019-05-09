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
 package fr.amapj.service.services.mescontrats;


import java.util.ArrayList;
import java.util.List;

import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.service.services.producteur.ProdUtilisateurDTO;

/**
 * Informations sur les paiements de ce contrat
 *
 */
public class InfoPaiementDTO
{
	// Lignes de paiement, ordonnées
	// Toutes les lignes sont présentes, même celles qui sont à zéro
	public List<DatePaiementDTO> datePaiements = new ArrayList<DatePaiementDTO>();
	
	public GestionPaiement gestionPaiement;
	
	public String textPaiement;
	
	public int avoirInitial;
	
	
	// Ordre du chèque 
	public String libCheque;
	
	// Référents qui vont récolter les chèques 
	public List<ProdUtilisateurDTO> referentsRemiseCheque;
	
	
}
