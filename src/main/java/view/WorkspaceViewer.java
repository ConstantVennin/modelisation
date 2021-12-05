package view;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import exceptions.CustomException;
import exceptions.NoPLYFileInDirectoryException;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

public class WorkspaceViewer extends VBox {

	private View parent;
	/**
	 * Le fichier correspondant � l'emplacement du workspace
	 */
	private File workspace;
	
	private List<PLYFile> fileList;

	private ToolBar toolBar;
	/**
	 * Boutton pour charger un nouveau workspace
	 */
	private Button openWorkspaceButton;
	/**
	 * Hbox qui stocke les boutton a cacher si aucun workspace n'est charg�
	 */
	private HBox rootToggled;
	/**
	 * Label pour afficher l'emplacement du workspace
	 */
	private TextField workspacePathLabel;
	/**
	 * le champ de recherche par nom de fichiers
	 */
	private TextField searchField;
	
	private TableView<PLYFile> tableView;
	
	private boolean collapsed;
	private List<TableColumn<?, ?>> collapsableColumns=new ArrayList<TableColumn<?, ?>>();
	private Button refreshButton;
	private Button collapseButton;

	public WorkspaceViewer(View parent) {
		this.parent=parent;
		
		toolBar=new ToolBar();
		
		openWorkspaceButton = new Button("Open Workspace");
		openWorkspaceButton.setOnAction(e -> {
			openWorkspace();
		});

		rootToggled = new HBox();
		refreshButton = new Button("RefreshWorkspace");

		refreshButton.setOnAction(e -> {
			loadWorkspace(workspace);
		});
		workspacePathLabel = new TextField("");
		workspacePathLabel.setEditable(false);
		Button loadModelButton = new Button("Load Model");
		loadModelButton.setOnAction(e -> {
			parent.getModel().loadFromFile(tableView.getSelectionModel().getSelectedItem().getFile());
		});

		searchField = new TextField();
		searchField.setPromptText("Search");

		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			updateTableView();
		});
		
		collapseButton=new Button("Hide Details");
		collapseButton.setOnAction(e->{
			setCollapsed(!this.collapsed);
		});
		

		rootToggled.getChildren().addAll(refreshButton, workspacePathLabel, searchField, loadModelButton,collapseButton);
		rootToggled.setManaged(false);

		toolBar.getItems().addAll(rootToggled, openWorkspaceButton);
		
		//Default workspace when lauching the app
		workspace = new File("." + File.separator + "Example");

		tableView=new TableView<PLYFile>();

		TableColumn<PLYFile, String> fileInfo = new TableColumn<PLYFile, String>("File Information");
		TableColumn<PLYFile, String> filename = new TableColumn<PLYFile, String>("Name");
		filename.setCellValueFactory(item->item.getValue().fileNameProperty());
		filename.setCellFactory(TextFieldTableCell.forTableColumn());
		filename.setOnEditCommit(t -> {
			try {
				t.getTableView().getItems().get(t.getTablePosition().getRow()).renameFile(t.getNewValue());
				updateTableView();
			} catch (FileAlreadyExistsException e2) {
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setHeaderText("Could not rename the file");
				errorAlert.setContentText("File \""+e2.getFile()+"\" already exists");
				errorAlert.showAndWait();
				updateTableView();
			} catch (IOException e1) {
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setHeaderText("Could not rename the file");
				errorAlert.setContentText(e1.getMessage());
				errorAlert.showAndWait();
			}
		});
		TableColumn<PLYFile, Integer> fileSize = new TableColumn<PLYFile, Integer>("Size");
		collapsableColumns.add(fileSize);
		fileSize.setCellValueFactory(item->item.getValue().fileSizeProperty());
		fileSize.setCellFactory(item -> new TableCell<PLYFile,Integer>() {
			@Override
		    protected void updateItem(Integer item, boolean empty) {
		        super.updateItem(item, empty) ;
		        setText(empty ? null : convertFileSize(item) );
		    }
		});
		fileInfo.getColumns().addAll(filename,fileSize);
		
		TableColumn<PLYFile, String> modelInfo = new TableColumn<PLYFile, String>("3D Model Information");
		collapsableColumns.add(modelInfo);
		TableColumn<PLYFile, Integer> nbPoints = new TableColumn<PLYFile, Integer>("Points");
		nbPoints.setCellValueFactory(item->item.getValue().nbPointsProperty());
		TableColumn<PLYFile, Integer> nbFaces = new TableColumn<PLYFile, Integer>("Faces");
		nbFaces.setCellValueFactory(item->item.getValue().nbFacesProperty());
		TableColumn<PLYFile, Boolean> hasColor = new TableColumn<PLYFile, Boolean>("Color");
		hasColor.setCellValueFactory(item->item.getValue().hasColorProperty());
		hasColor.setCellFactory(item -> new TableCell<PLYFile,Boolean>() {
			@Override
		    protected void updateItem(Boolean item, boolean empty) {
		        super.updateItem(item, empty) ;
		        setText(empty ? null : item ? "Colored" : "No Colors");
		    }
		});
		modelInfo.getColumns().addAll(nbPoints,nbFaces,hasColor);
		
		TableColumn<PLYFile, String> plyInfo = new TableColumn<PLYFile, String>("PLY Information");
		collapsableColumns.add(plyInfo);
		TableColumn<PLYFile, String> plyName = new TableColumn<PLYFile, String>("Name");
		plyName.setCellValueFactory(item->item.getValue().plyNameProperty());
		plyName.setCellFactory(TextFieldTableCell.forTableColumn());
		plyName.setOnEditCommit(t -> {
			t.getTableView().getItems().get(t.getTablePosition().getRow()).setNom(t.getNewValue());
		});
		TableColumn<PLYFile, String> plyAuteur = new TableColumn<PLYFile, String>("Author");
		plyAuteur.setCellValueFactory(item->item.getValue().plyAuteurProperty());
		plyAuteur.setCellFactory(TextFieldTableCell.forTableColumn());
		plyAuteur.setOnEditCommit(t -> {
			t.getTableView().getItems().get(t.getTablePosition().getRow()).setAuteur(t.getNewValue());
		});
		TableColumn<PLYFile, String> plyDateCreation = new TableColumn<PLYFile, String>("Date");
		plyDateCreation.setCellValueFactory(item->item.getValue().plyDateCreationProperty());
		plyDateCreation.setCellFactory(TextFieldTableCell.forTableColumn());
		plyDateCreation.setOnEditCommit(t -> {
			t.getTableView().getItems().get(t.getTablePosition().getRow()).setDateCreation(t.getNewValue());
		});
		TableColumn<PLYFile, String> plyDescription = new TableColumn<PLYFile, String>("Description");
		plyDescription.setCellValueFactory(item->item.getValue().plyDescriptionProperty());
		plyDescription.setCellFactory(TextFieldTableCell.forTableColumn());
		plyDescription.setOnEditCommit(t -> {
			t.getTableView().getItems().get(t.getTablePosition().getRow()).setDescription(t.getNewValue());
		});
		
		plyInfo.getColumns().addAll(plyName,plyAuteur,plyDateCreation,plyDescription);
		
		tableView.setEditable(true);
		tableView.getColumns().addAll(fileInfo,modelInfo,plyInfo);
		
		
		
		this.getChildren().addAll(toolBar,tableView);
		
		this.setCollapsed(true);
	}
	
	public void setCollapsed(boolean value) {
		collapsed=value;
		if (value) {
			for (TableColumn<?, ?> column : collapsableColumns) {
				column.setVisible(false);
			}
			refreshButton.setText("R");
			collapseButton.setText(">");
			workspacePathLabel.setVisible(false);
			workspacePathLabel.setManaged(false);
			//openWorkspaceButton.setVisible(false);
		}else{
			for (TableColumn<?, ?> column : collapsableColumns) {
				column.setVisible(true);
			}
			refreshButton.setText("RefreshWorkspace");
			collapseButton.setText("Hide Details");
			workspacePathLabel.setVisible(true);
			workspacePathLabel.setManaged(true);
		}
			
	}
	
	private static String convertFileSize(int bytes) {
		if      (bytes >= 1073741824) { return Math.round((bytes / 1073741824.0)*100.0)/100.0 + " GB"; }
		else if (bytes >= 1048576)    { return Math.round((bytes / 1048576.0)*100.0)/100.0 + " MB"; }
		else if (bytes >= 1024)       { return Math.round((bytes / 1024.0)*100.0)/100.0 + " KB"; }
		else if (bytes > 1)           { return bytes + " bytes"; }
		else if (bytes == 1)          { return bytes + " byte"; }
		else                          { return "0 bytes"; }
	}

	public void openWorkspace() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(workspace);
		File workspaceFolder = dc.showDialog(this.parent);
		loadWorkspace(workspaceFolder);
	}

	public void loadWorkspace(File workspaceFolder) {
		if (workspaceFolder == null || !workspaceFolder.isDirectory())
			return;
		File[] plyFiles = workspaceFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".ply");
			}
		});
		if (plyFiles.length == 0) {
			parent.update(parent.getModel(), new NoPLYFileInDirectoryException());
			return;
		}

		this.workspace = workspaceFolder;
		this.workspacePathLabel.setText(workspace.getPath());

		fileList = new ArrayList<PLYFile>();

		for (double i = 0; i < plyFiles.length; i++) {
			fileList.add(new PLYFile(plyFiles[(int) i]));
		}
		
		updateTableView();
		
		rootToggled.setVisible(true);
		rootToggled.setManaged(true);

		openWorkspaceButton.setManaged(false);
		openWorkspaceButton.setVisible(false);
	}
	
	
	
	private void updateTableView() {
		String searchTerm = searchField.getText().toLowerCase();

		tableView.getItems().clear();
		for (PLYFile item : fileList) {
			if (item.getFileName().toLowerCase().contains(searchTerm)) {
				tableView.getItems().add(item);
			}
		}
	}
}
