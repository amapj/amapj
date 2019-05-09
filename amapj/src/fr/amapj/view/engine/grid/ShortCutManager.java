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
 package fr.amapj.view.engine.grid;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class ShortCutManager
{
	
	private int nbLig;
	private int nbCol;
	
	// Peut etre null
	private boolean[][] excluded;
	
	private Map<GridIJData, TextField> cells = new HashMap<>();
	
	
	public enum Key
	{
		ENTER, UP , DOWN , RIGHT, LEFT , PLUS;
	}

	
	
	
	public ShortCutManager(int nbLig, int nbCol, boolean[][] excluded)
	{
		super();
		this.nbLig = nbLig;
		this.nbCol = nbCol;
		this.excluded = excluded;
	}
	
	class ShortcutListenerImpl extends ShortcutListener
	{
		private Key key;
		
		public ShortcutListenerImpl(int keyCode, Key key)
		{
			super("", keyCode, null);
			this.key = key;
		}

		@Override
		public void handleAction(Object sender, Object target)
		{
			processAction(key,target);
		}
		
	}

	

	public void addShorcut(Window w)
	{
		w.addShortcutListener(new ShortcutListenerImpl(KeyCode.ENTER,Key.ENTER));
		w.addShortcutListener(new ShortcutListenerImpl(107,Key.PLUS));
		
		w.addShortcutListener(new ShortcutListenerImpl(KeyCode.ARROW_RIGHT,Key.RIGHT));
		w.addShortcutListener(new ShortcutListenerImpl(KeyCode.ARROW_LEFT,Key.LEFT));
		w.addShortcutListener(new ShortcutListenerImpl(KeyCode.ARROW_UP,Key.UP));
		w.addShortcutListener(new ShortcutListenerImpl(KeyCode.ARROW_DOWN,Key.DOWN));
	}



	protected void processAction(Key key, Object target)
	{
		if ( (target instanceof TextField)==false)
		{
			return ;
		}
		
		TextField from = (TextField) target;
		GridIJData fromIJ = (GridIJData) from.getData();
		GridIJData toIJ = findNext(key,fromIJ);
		if (toIJ!=null)
		{
			TextField to = cells.get(toIJ);
			to.selectAll();
			to.focus();
		}
	}

	private GridIJData findNext(Key key, GridIJData fromIJ)
	{
		do	
		{
			fromIJ = createNext(key,fromIJ);
		}
		while ( (fromIJ!=null) && (isExcluded(fromIJ)==true) );
		
		return fromIJ;
	}
		
		
		
		



	private GridIJData createNext(Key key,GridIJData fromIJ)
	{	
		switch (key)
		{
		
		// Meme comportement pour enter ou fleche bas
		case ENTER:
		case DOWN:
			return createNextDown(fromIJ);
			
		case UP:
			return createNextUp(fromIJ);
			
		case RIGHT:
			return createNextRight(fromIJ);
			
		case LEFT:
			return createNextLeft(fromIJ);
			
		case PLUS:
			return createNextPlus(fromIJ);

		default:
			throw new RuntimeException("Erreur inattendue");
		}
		
	}



	


	/**
	 * La touche plus permet de se deplacer de gauche à droite, et quand
	 * on arrive au bout on revient à droite à la ligne en dessous
	 * 
	 */
	private GridIJData createNextPlus(GridIJData fromIJ)
	{
		// Si c'est la derniere case : pas de suivant 
		if ( (fromIJ.i()==nbLig-1) && (fromIJ.j()==nbCol-1) )
		{
			return null;
		}
		
		// Si c'est la derniere colonne 
		if ( fromIJ.j()==nbCol-1) 
		{
			return new GridIJData(fromIJ.i()+1, 0);
		}
		
		// Sinon
		return new GridIJData(fromIJ.i(), fromIJ.j()+1);
		
	}
	
	
	private GridIJData createNextUp(GridIJData fromIJ)
	{
		// Si c'est la premiere ligne : pas de suivant 
		if (fromIJ.i()==0)
		{
			return null;
		}
				
		// Sinon
		return new GridIJData(fromIJ.i()-1, fromIJ.j());
	}
	
	private GridIJData createNextDown(GridIJData fromIJ)
	{
		// Si c'est la derniere ligne : pas de suivant 
		if (fromIJ.i()==nbLig-1)
		{
			return null;
		}
				
		// Sinon
		return new GridIJData(fromIJ.i()+1, fromIJ.j());
	}
	
	
	private GridIJData createNextRight(GridIJData fromIJ)
	{
		// Si c'est la derniere colonne : pas de suivant 
		if (fromIJ.j()==nbCol-1)
		{
			return null;
		}
				
		// Sinon
		return new GridIJData(fromIJ.i(), fromIJ.j()+1);
	}
	
	private GridIJData createNextLeft(GridIJData fromIJ)
	{
		// Si c'est la premiere colonne : pas de suivant 
		if (fromIJ.j()==0)
		{
			return null;
		}
				
		// Sinon
		return new GridIJData(fromIJ.i(), fromIJ.j()-1);
	}
	

	
	private boolean isExcluded(GridIJData fromIJ)
	{
		if (excluded==null)
		{
			return false;
		}
		return excluded[fromIJ.i()][fromIJ.j()];
	}



	public void registerTextField(TextField tf)
	{
		GridIJData ij = (GridIJData) tf.getData();
		cells.put(ij, tf);
	}	
	
}

