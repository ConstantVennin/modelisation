package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import utils.Matrice;

class VecteurTest {
	Point p1,p2,p3,p4, pL1, pL2,p,pA,pB;

	@BeforeEach
	void init() {
		p1 = new Point(1, 1, 1);
		p2 = new Point(2, 3, 4);
		p3 = new Point(2, 1, 02);
		p4 = new Point(8, 8, 10);
		
		pL1 = new Point(2,2,2);
		pL2 = new Point(1,1,1);
		
		p = new Point(0,0,0);
		pA = new Point(1,1,0);
		pB = new Point(2,1,2);

		}
	@Test
	void testCreationVecteurAPartirDePoints() {
		
		Vecteur v1 = new Vecteur(p1,p2);
		assertEquals(new Vecteur(1, 2, 3), v1);
		Vecteur v2 = new Vecteur(p3,p4);
		assertEquals(new Vecteur(6, 7, 8), v2);
	}
	
	@Test
	void testCreationVecteurAPartirDeVecteurs() {
		Vecteur v1 = new Vecteur(p1,p2);
		Vecteur v2 = new Vecteur(p3,p4);
		Vecteur v3 = Vecteur.produitVectoriel(v1, v2);
		assertEquals(new Vecteur(-5,10,-5), v3);
	}
	
	@Test
	void testProduitsVectorielle() {
		Vecteur v1 = new Vecteur(p1,p2);
		Vecteur v2 = new Vecteur(p3,p4);
		assertEquals(new Vecteur(-5,10,-5), Vecteur.produitVectoriel(v1, v2));
	}
	
	@Test
	void testProduitsScalaire() {
		Vecteur v1 = new Vecteur(p1,p2);
		Vecteur v2 = new Vecteur(p3,p4);
		assertEquals(44, Vecteur.produitScalaire(v1, v2));
	}
	
	@Test
	void testGetNorme() {
		Vecteur v1 = new Vecteur(p1,p2);
		assertEquals(Math.sqrt(14), Vecteur.getNorme(v1));
	}

}
