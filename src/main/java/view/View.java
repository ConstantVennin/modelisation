
package view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exceptions.CustomException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Model;
import model.PLYData;
import sides.Side;
import sides.SidesFabrique;
import utils.Observer;
import utils.Subject;
import utils.TransformationMatrice;
import utils.TransformationType;

/**
 * Classe Vue, gère la vue du modèle et de l'interface
 */
public class View extends Stage implements IView, Observer {
	/**
	 * modèle
	 */
	private Model model;
	/**
	 * canvas du modèle
	 */
	private Custom3DCanvas canvas;
	/**
	 * affichage des fichiers PLY
	 */
	private WorkspaceViewer workspaceViewer;
	/**
	 * groupe de selection du render
	 */
	private ToggleGroup renderModeGroup;
	/**
	 * groupe de selection des cotés
	 */
	private ToggleGroup sideGroup;
	/**
	 * Selectionneur de couleur pour le fond
	 */
	private CustomColorPicker backgroundColorPicker;
	/**
	 * Selectionneur de couleur pour les arêtes
	 */
	private CustomColorPicker edgesColorPicker;
	/**
	 * Selectionneur de couleur pour les faces
	 */
	private CustomColorPicker defaultFaceColorPicker;
	
	private ParametersSection parametersSection;
	/**
	 * liste des vues secondaires
	 */
	private List<SecondaryView> secondaryViews;

	/**
	 * Cree un vue simple et lui attache un modele
	 * 
	 * @param model Le modele a attacher a la vue
	 */

	public View(Model model) {
		this.model = model;

		secondaryViews = new ArrayList<SecondaryView>();

		BorderPane root = new BorderPane();

		// Partie haute : menu
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("_File");
		MenuItem openWorkspace = new MenuItem("Open Workspace");
		openWorkspace.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		openWorkspace.setOnAction(e -> {
			workspaceViewer.openWorkspace();
		});
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(e -> {
			closeApplication();
		});

		fileMenu.getItems().addAll(openWorkspace, exit);

		Menu viewMenu = new Menu("_View");

		Menu renderModeMenu = new Menu("Rendering Mode");
		RadioMenuItem edgesOnly = new RadioMenuItem("Edges Only");
		RadioMenuItem facesOnly = new RadioMenuItem("Faces Only");
		RadioMenuItem bothEdgesAndFaces = new RadioMenuItem("Both Edges and Faces");

		renderModeGroup = new ToggleGroup();
		edgesOnly.setToggleGroup(renderModeGroup);
		facesOnly.setToggleGroup(renderModeGroup);
		bothEdgesAndFaces.setToggleGroup(renderModeGroup);
		facesOnly.setSelected(true);

		renderModeMenu.setOnAction(e -> {
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGERENDERMODE);
			this.canvas.render3DModel(types);
		});
		renderModeMenu.getItems().addAll(edgesOnly, facesOnly, bothEdgesAndFaces);

		Menu sideMenu = new Menu("View from");
		RadioMenuItem positiveX = new RadioMenuItem("Positive X");
		RadioMenuItem positiveY = new RadioMenuItem("Positive Y");
		RadioMenuItem positiveZ = new RadioMenuItem("Positive Z");

		sideGroup = new ToggleGroup();
		positiveX.setToggleGroup(sideGroup);
		positiveY.setToggleGroup(sideGroup);
		positiveZ.setToggleGroup(sideGroup);
		positiveZ.setSelected(true);

		sideMenu.setOnAction(e -> {
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGESIDE);
			this.canvas.render3DModel(types);
		});
		sideMenu.getItems().addAll(positiveX, positiveY, positiveZ);

		MenuItem addSecondaryView = new MenuItem("Add Secondary View");
		addSecondaryView.setOnAction(e -> {
			secondaryViews.add(new SecondaryView(this, model));
			this.requestFocus();
		});
		addSecondaryView.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));

		Menu colorsMenu = new Menu("Colors");

		backgroundColorPicker = new CustomColorPicker(Color.color(0.9, 0.9, 0.9));
		MenuItem backgroundColor = new MenuItem("Background Color");
		backgroundColor.setGraphic(backgroundColorPicker);
		backgroundColorPicker.setOnAction(e -> {
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGECOLOR);
			model.notifyObservers(types);
		});

		edgesColorPicker = new CustomColorPicker(Color.color(0, 0, 0));
		MenuItem edgesColor = new MenuItem("Edges Color");
		edgesColor.setGraphic(edgesColorPicker);
		edgesColorPicker.setOnAction(e -> {
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGECOLOR);
			model.notifyObservers(types);
		});

		defaultFaceColorPicker = new CustomColorPicker(Color.color(0.4, 0.4, 0.4));
		MenuItem defaultFaceColor = new MenuItem("Default Face Color");
		defaultFaceColor.setGraphic(defaultFaceColorPicker);
		defaultFaceColorPicker.setOnAction(e -> {
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGECOLOR);
			model.notifyObservers(types);
		});

		colorsMenu.getItems().addAll(backgroundColor, edgesColor, defaultFaceColor);

		viewMenu.getItems().addAll(addSecondaryView, sideMenu, renderModeMenu, colorsMenu);

		Menu helpMenu = new Menu("_Help");

		menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);

		root.setTop(menuBar);

		// Partie gauche
		VBox rootLeft = new VBox();

		this.workspaceViewer = new WorkspaceViewer(this);
		rootLeft.getChildren().addAll(workspaceViewer);

		root.setLeft(rootLeft);

		// Partie centrale
		this.canvas = new Custom3DCanvas(this, 1000, 1000);

		root.setCenter(canvas);

		// Partie droite
		VBox rootRight = new VBox();

		this.parametersSection=new ParametersSection(this);
		rootRight.getChildren().add(new Separator());
		rootRight.getChildren().add(parametersSection);

		root.setRight(rootRight);

		Scene scene = new Scene(root, 600, 400);
		this.setScene(scene);
		this.setTitle("3D Rendering Project");

		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();

		this.setX(bounds.getMinX());
		this.setY(bounds.getMinY());
		this.setWidth(bounds.getWidth());
		this.setHeight(bounds.getHeight());

		this.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				closeApplication();
			}
		});

		this.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				for (SecondaryView sView : getSecondaryViews()) {
					sView.setIconified(t1.booleanValue());
				}
			}
		});

		this.show();

		model.attach(this);
	}

	/**
	 * ferme la fenetre de la vue secondaire
	 */
	public void closeApplication() {
		for (SecondaryView sView : this.getSecondaryViews()) {
			sView.close();
		}
		this.close();
	}

	public List<SecondaryView> getSecondaryViews() {
		return secondaryViews;
	}

	public Model getModel() {
		return model;
	}

	/**
	 * Recupère le mode de rendu
	 * 
	 * @return le mode de rendu
	 */

	@Override
	public RenderMode getRenderMode() {
		return RenderMode.getFromString(((RadioMenuItem) (this.renderModeGroup.getSelectedToggle())).getText());
	}

	@Override
	public void setRenderMode(RenderMode renderMode,boolean update) {
		for (Toggle toggle : renderModeGroup.getToggles()) {
			RadioMenuItem item = (RadioMenuItem) toggle;

			if (RenderMode.getFromString(item.getText()) == renderMode) {
				toggle.setSelected(true);
			}
		}

		if (update) {
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGERENDERMODE);
			this.canvas.render3DModel(types);
		}
	}

	@Override
	public Side getSide() {
		SidesFabrique fabrique = new SidesFabrique();

		return fabrique.create(((RadioMenuItem) (this.sideGroup.getSelectedToggle())).getText());
	}

	@Override
	public void setSide(Side side,boolean update) {
		SidesFabrique fabrique = new SidesFabrique();
		for (Toggle toggle : sideGroup.getToggles()) {
			RadioMenuItem item = (RadioMenuItem) toggle;

			if (fabrique.create(item.getText()).equals(side)) {
				toggle.setSelected(true);
			}
		}
		
		if (update) {
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGESIDE);
			this.canvas.render3DModel(types);
		}
	}

	@Override
	public void update(Subject subj) {
		canvas.render3DModel(null);
	}

	@Override
	public void update(Subject subj, Object data) {
		if (data instanceof CustomException) {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText(((CustomException) data).getName());
			errorAlert.setContentText(((CustomException) data).getMessage());
			errorAlert.showAndWait();
		}else if (data instanceof List<?>) {
			canvas.applyTranformations( (List<TransformationMatrice>)data );
		}else if (data instanceof PLYData) {
			canvas.setTransformedData( (PLYData) data);
		}else if (data instanceof Set<?>) {
			canvas.render3DModel((Set<TransformationType>) data);
		}
	}

	@Override
	public Custom3DCanvas getCanvas() {
		return this.canvas;
	}

	@Override
	public Color getDefaultFaceColor() {
		return defaultFaceColorPicker.getValue();
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColorPicker.getValue();
	}

	@Override
	public Color getEdgesColor() {
		return edgesColorPicker.getValue();
	}

	@Override
	public boolean computeShadows() {
		return parametersSection.computeShadows();
	}

	@Override
	public double getShadowStrength() {
		return parametersSection.getShadowStrength();
	}

	@Override
	public double getLightStrength() {
		return parametersSection.getLightStrength();
	}
}
