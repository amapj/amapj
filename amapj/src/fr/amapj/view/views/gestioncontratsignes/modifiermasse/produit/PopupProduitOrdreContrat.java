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

import com.vaadin.data.util.BeanItem;

import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.LigneContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet de modifier l'ordre des produits, même quand des constrats sont signés  
 * 
 *
 */
public class PopupProduitOrdreContrat extends WizardFormPopup
{

	private ModeleContratDTO modeleContrat;
	
	public enum Step
	{
		SAISIE_PRODUIT;
	}

	/**
	 * 
	 */
	public PopupProduitOrdreContrat(Long mcId)
	{
		setWidth(80);
		popupTitle = "Ordre des produits dans un contrat";

		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(mcId);
		item = new BeanItem<ModeleContratDTO>(modeleContrat);
				
	
	}
	
	@Override
	protected void configure()
	{
		add(Step.SAISIE_PRODUIT,()->addFieldOrdreProduit());
	}

	

	private void addFieldOrdreProduit()
	{
		// Titre
		setStepTitle("changer l'ordre des produits");
		
		// Le producteur
		Searcher prod = new Searcher(SearcherList.PRODUCTEUR);
		prod.bind(binder, "producteur");
		form.addComponent(prod);
		prod.setEnabled(false);
		
		// Les produits
		CollectionEditor<LigneContratDTO> f1 = new CollectionEditor<LigneContratDTO>("Produits", (BeanItem) item, "produits", LigneContratDTO.class);
		f1.addSearcherColumn("produitId", "Nom du produit",FieldType.SEARCHER, false,null,SearcherList.PRODUIT,prod);
		f1.addColumn("prix", "Prix du produit", FieldType.CURRENCY, false,null);
		f1.addBeanIdToPreserve("idModeleContratProduit");
			
		f1.activeButton(false, false, true, true);
		binder.bind(f1, "produits");
		form.addComponent(f1);
		
	}
	
	
	

	@Override
	protected void performSauvegarder()
	{
		new GestionContratSigneService().performModifProduitOrdreContrat(modeleContrat);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	
}
