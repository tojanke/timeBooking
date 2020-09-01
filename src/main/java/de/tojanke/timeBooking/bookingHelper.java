package de.tojanke.timeBooking;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class bookingHelper extends Application {

	private VBox root;
	private ListView projectView;
	private TextField newProjField;
	private static ObservableList<String> projects;
	private static ObservableList<TimeEntry> entries;
	
	private static class TimeEntry implements Serializable {
		public long time;
		public String project;
		private static SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM. HH:mm");
		
		public TimeEntry(String p, long t) {
			project = p;
			time = t;
		}
		
		public StringProperty timeAsString() {
			SimpleStringProperty timeString= new SimpleStringProperty();
			timeString.set(timeFormat.format(new Date(time)));
			return timeString;
		}
		
		public StringProperty nameAsString() {
			SimpleStringProperty nameString= new SimpleStringProperty();
			nameString.set(project);
			return nameString;
		}
	}
	
	public static void main(String[] args) {
		List<String> projIn = (List<String>) Serialization.in("projects.save"); 
		
		if(projIn != null) {
			projects = FXCollections.observableList(new ArrayList(projIn));	
		}
		else {
		
			projects = FXCollections.observableList(new ArrayList<String>());
			projects.add("~");
		}
		projects.addListener(new ListChangeListener() {
			 
            @Override
            public void onChanged(ListChangeListener.Change change) {
                Serialization.out(Arrays.asList(projects.toArray()),  "projects.save");
            }
        });
		
		List<TimeEntry> entrIn = (List<TimeEntry>) Serialization.in("entries.save");
		
		if(entrIn != null) {
			entries = FXCollections.observableList(new ArrayList(entrIn));	
		}
		else {
			System.err.println("Keine Einträge");
			entries = FXCollections.observableList(new ArrayList<TimeEntry>());
		}
		entries.addListener(new ListChangeListener() {
			 
            @Override
            public void onChanged(ListChangeListener.Change change) {
                Serialization.out(Arrays.asList(entries.toArray()),  "entries.save");
            }
        });
		
        launch(args);
    }
	
	
	private void book() {
		entries.add(new TimeEntry(projectView.getSelectionModel().getSelectedItem().toString(), System.currentTimeMillis()));
	}
	
	private void deleteProject() {
		projects.remove(projectView.getSelectionModel().getSelectedItem());
		if(projects.isEmpty()) {
			projects.add("~");
		}
	}
	
	private void addProject() {
		String t = newProjField.getText();
		if(!t.isEmpty()) {
			projects.add(t);
		}
	}
	
	private void clearEntries() {
		entries.clear();
	}
	
	@Override
	public void start(Stage primary) throws Exception {		
        primary.setTitle("bookingHelper");
        primary.getIcons().add(new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("time.png")));
        
        
        Label title = new Label("Zeitbuchung");        
        title.setStyle("-fx-font: 20 arial;-fx-font-weight: bold");        

        newProjField = new TextField();
        
        projectView = new ListView<String>(projects);
        projectView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);        
        projectView.getSelectionModel().selectedItemProperty()
        .addListener(new ChangeListener<String>() {
    		@Override
        	public void changed(ObservableValue observable,
        			String oldValue, String newValue) {
    			if (newValue==null) {
    				projectView.getSelectionModel().selectFirst();
    			}
          }		
        });
        
        projectView.setStyle("-fx-font: 20 arial;-fx-font-weight: bold");
        
        TableColumn<TimeEntry, String> timeCol = new TableColumn<TimeEntry, String>("Zeit");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().timeAsString());
        timeCol.setMinWidth(100);
        timeCol.setStyle("-fx-font: 12 arial;");
        
        TableColumn<TimeEntry, String> projCol = new TableColumn<TimeEntry, String>("Projekt");
        projCol.setCellValueFactory(cellData -> cellData.getValue().nameAsString());
        projCol.setMinWidth(200);
        projCol.setStyle("-fx-font: 12 arial;");
        
        TableView<TimeEntry> eTable = new TableView<TimeEntry>(entries);
        eTable.getColumns().addAll(timeCol, projCol);
        
        
        Button bookBtn = new Button("Buchen");
        bookBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                book();
            }
        });
        bookBtn.setStyle("-fx-font: 20 arial;-fx-font-weight: bold");
        
        Button newBtn = new Button("+");
        newBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                addProject();
            }
        });
        newBtn.setStyle("-fx-font: 20 arial;-fx-font-weight: bold");
        
        Button remBtn = new Button("-");
        remBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                deleteProject();
            }
        });
        remBtn.setStyle("-fx-font: 20 arial;-fx-font-weight: bold");

        Button clrBtn = new Button("Leeren");
        clrBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	clearEntries();
            }
        });
        clrBtn.setStyle("-fx-font: 20 arial;-fx-font-weight: bold");
        
        HBox btnBox = new HBox(bookBtn, newBtn, remBtn,clrBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setSpacing(20);
        
        root = new VBox(title, projectView, newProjField, btnBox, eTable);
        root.setSpacing(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        primary.setScene(new Scene(root,400,800));
        primary.show();
		
	}
	
	
	

}
