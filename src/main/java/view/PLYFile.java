package view;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import model.LoadingFilePLY;

public class PLYFile {
	
	private File file;
	private ObservableValue<String> fileName;

	private ObjectProperty<Integer> nbPoints;
	private ObservableValue<Integer> nbFaces;
	private ObservableValue<Boolean> hasColor;
	
	private PlyFileComment comment;
	
	public PLYFile(File file) {
		this.file = file;
		this.fileName=new SimpleStringProperty(this.getFileName());
		
		LoadingFilePLY.updatePLYParameters(file);
		
		this.nbPoints=new SimpleIntegerProperty(LoadingFilePLY.getNbPoint()).asObject();
		this.nbFaces=new SimpleIntegerProperty(LoadingFilePLY.getNbFace()).asObject();
		this.hasColor=new SimpleBooleanProperty(LoadingFilePLY.hasColor());
		
		this.comment=new PlyFileComment(file);
	
		
	}
	
	public int getNbPoints() {
		return nbPoints.getValue();
	}
	
	public ObservableValue<Integer> nbPointsProperty() {
		return nbPoints;
	}
	
	public int getNbFaces() {
		return nbFaces.getValue();
	}
	
	public ObservableValue<Integer> nbFacesProperty() {
		return nbFaces;
	}
	
	public boolean hasColor() {
		return hasColor.getValue();
	}
	
	public ObservableValue<Boolean> hasColorProperty() {
		return hasColor;
	}
	
	/**
	 * @return le nom
	 */
	public  ObservableValue<String> plyNameProperty() {
		return comment.getNom();
	}

	/**
	 * @return la description
	 */
	public ObservableValue<String> plyDescriptionProperty() {
		return comment.getDescription();
	}

	/**
	 * @return la dateCreation
	 */
	public ObservableValue<String> plyDateCreationProperty() {
		return comment.getDateCreation();
	}


	/**
	 * @return l'auteur
	 */
	public ObservableValue<String> plyAuteurProperty() {
		return comment.getAuteur();
	}
	
	public String getFileName() {
		String name = this.file.getName();
		return name.substring(0, name.length() - 4);
	}
	
	public ObservableValue<String> fileNameProperty(){
		return this.fileName;
	}
	
	public int getFileSize() {
		Path path=Paths.get(file.getPath());
		try {
			return (int) Files.size(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public ObservableValue<Integer> fileSizeProperty() {
		return new SimpleIntegerProperty(this.getFileSize()).asObject();
	}
	
	public File getFile() {
		return file;
	}
	
	
	public void renameFile(String newValue) throws IOException,FileAlreadyExistsException {
		File newFile = new File(file.getParent(), newValue+".ply");
		if (newFile.equals(file)) {
			throw new FileAlreadyExistsException(newFile.getPath());
		}else {
			Files.move(file.toPath(), newFile.toPath());
			this.file=newFile;
			((SimpleStringProperty)this.fileName).set(newValue);
		}
		
	}
	
	/**
	 * Cette methode permet d'editer le nom du fichier, en renseignant le fichier File, et le nouveau nom s
	 * @param s nouveau nom
	 */
	public void setNom(String s){
		comment.setNom(this.file, s);
	}
	/**
	 * Cette methode permet d'editer la description du fichier, en renseignant le fichier File, et la nouvelle description s
	 * @param s nouvelle description
	 */
	public void setDescription(String s){
		comment.setDescription(this.file, s);
	}
	/**
	 * Cette methode permet d'editer la date de cr�ation du fichier, en renseignant le fichier File, et la nouvelle date de creation s
	 * @param s nouvelle date
	 */
	public void setDateCreation(String s){
		comment.setDateCreation(this.file, s);
	}
	/**
	 * Cette methode permet d'editer le nom du fichier, en renseignant le fichier File, et le nouvel auteur s
	 * @param s nouvel auteur
	 */
	public void setAuteur(String s){
		comment.setAuteur(this.file, s);
	}
	
}	
