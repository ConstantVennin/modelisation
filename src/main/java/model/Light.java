package model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Matrice;
import utils.TransformationMatrice;
import utils.TransformationType;

public class Light {
	
	public Map<Face, Vecteur> vecteursN;
	public static Vecteur lightSource = new Vecteur(1,1,1);
	
	public Light(PLYData p) {
		Vecteur.unitariser(lightSource);
	}

	public void shadeOnColor(int j, Couleur def, PLYData p,double shadowStrength, double lightStrength) {
		for(int i = 0; i<p.getFaces().size(); i++) {
			Vecteur v1 = new Vecteur(p.getPoint(p.getFaces().get(i).getPoints()[0]),p.getPoint(p.getFaces().get(i).getPoints()[1]));
			Vecteur v2 = new Vecteur(p.getPoint(p.getFaces().get(i).getPoints()[0]),p.getPoint(p.getFaces().get(i).getPoints()[2]));
			Vecteur normal =  Vecteur.produitVectoriel(v1,v2);
			Vecteur.unitariser(normal);
			if(normal.get(j)<0) {
				normal.set(0, normal.get(0)*-1);
				normal.set(1, normal.get(1)*-1);
				normal.set(2, normal.get(2)*-1);
			}
			
			double x = Vecteur.produitScalaire(normal, lightSource);
			
			Couleur c = p.getFaces().get(i).getColorInit();
			if(c==null) {
				c = def;
			}
			
			
			double coeff=map(x,0,1,shadowStrength,lightStrength);
			p.getFaces().get(i).setColor(new Couleur((int)(c.getR()*coeff),(int)(c.getG()*coeff),(int)(c.getB()*coeff)) );
		}
	}
	
	public static double map(double n, double inMin, double inMax, double outMin, double outMax)
	{
	  return (n - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
	}
	
	public static void applyTranformations(List<TransformationMatrice> transformationMatrices) {
		for (TransformationMatrice transformationMatrice : transformationMatrices) {
			lightSource.applyTransformationWithMatrix(transformationMatrice.get());
		}
	}
}
