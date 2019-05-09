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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Permet de créer un Inpustream sur un fichier, avec effacement du fichier lors du close
 * sur le InputStream
 *
 */
public class DeleteOnCloseFileInputStream extends FileInputStream
{
	private File file;
	private boolean deleteFileOnClose;

	public DeleteOnCloseFileInputStream(File file,boolean deleteFileOnClose) throws FileNotFoundException
	{
		super(file);
		this.file = file;
		this.deleteFileOnClose = deleteFileOnClose;
	}

	public void close() throws IOException
	{
		try
		{
			super.close();
		} 
		finally
		{
			if (file != null && deleteFileOnClose)
			{
				file.delete();
				file = null;
			}
		}
	}
}
