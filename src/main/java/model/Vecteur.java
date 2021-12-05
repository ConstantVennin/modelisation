package model;

import java.util.Map;
import java.util.Objects;

import utils.Matrice;

public class Vecteur {
	private Matrice m;
	
	public Vecteur (double x,double y,double z) {
		m = new Matrice(new double[][] {{x},{y},{z},{1}});
	}
	
	public Vecteur(Point p1, Point p2){
		this(p2.get(0)-p1.get(0), p2.get(1)-p1.get(1) , p2.get(2)-p1.get(2));
	}
	
	static public Vecteur produitVectoriel(Vecteur v1, Vecteur v2) {
		Vecteur res=new Vecteur(0,0,0);
		
		double tmp;
		int idx1 = 1;
		int idx2 = 2;
		for(int i = 0; i<3; i++) {
			tmp = (v1.get(idx1) * v2.get(idx2))-(v1.get(idx2) * v2.get(idx1));
			idx1 = (idx1+1)%3;
			idx2 = (idx2+1)%3;
			res.set(i, tmp);
		}
		return res;
	}
	
	static public double produitScalaire(Vecteur v1, Vecteur v2) {
		return v1.get(0) * v2.get(0) + 
				v1.get(1) * v2.get(1) + 
				v1.get(2) * v2.get(2);
	}
	
	static public double getNorme(Vecteur v1) {
		double x = produitScalaire(v1,v1);
		return Math.sqrt(x);
	}

	public Matrice getMatrice() {
		return this.m;
	}
	
	public static void unitariser(Vecteur v1) {
		double x = getNorme(v1);
		v1.set(0, v1.get(0)/x);
		v1.set(1, v1.get(1)/x);
		v1.set(2, v1.get(2)/x);
	}
	/**
	 * 
	 * @param i x=0 y=1 z=2
	 * @return
	 */
	public double get(int i) {
		return m.get(i, 0);
	}
	
	public void set(int i,double value) {
		m.set(i, 0, value);
	}
	
	public void applyTransformationWithMatrix(Matrice transformationMatrix) {
		this.m= Matrice.multiply(transformationMatrix, this.m);
	}
	
	public String toString() {
		return m.toString();
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vecteur other = (Vecteur) obj;
		return Objects.equals(m, other.m);
	}
	
	
}
