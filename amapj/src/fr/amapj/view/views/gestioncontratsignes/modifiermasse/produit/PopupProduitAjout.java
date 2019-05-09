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

import java.util.List;

import com.vaadin.data.util.BeanItem;

import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.LigneContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.views.searcher.SDProduitHorsContrat;

/**
 * Permet d'ajouter des produits, même quand des constrats sont signés  
 * 
 *
 */
public class PopupProduitAjout extends WizardFormPopup
{

	private ModeleContratDTO modeleContrat;
	
	public enum Step
	{
		SAISIE_PRODUIT;
	}

	/**
	 * 
	 */
	public PopupProduitAjout(Long mcId)
	{
		setWidth(80);
		popupTitle = "Ajout de produits à un contrat";

		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(mcId);
		item = new BeanItem<ModeleContratDTO>(modeleContrat);
		
		// On efface la liste des produits déjà inscrits 
		modeleContrat.produits.clear();
		
		

	}
	
	@Override
	protected void configure()
	{
		add(Step.SAISIE_PRODUIT,()->addFieldSaisieProduit());
	}

	/**
	 * Vérifie si il y a encore des produits disponibles 
	 */
	@Override
	protected String checkInitialCondition()
	{
		List<Produit> prs = new GestionContratSigneService().getProduitHorsContrat(modeleContrat.id);
		if (prs.size()!=0)
		{
			return null;
		}
		return "Ce producteur ne posséde plus de produits qui pourraient être ajoutés à ce contrat.<br/>"+
			   "Si vous voulez ajouter un produit, il faut d'abord le créer en allant dans le menu \"Gestion des produits\"";
		
		
		
	}
	
	

	private void addFieldSaisieProduit()
	{
		// Titre
		setStepTitle("les nouveaux produits");
			
		SDProduitHorsContrat searcher = new SDProduitHorsContrat(modeleContrat.id);
		
		// Les produits
		CollectionEditor<LigneContratDTO> f1 = new CollectionEditor<LigneContratDTO>("Produits", (BeanItem) item, "produits", LigneContratDTO.class);
		f1.addSearcherColumn("produitId", "Nom du produit",FieldType.SEARCHER, null,searcher,null);
		f1.addColumn("prix", "Prix du produit", FieldType.CURRENCY, null);
		binder.bind(f1, "produits");
		form.addComponent(f1);
		
	}
	
	

	@Override
	protected void performSauvegarder()
	{
		new GestionContratSigneService().performAjoutProduit(modeleContrat);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	
}
