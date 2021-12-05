package model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exceptions.PLYFileInvalidException;
import view.PlyFileComment;

class PlyFileCommentVoidTest {
	PlyFileComment pc1;
	String path;
	File f;
	@BeforeEach
	void init() throws PLYFileInvalidException, IOException {
		path = "src/test/resources/TestCommentVoid.ply";
		f = new File(path);
		pc1 = new PlyFileComment(f);
	}
	
	@Test
	void getterTest() {
		assertEquals("", pc1.getAuteur().getValue());
		assertEquals("", pc1.getDateCreation().getValue());
		assertEquals("", pc1.getDescription().getValue());
		assertEquals("Test.ply", pc1.getNom().getValue());
	}
	
	@Test 
	void settersTest() {
		pc1.setAuteur(f, "tartenpion");
		pc1.setDateCreation(f, "11/12/2021");
		pc1.setDescription(f, "fichier de test servant a tester les differentes classes");
		pc1.setNom(f, "Test.ply");
		assertEquals("tartenpion", pc1.getAuteur().getValue());
		assertEquals("11/12/2021", pc1.getDateCreation().getValue());
		assertEquals("fichier de test servant a tester les differentes classes", pc1.getDescription().getValue());
		assertEquals("Test.ply", pc1.getNom().getValue());
	}

}
