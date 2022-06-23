package application;
	
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class CourseApplication extends Application {
	
	private static final String APPLICATION_ID = "yorku_course_bookmarks";
	private static final String ALWAYS_ON_TOP = "Always_on_top";
    private static final String WINDOW_POSITION_X = "Window_Position_X";
    private static final String WINDOW_POSITION_Y = "Window_Position_Y";
//    private static final String WINDOW_WIDTH = "Window_Width";
    private static final String WINDOW_HEIGHT = "Window_Height";
    private static final double DEFAULT_WIDTH = 420;
    private static final double DEFAULT_HEIGHT = 400;
    
    
    
	
	
	
	@Override
	public void start(Stage stage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Application.fxml"));
			BorderPane border = (BorderPane) ((AnchorPane)root).getChildren().get(0);
			Preferences prefs = Preferences.userRoot().node(APPLICATION_ID);
			Scene scene = new Scene(root);
			CheckBox alwaysOnTop = (CheckBox)scene.lookup("#alwaysOnTop");
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("YorkU Course Bookmarker");
			stage.setMaxWidth(DEFAULT_WIDTH);
			stage.setMinWidth(DEFAULT_WIDTH);
			stage.setMinHeight(301);
			stage.setHeight(prefs.getDouble(WINDOW_HEIGHT, DEFAULT_HEIGHT));
			stage.setOnCloseRequest(e ->{
				prefs.putBoolean(ALWAYS_ON_TOP, alwaysOnTop.isSelected());
				prefs.putDouble(WINDOW_POSITION_X, stage.getX());
	            prefs.putDouble(WINDOW_POSITION_Y, stage.getY());
	           // prefs.putDouble(WINDOW_WIDTH, stage.getWidth());
	            prefs.putDouble(WINDOW_HEIGHT, stage.getHeight());
			});
			border.prefWidthProperty().bind(scene.widthProperty());
			border.prefHeightProperty().bind(scene.heightProperty());
			
			
			stage.setAlwaysOnTop(alwaysOnTop.isSelected());
			alwaysOnTop.selectedProperty().addListener((observable, oldValue, newValue) ->{
				stage.setAlwaysOnTop(newValue);
			});
				
		
			stage.setX(prefs.getDouble(WINDOW_POSITION_X, stage.getX()));
			stage.setY(prefs.getDouble(WINDOW_POSITION_Y, stage.getY()));
			alwaysOnTop.setSelected(prefs.getBoolean(ALWAYS_ON_TOP, false));
			stage.getIcons().add(new Image(CourseApplication.class.getResourceAsStream("icon.png")));
			
			stage.show();
//			System.out.println(stage.getWidth());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	
}
