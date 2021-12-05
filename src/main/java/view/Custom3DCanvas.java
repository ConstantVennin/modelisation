package view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import model.Couleur;
import model.Face;
import model.Light;
import model.PLYData;
import model.Point;
import utils.Matrice;
import utils.TransformationMatrice;
import utils.TransformationType;

/**
 * 
 * Canvas inclu dans les sc�ns o� l'on dessine les mod�les 3D
 *
 */
public class Custom3DCanvas extends Canvas {

	/**
	 * La vue dans laquelle le canvas est situ�
	 */
	private IView parent;

	/**
	 * le facteur de zoom du canvas
	 */
	private double scaleFactor;

	/**
	 * Coordonn�e x du d�but d'un drag sur le canvas
	 */
	private double mouseDragX;

	/**
	 * Coordonn�e y du d�but d'un drag sur le canvas
	 */
	private double mouseDragY;
	
	
	private PLYData transformedData;
	
	
	private Light light;
	/**
	 * Cr�e un nouveau canvas
	 * 
	 * @param parent la vue � laquelle appartient en canvas
	 * @param width  largeur du canvas
	 * @param height hauteur du canvas
	 */
	public Custom3DCanvas(IView parent, int width, int height) {
		this(parent, width, height, 1);
	}

	/**
	 * Cr�e un nouveau canvas
	 * 
	 * @param parent      la vue � laquelle appartient en canvas
	 * @param width       largeur du canvas
	 * @param height      hauteur du canvas
	 * @param scaleFactor niveau de zoom de la fenetre
	 */
	public Custom3DCanvas(IView parent, int width, int height, int scaleFactor) {
		super(width, height);
		this.parent = parent;
		this.scaleFactor = scaleFactor;
		
		this.setOnScroll((ScrollEvent event) -> {
			List<TransformationMatrice> transformationMatrices=new ArrayList<>();
			transformationMatrices.add(new TransformationMatrice(TransformationType.XTRANSLATION, -parent.getModel().getRepere().getParameter(0) ));
			transformationMatrices.add(new TransformationMatrice(TransformationType.YTRANSLATION, -parent.getModel().getRepere().getParameter(1) ));
			transformationMatrices.add(new TransformationMatrice(TransformationType.ZTRANSLATION, -parent.getModel().getRepere().getParameter(2) ));
			
			if (event.getDeltaY() > 0) {
				transformationMatrices.add(new TransformationMatrice(TransformationType.SCALE,1.1));
				
			} else {
				transformationMatrices.add(new TransformationMatrice(TransformationType.SCALE,0.9));
			}
			
			transformationMatrices.add(new TransformationMatrice(TransformationType.XTRANSLATION, parent.getModel().getRepere().getParameter(0) ));
			transformationMatrices.add(new TransformationMatrice(TransformationType.YTRANSLATION, parent.getModel().getRepere().getParameter(1) ));
			transformationMatrices.add(new TransformationMatrice(TransformationType.ZTRANSLATION, parent.getModel().getRepere().getParameter(2) ));
			
			this.parent.getModel().applyTransformation(transformationMatrices);
		});

		this.setOnMousePressed(e -> {
			mouseDragX = e.getX();
			mouseDragY = e.getY();
		});

		this.setOnMouseDragged(e -> {
			double xDelta = e.getX() - mouseDragX;
			double yDelta = e.getY() - mouseDragY;

			final double dragIncrement = 3;
			if (Math.abs(xDelta) > dragIncrement || Math.abs(yDelta) > dragIncrement) {
				if (e.getButton() == MouseButton.PRIMARY) {
					List<TransformationMatrice> transformationMatrice = this.parent.getSide().getTranslationFromDrag(xDelta, yDelta);
					this.parent.getModel().applyTransformation(transformationMatrice);
				} else if (e.getButton() == MouseButton.SECONDARY) {
					List<TransformationMatrice> transformationMatrice = this.parent.getSide().getCenteredRotationFromDrag(parent.getModel().getRepere(), xDelta / 100,yDelta / 100);
					this.parent.getModel().applyTransformation(transformationMatrice);
				} else if (this.parent.computeShadows() && e.getButton() == MouseButton.MIDDLE) {
					List<TransformationMatrice> transformationMatrice = this.parent.getSide().getRotationFromDrag(xDelta / 100,yDelta / 100);
					Light.applyTranformations(transformationMatrice);
					Set<TransformationType> types=new HashSet<TransformationType>();
					types.add(TransformationType.CHANGELIGHTPARAMETER);
					this.parent.getModel().notifyObservers(types);
				}

				mouseDragX = e.getX();
				mouseDragY = e.getY();
			}
		});		
	}
	
	public void applyTranformations(List<TransformationMatrice> transformationMatrices) {
		if (transformedData==null)return;
		Set<TransformationType> types=new HashSet<>();
		for (TransformationMatrice transformationMatrice : transformationMatrices) {
			this.transformedData.applyTransformationWithMatrix(transformationMatrice.get());
			types.add(transformationMatrice.getType());
		}
		
		render3DModel(types);
	}

	/**
	 * Fait un rendu du modele 3D stocke dans le modele lie a cette vue. Ce rendu
	 * depend du mode de rendu de la vue
	 */
	public void render3DModel(Set<TransformationType> types) {	
		if (transformedData==null || !parent.getSide().hasToRender(types)) return;
		
		System.out.println("======== Rendu de "+transformedData.getFaces().size()+" faces et de "+transformedData.getPoints().size()+" points========");
		
		if (types!=null)
			System.out.println("Parametres du rendu : "+types.toString());
		
		double time=System.currentTimeMillis();
		
		if (parent.getRenderMode() != RenderMode.EDGESONLY && parent.computeShadows() && parent.getSide().hasToCalculateLight(types) && transformedData != null) {
			Couleur couleur=new Couleur(parent.getDefaultFaceColor().getRed()*255,
										parent.getDefaultFaceColor().getGreen()*255,
										parent.getDefaultFaceColor().getBlue()*255,
										parent.getDefaultFaceColor().getOpacity()*255);
			light.shadeOnColor(parent.getSide().getI(), couleur,transformedData, parent.getShadowStrength(),parent.getLightStrength());
			System.out.println("Calculs de la lumiere : "+(System.currentTimeMillis()-time)+" ms");
		}
		
		GraphicsContext gc = this.getGraphicsContext2D();
		gc.setTransform(new Affine());
		gc.translate(this.getWidth() / 2, this.getHeight() / 2);
		gc.scale(1, -1);

		time=System.currentTimeMillis();

		if (parent.getRenderMode() != RenderMode.EDGESONLY && parent.getSide().hasToSort(types) ) {
			Collections.sort(transformedData.getFaces(), parent.getSide().getComparator(transformedData));

			System.out.println("Tri des faces : "+(System.currentTimeMillis()-time)+" ms");

		}
		time=System.currentTimeMillis();

		gc.setFill(parent.getBackgroundColor());
		gc.fillRect(-this.getWidth() / 2, -this.getHeight() / 2, this.getWidth(), this.getHeight());

		time=System.currentTimeMillis();

		gc.setStroke(parent.getEdgesColor());

		int len = transformedData.getFaces().size();
		for (int idx = 0; idx < len; idx++) {
			Face face = transformedData.getFaces().get(idx);
			List<Point> points = face.getPointsFromData(transformedData);
			double[] xs = new double[points.size()];
			double[] ys = new double[points.size()];

			for (int i = 0; i < points.size(); i++) {
				xs[i] = parent.getSide().getXOfPoint(points.get(i)) * scaleFactor;
				ys[i] = parent.getSide().getYOfPoint(points.get(i)) * scaleFactor;
			}
			
			model.Couleur faceColor = (parent.computeShadows() ? face.getColor() : face.getColorInit());
			if (faceColor == null) {
				gc.setFill(parent.getDefaultFaceColor());
			} else {
				gc.setFill(Color.color(faceColor.getR() / 255.0, faceColor.getG() / 255.0, faceColor.getB() / 255.0,
						faceColor.getA() / 255.0));
			}

			if (parent.getRenderMode() == RenderMode.EDGESFACES || parent.getRenderMode() == RenderMode.FACESONLY)
				gc.fillPolygon(xs, ys, points.size());
			if (parent.getRenderMode() == RenderMode.EDGESFACES || parent.getRenderMode() == RenderMode.EDGESONLY)
				gc.strokePolygon(xs, ys, points.size());
		}

		System.out.println("Rendu des faces : "+(System.currentTimeMillis()-time)+"ms");
		time=System.currentTimeMillis();
	}

	/**
	 * Change le zoom de l'affichage
	 * 
	 * @param scaleFactor le zoom
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
		Set<TransformationType> types=new HashSet<>();
		types.add(TransformationType.CHANGECANVASSCALEFACTOR);
		this.render3DModel(types);
	}

	/**
	 * Retourne le zoom de l'affichage
	 * 
	 * @return le zoom
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * redimensionne le canvas
	 * 
	 * @param width       la largueur
	 * @param height      la hauteur
	 * @param scaleFactor le facteur de zoom
	 */
	public void setSize(int width, int height, double scaleFactor) {
		this.scaleFactor = scaleFactor;
		this.setWidth(width);
		this.setHeight(height);
		
		Set<TransformationType> types=new HashSet<>();
		types.add(TransformationType.CHANGECANVASSIZE);
		this.render3DModel(types);
	}
	
	public void setTransformedData(PLYData transformedData) {
		this.transformedData = new PLYData(transformedData);
		
		Set<TransformationType> types=new HashSet<>();
		types.add(TransformationType.LOADMODEL);
		this.light = new Light(transformedData);
		this.render3DModel(types);
		
	}

}
