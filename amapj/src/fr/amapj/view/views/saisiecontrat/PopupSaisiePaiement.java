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

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.contrat.modele.extendparam.MiseEnFormeGraphique;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.param.paramecran.PESaisiePaiement;
import fr.amapj.service.services.gestioncontrat.ExtPModeleContratService;
import fr.amapj.service.services.mescontrats.DatePaiementDTO;
import fr.amapj.service.services.mescontrats.InfoPaiementDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.producteur.ProdUtilisateurDTO;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.currencyvector.PopupCurrencyVector;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.SaisieContratData;

/**
 * Popup pour la saisie des paiements pour un contrat
 *  
 */
public class PopupSaisiePaiement extends PopupCurrencyVector
{
	SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");
	
	private InfoPaiementDTO paiementDTO;
	
	private SaisieContratData data;
	
	private PESaisiePaiement peConf;
	
	/**
	 * 
	 */
	public PopupSaisiePaiement(SaisieContratData data)
	{
		super();
		this.data = data;
		this.paiementDTO = data.contratDTO.paiement;
		this.peConf =  (PESaisiePaiement) new ParametresService().loadParamEcran(MenuList.OUT_SAISIE_PAIEMENT);
		
		MiseEnFormeGraphique miseEnForme = new ExtPModeleContratService().loadMiseEnFormeGraphique(data.contratDTO.modeleContratId);
		
		//
		popupTitle = "Vos paiements pour le contrat "+data.contratDTO.nom;
		setWidth(50);
		
		// 
		param.readOnly = (data.modeSaisie==ModeSaisie.READ_ONLY);
		
		// Message 1 
		param.messageSpecifique = data.messageSpecifique;
		
		// Message 2
		if (miseEnForme.paiementStdLib1Modifier==ChoixOuiNon.NON)
		{
			param.messageSpecifique2 = getDefaultMessageOrdreCheque();
		}
		else
		{
			param.messageSpecifique2 = miseEnForme.paiementStdLib1;
		}
		
		// Message 3
		if ((param.readOnly==false) && (param.computeLastLine==true))
		{
			if (miseEnForme.paiementStdLib2Modifier==ChoixOuiNon.NON)
			{
				param.messageSpecifique3 = getDefaultMessageIndicationRemplissage();
			}
			else
			{
				param.messageSpecifique3 = miseEnForme.paiementStdLib2;
			}
		}
		
		
		
		
		param.montantCible = data.contratDTO.getMontantTotal();
		param.computeLastLine = (data.modeSaisie!=ModeSaisie.CHEQUE_SEUL);
	}
	
	
	
	private String getDefaultMessageIndicationRemplissage()
	{
		String message = 	"Une proposition de paiement a été calculée et est affichée ci dessous.<br/>"+
				"Vous pouvez modifier cette proposition en saisissant directement les montants en face de chaque mois<br/>"+
				"Le dernier mois est calculé automatiquement pour ajuster le contrat<br/><br/>";
		return message;
	}



	private String getDefaultMessageOrdreCheque()
	{
		String str = "<b>Ordre des chèques : "+paiementDTO.libCheque+"</b>";
		if (paiementDTO.referentsRemiseCheque.size()>0)
		{
			ProdUtilisateurDTO r = paiementDTO.referentsRemiseCheque.get(0);
			str = str + "<br/><b>Chèques à remettre à "+r.prenom+" "+r.nom+"</b>";
		}
		return str;
	}



	public void loadParam()
	{
		param.nbLig = paiementDTO.datePaiements.size();
		param.montant = new int[param.nbLig];
		param.avoirInitial = paiementDTO.avoirInitial;
		param.excluded =  new boolean[param.nbLig];
	
		for (int i = 0; i < param.nbLig; i++)
		{
			param.montant[i] = paiementDTO.datePaiements.get(i).montant;
			param.excluded[i] = false;
		}
		
		param.largeurCol = 170;
		param.espaceInterCol = 3;
		
		// Calcul d'une proposition de paiement si necessaire
		computePropositionPaiement();
		
				
		// Construction du header 1
		GridHeaderLine line1  =new GridHeaderLine();
		line1.height = 40;
		line1.styleName = "tete";
		line1.cells.add("Date");
		line1.cells.add("Montant €");
		
		param.headerLines.add(line1);
		
		// Partie gauche de chaque ligne
		for (DatePaiementDTO datePaiement : paiementDTO.datePaiements)
		{
			param.leftPartLine.add(df.format(datePaiement.datePaiement));
		}	
	}
	
	
	/**
	 * Calcul d'une proposition de paiement, dans le cas d'un nouveau contrat
	 */
	private void computePropositionPaiement()
	{
		// Si visualisation seule : pas de recalcul , on pourra visualiser l'eventuel ecart
		if (data.modeSaisie==ModeSaisie.READ_ONLY)
		{
			return ;
		}
		
		// Si cheque seul : pas de recalcul, on fait tout à la main 
		if (data.modeSaisie==ModeSaisie.CHEQUE_SEUL)
		{
			return ;
		}
		
		// Si contrat existant et en modification : on recalcule uniquement la dernière ligne 
		if (data.contratDTO.contratId!=null) 
		{
			int cumul = data.contratDTO.paiement.avoirInitial;
			for (int i = 0; i < param.nbLig-1; i++)
			{
				cumul = cumul+param.montant[i];
			}
			param.montant[param.nbLig-1] = param.montantCible-cumul;
			
			return ;
		}

		// Sinon on fait un calcul complet, en fonction du paramétrage 
		switch (peConf.modeCalculPaiement) 
		{
		case STANDARD:
			performPropositionStandard(peConf.montantChequeMiniCalculProposition);
			break;
			
		case TOUS_EGAUX:
			performPropositionTousEgaux();
			break;
			
		default:
			throw new AmapjRuntimeException();
		}

	}



	private void performPropositionStandard(int montantMini) 
	{
		//
		for (int i = 0; i < param.nbLig; i++)
		{
			if (shouldBeZero(i)==false)
			{
				// Calcul du montant restant à affecter
				int montantRestant = param.montantCible;
				for (int j = 0; j < i; j++)
				{
					montantRestant = montantRestant-param.montant[j];
				}
				
				
				// Ce montant à affecter  est inférieur au montant mini, on met tout sur cette ligne et on arrete
				if (montantRestant<montantMini)
				{
					param.montant[i] = montantRestant;
					break;
				}
				
				// Calcul du nombre de paiements disponibles
				int nbPaiement=param.nbLig-i;
				
				// Calcul du montant
				int mnt = round(montantRestant,nbPaiement);
				param.montant[i] = (mnt<montantMini) ? montantMini : mnt; 
			}
		}
	}

	/**
	 * Détermine si ce mois doit être à 0
	 * 
	 * Ceci est utile pour repartir equitablement les paiements
	 * 
	 * Exemple : l'utilisateur doit 40 euro sur 4 mois , avec un montant mini de 20 euro
	 * 
	 * Dans ce cas, le programme doit proposer 20 0 20 0 et non pas 20 20 0 0 
	 * 
	 * @param i
	 * @return
	 */
	private boolean shouldBeZero(int i)
	{
		// ne s'applique pas pour la dernière ligne ou la premiere ligne 
		if (  (i==(param.nbLig-1)) || (i==0) )
		{
			return false;
		}
		
		// Calcul du montant moyen par mois 
		int montantMoyen = param.montantCible/param.nbLig;
		
		// Calcul du montant qui doit être payé à cette date 
		int montantDu = montantMoyen*(i+1);
		
		
		// Calcul du montant déja affecté 
		int montantPaye = 0;
		for (int j = 0; j < i; j++)
		{
			montantPaye = montantPaye+param.montant[j];
		}
		
		if (montantPaye>=montantDu)
		{
			return true;
		}
		else
		{
			return false;	
		}
	}



	/**
	 */
	private int round(int montantRestant, int nbPaiement)
	{
		// Si il reste un dernier paiement : on l'affecte en entier
		if (nbPaiement==1)
		{
			return montantRestant;
		}
		return (montantRestant/nbPaiement/100)*100;
	}
	
	
	private void performPropositionTousEgaux() 
	{
		int montantStandard = param.montantCible/param.nbLig;
		
		int montantAffecte = 0;
		
		//
		for (int i = 0; i < param.nbLig-1; i++)
		{
			param.montant[i] = montantStandard;
			montantAffecte = montantAffecte+montantStandard;
			
		}
		
		// Pour la derniere ligne (pour corriger les erreurs sur les quarts de centimes)
		param.montant[param.nbLig-1] = param.montantCible-montantAffecte;
	}
	
	
	
	
	



	public void performSauvegarder() throws OnSaveException
	{
		if (param.montant[param.nbLig-1]<0)
		{
			throw new OnSaveException("Les paiements saisis sont incorrects. Il y a un trop payé de "+
								new CurrencyTextFieldConverter().convertToString(-param.montant[param.nbLig-1])+" €");
			
		}
		
		// Copie dans le DTO
		for (int i = 0; i < param.montant.length; i++)
		{
			paiementDTO.datePaiements.get(i).montant = param.montant[i];
		}
		
		
		if (data.modeSaisie!=ModeSaisie.FOR_TEST)
		{
			new MesContratsService().saveNewContrat(data.contratDTO,data.userId);
		}
	}
	
}
