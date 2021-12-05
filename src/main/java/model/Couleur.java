package model;


/**
 * Cette classe sert à stocker les valeurs RGB d'une couleur
 */
public class Couleur {

	/**
	 * Composant rouge de la couleur
	 */
	private double r;
	/**
	 * composante verte de la couleur
	 */
	private double g;
	/**
	 * composante bleue de la couleur
	 */
	private double b;
	/**
	 * composant alpha de la couleur
	 */
	private double a;

	/**
	 * Cree une nouvelle couleur
	 * 
	 * @param r composante en rouge entre 0 et 255
	 * @param g composante en vert entre 0 et 255
	 * @param b composante en bleu entre 0 et 255
	 * @param a composante en alpha entre 0 et 255
	 */
	public Couleur(double r, double g, double b, double a) {
		this.r = Math.max(0, Math.min(r, 255));
		this.g = Math.max(0, Math.min(g, 255));;
		this.b = Math.max(0, Math.min(b, 255));;
		this.a = Math.max(0, Math.min(a, 255));;
	}

	/**
	 * Cree une nouvelle couleur
	 * 
	 * @param r composante en rouge entre 0 et 255
	 * @param g composante en vert entre 0 et 255
	 * @param b composante en bleu entre 0 et 255
	 */
	public Couleur(double r, double g, double b) {
		this(r, g, b, 255);
	}

	/**
	 * Instantie une copie de la couleur pass�e en param�tre
	 * 
	 * @param color
	 */
	public Couleur(Couleur color) {
		this(color.getR(), color.getG(), color.getB(), color.getA());
	}

	/**
	 * Retourne la compasante en rouge de la couleur
	 * 
	 * @return niveau de rouge
	 */
	public double getR() {
		return r;
	}

	/**
	 * Retourne la compasante en vert de la couleur
	 * 
	 * @return niveau de vert
	 */
	public double getG() {
		return g;
	}

	/**
	 * Retourne la compasante en bleu de la couleur
	 * 
	 * @return niveau de bleu
	 */
	public double getB() {
		return b;
	}

	/**
	 * Retourne la compasante en alpha de la couleur
	 * 
	 * @return niveau de alpha
	 */
	public double getA() {
		return a;
	}

	@Override
	public String toString() {
		return "Color [r=" + r + ", g=" + g + ", b=" + b + ", a=" + a + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Couleur other = (Couleur) obj;
		if (b != other.b)
			return false;
		if (g != other.g)
			return false;
		if (r != other.r)
			return false;
		return true;
	}

}
