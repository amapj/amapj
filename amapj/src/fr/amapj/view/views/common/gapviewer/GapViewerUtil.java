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
 package fr.amapj.view.views.common.gapviewer;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.param.paramecran.GapViewer;
import fr.amapj.view.engine.popup.PopupListener;

public class GapViewerUtil
{
	static public AbstractGapViewer createGapWiever(GapViewer type,PopupListener listener)
	{
		switch (type)
		{
		case WEEK:
			return new WeekViewer(listener);

		case MONTH:
			return new MonthViewer(listener);
			
		/*case DATE_PER_DATE:
			return new DatePerDateViewer<T>(dates, toDate, listener);
			*/
			
		default:
			throw new AmapjRuntimeException();
		}
	}
}
