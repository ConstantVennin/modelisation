package view;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.Light;
import utils.TransformationType;

public class ParametersSection extends VBox{
	
	private CheckBox lightCheckbox;
	private VBox lightParametersVBox;
	private Slider shadowStrengthSlider;
	private Slider lightStrengthSlider;
	
	public ParametersSection(View parent) {
		super();
		
		/*lightIntensitySlider = new RangeSlider(0.1, 3, 0.8, 1.5);
		lightIntensitySlider.setShowTickLabels(true);
		lightIntensitySlider.setShowTickMarks(true);
		lightIntensitySlider.setMajorTickUnit(1);
		lightIntensitySlider.setBlockIncrement(0.1);*/
		
		Label titleTransformationLabel=new Label("Transformations : ");
		titleTransformationLabel.setFont(new Font(15));
		titleTransformationLabel.setStyle("-fx-font-weight: bold");
		this.getChildren().addAll(titleTransformationLabel,new Separator());
		
		CustomSpinner xTranslation=new CustomSpinner(parent,0);
		this.getChildren().addAll(new Label("X Translation"),xTranslation);
		CustomSpinner yTranslation=new CustomSpinner(parent,1);
		this.getChildren().addAll(new Label("Y Translation"),yTranslation);
		CustomSpinner zTranslation=new CustomSpinner(parent,2);
		this.getChildren().addAll(new Label("Z Translation"),zTranslation);
		CustomSpinner xRotation=new CustomSpinner(parent,3);
		this.getChildren().addAll(new Label("X Rotation"),xRotation);
		CustomSpinner yRotation=new CustomSpinner(parent,4);
		this.getChildren().addAll(new Label("Y Rotation"),yRotation);
		CustomSpinner zRotation=new CustomSpinner(parent,5);
		this.getChildren().addAll(new Label("Z Rotation"),zRotation);
		CustomSpinner scale=new CustomSpinner(parent,6);
		this.getChildren().addAll(new Label("Scale"),scale);
		
		Button resetTransformationButton = new Button("Reset Transformation");
		resetTransformationButton.setOnAction(e -> {
			parent.getModel().resetTransformation();
		});
		
		
		
		this.getChildren().addAll(resetTransformationButton);
		
		Label titleLightLabel=new Label("Light settings : ");
		titleLightLabel.setFont(new Font(15));
		titleLightLabel.setStyle("-fx-font-weight: bold");
		
		
		lightCheckbox=new CheckBox("Compute light");
		lightCheckbox.setSelected(true);
		lightCheckbox.setOnAction(e->{
			if (lightCheckbox.isSelected()) {
				lightParametersVBox.setVisible(true);
				lightParametersVBox.setManaged(true);
			}else {
				lightParametersVBox.setVisible(false);
				lightParametersVBox.setManaged(false);
			}
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGELIGHTPARAMETER);
			parent.getModel().notifyObservers(types);
		});
		
		lightParametersVBox=new VBox();
		shadowStrengthSlider=new Slider(0,100,20);
		shadowStrengthSlider.setShowTickLabels(true);
		shadowStrengthSlider.setShowTickMarks(true);
		shadowStrengthSlider.setMajorTickUnit(10);
		shadowStrengthSlider.setMinorTickCount(2);
		shadowStrengthSlider.setOnMouseReleased(e->{
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGELIGHTPARAMETER);
			parent.getModel().notifyObservers(types);
		});
		lightStrengthSlider=new Slider(0,100,40);
		lightStrengthSlider.setShowTickLabels(true);
		lightStrengthSlider.setShowTickMarks(true);
		lightStrengthSlider.setMajorTickUnit(10);
		lightStrengthSlider.setMinorTickCount(2);
		lightStrengthSlider.setOnMouseReleased(e->{
			Set<TransformationType> types=new HashSet<>();
			types.add(TransformationType.CHANGELIGHTPARAMETER);
			parent.getModel().notifyObservers(types);
		});
		
		lightParametersVBox.getChildren().addAll(new Label("Shadow Strength"),shadowStrengthSlider,new Label("Light Strength"),lightStrengthSlider);
		
		CustomSpinner xLight=new CustomSpinner(parent,0);
		lightParametersVBox.getChildren().addAll(new Label("Light Source X position"),xLight);
		CustomSpinner yLight=new CustomSpinner(parent,0);
		lightParametersVBox.getChildren().addAll(new Label("Light Source Y position"),yLight);
		CustomSpinner zLight=new CustomSpinner(parent,0);
		lightParametersVBox.getChildren().addAll(new Label("Light Source Z position"),zLight);
		

		
		this.getChildren().addAll(titleLightLabel,new Separator(),lightCheckbox,lightParametersVBox);
		
	}
	
	public boolean computeShadows() {
		return lightCheckbox.isSelected();
	}
	
	public double getShadowStrength() {
		return Light.map(shadowStrengthSlider.getValue(), 0, 100, 1, 0);
	}
	
	public double getLightStrength() {
		return Light.map(lightStrengthSlider.getValue(), 0, 100, 1, 2);
	}
}
