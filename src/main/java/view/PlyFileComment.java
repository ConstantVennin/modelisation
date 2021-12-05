package view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

/**
 * Cette classe permet de lire et modifier les informations a propos du fichier PLY
 */
public class PlyFileComment {
	/**
	 * Nom du fichier
	 */
	private ObservableValue<String> nom;
	/**
	 * Description du fichier
	 */
	private ObservableValue<String> description;
	/**
	 * Date de creation du fichier
	 */
	private ObservableValue<String> dateCreation; 
	/**
	 * Auteur du fichier
	 */
	private ObservableValue<String> auteur;
	
	/**
	 * Constructeur avec parametres
	 * @param nom
	 * @param description
	 * @param dateCreation
	 * @param auteur
	 */
	public PlyFileComment(String nom, String description, String dateCreation, String auteur) {
		this.nom = new SimpleStringProperty(nom);
		this.description = new SimpleStringProperty(description);
		this.dateCreation = new SimpleStringProperty(dateCreation);
		this.auteur = new SimpleStringProperty(auteur);
	}
	
	/**
	 * Constructeur vide
	 */
	public PlyFileComment() {
		this("","","","");
	}
	
	/**
	 * Constructeur completant les attributs a partir du fichier
	 * @throws IOException 
	 * @throws PLYFileInvalidException 
	 */
	public PlyFileComment(File file) {
		this();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line!=null && !line.contains("end_header")) {
				String lineLower = line.toLowerCase();
				if(lineLower.contains("comment")) {
					updateFromLine(line, PlyCommentEnum.AUTEUR);
					updateFromLine(line, PlyCommentEnum.DESCRIPTION);
					updateFromLine(line, PlyCommentEnum.DATECREATION);
					updateFromLine(line, PlyCommentEnum.NOM);
				}
				line = br.readLine();
			}
			br.close();
		} catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	/**
	 * Ne pas utiliser, permet de modifier les informations du fichier de maniere generique, 
	 * utilisez plutot les methodes specifiques
	 * @param choix
	 * @param file
	 * @throws IOException 
	 * @throws PLYFileInvalidException 
	 */
	private void editPLY(PlyCommentEnum choix, File file, String s, boolean estNull) {
		try {
			ArrayList<String> lines = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(file));
			boolean dejaEcrit = false;
			String line = br.readLine();
			int cpt = 1;
			while (line!=null) {
				String lineLower = line.toLowerCase();
				if(dejaEcrit == false && lineLower.contains("comment") && !estNull) {
					if(lineLower.contains(choix.getKeyWord())) {
						lines.add("comment "+choix.getKeyWord() + " " + s);
					}else {
						lines.add(line);
					}
				}else if(cpt>2 && estNull && !dejaEcrit) {
					System.out.println("estNull");
					lines.add(line);
					lines.add("comment "+choix.getKeyWord() + " " + s);
					dejaEcrit = true;
				}else{
					lines.add(line);
				}
				line = br.readLine();
				cpt++;
			}
			br.close();
			
			FileWriter fw = new FileWriter(file);
	        BufferedWriter out = new BufferedWriter(fw);
	        for(String l : lines)
	             out.write(l+"\n");
	        out.flush();
	        out.close();
		} catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	/**
	 * Cette methode permet d'editer le nom du fichier, en renseignant le fichier File, et le nouveau nom s
	 * @param file
	 * @param s
	 * @throws IOException 
	 * @throws PLYFileInvalidException 
	 */
	public boolean setNom(File file, String s){
		boolean estNull = this.nom==null || this.nom.getValue().isBlank();
		this.editPLY(PlyCommentEnum.NOM, file, s, estNull);
		((SimpleStringProperty)this.nom).set(s);
		return true;
	}
	/**
	 * Cette methode permet d'editer la description du fichier, en renseignant le fichier File, et la nouvelle description s
	 * @param file
	 * @param s
	 * @throws IOException 
	 * @throws PLYFileInvalidException 
	 */
	public boolean setDescription(File file, String s){
		boolean estNull = this.description==null || this.description.getValue().isBlank();
		this.editPLY(PlyCommentEnum.DESCRIPTION, file, s, estNull);
		((SimpleStringProperty)this.description).set(s);
		return true;
	}
	/**
	 * Cette methode permet d'editer la date de cr�ation du fichier, en renseignant le fichier File, et la nouvelle date de creation s
	 * @param file
	 * @param s
	 * @throws IOException 
	 * @throws PLYFileInvalidException 
	 */
	public boolean setDateCreation(File file, String s){
		boolean estNull = this.dateCreation==null || this.dateCreation.getValue().isBlank();
		this.editPLY(PlyCommentEnum.DATECREATION, file, s, estNull);
		((SimpleStringProperty)this.dateCreation).set(s);
		return true;
	}
	/**
	 * Cette methode permet d'editer le nom du fichier, en renseignant le fichier File, et le nouvel auteur s
	 * @param file
	 * @param s
	 * @throws IOException 
	 * @throws PLYFileInvalidException 
	 */
	public boolean setAuteur(File file, String s){
		boolean estNull = this.nom==null || this.nom.getValue().isBlank();
		this.editPLY(PlyCommentEnum.AUTEUR, file, s, estNull);
		((SimpleStringProperty)this.auteur).set(s);
		return true;
	}
	
	public void updateFromLine(String line, PlyCommentEnum pce) {
		//TODO A optimiser
		if(line.contains(pce.getKeyWord())) {
			if(line.contains(PlyCommentEnum.AUTEUR.getKeyWord())) {
				((SimpleStringProperty)this.auteur).set( line.substring("comment ".length() + pce.getKeyWord().length() + 1) );
			}else if(line.contains(PlyCommentEnum.DESCRIPTION.getKeyWord())) {
				((SimpleStringProperty)this.description).set( line.substring("comment ".length() + pce.getKeyWord().length() + 1) );
			}else if(line.contains(PlyCommentEnum.NOM.getKeyWord())) {
				((SimpleStringProperty)this.nom).set( line.substring("comment ".length() + pce.getKeyWord().length() + 1) );
			}else if(line.contains(PlyCommentEnum.DATECREATION.getKeyWord())) {
				((SimpleStringProperty)this.dateCreation).set( line.substring("comment ".length() + pce.getKeyWord().length() + 1) );
			}
		}
		
	}
	
	/**
	 * @return le nom
	 */
	public ObservableValue<String> getNom() {
		return nom;
	}

	/**
	 * @return la description
	 */
	public ObservableValue<String> getDescription() {
		return description;
	}

	/**
	 * @return la dateCreation
	 */
	public ObservableValue<String> getDateCreation() {
		return dateCreation;
	}

	/**
	 * @return l'auteur
	 */
	public ObservableValue<String> getAuteur() {
		return auteur;
	}

	
}
