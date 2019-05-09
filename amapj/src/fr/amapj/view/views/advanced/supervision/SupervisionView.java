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
 package fr.amapj.view.views.advanced.supervision;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.routing.RoutingAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.GenericUtils;
import fr.amapj.common.GenericUtils.StringAction;
import fr.amapj.service.services.advanced.maintenance.MaintenanceService;
import fr.amapj.service.services.advanced.supervision.SupervisionService;
import fr.amapj.service.services.mailer.MailerCounter;
import fr.amapj.view.engine.infoservlet.MonitorInfo;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.template.BackOfficeLongView;

public class SupervisionView extends BackOfficeLongView implements View
{

	private final static Logger logger = LogManager.getLogger();
	
	@Override
	public String getMainStyleName()
	{
		return "maintenance";
	}
	
	Label labelDateHeure;
	TextField textDateHeure;
	

	@Override
	public void enterIn(ViewChangeEvent event)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		

		addLabel(this, "Supervision du système","titre");
	 
		addLabel(this, "Date et heure courante :"+df.format(DateUtils.getDate()));
		addLabel(this, "Version de l'application : "+new MaintenanceService().getVersion());
		addLabel(this, "Nombre d'emails envoyés aujourd'hui : "+MailerCounter.getNbMails());
		
		
		Panel diversPanel = new Panel("Outils d'admin - Experts uniquement !");
		diversPanel.addStyleName("action");
		diversPanel.setContent(getDiversPanel());
		
		
		addComponent(diversPanel);
	}
	
	
	
	
	private Component getDiversPanel()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		
		
		addEmptyLine(layout);
		
		
		addButtonForInfo(layout,"Informations générales",()->displayInfoGenerales());
		
		addButtonForInfo(layout,"Visualiser le nombre de loggers actifs",()->displayLoggerActif());
		
		addButtonForInfo(layout,"Afficher le cache JPA dans toutes les bases",()->new SupervisionService().dumpCacheForAllBases());
		
		Button b2 = new Button("Remise à zéro du cache JPA dans toutes les bases", e->handleResetAllDatabaseCache());
		layout.addComponent(b2);
		addEmptyLine(layout);
		
		Button b3 = new Button("Appel du garbage collector", e->handleGarbageCollector());
		layout.addComponent(b3);
		addEmptyLine(layout);	

				
		
		
			
		addEmptyLine(layout);
		
		return layout;
	}
	
	
	

	private String displayInfoGenerales()
	{
		MonitorInfo info =  MonitorInfo.calculateMonitorInfo();
		return info.toString();
	}
	



	private String displayLoggerActif()
	{
		int nb = countLoggerActif();
		return "Le nombre de logger actif est :"+nb;
	}
	
	
	private int  countLoggerActif()
	{
		// Code repris depuis la classe AmapJLogManager
		
		org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) logger; 
		org.apache.logging.log4j.core.LoggerContext context = (org.apache.logging.log4j.core.LoggerContext)coreLogger.getContext(); 
		RoutingAppender appender = (RoutingAppender) context.getConfiguration().getAppender("Routing");
		
		
		try
		{
			Field f = appender.getClass().getDeclaredField("appenders");
			f.setAccessible(true);
			ConcurrentMap<String, AppenderControl> appenders = (ConcurrentMap<String, AppenderControl>) f.get(appender);
			
			return appenders.size();
			
		} 
		catch (SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException e)
		{
			throw new AmapjRuntimeException(e);
		}
	}
	
	
	
	private void handleGarbageCollector()
	{
		System.gc();
		Notification.show("Done!");
	}




	private void handleResetAllDatabaseCache()
	{
		new SupervisionService().resetAllDataBaseCache();
		Notification.show("Done!");
	}




	

	
	private Label addLabel(VerticalLayout layout, String str,String stylename)
	{
		Label tf = new Label(str);
		if (stylename!=null)
		{
			tf.addStyleName(stylename);
		}
		layout.addComponent(tf);
		return tf;
	}
	
	private Label addLabel(VerticalLayout layout, String str)
	{
		return addLabel(layout, str, null);
	}
	
	
	private Label addEmptyLine(VerticalLayout layout)
	{
		Label tf = new Label("<br/>",ContentMode.HTML);
		tf.addStyleName(ChameleonTheme.LABEL_BIG);
		layout.addComponent(tf);
		return tf;

	}
	
	
	private void addButtonForInfo(VerticalLayout layout, String titre, GenericUtils.StringAction stringAction)
	{
		Button b2 = new Button(titre, e->handleButtonForInfo(titre,stringAction));
		layout.addComponent(b2);
		addEmptyLine(layout);
	}

	private void handleButtonForInfo(String titre,StringAction stringAction)
	{
		String msg = stringAction.action();
		MessagePopup.open(new MessagePopup(titre,ContentMode.HTML,ColorStyle.GREEN,msg));
	}

	
}
