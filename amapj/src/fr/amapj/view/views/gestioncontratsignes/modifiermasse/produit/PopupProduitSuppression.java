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
 package fr.amapj.view.views.gestioncontratsignes.modifiermasse.produit;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;

import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.LigneContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;

/**
 * Permet de supprimer des produits, même quand des constrats sont signés  
 * 
 *
 */
public class PopupProduitSuppression extends WizardFormPopup
{

	private ModeleContratDTO modeleContrat;
	
	private ComplexTableBuilder<LigneContratDTO> builder;
	
	private List<Long> modeleContratProduitsToSuppress;


	public enum Step
	{
		INFO_GENERALES, SAISIE_PRODUITS_A_SUPPRIMER , CONFIRMATION;
	}

	/**
	 * 
	 */
	public PopupProduitSuppression(Long mcId)
	{
		setWidth(80);
		popupTitle = "Suppression des produits d'un contrat";

		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(mcId);
		
		modeleContratProduitsToSuppress = new ArrayList<Long>();

	}
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.SAISIE_PRODUITS_A_SUPPRIMER,()->addFieldSaisiePrix(),()->readProduitsToSuppress());
		add(Step.CONFIRMATION,()->addFieldConfirmation());
	}

	private void addFieldInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales.");
		
		int nbInscrits = new GestionContratService().getNbInscrits(modeleContrat.id);
		String str;
		
		if (nbInscrits==0)
		{
			str = "Aucun adhérent n'est inscrit à ce contrat. Vous pouvez donc supprimer les prdouits librement.";
		}
		else
		{
			
			str = ""+nbInscrits+" adhérents ont déjà souscrits à ce contrat.<br/>"+
						 "La suppression d'un produit peut donc modifier le prix total du contrat pour ces adhérents.<br/><br/>"+
						 "Une fois que vous aurez supprimé des produits, le programme vous affichera la liste des adhérents impactés pour que vous puissiez les prévenir.<br/>";
			
			if (modeleContrat.gestionPaiement!=GestionPaiement.NON_GERE)
			{
				str = str + "Il faudra également modifier les paiements manuellement pour compenser l'écart de prix ou gérer un avoir en fin de contrat.<br/>.";
			}
						 
		}
		addLabel(str, ContentMode.HTML);
		

	}
	
	

	private void addFieldSaisiePrix()
	{
		// Titre
		setStepTitle("les produits à supprimer");
			
		builder = new ComplexTableBuilder<LigneContratDTO>(modeleContrat.produits);
		builder.setPageLength(7);
		
		builder.addString("Nom du produit", false, 300, e->e.produitNom);
		builder.addString("Conditionnement", false, 300,  e->e.produitConditionnement);
		builder.addCheckBox("Supprimer ce produit", "cb",true, 150, e->modeleContratProduitsToSuppress.contains(e.idModeleContratProduit), null);
		
		addComplexTable(builder);
		
	}
	
	private String readProduitsToSuppress()
	{
		modeleContratProduitsToSuppress.clear();
		
		
		for (int i = 0; i < modeleContrat.produits.size(); i++)
		{
			LigneContratDTO lig = modeleContrat.produits.get(i);
			
			// case du prix 
			CheckBox cb = (CheckBox) builder.getComponent(i, "cb");
			
			if (cb.getValue()==true)
			{
				modeleContratProduitsToSuppress.add(lig.idModeleContratProduit);
			}
		}	
		
		if (modeleContratProduitsToSuppress.size()==0)
		{
			return "Vous devez supprimer au moins un produit pour pouvoir continuer.";
		}
		
		return null;
	}
	
	
	private void addFieldConfirmation()
	{
		// Titre
		setStepTitle("confirmation");
		
		
		String info = new GestionContratSigneService().getSuppressProduitInfo(modeleContrat.id,modeleContratProduitsToSuppress);
			
		addLabel(info, ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier", ContentMode.HTML);
		
	}


	@Override
	protected void performSauvegarder()
	{
		new GestionContratSigneService().performSupressProduit(modeleContrat.id,modeleContratProduitsToSuppress);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	
}
