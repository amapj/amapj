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
 package fr.amapj.view.views.saisiecontrat;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;


/**
 * Classe utilitaire permettant la gestion des contrats de type abonnement, avec prise en compte des jokers
 *
 */
public class ContratAboManager
{

	static public class ContratAbo
	{
		// Tableau de taille "nombre de produits" : pour chaque produit, donne la quantité commandé
		public int qte[];
		
		// Liste des dates jokers
		public List<ContratLigDTO> dateJokers;
		
		public boolean isJoker(int lineNumber)
		{
			return dateJokers.stream().anyMatch(e->e.i==lineNumber);
		}


	}
	
	
	/**
	 * Permet d'obtenir les caracteristiques condensées du contrat d'abonnement
	 * Il faut être certain que le contrat est régulier 
	 */
	public ContratAbo computeContratAbo(ContratDTO dto)
	{
		ContratAbo contratAbo = computeFirstStep(dto);
		
		if (isRegulier(contratAbo,dto)==false)
		{
			throw new AmapjRuntimeException("Le contrat n'est pas régulier. Vous ne pouvez pas le modifier / visualiser");
		}
		return contratAbo;
	}
	
	
	
	/**
	 * Permet de savoir si un contrat est regulier
	 */
	public boolean isRegulier(ContratDTO dto)
	{
		ContratAbo contratAbo = computeFirstStep(dto);
		
		return isRegulier(contratAbo,dto);
		
	}
	
	
	// BLOC 1 - CALCUL FIRST STEP
	
	/**
	 * Calcul des informations condensées du contrat, mais sans vérification de la validité 
	 */
	private ContratAbo computeFirstStep(ContratDTO dto)
	{
		ContratAbo abo = new ContratAbo();
		
		// On calcule d'abord les quantités condensee
		abo.qte = condenseQte(dto,dto.qte);
		
		// On détermine ensuite les dates jokers à partir de ces quantités déterminées 
		abo.dateJokers = getJokers(dto,abo.qte,dto.qte);
		
		return abo;
	}
	
	
	private int[] condenseQte(ContratDTO dto,int[][] qte)
	{
		int[] res = new int [dto.contratColumns.size()];
		
		for (int j = 0; j < dto.contratColumns.size(); j++)
		{
			res[j] = extractFirstQte(j,dto,qte);
		}
		
		return res;
	}
	
	/**
	 * Extrait la quantité de la premiere ligne non exclue et non égale à 0, pour le produit indiqué par cette colone
	 * 
	 * Retourne 0 si toutes les lignes sont exclues pour ce produit ou si toutes les lignes ont une quantité de 0 
	 * 
	 */
	private int extractFirstQte(int col,ContratDTO dto,int[][] qte)
	{
		// 
		for (int i = 0; i < dto.contratLigs.size(); i++)
		{
			if (dto.isExcluded(i, col)==false) 
			{
				if (qte[i][col]!=0)
				{
					return qte[i][col];
				}
			}
		}
		return 0;
	}
	
	
	/**
	 * Retourne la liste des dates jokers, en prenant en entrée les quantités condensées
	 * 
	 * Attention, on raméne aussi les dates avec des quantités non nulles différentes de la quantité commandée 
	 * habituellement
	 * 
	 * Ceci sera vérifié plus tard par le isRegulier
	 */
	private List<ContratLigDTO> getJokers(ContratDTO dto, int[] qteCondense,int[][] qte)
	{
		return dto.contratLigs.stream().filter(e->isDateJoker(dto,e,qteCondense,qte)).collect(Collectors.toList());
	}
	
	/**
	 * Une date joker est une date avec des quantités qui sont différente de la quantité 
	 * commandée habituellement , sur une case non exclue  
	 * 
	 */
	private boolean isDateJoker(ContratDTO dto,ContratLigDTO lig, int[] qteCondense,int[][] qte)
	{
		for (int j = 0; j < dto.contratColumns.size(); j++)
		{
			//   
			if (dto.isExcluded(lig.i, j)==false && (qte[lig.i][j]!=qteCondense[j]) )
			{
				return true;
			}
		}
		return false;
	}
	
	
	// BLOC 2 - VERIFICATION DE LA REGULARITE D'UN CONTRAT
	
	

	/**
	 * Retourne true si ce contrat est regulier strictement, c'est à dire si les quantités sont strictement 
	 * égales sur toutes les dates, et en tenant compte des dates exclues et en prenant en compte les jokers
	 * 
	 */
	private boolean isRegulier(ContratAbo contratAbo,ContratDTO dto)
	{
		// Cas particulier des contrats sans date (ne devrait pas arriver) 
		if (dto.contratLigs.size()==0)
		{
			return false;
		}
		
		// On verifie les lignes jokers : elles ne doivent pas contenir des valeurs !=0
		for (ContratLigDTO ligDTO : contratAbo.dateJokers)
		{
			if (isDateJokerConforme(dto,ligDTO)==false)
			{
				return false;
			}
		}
		
		// Toutes nos dates jokers sont valides à ce point 
		
		
		// Si il y a trop de dates jokers, alors le contrat est considéré non régulier
		// Par contre, pas de blocage si il n'y a pas assez de date, l'utilisateur va pouvoir les saisir ensuite 
		if (contratAbo.dateJokers.size()>dto.jokerNbMax)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Verifie une ligne jokers : elles ne doivent pas contenir des valeurs !=0
	 * @return
	 */
	private boolean isDateJokerConforme(ContratDTO dto, ContratLigDTO ligDTO)
	{
		for (int j = 0; j < dto.contratColumns.size(); j++)
		{
			if (dto.qte[ligDTO.i][j]!=0)
			{
				return false;
			}
		}
		return true;
	}
	
	
	
	/**
	 * Permet de verifier si l'utilisateur a saisi suffisamment de date jokers
	 */
	public String checkJokerMin(ContratDTO dto,int[][] qte)
	{
		
		// On calcule d'abord les quantités condensées
		int[] qteCondensee = condenseQte(dto,qte);
		
		// On détermine ensuite les dates jokers à partir de ces quantités déterminées 
		List<ContratLigDTO> dateJokers = getJokers(dto,qteCondensee,qte);
		
		if(dateJokers.size()>=dto.jokerNbMin)
		{
			return null;
		}
		else
		{
			return "Vous n'avez pas saisi suffisamment de dates jokers. Il faut au minimum "+dto.jokerNbMin+" jokers.";
		}
	}
	
	
	// BLOC 3 - COPIE D'UN ContratAbo DANS UN ContratDTO
	
	
	/**
	 * Extraction des quantités du ContratAbo et remise en forme classique dans  tableau int[][]
	 * 
	 * Attention, le tableau n'est pas copié dans le ContratDTO
	 */
	public int[][] extractQte(ContratAbo abo,ContratDTO dto)
	{
		int[][] qte = new int[dto.contratLigs.size()][dto.contratColumns.size()];
		for (int j = 0; j < dto.contratColumns.size(); j++)
		{
			// On lit la quantité saisie
			int qteSaisie = abo.qte[j];
			
			// On l'applique à toutes les dates (sauf si exclusion ou si date joker) 
			for (int i = 0; i < dto.contratLigs.size(); i++)
			{
				if (dto.isExcluded(i, j)==false && abo.isJoker(i)==false)
				{
					qte[i][j] = qteSaisie;
				}
			}
		}
		return qte;
	}

	
	// BLOC 4 - Modification des jokers


	/**
	 * @return true si cette dateJoker peut encore être modifiée , false sinon 
	 */
	public boolean isModifiable(ContratLigDTO dateJoker, ContratDTO contratDTO,LocalDate now)
	{
		switch (contratDTO.jokerMode)
		{
		case INSCRIPTION:
			LocalDate fin = DateUtils.asLocalDate(contratDTO.dateFinInscription);
			return now.isBefore(fin) || now.isEqual(fin);
			
		case LIBRE:
			LocalDate limit = DateUtils.asLocalDate(dateJoker.date);
			limit = limit.plusDays(-contratDTO.jokerDelai);
			return now.isBefore(limit);
			
		default:
			throw new AmapjRuntimeException();
		}
	}
	
	
	/**
	 * @return true si 
	 * 			- ce contrat est de type abonnement avec des jokers autorisés
	 *  		- il n'est plus possible de modifier globalement ce contrat
	 *  
	 *  Cette méthode doit être appelée uniquement depuis MesContratsService
	 */
	public boolean hasJokerButton(ModeleContrat mc,Boolean isModifiable)
	{
		//
		if (mc.nature!=NatureContrat.ABONNEMENT || mc.jokerNbMax<=0)
		{
			return false;
		}
		
		// Cas du contrat encore modifiable 
		if (isModifiable==true)
		{
			return false;
		}
				
		return true;
	}
	
	
	// BLOC 5 - MESSAGE D'AFFICHAGE
	
	public String computeJokerMessage(ContratDTO contratDTO,int used)
	{
		StringBuilder sb = new StringBuilder();
		
		// Partie 1 de la phrase
		if (contratDTO.jokerNbMin==0)
		{
			if (contratDTO.jokerNbMax==1)
			{
				sb.append("Ce contrat autorise 1 joker maximum.");
			}
			else
			{
				sb.append("Ce contrat autorise "+contratDTO.jokerNbMax+" jokers maximum.");
			}
		}
		else
		{
			if (contratDTO.jokerNbMin==contratDTO.jokerNbMax)
			{
				sb.append("Ce contrat impose "+contratDTO.jokerNbMin+" joker(s).");
			}
			else
			{
				sb.append("Ce contrat impose "+contratDTO.jokerNbMin+" joker(s) au minimum et autorise "+contratDTO.jokerNbMax+" joker(s) au maximum.");
			}
		}
		
		// Partie 2 de la phrase
		if (used==0)
		{
			sb.append("Vous avez utilisé aucun joker.");
		}
		else if (used==1)
		{
			sb.append("Vous avez utilisé 1 joker.");
		}
		else
		{
			sb.append("Vous avez utilisé "+used+" jokers.");
		}
		
		return sb.toString();
	}

	// BLOC verification de la conformité 

	/**
	 * Verifie la conformité de ce contrat par rapport à jokerMin et jokerMax
	 * 
	 * Attention : les regles dans ContratDTO sont différentes des régles passées en paramétres (int jokerMin,int jokerMax ) 
	 * 
	 * A utiliser uniquement par GestionContratSigneService
	 */
	public String isConforme(ContratDTO dto,int jokerMin,int jokerMax)
	{
		// On calcule d'abord les quantités condensées
		int[] qteCondensee = condenseQte(dto,dto.qte);
		
		// On détermine ensuite les dates jokers à partir de ces quantités déterminées 
		List<ContratLigDTO> dateJokers = getJokers(dto,qteCondensee,dto.qte);
		
		// On vérifie la validité des dates jokers
		for (ContratLigDTO ligDTO : dateJokers)
		{
			if (isDateJokerConforme(dto, ligDTO)==false)
			{
				return "la date du "+FormatUtils.getStdDate().format(ligDTO.date)+" n'est pas conforme";
			}
		}
		
		int nb = dateJokers.size();
		
		if(dateJokers.size()<jokerMin)
		{
			return "il n'y a pas assez de jokers sur ce contrat ("+nb+" jokers sur le contrat alors que le minimum est "+jokerMin+")";
		}
		
		if(dateJokers.size()>jokerMax)
		{
			return "il y a trop de jokers sur ce contrat ("+nb+" jokers sur le contrat alors que le maximum est "+jokerMax+")";
		}
		
		return null;
	}
	
}
