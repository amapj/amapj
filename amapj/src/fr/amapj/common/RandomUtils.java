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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Gestion des nombres aleatoires
 * 
 *  
 */
public class RandomUtils 
{
	
	/**
	 * Génére un password constitué de majuscules uniquement
	 * @param length
	 * @return
	 */
	static public String generatePasswordMaj(int length)
	{
		return generatePassword(length,'A',26);
	}
	
	/**
	 * Génére un password constitué de minuscules uniquement
	 * @param length
	 * @return
	 */
	static public String generatePasswordMin(int length)
	{
		return generatePassword(length,'a',26);
	}
	
	
	
	private static String generatePassword(int length, char firstChar, int nbCar)
	{
		try
		{
			// 
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			byte[] buf = new byte[length];
			
			for (int i = 0; i < length; i++)
			{
				buf[i] = (byte) (firstChar+random.nextInt(nbCar));
			}
			return new String(buf);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Erreur inattendue", e);
		}
	}

	public static void main(String[] args)
	{
		for (int i = 0; i < 100; i++)
		{
			String str = generatePasswordMaj(10);
			System.out.println("str="+str);
		}
		
		System.out.println("=============================================");
		
		for (int i = 0; i < 100; i++)
		{
			String str = generatePasswordMin(10);
			System.out.println("str="+str);
		}
	}
	

}
