package model;

import java.util.ArrayList;
import java.util.List;
import utils.Matrice;
import utils.TransformationMatrice;
import utils.TransformationType;

/**
 * Classe qui définit un repère
 */
public class Repere {

	private Matrice matrice;
	
	/**
	 * Constructeur du repère
	 */
	public Repere() {
		this.reset();
	}

	/**
	 * Application de la transformation à partir de la matrice donnée
	 * 
	 * @param transformationMatrix - Matrice de transformation
	 */
	public void applyTransformation(List<TransformationMatrice> transformationMatrices) {
		for (TransformationMatrice transformationMatrice : transformationMatrices) {
			if (transformationMatrice.getType().getRepereIndex()!=-1) {
				if (transformationMatrice.getType()==TransformationType.SCALE) {
					double oldValue=this.getParameter(TransformationType.SCALE.getRepereIndex());
					this.setParameter(TransformationType.SCALE.getRepereIndex(), oldValue*transformationMatrice.getValue());
				}else {
					int idxParameter=transformationMatrice.getType().getRepereIndex();
					double oldValue=this.getParameter(idxParameter);
					this.setParameter(idxParameter, oldValue+transformationMatrice.getValue());
				}
			}
		}
		System.out.println("\nTRANSFORMATION EFFECTUEE -> NOUVELLE POSITION : "+this.toString());
	}
	
	/**
	 * 
	 * @param i x:0 y:1 z:2 rx:3 ry:4 rz:5 scale:6
	 * @return
	 */
	public double getParameter(int i){
		return matrice.get(i,0);
	}

	public void setParameter(int i,double value) {
		matrice.set(i,0,value);
	}
	
	/**
	 * Remet à zéro le repère
	 */
	public void reset() {
		matrice=new Matrice(new double[][] { {0},{0},{0},{0},{0},{0},{1} });
	}
	
	@Override
	public String toString() {
		String res="";
		res+="Translations : "+getParameter(0)+","+getParameter(1)+","+getParameter(2)+" | ";
		res+="Rotations : "+getParameter(3)+","+getParameter(4)+","+getParameter(5)+" | ";
		res+="Scalling : "+getParameter(6);
		return res;
		
	}
}
