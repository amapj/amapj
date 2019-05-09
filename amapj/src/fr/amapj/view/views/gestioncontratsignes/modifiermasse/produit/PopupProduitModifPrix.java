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

import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.TextField;

import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.LigneContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;

/**
 * Permet de modifier le prix des produits, même quand des constrats sont signés  
 * 
 *
 */
public class PopupProduitModifPrix extends WizardFormPopup
{

	private ModeleContratDTO modeleContrat;
	
	private ComplexTableBuilder<LigneContratDTO> builder;


	public enum Step
	{
		INFO_GENERALES, SAISIE_PRIX , CONFIRMATION;
	}

	/**
	 * 
	 */
	public PopupProduitModifPrix(Long mcId)
	{
		setWidth(80);
		popupTitle = "Modification du prix des produits d'un contrat";

		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(mcId);

	}
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.SAISIE_PRIX,()->addFieldSaisiePrix(),()->readPrix());
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
			str = "Aucun adhérent n'est inscrit à ce contrat. Vous pouvez donc modifier les prix librement.";
		}
		else
		{
			
			str = ""+nbInscrits+" adhérents ont déjà souscrits à ce contrat.<br/>"+
						 "La modification des prix peut donc modifier le prix total du contrat pour ces adhérents.<br/><br/>"+
						 "Une fois que vous aurez modifié les prix, le programme vous affichera la liste des adhérents impactés pour que vous puissiez les prévenir.<br/>";
			
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
		setStepTitle("les nouveaux prix");
			
		builder = new ComplexTableBuilder<LigneContratDTO>(modeleContrat.produits);
		builder.setPageLength(7);
		
		builder.addString("Nom du produit", false, 300, e->e.produitNom);
		builder.addString("Conditionnement", false, 300,  e->e.produitConditionnement);
		builder.addCurrency("Prix", "prix",true , 100,  e->e.prix);
		
		addComplexTable(builder);
		
	}
	
	private String readPrix()
	{
		StringBuffer buf = new StringBuffer();
		
		for (int i = 0; i < modeleContrat.produits.size(); i++)
		{
			LigneContratDTO lig = modeleContrat.produits.get(i);
			
			// case du prix 
			TextField tf = (TextField) builder.getComponent(i, "prix");
			
			Integer p=null;
			try
			{
				p = (Integer) tf.getConvertedValue();
			}
			catch(Converter.ConversionException e)
			{
				
			}
			
			if (p==null)
			{
				buf.append("Erreur sur la saisie du prix pour le produit "+lig.produitNom+", "+lig.produitConditionnement+"<br/>");
			}
			else
			{
				lig.prix = p;
			}
		}	
		
		
		if (buf.length()!=0)
		{
			return "Merci de corriger les erreurs suivantes:<br/>"+buf.toString();
		}
		else
		{
			return null;
		}
	}
	
	
	private void addFieldConfirmation()
	{
		// Titre
		setStepTitle("confirmation");
		
		
		String info = new GestionContratSigneService().getModifPrixInfo(modeleContrat);
			
		addLabel(info, ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier", ContentMode.HTML);
		
	}


	@Override
	protected void performSauvegarder()
	{
		new GestionContratSigneService().performModifPrix(modeleContrat);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	
}
