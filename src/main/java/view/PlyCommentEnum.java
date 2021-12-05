package view;
/**
 * Enumération des informations du fichier ply, cette classe permet d'optimiser et de rendre le code plus lisible
 */
public enum PlyCommentEnum {
	NOM("nom"), DESCRIPTION("description"), DATECREATION("datecreation"), AUTEUR("auteur");
	String keyWord;
	
	PlyCommentEnum(String keyWord) {
		this.keyWord = keyWord;
	}
	
	public String getKeyWord() {
		return this.keyWord;
	}
	
}
