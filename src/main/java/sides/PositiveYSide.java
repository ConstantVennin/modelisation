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
 * Classe qui fournit les methodes à un canvas qui est vu du coté y positif
 * (caméra en y=infini)
 *
 */
public class PositiveYSide extends Side {

	/**
	 * Instantie le coté
	 */
	public PositiveYSide() {
		super(1);
	}

	/**
	 * Retourne la coordonnée X du point s'il été dessiné sur un canvas
	 */
	@Override
	public double getXOfPoint(Point point) {
		return point.get(0);
	}

	/**
	 * Retourne la coordonnée Y du point s'il été dessiné sur un canvas
	 */
	@Override
	public double getYOfPoint(Point point) {
		return -point.get(2);
	}

	/**
	 * Retourne la matrice de translation a appliquer sur le modèle qui correspond à
	 * un drag sur un canvas de xDelta pixel sur l'axe x et yDelta pixel sur l'axe y
	 */
	@Override
	public List<TransformationMatrice> getTranslationFromDrag(double xDelta, double yDelta) {
		List<TransformationMatrice> transformationMatrices=new ArrayList<>();
		if (xDelta!=0)
		transformationMatrices.add(new TransformationMatrice(TransformationType.XTRANSLATION,xDelta));
		if (yDelta!=0)
		transformationMatrices.add(new TransformationMatrice(TransformationType.ZTRANSLATION,yDelta));
		
		return transformationMatrices;
	}

	/**
	 * Retourne la matrice de rotation a appliquer sur le modèle qui correspond à un
	 * drag sur un canvas de xDelta pixel sur l'axe x et yDelta pixel sur l'axe y
	 */
	@Override
	public List<TransformationMatrice> getRotationFromDrag(double xDelta, double yDelta) {
		List<TransformationMatrice> transformationMatrices=new ArrayList<>();
		
		if (xDelta!=0)
		transformationMatrices.add(new TransformationMatrice(TransformationType.ZROTATION,-xDelta));
		if (yDelta!=0)
		transformationMatrices.add(new TransformationMatrice(TransformationType.XROTATION,yDelta));

		return transformationMatrices;
	}
	
	public boolean hasToRender(Set<TransformationType> types) {
		if (types==null)return true;
		return !(types.size()==1 && types.contains(TransformationType.YTRANSLATION));
	}

	/**
	 * Retourne le nom du coté
	 */
	@Override
	public String toString() {
		return "Positive Y";
	}
}