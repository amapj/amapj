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
 package fr.amapj.common;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Permet l'execution d'une commande système avec time out
 * 
 * 
 */
public class RuntimeUtils
{

	static public int executeCommandLine(String commandLine, final long timeout) throws IOException, InterruptedException, TimeoutException
	{
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(commandLine);

		Worker worker = new Worker(process);
		worker.start();
		try
		{
			worker.join(timeout);
			if (worker.exit != null)
				return worker.exit;
			else
				throw new TimeoutException();
		} catch (InterruptedException ex)
		{
			worker.interrupt();
			Thread.currentThread().interrupt();
			throw ex;
		} finally
		{
			process.destroy();
		}
	}

	private static class Worker extends Thread
	{
		private final Process process;
		private Integer exit;

		private Worker(Process process)
		{
			this.process = process;
		}

		public void run()
		{
			try
			{
				exit = process.waitFor();
			} catch (InterruptedException ignore)
			{
				return;
			}
		}
	}

}
