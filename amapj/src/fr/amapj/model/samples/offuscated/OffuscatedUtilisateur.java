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
 package fr.amapj.model.samples.offuscated;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.StringUtils;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;

/**
 * Cette classe permet de changer le nom de tous les utilisateurs 
 * pour pouvoir faire des copies d'écrans facilement 
 * 
 * Tous les mots de passe sont aussi ré initialisés égaux à "a"
 */
public class OffuscatedUtilisateur
{

	static private String[] NOMS = { "Tremblay", "Dupuis" , "Dubois" , "Gagnon", "Roy", "Côté", "Bouchard", "Gauthier", "Morin", "Lavoie", "Fortin", "Gagné", "Ouellet", "Pelletier",
			"Bélanger", "Lévesque", "Bergeron", "Leblanc", "Paquette", "Girard", "Simard", "Boucher", "Caron", "Beaulieu", "Cloutier", "Dubé", "Poirier",
			"Fournier", "Lapointe", "Leclerc", "Lefebvre", "Poulin", "Thibault", "St-Pierre", "Nadeau", "Martin", "Landry", "Martel", "Bédard", "Grenier",
			"Lessard", "Bernier", "Richard", "Michaud", "Hébert", "Desjardins", "Couture", "Turcotte", "Lachance", "Parent", "Blais", "Gosselin", "Savard",
			"Proulx", "Beaudoin", "Demers", "Perreault", "Boudreau", "Lemieux", "Cyr", "Perron", "Dufour", "Dion", "Mercier", "Bolduc", "Bérubé", "Boisvert",
			"Langlois", "Ménard", "Therrien", "Plante", "Bilodeau", "Blanchette", "Champagne", "Paradis", "Fortier", "Arsenault", 
			"Gaudreault", "Hamel", "Houle", "Villeneuve", "Rousseau", "Gravel", "Thériault", "Lemay", "Robert", "Allard", "Deschênes", "Giroux", "Guay",
			"Leduc", "Boivin", "Charbonneau", "Lambert", "Raymond", "Vachon", "Gilbert", "Audet", "Jean", "Larouche", "Legault", "Trudel", "Fontaine",
			"Picard", "Labelle", "Lacroix", "Jacques", "Moreau", "Carrier", "Bernard", "Desrosiers", "Goulet", "Renaud", "Dionne", "Lapierre", "Vaillancourt",
			"Fillion", "Lalonde", "Tessier", "Bertrand", "Tardif", "Lepage", "Gingras", "Benoît", "Rioux", "Giguère", "Drouin", "Harvey", "Lauzon", "Nguyen",
			"Gendron", "Boutin", "Laflamme", "Vallée", "Dumont", "Breton", "Paré", "Paquin", "Robitaille", "Gélinas", "Duchesne", "Lussier", "Séguin",
			"Veilleux", "Potvin", "Gervais", "Pépin", "Laroche", "Morissette", "Charron", "Lavallée", "Laplante", "Chabot", "Brunet", "Vézina", "Desrochers",
			"Labrecque", "Coulombe", "Tanguay", "Chouinard", "Noël", "Pouliot", "Lacasse", "Daigle", "Marcoux", "Lamontagne", "Turgeon", "Larocque", "Roberge",
			"Auger", "Massé", "Pilon", "Racine", "Dallaire", "Émond", "Grégoire", "Beauregard", "Smith", "Denis", "Lebel", "Blouin", "Martineau", "Labbé",
			"Beauchamp", "St-Onge", "Charette", "Dupont", "Létourneau", "Rodrigue", "Cormier", "Rivard", "Mathieu", "Asselin", "St-Jean", "Plourde",
			"Thibodeau", "Bélisle", "St-Laurent", "Godin", "Desbiens", "Lavigne", "Doucet", "Labonté", "Marchand", "Brassard", "Forget", "Patel", "Marcotte",
			"Béland", "Larose", "Duval", "Archambault", "Maltais", "Trépanier", "Laliberté", "Bisson", "Brisson", "Dufresne", "Beaudry", "Chartrand", "Houde",
			"Fréchette", "Lafontaine", "Guillemette", "Drolet", "Vincent", "Richer", "Germain", "Larivière", "Ferland", "Trottier", "Piché", "Boulanger",
			"Sirois", "Charest", "Provost", "Durand", "Dumas", "Soucy", "Lamoureux", "Lachapelle", "Bégin", "Boily", "Croteau", "Savoie", "Provencher",
			"Prévost", "Duguay", "Lemire", "Delisle", "Desmarais", "Laberge", "Nault", "Bourgeois", "Lafrance", "Lagacé" };

	static private String[] PRENOMS = { "Antonin" , "Romain" , "Rémi" ,  "Magali" , "Gaelle" , "Nathalie" , "Benjamin" , "Alex" , "Karine" , "Arthur" , "Sophie" , "Dylan" , "Mathis" , "Matthieu"  , "David"  , "Joelle" ,
											 "Nadege" , "Jeanne " , "Emeline" , "Florent" , "Pascal" , "Charles" , "Jean-Luc" , "Mylène" , "Nadine" , "Marine" , "Frédéric" , "Yves" , "Bruno" , "Valentin"};
	
	static int indexNom = -1;
	
	static int indexPrenom = -1;
	

	@DbWrite
	public void createData()
	{
		EntityManager em = TransactionHelper.getEm();
		
		Query q = em.createQuery("select u from Utilisateur u order by u.id");

		List<Utilisateur> us = q.getResultList();
		for (Utilisateur u : us)
		{
			u.setNom(getNom());
			u.setPrenom(getPrenom());

			String email = (u.getPrenom() + "." + u.getNom() + "@example.fr").toLowerCase();
			email = StringUtils.sansAccent(email);
			
			u.setEmail(email);
			
			
			// Mot de passe : a 
			u.setPassword("S3SDt6lhE40QpL/8QFZgLeJveys=");
			u.setSalt("EkCq0lok/zk=");
			// System.out.println(u.getNom()+"=>"+getNom()+" "+getPrenom());

		}

	}

	private String getPrenom()
	{
		indexPrenom++;
		if (indexPrenom>=PRENOMS.length)
		{
			indexPrenom=0;
		}
		return PRENOMS[indexPrenom];
	}

	private String getNom()
	{
		indexNom++;
		if (indexNom>=NOMS.length)
		{
			indexNom=0;
		}
		return NOMS[indexNom].toUpperCase();
	}

	public static void main(String[] args)
	{
		TestTools.init();
		
		OffuscatedUtilisateur insertDbRole = new OffuscatedUtilisateur();
		System.out.println("Debut du changement des noms");
		insertDbRole.createData();
		System.out.println("Fin du changement des noms");

	}

}
