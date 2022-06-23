package application;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.controlsfx.control.SearchableComboBox;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;



public class Controller {
	
	private static final String APPLICATION_ID = "yorku_course_bookmarks";
	
	private static final String FACULTY = "FACULTY";
	private static final String SUBJECT = "SUBJECT";
	private static final String COURSE = "COURSE";
//	private static final String YEAR = "YEAR";
	private static final String TERM = "TERM";
	private static final String URL = "URL";
	
	private static final String PASS = "PASS";
	private static final String FAIL = "FAIL";
	private static final String INVALID = "INVALID";
	
	
	private Preferences prefs;
	
	final String[] commonCreditValues = {"3.00","4.00","6.00","1.00"};
	
	private Map<String, String> deptFacMap;
	
	private String sourceURL;
	
	private String finalURL;
	
//	private String lastFac;
//
//	private String lastSub;
//	
//	private String lastCourse;
//	
//	private Integer lastYear;
//
//	private String lastTerm;
	
	
    @FXML
    private ComboBox<String> facultyBox;

//    @FXML
//    private ComboBox<String> subjectBox;
    @FXML
    private SearchableComboBox<String> subjectBox;
    
    
  

    @FXML
    private TextField courseBox;
//    @FXML
//    private ComboBox<String> courseBox;
    
    @FXML
    private ChoiceBox<String> yearBox;
    
    @FXML
    private ChoiceBox<String> termBox;
    
    @FXML
    private TextField outputTextField;

    @FXML
    private ScrollPane rightPane;
    
    
    @FXML
    private VBox savedCoursePane;
    
    @FXML
    private Button goButton;
    
    @FXML
    private Button saveButton;
    
    
    
    @FXML
    private void initialize() {
    	
    	prefs = Preferences.userRoot().node(APPLICATION_ID);
    	
    	deptFacMap = new HashMap<String, String>();
    	HashSet<String[]> multiFacDepts = new HashSet<String[]>();
//    	deptFacMap.
    	
    	try {
			Document doc = Jsoup.connect("https://registrar.yorku.ca/enrol/course-contacts").get();
			Elements ele = doc.select("tr.course-contacts");
//			System.out.println(ele.size());
//			System.out.println(ele.first().child(0).text());
			for (Element e : ele) {
				if (!e.child(0).text().equals("GL") 
						&& !e.child(0).text().equals("ED") 
						&& !e.child(0).text().equals("SB")
						&& !e.child(0).text().equals("GS")) {
					deptFacMap.put(e.child(1).text(), e.child(0).text());
				}
				else
					multiFacDepts.add(new String[]{e.child(1).text(), e.child(0).text()});

			}
			
			for (String[] leftover : multiFacDepts) {
				if (!deptFacMap.containsKey(leftover[0]))
					deptFacMap.put(leftover[0], leftover[1]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	List<String> terms = new ArrayList<String>();
    	try {
			Document doc2 = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").get();
			Element ele2 = doc2.selectFirst("ul.bodytext");
//			System.out.println(ele2.child(0).child(0).absUrl("href"));
			
			doc2 = Jsoup.connect(ele2.child(0).child(0).absUrl("href")).get();
			ele2 = doc2.selectFirst("#sessionSelect");
			for (Element e: ele2.children()) {
//				System.out.println(e.text());
				String termYear = "";
				String[] termYears = e.text().split(" ");
				termYears[1] = termYears[1].substring(0, 4);
				if (termYears[0].equals("Summer"))
					termYear = termYear.concat("SU");
				else
					termYear = termYear.concat("FW");
				termYear = termYear.concat(termYears[1]);
				terms.add(termYear);
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
  
//    	facultyBox.getItems().addAll("AP","ED","ES","EU","FA","LE","SC");
    	List<String> temp2 = new ArrayList<>(deptFacMap.values());
    	Collections.sort(temp2);
    	Set<String> temp3 = new LinkedHashSet<>(temp2);
    	
    	facultyBox.getItems().addAll(temp3);
    	facultyBox.setValue(prefs.get(FACULTY, ""));
    	facultyBox.getSelectionModel().selectedItemProperty().addListener(
    			(observable, oldValue, newValue) -> {
    				if(newValue.matches("[A-z]+"))
    					prefs.put(FACULTY, newValue.toUpperCase());
    			});
    	
//    	subjectBox.getItems().addAll("EECS", "ENG");
    	List<String> temp = new ArrayList<>(deptFacMap.keySet());
    	Collections.sort(temp);
    	subjectBox.getItems().addAll(temp);
    	subjectBox.setValue(prefs.get(SUBJECT, ""));
    	subjectBox.getSelectionModel().selectedItemProperty().addListener(
    			(observable, oldValue, newValue) -> {
    			if(newValue !=null && newValue.matches("[A-z]+")) {
    				prefs.put(SUBJECT, newValue.toUpperCase());
    				facultyBox.setValue(deptFacMap.get(newValue));
    			}
    			});

    	
    	courseBox.setText(prefs.get(COURSE, ""));
    	courseBox.textProperty().addListener(
    			(observable, oldValue, newValue) -> {
				if(newValue.matches("[0-9]*"))
					prefs.put(COURSE, newValue);
			});
	
    	
//    	courseBox.setValue(prefs.get(COURSE, ""));
//    	courseBox.get getSelectionModel().selectedItemProperty().addListener(
//    			(observable, oldValue, newValue) -> {
//    				if(newValue.matches("[0-9]+"))
//    					prefs.put(COURSE, newValue);
//    			});
//    	
//    	Integer currentYear = LocalDate.now().getYear();
//    	yearBox.getItems().addAll(Integer.toString(currentYear - 1), currentYear.toString());
//    	yearBox.setValue(prefs.get(YEAR, currentYear.toString()));
//    	yearBox.getSelectionModel().selectedItemProperty().addListener(
//    			(observable, oldValue, newValue) -> {
//    				prefs.put(YEAR, newValue);
//    			});
    	
    	termBox.getItems().addAll(terms);
//    	termBox.getItems().addAll("FW","SU");
    	termBox.setValue(prefs.get(TERM, "FW"));
    	termBox.getSelectionModel().selectedItemProperty().addListener(
    			(observable,  oldValue, newValue) -> {
    				prefs.put(TERM, newValue);
    			});
    	
    	finalURL = "";
    	
    	try {
			for (String bookmark: prefs.childrenNames()) {
				Preferences button = prefs.node(bookmark);
				String faculty = button.get(FACULTY, "Some");
				String subject = button.get(SUBJECT, "thing");
				String course = button.get(COURSE, " went");
//				String year = button.get(YEAR, " wrong");
				String term = button.get(TERM, " wrong");
				String url = button.get(URL, "https://www.yorku.ca");
				
				createNewButton(faculty, subject, course, "year", term, url);

			}
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    //	sourceURL = "https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm.woa/wa/crsq?fa=LE&sj=ENG&cn=3000&cr=3.00&ay=2019&ss=SU";
//    	Computer\HKEY_CURRENT_USER\Software\JavaSoft\Prefs\yorku_course_bookmarks
    	
    	savedCoursePane.prefHeightProperty().bind(rightPane.heightProperty());
    	
    	
    
    
 	
    }

    
    private String checkInput() throws InterruptedException {
    	
 
		
    	Boolean flag = false;
//    	String faculty = facultyBox.getValue().toUpperCase();
    	
    	String subject = subjectBox.getValue().toUpperCase();
    	String faculty = facultyBox.getValue();
    	String course = courseBox.getText();
//    	String course = courseBox.getValue();
//    	String year = yearBox.getValue();
    	String year = termBox.getValue().substring(2);
    	String term = termBox.getValue().substring(0,2);
    	
    	if (term.equals("SU")) {
    		year = Integer.toString (Integer.valueOf(year) - 1);
    	}
    	
//    	System.out.println(subject  +"     "+facultyBox.getValue());
    	 			    	
    	if ( course.matches("[0-9]{4}")&& 
//    			faculty.matches("[A-Z]{2}") && 
    			subject.matches("[A-Z]{2,4}")) { 
	    		
	    	flag = createSourceURL(flag, faculty, subject, course, year, term);
	    	
    	}
    	else {
    		return INVALID;
    	}
    	
    	if (flag && finalURL != null && !finalURL.trim().isEmpty())
    		return PASS;
    	else 
    		return FAIL;
    }


	private Boolean createSourceURL(Boolean flag, String faculty, 
			String subject, String course, String year, String term) throws InterruptedException {
		
		
		for (int i = 0; i < commonCreditValues.length; i++) {
			
			sourceURL = String.format(
					"https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm.woa/wa/crsq"
							+ "?fa=%s&sj=%s&cn=%s&cr=%s&ay=%s&ss=%s",
					faculty, subject, course,
					commonCreditValues[i], year, term);
			System.out.println(sourceURL);
			
			if(createFinalURL(sourceURL)) {
				flag = true;
				break;
			}
			if (i >= 1)
				Thread.sleep(1000);
		}
		return flag;
	}
	

	private Boolean createFinalURL(String sourceURL) {

		String pageTitle = "";
		try {
			Document doc = Jsoup.connect(sourceURL).get();
//			System.out.println(doc.title());
			pageTitle = doc.title();
			Elements scheduleURLs = doc.select("a:matchesOwn([0-9]{4} (?:Course )*Schedule)");
			for (Element scheduleURL : scheduleURLs) {
//			  System.out.printf("%s\n%s\n", 
//			    headline.attr("title"), headline.absUrl("href"));
				finalURL = scheduleURL.absUrl("href");
				System.out.println(scheduleURL.absUrl("href"));
			}

			
		} catch (HttpStatusException e) {
			System.out.println("error:too many requests at once");	
			finalURL = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//System.out.println(pageTitle);
		return (!pageTitle.contains("Error Page"));
	}

	
	private void createNewButton(	String faculty, String subject,
									String course, String year, String term, 
									String sourceURL) throws BackingStoreException {
		faculty = faculty.toUpperCase();
		subject = subject.toUpperCase();
		String buttonText = subject + course + " " + term;
		String saveFolderPath = String.format("%s-%s-%s-%s", 
				faculty, subject, course, term);
		if(prefs.childrenNames().length > savedCoursePane.getChildren().size()
				|| !prefs.nodeExists(saveFolderPath)) {
			Preferences saveData = prefs.node(saveFolderPath);

			Button save = new Button(buttonText);
			save.setStyle("-fx-text-alignment: left");
			save.setId(buttonText);
			save.setOnAction(e -> {
				if (createFinalURL(sourceURL))
					openFinalURL();
				else {
					outputTextField.setStyle("-fx-text-fill: red");
					outputTextField.setText("Saved Page Error");
				}
					
			});
			save.setOnContextMenuRequested(e -> {
				savedCoursePane.getChildren().remove(save);
				outputTextField.setStyle("-fx-text-fill: red");
				outputTextField.setText("Saved Course Removed");
				try {
					saveData.removeNode();
					//saveData.flush();
				} catch (BackingStoreException e1) {
					// TODO Auto-generated catch block
					outputTextField.setText("failed");
					e1.printStackTrace();
				}
			});

			savedCoursePane.getChildren().add(save);				
			save.setMinSize(150, 25);

			saveData.put(FACULTY, faculty);
			saveData.put(SUBJECT, subject);
			saveData.put(COURSE, course);
//			saveData.put(YEAR, year);
			saveData.put(TERM, term);
			saveData.put(URL, sourceURL);
		}

		//prefs.putInt("COUNTER", prefs.getInt("COUNTER", 0));

	}


    private void openFinalURL() {
    	try {
    		System.out.println(finalURL);
    		Desktop.getDesktop().browse(new URI(finalURL));
    		outputTextField.setStyle("-fx-text-fill: green");
			outputTextField.setText("Course Page Opened!");
    	} catch (MalformedURLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (URISyntaxException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 


    }

	@FXML
    void onSaveButton(ActionEvent event) throws InterruptedException, BackingStoreException {
		int counter = savedCoursePane.getChildren().size();
    	switch(checkInput()) {
    		case PASS:
				createNewButton(facultyBox.getValue(), subjectBox.getValue(), 
						courseBox.getText(), "", termBox.getValue(), sourceURL);
				if (savedCoursePane.getChildren().size() != counter) {
		    		outputTextField.setText("Course Saved!");
		    		outputTextField.setStyle("-fx-text-fill: green");
    			}
    			else {
    	    		outputTextField.setText("Course Already Saved");
		    		outputTextField.setStyle("-fx-text-fill: red");
    			}
	    		break;
    		case FAIL:
	    		outputTextField.setStyle("-fx-text-fill: red");
	    		outputTextField.setText("No Course Found");
	    		break;
    		case INVALID:
        		outputTextField.setStyle("-fx-text-fill: red");
        		outputTextField.setText("One or More Inputs Invalid");
        		break;
    	}
    	
    }
	
    @FXML
    void websiteButton(ActionEvent event) throws InterruptedException {

	   	outputTextField.setText("Searching For Course...");
		outputTextField.setStyle("-fx-text-fill: orange");
    	switch(checkInput()) {
			case PASS:
				openFinalURL();
				break;
			case FAIL:
				outputTextField.setText("No Course Found");
				outputTextField.setStyle("-fx-text-fill: red");
				
				break;
			case INVALID:
				outputTextField.setStyle("-fx-text-fill: red");
        		outputTextField.setText("One or More Inputs Invalid");
        		break;
    	}
				
    }





}
