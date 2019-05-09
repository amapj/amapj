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
 package fr.amapj.view.views.cotisation.reception;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.DateUtils;
import fr.amapj.model.models.cotisation.EtatPaiementAdhesion;
import fr.amapj.service.services.gestioncotisation.BilanAdhesionDTO;
import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.gestioncotisation.PeriodeCotisationUtilisateurDTO;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.tools.DateToStringConverter;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Popup pour la réception des cotisations
 *  
 */
@SuppressWarnings("serial")
public class PopupReceptionMasseCotisation extends OKCancelPopup 
{
		
	private List<PeriodeCotisationUtilisateurDTO> dtos;

	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private Table beanTable;
	private BeanItemContainer<PeriodeCotisationUtilisateurDTO> listPartContainer;
	
	/**
	 * 
	 */
	public PopupReceptionMasseCotisation(Long idPeriodeCotisation)
	{
		popupTitle = "Réception des cotisations";
		BilanAdhesionDTO bilanAdhesionDTO = new GestionCotisationService().loadBilanAdhesion(idPeriodeCotisation);
		this.dtos = filter(bilanAdhesionDTO.utilisateurDTOs);
			
	}
	

	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		listPartContainer = new BeanItemContainer<>(PeriodeCotisationUtilisateurDTO.class);
		
		// Bind it to a component
		beanTable = new Table("", listPartContainer);
		beanTable.setStyleName("big strong");
		
		
		beanTable.addGeneratedColumn("etatPaiement", new ColumnGenerator() 
		{ 
		    @Override
		    public Object generateCell(final Table source, final Object itemId, Object columnId) 
		    {
		    	final PeriodeCotisationUtilisateurDTO dto = (PeriodeCotisationUtilisateurDTO) itemId;
		    	
		    	if (dto.etatPaiementAdhesion==EtatPaiementAdhesion.ENCAISSE)
		    	{
			    	Label l = new Label("OUI");
			    	return l;
		    	}
		    	else
		    	{
		    		CheckBox box = new CheckBox();
		    		box.setValue(false);
		    		box.addValueChangeListener(new ValueChangeListener()
					{
						
						@Override
						public void valueChange(ValueChangeEvent event)
						{
							Boolean b = (Boolean) event.getProperty().getValue();
							if (b.booleanValue()==true)
							{
								dto.etatPaiementAdhesion = EtatPaiementAdhesion.ENCAISSE;
								dto.dateReceptionCheque = DateUtils.getDate();
							}
							else
							{
								dto.etatPaiementAdhesion = EtatPaiementAdhesion.A_FOURNIR;
								dto.dateReceptionCheque = null;
							}
						}
					});
			    	return box;
		    	}
		    	
		    }
		});
		
		

		
		// Gestion de la liste des colonnes visibles
		beanTable.setVisibleColumns("nomUtilisateur","prenomUtilisateur","montantAdhesion","etatPaiement","typePaiementAdhesion");
		
		beanTable.setColumnHeader("nomUtilisateur","Nom");
		beanTable.setColumnHeader("prenomUtilisateur","Prénom");
		beanTable.setColumnHeader("montantAdhesion","Montant en €");
		beanTable.setColumnAlignment("montantAdhesion",Align.RIGHT);
		beanTable.setColumnHeader("etatPaiement","Réceptionné");
		beanTable.setColumnAlignment("montantAdhesion",Align.CENTER);
		beanTable.setColumnHeader("typePaiementAdhesion","Type");
		
		beanTable.setConverter("montantAdhesion", new CurrencyTextFieldConverter());
		
		
		
		beanTable.setSelectable(true);
		beanTable.setImmediate(true);

		beanTable.setSizeFull();
		
		contentLayout.addComponent(beanTable);
		contentLayout.setExpandRatio(beanTable, 1);
		
		listPartContainer.addAll(dtos);
	}
	
	
	/**
	 * On conserve uniquement les adhesions à réceptionner
	 * @param utilisateurDTOs
	 * @return
	 */
	private List<PeriodeCotisationUtilisateurDTO> filter(List<PeriodeCotisationUtilisateurDTO> utilisateurDTOs)
	{
		List<PeriodeCotisationUtilisateurDTO> res = new ArrayList<PeriodeCotisationUtilisateurDTO>();
		
		for (PeriodeCotisationUtilisateurDTO pcu : utilisateurDTOs)
		{
			if (pcu.etatPaiementAdhesion==EtatPaiementAdhesion.A_FOURNIR)
			{
				res.add(pcu);
			}
		}
		return res;
	}


	private String getDate(Date dateReceptionCheque)
	{
		if (dateReceptionCheque==null)
		{
			return "";
		}
		return df.format(dateReceptionCheque);
	}


	@Override
	protected boolean performSauvegarder()
	{
		new GestionCotisationService().receptionMasseAdhesion(dtos);
		return true;
		
	}




}
