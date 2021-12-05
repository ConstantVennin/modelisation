package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contient toutes les transformations possible et permet de calculer le
 * résultat d'une transformation spécifique sur une matrice
 */
public class TransformationMatrice {
	/**
	 * élément de l'énumération transformation pour choisir la transformation à
	 * appliquer
	 */
	private TransformationType type;
	
	
	/**
	 * liste d'arguments utilis� par defaut lors d'un get (si aucn param�tres n'est renseign�)
	 */
	private double value;
	

	
	public TransformationMatrice(TransformationType type,double value) {
		this.type = type;
		this.value=value;
	}

	
	/**
	 * retourne la matrice correspondant au type de matrice de l'object
	 * 
	 * @param args parametres eventuels de la matrice
	 * @return la matrice
	 */
	public Matrice get() {
		
		//TODO Pour le cleancode (eviter ce giga if), il faudrait faire une classe pour type de transformation et tout mettre dans un meme package

		if (this.type == TransformationType.XROTATION) {
			return getRotateXMatrix(value);
		} else if (this.type == TransformationType.YROTATION) {
			return getRotateYMatrix(value);
		} else if (this.type == TransformationType.ZROTATION) {
			return getRotateZMatrix(value);
		}
		if (this.type == TransformationType.XTRANSLATION) {
			return getTranslationMatrix(value, 0, 0);
		} else if (this.type == TransformationType.YTRANSLATION) {
			return getTranslationMatrix(0, value, 0);
		} else if (this.type == TransformationType.ZTRANSLATION) {
			return getTranslationMatrix(0, 0, value);
		} else if (this.type == TransformationType.SCALE) {
			return getScaleMatrix(value);
		}
		return null;
	}

	public TransformationType getType() {
		return type;
	}
	
	public double getValue() {
		return value;
	}

	/**
	 * Retourne la matrice de translation x,y,z
	 * 
	 * @param x x
	 * @param y y
	 * @param z z
	 * @return la matrice
	 */

	private static Matrice getTranslationMatrix(double x, double y, double z) {
		return new Matrice(new double[][] { { 1, 0, 0, x }, { 0, 1, 0, y }, { 0, 0, 1, z }, { 0, 0, 0, 1 } });
	}

	private static Matrice getScaleMatrix(double x, double y, double z) {
		return new Matrice(new double[][] { { x, 0, 0, 0 }, { 0, y, 0, 0 }, { 0, 0, z, 0 }, { 0, 0, 0, 1 } });
	}

	private static Matrice getScaleMatrix(double size) {
		return getScaleMatrix(size, size, size);
	}

	/**
	 * 
	 * @param angle prévu en radian
	 * @return matrice prévue pour une rotation en X
	 */
	private static Matrice getRotateXMatrix(double angle) {
		return new Matrice(new double[][] { { 1, 0, 0, 0 }, { 0, Math.cos(angle), -Math.sin(angle), 0 },
				{ 0, Math.sin(angle), Math.cos(angle), 0 }, { 0, 0, 0, 1 } });
	}

	/**
	 * 
	 * @param angle prévu en radian
	 * @return matrice prévue pour une rotation en Y
	 */
	private static Matrice getRotateYMatrix(double angle) {
		return new Matrice(new double[][] { { Math.cos(angle), 0, -Math.sin(angle), 0 }, { 0, 1, 0, 0 },
				{ Math.sin(angle), 0, Math.cos(angle), 0 }, { 0, 0, 0, 1 } });
	}

	/**
	 * 
	 * @param angle prévu en radian
	 * @return matrice prévue pour une rotation en Z
	 */
	private static Matrice getRotateZMatrix(double angle) {
		return new Matrice(new double[][] { { Math.cos(angle), -Math.sin(angle), 0, 0 },
				{ Math.sin(angle), Math.cos(angle), 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 } });
	}
}
