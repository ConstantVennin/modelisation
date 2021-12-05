package view;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CustomSpinner extends HBox{
	
	private View parent;
	private int parameterIndex;
	
	private Button minusminus;
	private Button minus;
	private TextField textField;
	private Button plus;
	private Button plusplus;
	
	
	public CustomSpinner(View parent,int parameterIndex){
		super();
		this.parent=parent;
		this.parameterIndex=parameterIndex;
		
		minusminus=new Button("--");
		minus=new Button("-");
		textField=new TextField();
		plus=new Button("+");
		plusplus=new Button("++");
		
		this.getChildren().addAll(minusminus,minus,textField,plus,plusplus);
		
		update();
	}
	
	public void update() {
		textField.setText(""+parent.getModel().getRepere().getParameter(parameterIndex));
		
	}
	
}
