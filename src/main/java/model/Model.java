package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.Matrice;
import utils.Subject;
import utils.TransformationMatrice;
import utils.TransformationType;

/**
 * Cette classe sert à instancier et interragir avec un fichier PLY
 */
public class Model extends Subject {
	/**
	 * data - Objet de type PLYData qui contient toutes les informations contenues
	 * dans le fichier PLY
	 */
	private PLYData data;
	/**
	 * repere - Objet de type Repere qui represente le repere utilise pour les
	 * transformations
	 */
	private Repere repere;

	/**
	 * Constructeur du Model
	 */
	public Model() {
		this.data = new PLYData();
		this.repere = new Repere();
	}

	/**
	 * Chargement du fichier avec la modification matriciel par défaut
	 * 
	 * @param file
	 */
	public void loadFromFile(File file) {
		this.loadFromFile(file, true);
	}

	/**
	 * Chargement du fichier avec la possibilités de faire ou non la modificiation
	 * matriciel -> Utile de la désactivé pour les tests
	 * 
	 * @param file
	 * @param doTransfo
	 */
	public void loadFromFile(File file, boolean doTransfo) {
		this.data = new PLYData();
		LoadingFilePLY.readFilePLY(this, file);
		
		if (doTransfo)
			this.transformationToCenter();
		this.repere.reset();
		
		this.notifyObservers(this.data);
	}

	/**
	 * Applique une transformation matriciel à la data afin de centrer l'objet à
	 * son chargement
	 */
	public void transformationToCenter() {
		this.getData().applyTransformationWithMatrix(new TransformationMatrice(TransformationType.SCALE,1600 / this.getData().getAverageAbsoluteXYZ()).get());

		this.getData().applyTransformationWithMatrix(new TransformationMatrice(TransformationType.XTRANSLATION,(this.getData().getMax(0) + this.getData().getMin(0)) / -2).get());
		this.getData().applyTransformationWithMatrix(new TransformationMatrice(TransformationType.YTRANSLATION,(this.getData().getMax(1) + this.getData().getMin(1)) / -2).get());
		this.getData().applyTransformationWithMatrix(new TransformationMatrice(TransformationType.ZTRANSLATION,(this.getData().getMax(2) + this.getData().getMin(2)) / -2).get());
	}

	public PLYData getData() {
		return this.data;
	}
	
	public Repere getRepere() {
		return repere;
	}

	public void setData(PLYData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Model [data=" + data + "]";
	}

	/**
	 * Remet à zéro la data transformé, toutes les transformations et rotations
	 * sont annulées
	 */
	public void resetTransformation() {
		this.repere.reset();
		this.notifyObservers(data);
	}

	
	public void applyTransformation(TransformationMatrice transformationMatrice) {
		List<TransformationMatrice> transformationMatrices=new ArrayList<TransformationMatrice>();
		transformationMatrices.add(transformationMatrice);
		this.applyTransformation(transformationMatrices);
	}
	
	
	/**
	 * Applique la transformation de la matrice
	 * 
	 * @param transformationMatrix - Transformation à appliquer
	 */
	public void applyTransformation(List<TransformationMatrice> transformationMatrices) {
		this.repere.applyTransformation(transformationMatrices);
		this.notifyObservers(transformationMatrices);
	}

}
