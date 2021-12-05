package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.NoPLYHeaderInFileException;
import exceptions.PLYFileInvalidException;
import exceptions.PLYSynthaxPointFaceException;
import utils.Subject;
import utils.VerifSynthaxFace;
import utils.VerifSynthaxPoint;

/**
 * Cette classe est une classe méthode qui sert à charger et initialiser tous
 * les points et face du Modèle
 */
public class LoadingFilePLY extends Subject {
	/**
	 * Nombre de point qui constitue le PLY
	 */
	private static int nbPoint = 0;
	/**
	 * Nombre de face qui constitue le PLY
	 */
	private static int nbFace = 0;
	/**
	 * Indice de la ligne où se situe le "end_header"
	 */
	private static int idxEndHeader = 0;
	/**
	 * Permet de savoir si le modèle contient de la couleur
	 */
	private static boolean hasColors = false;

	/**
	 * Méthode qui permet de vérifier la validité d'un fichier puis qui instancie
	 * un Modèle avec les données du fichier
	 * 
	 * @param model -> Model que l'on souhaite instancier
	 * @param ply   -> Fichier PLY duquel on fécupère les données
	 */
	public static void readFilePLY(Model model, File file) {
		resetModel();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			if (line.contentEquals("ply")) {
				updatePLYParameters(file);
				initModel(model, file);
			} else {
				throw new NoPLYHeaderInFileException();
			}
		} catch (Exception e) {
			model.notifyObservers(e);
			return;
		}

	}

	/**
	 * Méthode qui permet d'instancier un modèle avec les données d'un fichier
	 * PLY (points + faces + couleur)
	 * 
	 * @param model
	 * @param ply
	 * @throws IOException
	 * @throws PLYSynthaxPointFaceException
	 */
	@SuppressWarnings("resource")
	public static void initModel(Model model, File ply) throws IOException, PLYSynthaxPointFaceException {
		BufferedReader br = new BufferedReader(new FileReader(ply));
		String line = br.readLine();
		List<Face> listFace = new ArrayList<Face>();
		Map<Integer, Point> listPoint = new HashMap<Integer, Point>();
		int cpt = 0;
		int i = 0;
		int nbEspace = 0;
		int nbPoints = getNbPoint();
		while (line != null) {
			if (!line.isBlank() && cpt >= getIdxEndHeader() && cpt < (getIdxEndHeader() + getNbPoint() + nbEspace)) {
				VerifSynthaxPoint.verifSynthaxPoint(line, cpt + 1);
				listPoint.put(i, getPointFromPLY(line));
				i++;
			} else if (!line.isBlank() && cpt >= (idxEndHeader + nbPoint + nbEspace)) {
				VerifSynthaxFace.verifSynthaxFace(line, nbPoints, cpt + 1);
				listFace.add(getFaceFromPLY(listPoint, line));
			}
			if (line.isEmpty() && cpt >= idxEndHeader)
				nbEspace++;
			cpt++;
			line = br.readLine();
		}
		model.setData(new PLYData(listPoint, listFace));
	}

	/**
	 * Reset les attributs de la classe afin de générer un nouveau modèle
	 */
	private static void resetModel() {
		nbPoint = 0;
		nbFace = 0;
		idxEndHeader = 0;
		hasColors = false;
	}

	/**
	 * Méthode servant à récupérer les différentes paramètres du fichier PLY -
	 * Nombre de points du modèle - Nombre de face du modèle - Index de la ligne
	 * ou se situe "end_header" -> correspond au début de la lecture des points
	 * 
	 * @param ply = Fichier PLY à lire
	 */
	public static void updatePLYParameters(File ply) {
		try {
			// Récupération du nombre de Vertex dans le PLY
			nbPoint = getVarFromPLY("element vertex", ply);
			// On vérifie si le modèle intègre des faces de couleurs
			hasColors = doesPLYAsColor(ply);
			// Récupération du nombre de Face dans le PLY
			nbFace = getVarFromPLY("element face", ply);
			// Récupération de l'indice du end_header dans le PLY
			idxEndHeader = getIdxEndHeaderFromPLY(ply);
		} catch (FileNotFoundException e) {
			// System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			// System.out.println("Reading error");
			e.printStackTrace();
		} catch (PLYFileInvalidException e) {
			// System.out.println("Incorrect Parameter found in ");
			e.printStackTrace();
		}
	}

	/**
	 * Sous méthode qui récupère indice de la ligne "end_header"
	 * 
	 * @param br   = lecteur de fichier
	 * @param line = ligne à laquelle on est
	 * @return
	 * @throws IOException
	 * @throws PLYFileInvalidException
	 */
	public static int getIdxEndHeaderFromPLY(File ply) throws IOException, PLYFileInvalidException {
		BufferedReader br = new BufferedReader(new FileReader(ply));
		String line = br.readLine();
		int cpt = 1;
		while (!line.contains("end_header") && line != null) {
			line = br.readLine();
			cpt++;
		}
		br.close();
		if (line.contains("end_header")) {
			return cpt;
		} else {
			throw new PLYFileInvalidException(cpt);
		}
	}

	/**
	 * Vérifie si le fichier déclare avoir des couleurs
	 * 
	 * @param ply -> Fichier PLY
	 * @return
	 * @throws IOException
	 * @throws PLYFileInvalidException
	 */
	private static boolean doesPLYAsColor(File ply) throws IOException, PLYFileInvalidException {
		BufferedReader br = new BufferedReader(new FileReader(ply));
		String line = br.readLine();
		boolean red = false, green = false, blue = false;
		while ((!red || !green || !blue) && line != null) {
			if (!red && line.contains("property uchar red"))
				red = true;
			if (!green && line.contains("property uchar green"))
				green = true;
			if (!blue && line.contains("property uchar blue"))
				blue = true;
			line = br.readLine();
		}
		br.close();
		if (red && green && blue) {
			return true;
		}
		return false;
	}

	/**
	 * Permet de récupérer le nombre de "points" ou de "faces"
	 * 
	 * @param var -> "vertex" / "face"
	 * @param ply -> Fichier PLY
	 * @return "vertex"=return nb Point / "face"=return nb Face
	 * @throws IOException
	 * @throws PLYFileInvalidException
	 */
	private static int getVarFromPLY(String var, File ply) throws IOException, PLYFileInvalidException {
		BufferedReader br = new BufferedReader(new FileReader(ply));
		String line = br.readLine();
		int cpt = 1;
		while (!line.contains(var) && !line.contentEquals("end_header") && line != null) {
			line = br.readLine();
			cpt++;
		}
		br.close();
		if (line.contains(var)) {
			return Integer.parseInt(line.split(" ")[2]);
		} else {
			throw new PLYFileInvalidException(cpt);
		}
	}

	/**
	 * Permet d'instancier un point avec les coordonnés d'un des lignes du PLY
	 * 
	 * @param line -> Ligne contenant les coordonnées (et couleur ?) du points
	 * @return
	 */
	private static Point getPointFromPLY(String line) {

		String[] parameters = line.split(" ");
		double x = Double.parseDouble(parameters[0]);
		double y = Double.parseDouble(parameters[1]);
		double z = Double.parseDouble(parameters[2]);
		Couleur color = new Couleur(255, 255, 255);
		if (parameters.length >= 6) {
			color = new Couleur(Integer.parseInt(parameters[3]), Integer.parseInt(parameters[4]),
					Integer.parseInt(parameters[5]));
		}
		return new Point(x, y, z, color);
	}

	/**
	 * Permet d'instancier une face avec les indices des points données par une des
	 * lignes du fichiers PLY
	 * 
	 * @param listPointModel -> La Map de tous les points instancier et appartenant
	 *                       au model
	 * @param line           -> Ligne contenant les coordonnées (et couleur ?) du
	 *                       points
	 * @return
	 */
	private static Face getFaceFromPLY(Map<Integer, Point> listPointModel, String line) {
		String[] parameters = line.split(" ");
		int[] listPointsFace = new int[Integer.parseInt(parameters[0])];
		for (int i = 1; i <= Integer.parseInt(parameters[0]); i++) {
			listPointsFace[i - 1] = Integer.parseInt(parameters[i]);
		}
		Face face = new Face(listPointsFace);
		if (hasColors)
			face.setFaceColorFromPLY(parameters, listPointModel, listPointsFace);
		return face;
	}

	/**
	 * @return Valeur du nombre de point du PLY
	 */
	public static int getNbPoint() {
		return nbPoint;
	}

	/**
	 * Modifie le valeur du nombre de point du PLY
	 * 
	 * @param nbPoint = nouvelle valeur
	 */
	public void setNbPoint(int nbPoint) {
		LoadingFilePLY.nbPoint = nbPoint;
	}

	/**
	 * @return Valeur du nombre face du PLY
	 */
	public static int getNbFace() {
		return nbFace;
	}

	/**
	 * Modifie le valeur du nombre de face du PLY
	 * 
	 * @param nbFace = nouvelle valeur
	 */
	public void setNbFace(int nbFace) {
		LoadingFilePLY.nbFace = nbFace;
	}

	/**
	 * @return Valeur de l'indice de la ligne où se situe le "end_header"
	 */
	public static int getIdxEndHeader() {
		return idxEndHeader;
	}

	/**
	 * Modifie la valeur de l'indice où se situe le "end_header"
	 * 
	 * @param idxEndHeader = La nouvelle valeur
	 */
	public void setIdxEndHeader(int idxEndHeader) {
		LoadingFilePLY.idxEndHeader = idxEndHeader;
	}

	@Override
	public String toString() {
		return "LoadingFIlePLY [nbPoint=" + nbPoint + ", nbFace=" + nbFace + ", idxEndHeader=" + idxEndHeader + "]";
	}

	/**
	 * Permet de savoir si le fichier charg� contient des couleurs
	 * 
	 * @return true si il contient des couleurs, false sinon
	 */
	public static boolean hasColor() {
		return hasColors;
	}

}
