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
 package fr.amapj.view.engine.infoservlet;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.sun.management.UnixOperatingSystemMXBean;

import fr.amapj.common.StackUtils;

public class MonitorInfo
{
	// Charge du CPU entre 0 et 100 (totale de la machine)
	public int cpuLoad;

	// Pourcentage du disque disponible, compris entre 0 et 100 (totale de la
	// machine)
	public long diskFreeSpace;

	// Nombre de fichiers ouverts par l'application
	public long nbOpenFile;

	// Memoire (en Mo)
	public long memInit;
	public long memMax;
	public long memUsed;

	// Thread
	public int threadNb;
	public int threadPeak;
	
	// uptime en jours 
	public int upTimeDays;
	
	private final static Logger logger = LogManager.getLogger();
	
	private MonitorInfo()
	{
		
	}
	
	
	
	
	

	@Override
	public String toString()
	{
		return "cpuLoad=" + cpuLoad + "<br/> diskFreeSpace=" + diskFreeSpace + "<br/> nbOpenFile=" + nbOpenFile + "<br/> memInit=" + memInit + "<br/> memMax="
				+ memMax + "<br/> memUsed=" + memUsed + "<br/> threadNb=" + threadNb + "<br/> threadPeak=" + threadPeak + "<br/> upTimeDays=" + upTimeDays;
	}






	static public MonitorInfo calculateMonitorInfo()
	{
		MonitorInfo info = new MonitorInfo();

		try
		{
			// Charge CPU
			OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
			info.cpuLoad = (int) (os.getSystemLoadAverage() * 100);

			// Disque disponible
			File f = new File("/");
			if (f.getTotalSpace() != 0)
			{
				info.diskFreeSpace = (f.getFreeSpace() * 100L) / f.getTotalSpace();
			}

			// Nombre de fichiers ouverts
			if (os instanceof UnixOperatingSystemMXBean)
			{
				info.nbOpenFile = ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount();
			}

			// Memoire
			MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
			MemoryUsage mem = memBean.getHeapMemoryUsage();
			info.memInit = mem.getInit()/(1024*1024);
			info.memUsed = mem.getUsed()/(1024*1024);
			info.memMax = mem.getMax()/(1024*1024);
			
			// Nombre de threads
			ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
			info.threadNb = threadBean.getThreadCount();
			info.threadPeak = threadBean.getPeakThreadCount();
				
			// Uptime
			RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
			info.upTimeDays = (int) (runtimeBean.getUptime()/(1000*3600*24));

		} catch (Exception e)
		{
			logger.error(StackUtils.asString(e));
		}

		return info;

	}

	public static void main(String[] args)
	{
		MonitorInfo info = MonitorInfo.calculateMonitorInfo();
		Gson gson = new Gson();
		System.out.println(gson.toJson(info));
	}

}
