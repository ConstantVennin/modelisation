package sides;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import compare.XYZFacesComp;
import model.PLYData;
import model.Point;
import model.Repere;
import utils.Matrice;
import utils.TransformationMatrice;
import utils.TransformationType;

/**
 * Class qui représente les différents cotés des quels ont peut voir un modèle
 * 3D
 */
public abstract class Side {
	/**
	 * 0=x 1=y 2=z
	 */
	protected int i;

	/**
	 * Crée un coté
	 * 
	 * @param i 0=x 1=y 2=z
	 */
	protected Side(int i) {
		this.i = i;
	}

	/**
	 *
	 * Récupère le comparator correspondant au coté
	 *
	 * @param data la data correspondante
	 * @return le comparator
	 */
	public XYZFacesComp getComparator(PLYData data) {
		return new XYZFacesComp(data, i);
	}

	/**
	 * Retourne la coordonnée X du point s'il été dessiné sur un canvas
	 */

	public abstract double getXOfPoint(Point point);

	/**
	 * Retourne la coordonnée Y du point s'il été dessiné sur un canvas
	 */
	public abstract double getYOfPoint(Point point);

	/**
	 * Retourne la matrice de translation a appliquer sur le modèle qui correspond à
	 * un drag sur un canvas de xDelta pixel sur l'axe x et yDelta pixel sur l'axe y
	 */
	public abstract List<TransformationMatrice> getTranslationFromDrag(double xDelta, double yDelta);
	
	/**
	 * Retourne la matrice de rotation a appliquer sur le modèle qui correspond à un
	 * drag sur un canvas de xDelta pixel sur l'axe x et yDelta pixel sur l'axe y
	 */
	public abstract List<TransformationMatrice> getRotationFromDrag(double xDelta, double yDelta);

	public List<TransformationMatrice> getCenteredRotationFromDrag(Repere repere, double xDelta, double yDelta){
		List<TransformationMatrice> transformationMatrices=new ArrayList<>();
		
		transformationMatrices.add(new TransformationMatrice(TransformationType.XTRANSLATION, -repere.getParameter(0) ));
		transformationMatrices.add(new TransformationMatrice(TransformationType.YTRANSLATION, -repere.getParameter(1) ));
		transformationMatrices.add(new TransformationMatrice(TransformationType.ZTRANSLATION, -repere.getParameter(2) ));
		
		transformationMatrices.addAll(this.getRotationFromDrag(xDelta, yDelta));
		
		transformationMatrices.add(new TransformationMatrice(TransformationType.XTRANSLATION, repere.getParameter(0) ));
		transformationMatrices.add(new TransformationMatrice(TransformationType.YTRANSLATION, repere.getParameter(1) ));
		transformationMatrices.add(new TransformationMatrice(TransformationType.ZTRANSLATION, repere.getParameter(2) ));
		
		return transformationMatrices;
	}
	
	/**
	 * retourne le nombre correspondant au coté (0=x 1=y 2=z)
	 * 
	 * @return
	 */
	public int getI() {
		return i;
	}
	
	public abstract boolean hasToRender(Set<TransformationType> types);
	
	public boolean hasToSort(Set<TransformationType> types) {
		if (types==null)return true;
		return types.contains(TransformationType.XROTATION) ||
				types.contains(TransformationType.YROTATION) ||
				types.contains(TransformationType.ZROTATION) ||
				types.contains(TransformationType.LOADMODEL) ||
				types.contains(TransformationType.CHANGERENDERMODE) ||
				types.contains(TransformationType.CHANGESIDE);
	}
	
	public boolean hasToCalculateLight(Set<TransformationType> types) {
		if (types==null)return true;
		return types.contains(TransformationType.XROTATION) ||
				types.contains(TransformationType.YROTATION) ||
				types.contains(TransformationType.ZROTATION) ||
				types.contains(TransformationType.LOADMODEL) ||
				types.contains(TransformationType.CHANGERENDERMODE) ||
				types.contains(TransformationType.CHANGESIDE) ||
				types.contains(TransformationType.CHANGECOLOR) ||
				types.contains(TransformationType.CHANGELIGHTPARAMETER);
	}
	

	/**
	 * Méthode qui vérifie l'égalité de deux cotés
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Side other = (Side) obj;
		return i == other.i;
	}

}
