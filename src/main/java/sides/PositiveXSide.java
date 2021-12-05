package sides;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Point;
import model.Repere;
import utils.Matrice;
import utils.TransformationMatrice;
import utils.TransformationType;

/**
 * 
 * Classe qui fournit les methodes � un canvas qui est vu du cot� x positif
 * (cam�ra en x=infini)
 *
 */
public class PositiveXSide extends Side {

	/**
	 * Instantie le cot�
	 */
	public PositiveXSide() {
		super(0);
	}

	/**
	 * Retourne la coordonn�e X du point s'il �t� dessin� sur un canvas
	 */
	@Override
	public double getXOfPoint(Point point) {
		return -point.get(2);
	}

	/**
	 * Retourne la coordonn�e Y du point s'il �t� dessin� sur un canvas
	 */
	@Override
	public double getYOfPoint(Point point) {
		return point.get(1);
	}

	/**
	 * Retourne la matrice de translation a appliquer sur le mod�le qui correspond �
	 * un drag sur un canvas de xDelta pixel sur l'axe x et yDelta pixel sur l'axe y
	 */
	@Override
	public List<TransformationMatrice> getTranslationFromDrag(double xDelta, double yDelta) {
		List<TransformationMatrice> transformationMatrices=new ArrayList<>();
		if (yDelta!=0)
		transformationMatrices.add(new TransformationMatrice(TransformationType.YTRANSLATION,-yDelta));
		if (xDelta!=0)
		transformationMatrices.add(new TransformationMatrice(TransformationType.ZTRANSLATION,-xDelta));		
		
		return transformationMatrices;
	}

	/**
	 * Retourne la matrice de rotation a appliquer sur le mod�le qui correspond � un
	 * drag sur un canvas de xDelta pixel sur l'axe x et yDelta pixel sur l'axe y
	 */
	@Override
	public List<TransformationMatrice> getRotationFromDrag(double xDelta, double yDelta) {
		List<TransformationMatrice> transformationMatrices=new ArrayList<>();
		if (xDelta!=0)
		transformationMatrices.add(new TransformationMatrice(TransformationType.YROTATION,-xDelta));
		if (yDelta!=0)
		transformationMatrices.add(new TransformationMatrice(TransformationType.ZROTATION,-yDelta));
		return transformationMatrices;
	}
	
	public boolean hasToRender(Set<TransformationType> types) {
		if (types==null)return true;
		return !(types.size()==1 && types.contains(TransformationType.XTRANSLATION));
	}
	
	/**
	 * Retourne le nom du cot�
	 */
	@Override
	public String toString() {
		return "Positive X";
	}

}
