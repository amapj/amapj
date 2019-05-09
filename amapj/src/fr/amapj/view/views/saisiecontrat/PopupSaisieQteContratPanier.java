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

import java.text.SimpleDateFormat;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;

import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.GridSizeCalculator;
import fr.amapj.view.engine.grid.integergrid.PopupIntegerGrid;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.saisiecontrat.ContratAboManager.ContratAbo;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.SaisieContratData;

/**
 * Popup pour la saisie des quantites pour un contrat
 * de type "Panier", c'est à dire sans le choix des dates 
 *  
 */
public class PopupSaisieQteContratPanier extends PopupIntegerGrid implements PopupListener
{	
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private ContratDTO contratDTO;
	
	private SaisieContratData data;
	
	// Largeur de la colonne description des produits 
	private int largeurColonne = 500; //TODO faire varier en fonction de la taille de l'écran
	
	// Informations condensée du contrat
	private ContratAbo abo;
	
	/**
	 * 
	 */
	public PopupSaisieQteContratPanier(SaisieContratData data)
	{
		super();
		
		this.data = data;
		this.contratDTO = data.contratDTO;
		this.abo = new ContratAboManager().computeContratAbo(contratDTO);
		
		//
		popupTitle = "Mon contrat "+contratDTO.nom;
		
		// 
		param.readOnly = (data.modeSaisie==ModeSaisie.READ_ONLY);
		param.messageSpecifique = data.messageSpecifique;
		
	}
	
	public void loadParam()
	{
		// Partie jokers
		if (contratDTO.jokerNbMax>0)
		{
			param.messageSpecifiqueBottom = computeJokerMessage();
		}
		
		//
		param.nbLig = contratDTO.contratColumns.size();
		param.nbCol = 1;
		param.qte = computeQte();
		param.excluded =computeExcluded();
		param.buttonCopyFirstLine = false;
		
		// tableau des prix
		param.prix = new int[param.nbLig][param.nbCol];
		fillTableauPrix();
		param.libPrixTotal = "Prix Total";
		

		// Largeur des colonnes
		param.largeurCol = 110;
				
		// Construction du header 1
		GridHeaderLine line1  =new GridHeaderLine();
		line1.styleName = "tete";
		line1.cells.add("Produit");
		line1.cells.add("Qte");
		
		param.headerLines.add(line1);
				
		// Partie gauche de chaque ligne
		param.leftPartLineLargeur = largeurColonne; 
		param.leftPartLineStyle = "description-panier";
		for (ContratColDTO col : contratDTO.contratColumns)
		{
			param.leftPartLine.add(getText(col));
		}	
		
	
	}


	private void fillTableauPrix()
	{
		for (int i = 0; i < param.nbLig; i++)
		{
			ContratColDTO col = contratDTO.contratColumns.get(i);
			int nbLivraison = getNbLivraison(i);
			param.prix[i][0] = col.prix*nbLivraison;
		}
	}

	private int[][] computeQte()
	{
		int[][] res = new int [contratDTO.contratColumns.size()][1];
		
		for (int j = 0; j < contratDTO.contratColumns.size(); j++)
		{
			res[j][0] = abo.qte[j];
		}
		
		return res;
	}
	
	
	private boolean[][] computeExcluded()
	{
		boolean[][] res = new boolean [contratDTO.contratColumns.size()][1];
		
		for (int j = 0; j < contratDTO.contratColumns.size(); j++)
		{
			int nbLivraison = getNbLivraison(j);
			res[j][0] = (nbLivraison==0);
		}
		
		
		return res;
	}
	



	private String getText(ContratColDTO col)
	{
		String str = getLine1(col)+"<br/>";
		
		str = str+"Prix unitaire : "+new CurrencyTextFieldConverter().convertToString(col.prix)+" €<br/>";
		
		int nbLivraison = getNbLivraison(col.j);
		
		if (nbLivraison==0)
		{
			str = str+"Produit non disponible";
		}
		else
		{
			str = str+"<b>"+nbLivraison+" livraisons , prix total de "+new CurrencyTextFieldConverter().convertToString(nbLivraison*col.prix)+" €</b>";
		}
		
		return str;
	}


	private String getLine1(ContratColDTO col)
	{
		return "Abonnement pour 1 "+ col.nomProduit+","+col.condtionnementProduit;
	}

	/**
	 * On calcule le nombre de livraison pour ce produit 
	 * en tenant compte des dates exclues et des dates jokers
	 * 
	 * @param indexProduit
	 * @return
	 */
	private int getNbLivraison(int indexProduit)
	{
		int nbLivraison = 0;
		for (int i = 0; i < contratDTO.contratLigs.size(); i++)
		{
			if (contratDTO.isExcluded(i,indexProduit)==false && abo.isJoker(i)==false)
			{
				nbLivraison++;
			}
		}
		
		return nbLivraison;
	}



	@Override
	protected void handleContinuer()
	{
		data.validate();
		close();
	}

	@Override
	public boolean performSauvegarder()
	{
		// Extraction des quantités
		int[][] qte = extractQte();
		
		// On vérifie si on atteint le nombre minimum de jokers
		String msg = new ContratAboManager().checkJokerMin(contratDTO, qte);
		if (msg!=null)
		{
			MessagePopup.open(new MessagePopup("Impossible de continuer",ContentMode.HTML,ColorStyle.RED,msg));
			return false;
		}
		
		// On copie dans le contratDto
		contratDTO.qte = qte;
			
		//
		data.validate();
		return true;
	}
	
	/**
	 * Une ligne de la table contient dans le cas standard 3 lignes de texte
	 * Exemple : 
	 * 	 Abonnement pour 1 Pain de blé
	 * 	 Prix unitaire : 7.00 €
	 * 	 4 livraisons , prix total de 28.00 €
	 * 
	 *  Une ligne de texte fait 16 de hauteur, et il y a 10 de marges en haut et bas
	 *  Le mode readOnly n'a pas d'impact 
	 *  
	 *  Par contre, la première ligne ("Abonnement pour 1 Pain de blé" dans l'exemple) peut faire 
	 *  plus de 50 pixels, il faut donc compter le nombre de ligne max que peut prendre cette 
	 *  premiere ligne
	 *  
	 */
	@Override
	public int getLineHeight(boolean readOnly)
	{
		int nbLineMax = getNbLineMax();
		return (nbLineMax+1+1)*16+10+10;
	}
	
	/**
	 * Retourne le nombre de ligne max sur toutes les cellules pour la ligne 1 
	 */
	private int getNbLineMax()
	{
		int nbLine = 1;
		GridSizeCalculator cal = new GridSizeCalculator();
		
		for (ContratColDTO col : contratDTO.contratColumns)
		{
			String cell = getLine1(col);		
			nbLine = Math.max(nbLine, cal.getHeight(cell,  largeurColonne-22, "Arial",16));
		}
		return nbLine;
	}

	@Override
	public int getHeaderHeight()
	{
		// On cacule la place consommée par les headers, boutons, ...
		// 284 : nombre de pixel mesurée pour les haeders, les boutons, ... en mode normal, 185 en mode compact
		return BaseUiTools.isCompactMode() ? 185 : 284;
	}
	
	
	protected void createButtonBar()
	{
		Button detailDate = addButton("Détail des dates de livraison", e->handleDetailDate());
		setButtonAlignement(detailDate, Alignment.TOP_LEFT);
		
		if (contratDTO.jokerNbMax>0)
		{
			String caption = data.modeSaisie==ModeSaisie.READ_ONLY ? "Voir mes jokers" : "Choisir mes jokers"; 
			Button jokerBtn = addButton(caption, e->handleJoker());
			setButtonAlignement(jokerBtn, Alignment.TOP_LEFT);
		}
		
		super.createButtonBar();
	}

	private void handleDetailDate()
	{
		// On copie dans un tableau temporaire les quantités saisies 		
		int[][] qte = extractQte(); 

		// On affiche 
		PopupDetailDate.open(new PopupDetailDate(contratDTO,qte));
	}
	
	
	private void handleJoker()
	{
		PopupSaisieJoker.open(new PopupSaisieJoker(abo,contratDTO,data.modeSaisie==ModeSaisie.READ_ONLY),this);	
	}
	
	
	

	/**
	 * Extraction des quantités et remise en forme classique 
	 */
	private int[][] extractQte()
	{
		// On lit dans la grille de saisie
		for (int j = 0; j < contratDTO.contratColumns.size(); j++)
		{
			// On lit la quantité saisie
			int qteSaisie = param.qte[j][0];
			
			//
			abo.qte[j] = qteSaisie;
		}
		
		return new ContratAboManager().extractQte(abo, contratDTO);
	
	}
	

	/**
	 * Permet le rafraichissement de l'ecran quand on revient des jokers 
	 */
	@Override
	public void onPopupClose()
	{
		// On rafraichit tout d'abord le texte des jokers
		updateLabelMessageSpecifiqueBottom(computeJokerMessage());
		
		// On rafaichit ensuite les libellés 
		List<ContratColDTO> contratColumns = contratDTO.contratColumns;
		for (int i = 0; i < contratColumns.size(); i++)
		{
			ContratColDTO col = contratColumns.get(i);
			updateFirstCol(i, getText(col));
		}	
		
		// Il faut ensuite mettre à jour la matrice de prix
		fillTableauPrix();
		updatePrixTotal();
		
	}

	private String computeJokerMessage()
	{
		return new ContratAboManager().computeJokerMessage(contratDTO, abo.dateJokers.size());
	}
	
}
