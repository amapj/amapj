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
 package fr.amapj.view.engine.popup.formpopup.fieldlink;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.openesignforms.ckeditor.CKEditorTextField;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import fr.amapj.model.models.param.paramecran.ChoixImpressionBilanLivraison;
import fr.amapj.view.engine.popup.formpopup.ValidatorManager;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidatorConditionnal;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidatorConditionnalFieldLink;

/**
 * Permet de créer un lien entre une combox box et l'activation / desactivation
 * d'un certain nombre d'élements liés
 */
public class FieldLink
{
	// Combo maitre 
	private ComboBox box;
	
	// Liste des éléments liés
	private List<AbstractField<?>> fields = new ArrayList<>(); 
	
	// Validateur qui pourra être appliqué sur les searchers si besoin 
	private NotNullValidatorConditionnalFieldLink notNull;
	
	// Liste des valeurs activant les elements
	private List<Enum<?>> actives = new ArrayList<>();

	private ValidatorManager validatorManager;

	// Contient les fils 
	private List<FieldLink> childs = new ArrayList<>();
	
	// Contient le pere
	private FieldLink parent;
	
	public FieldLink(ValidatorManager validatorManager,List<Enum<?>> actives,ComboBox box)
	{
		this.validatorManager = validatorManager;
		this.actives = actives;
		this.box = box;
		
		notNull = new NotNullValidatorConditionnalFieldLink();
		notNull.checkIf(this);
		box.addValueChangeListener(e->valueChanged());
	}
	
	/**
	 * Permet d'indiquer que ce field Link est piloté par un parent 
	 */
	public void setParent(FieldLink parent)
	{
		this.parent = parent;
		parent.childs.add(this);
		
	}
	
	
	public IValidator getValidator()
	{
		return notNull;
	}
	
	public void doLink()
	{
		// Appel de la mise à jour 
		valueChanged();
	}
	
	
	
	
	public boolean isActif()
	{
		if (parent!=null)
		{
			boolean parentState = parent.isActif();
			box.setEnabled(parentState);
			if (parentState==false)
			{
				return false;
			}
		}
		
		
		Enum en = (Enum) box.getValue();
		return actives.contains(en);

	}
	
	
	public void addField(AbstractField<?> f)
	{
		fields.add(f);
	}

	private void valueChanged()
	{
		boolean actif = isActif();
		
		for (AbstractField<?> field : fields)
		{
			// Activation - desactivation 
			if (field instanceof CKEditorTextField)
			{
				// Attention : il y a un bug dans le wrapper ckeditor, le setEnabled ne fonctionne pas
				( (CKEditorTextField)field).setViewWithoutEditor(!actif);
			}
			else
			{
				field.setEnabled(actif);
			}

			// Remise à zéro si nécessaire
			if (actif==false)
			{
				if (field instanceof TextField)
				{
					// Tres important : sinon la desactivation des IntegerTextField ne fonctionne pas 
					((TextField) field).setValue("");
				}
				else
				{
					field.setValue(null);
				}
			}
		}
		
		// On passe ensuite aux FieldLink fils
		for (FieldLink child : childs)
		{
			child.valueChanged();
		}
		
	}

	public List<Enum<?>> getActives()
	{
		return actives;
	}

	public ComboBox getBox()
	{
		return box;
	}

	public String getComboTitle()
	{
		return validatorManager.getTitle(box);
	}
	
	
}
