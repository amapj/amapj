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
 package fr.amapj.service.services.mespaiements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.DebugUtil;
import fr.amapj.common.LongUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.model.models.contrat.reel.Paiement;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.mescontrats.DatePaiementDTO;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Permet la gestion des modeles de contrat
 * 
 *  
 *
 */
public class MesPaiementsService
{

	public MesPaiementsService()
	{

	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES PAIEMENTS

	/**
	 * Retourne la liste paiements pour l'utilisateur courant
	 */
	@DbRead
	public MesPaiementsDTO getMesPaiements(Long userId)
	{
		EntityManager em = TransactionHelper.getEm();
		
		MesPaiementsDTO res = new MesPaiementsDTO();
		Utilisateur user = em.find(Utilisateur.class, userId);

		res.paiementAFournir = getPaiementAFournir(em, user);
		res.paiementFourni = getPaiementFourni(em, user);
		res.paiementHistorique = getPaiementHistorique(em,user);

		return res;

	}

	private List<PaiementAFournirDTO> getPaiementAFournir(EntityManager em, Utilisateur user)
	{
		List<PaiementAFournirDTO> res = new ArrayList<>();

		// On récupère d'abord la liste des contrats de l'utilisateur avec des
		// paiements à l'état A_FOURNIR
		Query q = em.createQuery("select c from Contrat c "
				+ "WHERE c.utilisateur=:u and EXISTS ( select p from Paiement p where p.etat=:etat and p.contrat=c) "
				+ "order by c.modeleContrat.dateRemiseCheque asc , c.modeleContrat.nom , c.modeleContrat.id");
		q.setParameter("etat", EtatPaiement.A_FOURNIR);
		q.setParameter("u", user);

		List<Contrat> cs = q.getResultList();

		for (Contrat contrat : cs)
		{
			PaiementAFournirDTO dto = new PaiementAFournirDTO();
			dto.dateRemise = contrat.getModeleContrat().getDateRemiseCheque();
			dto.libCheque = contrat.getModeleContrat().getLibCheque();
			dto.nomContrat = contrat.getModeleContrat().getNom();
			dto.paiements = getPaiementAFournir(em, contrat);
			res.add(dto);
		}
		return res;
	}

	public List<DetailPaiementAFournirDTO> getPaiementAFournir(EntityManager em, Contrat contrat)
	{
		SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");

		List<DetailPaiementAFournirDTO> res = new ArrayList<>();

		Query q = em.createQuery("select p from Paiement p " + "WHERE p.etat=:etat and p.contrat=:c "
				+ "order by p.montant asc , p.modeleContratDatePaiement.datePaiement asc");
		q.setParameter("etat", EtatPaiement.A_FOURNIR);
		q.setParameter("c", contrat);

		List<Paiement> ps = q.getResultList();

		for (Paiement paiement : ps)
		{
			String datePaiement = df.format(paiement.getModeleContratDatePaiement().getDatePaiement());
			if (lastMatch(res, paiement) == true)
			{
				DetailPaiementAFournirDTO last = res.get(res.size() - 1);
				last.nbCheque++;
				last.moisPaiement = last.moisPaiement + ", " + datePaiement;
			}
			else
			{
				DetailPaiementAFournirDTO dto = new DetailPaiementAFournirDTO();
				dto.nbCheque = 1;
				dto.moisPaiement = datePaiement;
				dto.montant = paiement.getMontant();
				res.add(dto);
			}
		}
		return res;
	}

	private boolean lastMatch(List<DetailPaiementAFournirDTO> res, Paiement paiement)
	{
		if (res.size() == 0)
		{
			return false;
		}
		DetailPaiementAFournirDTO last = res.get(res.size() - 1);
		if (last.montant == paiement.getMontant())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private List<PaiementFourniDTO> getPaiementFourni(EntityManager em, Utilisateur user)
	{
		SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");
		List<PaiementFourniDTO> res = new ArrayList<>();

		// On récupère d'abord la liste des paiements qui n'ont pas été donné aux producteurs
		Query q = em.createQuery("select p from Paiement p " + "WHERE p.etat<>:etat and p.contrat.utilisateur=:u "
				+ "order by p.modeleContratDatePaiement.datePaiement," + " p.modeleContratDatePaiement.modeleContrat.nom ,"
				+ " p.modeleContratDatePaiement.modeleContrat.id");
		q.setParameter("etat", EtatPaiement.PRODUCTEUR);
		q.setParameter("u", user);

		List<Paiement> ps = q.getResultList();

		for (Paiement paiement : ps)
		{
			DetailPaiementFourniDTO detail = createDetail(paiement);
			String datePaiement = df.format(paiement.getModeleContratDatePaiement().getDatePaiement());
			if (lastMatch(res, datePaiement) == true)
			{
				PaiementFourniDTO last = res.get(res.size() - 1);
				last.paiements.add(detail);
				last.totalMois = last.totalMois+detail.montant;
			}
			else
			{
				PaiementFourniDTO dto = new PaiementFourniDTO();
				dto.moisPaiement = datePaiement;
				dto.totalMois = detail.montant;
				dto.paiements.add(detail);
				res.add(dto);
			}
		}
		return res;
	}

	private boolean lastMatch(List<PaiementFourniDTO> res, String datePaiement)
	{
		if (res.size() == 0)
		{
			return false;
		}
		PaiementFourniDTO last = res.get(res.size() - 1);
		if (last.moisPaiement.equals(datePaiement))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private DetailPaiementFourniDTO createDetail(Paiement paiement)
	{
		DetailPaiementFourniDTO dto = new DetailPaiementFourniDTO();
		dto.libCheque = paiement.getContrat().getModeleContrat().getLibCheque();
		dto.nomContrat = paiement.getContrat().getModeleContrat().getNom();
		dto.montant = paiement.getMontant();
		dto.etatPaiement = paiement.getEtat();
		return dto;
	}
	
	
	private List<PaiementHistoriqueDTO> getPaiementHistorique(EntityManager em, Utilisateur user)
	{
		List<PaiementHistoriqueDTO> res = new ArrayList<>();

		// On récupère d'abord la liste des paiements qui ont été donné aux
		// producteurs
		Query q = em.createQuery("select p from Paiement p  WHERE p.etat=:etat and p.contrat.utilisateur=:u order by p.id");
		q.setParameter("etat", EtatPaiement.PRODUCTEUR);
		q.setParameter("u", user);

		List<Paiement> ps = q.getResultList();

		for (Paiement paiement : ps)
		{
			PaiementHistoriqueDTO dto = new PaiementHistoriqueDTO();
			
			dto.id = paiement.getId();
			dto.nomProducteur = paiement.getContrat().getModeleContrat().getProducteur().nom;
			dto.nomContrat = paiement.getContrat().getModeleContrat().getNom();
			dto.montant = paiement.getMontant();
			dto.datePrevu = paiement.getModeleContratDatePaiement().getDatePaiement();
			dto.dateReelle = paiement.getRemise().getDateRemise();
			
			res.add(dto);
		}
		return res;
	}
	

	
	/*
	 * Partie gestion de la reception des cheques
	 */
	/**
	 * Permet de charger la liste de tous les paiements à réceptionner 
	 * dans une transaction en lecture
	 */
	@DbRead
	public List<DatePaiementDTO> getPaiementAReceptionner(Long contratId)
	{
		EntityManager em = TransactionHelper.getEm();

		Contrat contrat = em.find(Contrat.class, contratId);
		
		Query q = em.createQuery("select p from Paiement p " + "WHERE p.etat<>:etat and p.contrat=:c "
				+ "order by p.modeleContratDatePaiement.datePaiement asc");
		q.setParameter("etat", EtatPaiement.PRODUCTEUR);
		q.setParameter("c", contrat);
		
		List<DatePaiementDTO> res = new ArrayList<>();
		List<Paiement> paiements = (List<Paiement>) q.getResultList();
		for (Paiement paiement : paiements)
		{
			DatePaiementDTO dto = new DatePaiementDTO();
			dto.idPaiement = paiement.getId();
			dto.datePaiement = paiement.getModeleContratDatePaiement().getDatePaiement();
			dto.montant = paiement.getMontant();
			dto.etatPaiement = paiement.getEtat();
			dto.commentaire1 = paiement.getCommentaire1();
			dto.commentaire2 = paiement.getCommentaire2();
			
			res.add(dto);
		}
	
		
		return res;
	}
	
	
	/**
	 * 
	 * @param contratDTO
	 */
	@DbWrite
	public void receptionCheque(final List<DatePaiementDTO> paiementDto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		for (DatePaiementDTO dto : paiementDto)
		{
			Paiement p = em.find(Paiement.class, dto.idPaiement);
			p.setEtat(dto.etatPaiement);
			p.setCommentaire1(dto.commentaire1);
			p.setCommentaire2(dto.commentaire2);
						
		}	
	}

	
	/**
	 * Permet de retrouver les chéques à rendre aux amapiens
	 * @param mcId
	 * @return
	 */
	@DbRead
	public String chercherChequeARendre(Long mcId)
	{
		EntityManager em = TransactionHelper.getEm();
		
		StringBuffer buf = new StringBuffer();
		
		ModeleContrat mc = em.find(ModeleContrat.class, mcId);
		
		Query q = em.createQuery("select c from Contrat c where "+
				" c.modeleContrat=:mc "+
				" order by c.utilisateur.nom, c.utilisateur.prenom");
		
		q.setParameter("mc",mc);	
		
		List<Contrat> contrats = q.getResultList();
		for (Contrat contrat : contrats)
		{
			chercherChequeARendre(em,contrat,buf);
		}
		
		return buf.toString();
	}

	private void chercherChequeARendre(EntityManager em, Contrat contrat, StringBuffer buf)
	{
		// Pour chaque utilisateur, on calcule la liste des chèques encore en possession de l'AMAP
		// et le trop payé
		
		// Le montant des prdouits commandés
		int mntCommande = new GestionContratSigneService().getMontant(em, contrat);
		
		// Le montant donné à l'AMAP
		int mntDonneAmap = getMontantDonneAMAP(contrat,em);
		
		// La liste des chèques restant à remettre et en possession de l'AMAP 
		List<Paiement> paiements = getPaiementALAMAP(contrat,em);
		
		if ( (mntDonneAmap>mntCommande) && (paiements.size()>0) )
		{
			Utilisateur u = contrat.getUtilisateur();
			SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");
			
			buf.append("L'adhérent "+u.getNom()+" "+u.getPrenom()+" a un trop payé de "
						+new CurrencyTextFieldConverter().convertToString(mntDonneAmap-mntCommande)+" €<br/>");
			
			buf.append("Vous avez en votre possession ces chèques, que vous pouvez rendre ou faire refaire:<br/>");
			for (Paiement paiement : paiements)
			{
				buf.append(" - "+df.format(paiement.getModeleContratDatePaiement().getDatePaiement())+" - "
							+new CurrencyTextFieldConverter().convertToString(paiement.getMontant())+"€ <br/>");
			}
			buf.append("<br/>");
		}
		
		
		
	}

	/**
	 * Le montant donné à l'AMAP est la somme de 
	 * - l'avoir initial
	 * - les chèques qui sont chez le producteur
	 * - les chèques qui sont à l'AMAP
	 * 
	 */
	private int getMontantDonneAMAP(Contrat contrat, EntityManager em)
	{
		// On récupère  la liste des paiements qui sont à l'AMAP ou chez le producteur 
		Query q = em.createQuery("select sum(p.montant) from Paiement p  "
						+ "WHERE (p.etat=:e1 OR p.etat=:e2) and p.contrat=:c ");
						
				
		q.setParameter("e1", EtatPaiement.AMAP);
		q.setParameter("e2", EtatPaiement.PRODUCTEUR);
		q.setParameter("c", contrat);
				
		int mnt1 = LongUtils.toInt(q.getSingleResult());
		
		int avoir = contrat.getMontantAvoir();
		return mnt1+avoir;
	}

	private List<Paiement> getPaiementALAMAP(Contrat contrat, EntityManager em)
	{
		// On récupère  la liste des paiements qui sont à l'AMAP 
		Query q = em.createQuery("select p from Paiement p  "
				+ "WHERE p.etat=:etat and p.contrat=:c "
				+ "order by p.modeleContratDatePaiement.datePaiement");
		
		q.setParameter("etat", EtatPaiement.AMAP);
		q.setParameter("c", contrat);
		
		List<Paiement> paiements = q.getResultList();
		return paiements;
	}
	
	
	/**
	 * Permet d'obtenir le nombre de paiement (ou chèques) pour ce contrat 
	 * (quel que soit l'état)
	 * @return
	 */
	public int getNbChequeContrat(Contrat contrat, EntityManager em)
	{
		// On récupère  la liste des paiements 
		Query q = em.createQuery("select count(p) from Paiement p  WHERE p.contrat=:c ");
		q.setParameter("c", contrat);
		
		return LongUtils.toInt(q.getSingleResult());
	}
	
	
	
	/**
	 * Permet d'obtenir le montant de paiement (ou chèques) pour ce contrat 
	 * (quel que soit l'état) , sans tenir compte d'un avoir initial 
	 * @return
	 */
	public int getMontantChequeSansAvoir(Contrat contrat, EntityManager em)
	{
		// On récupère  la liste des paiements  
		Query q = em.createQuery("select sum(p.montant) from Paiement p  where p.contrat=:c ");
						
		q.setParameter("c", contrat);
				
		int mnt1 = LongUtils.toInt(q.getSingleResult());
		
		return mnt1;
	}
	
	

}
