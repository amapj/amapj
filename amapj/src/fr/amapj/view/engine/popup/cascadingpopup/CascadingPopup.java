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
 package fr.amapj.view.engine.popup.cascadingpopup;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;


/**
 * Utilitaire permettant de faire du cascading de popup 
 * 
 *
 */
public class CascadingPopup
{
	private PopupListener finalListener;
	
	private CascadingData data;
	
	static public interface GetInfo
	{
		public CInfo getInfo();
	}
	
	


	
	public CascadingPopup(PopupListener finalListener,CascadingData data)
	{
		this.finalListener = finalListener;
		this.data = data;
	}
	
	
	public void start(CInfo info)
	{
		// Si rien a faire, on ne fait rien et on revient au final listener 
		if (info==null)
		{
			callFinalListener();
			return;
		}
		
		if (info.popup==null)
		{
			throw new AmapjRuntimeException("Erreur : ne peut pas être null");
		}
		
		// On invalide l'information "Continuer"
		data.inValidate();
		
		// On ouvre la  fenetre 
		CorePopup.open(info.popup, ()->next(info));
		
	}


	private void next(CInfo info)
	{
		// L'opérateur a fait OK
		if (data.shouldContinue())
		{
			// Si pas de onSuccess : c'est fini 
			if (info.onSuccess==null)
			{
				callFinalListener();
				return;
			}
		
			// Demande de calcul du popup suivant 
			CInfo nextInfo = info.onSuccess.getInfo();
			start(nextInfo);
		}
		// L'opérateur n'a pas fait de OK
		else
		{
			// Si pas de onSuccess : c'est fini 
			if (info.onFail==null)
			{
				callFinalListener();
				return;
			}
		
			// Demande de calcul du popup suivant 
			CInfo nextInfo = info.onFail.getInfo();
			start(nextInfo);
		}
		
		
	}
	

	private void callFinalListener()
	{
		if (finalListener!=null)
		{
			finalListener.onPopupClose();
		}
	}


	
}
