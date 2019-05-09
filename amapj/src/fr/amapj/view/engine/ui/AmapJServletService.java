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
 package fr.amapj.view.engine.ui;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * Service principal
 * 
 */
@SuppressWarnings("serial")
public class AmapJServletService extends VaadinServletService
{

	public AmapJServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration) throws ServiceException
	{
		super(servlet, deploymentConfiguration);
	}

	@Override
	public UI findUI(VaadinRequest request) 
	{
		UI ui = super.findUI(request);
		AmapJLogManager.initializeLogUI(ui);
		return ui;
	}

	@Override
	public void requestEnd(VaadinRequest request, VaadinResponse response, VaadinSession session)
	{
		super.requestEnd(request, response, session);
		AmapJLogManager.endLog(false,null);
	}

}
