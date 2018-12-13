package com.ao.desktop.controllers;
import com.ao.desktop.database.SQLManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable
{

    @FXML
    private Label noClassLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button addClass;
    @FXML
    private Button removeClass;
    @FXML
    private Button goback;
    @FXML
    ListView<String> listClasses = new ListView<String>();
    HashMap<String, String> classesCodes = new HashMap<>();
    String selectedClass;
    Stage AddStage = new Stage();
    AddClassController get = new AddClassController();
    SQLManager sql = new SQLManager();


    public void StartButton(ActionEvent start) throws IOException
    {

        selectedClass = listClasses.getSelectionModel().getSelectedItem();
        if(selectedClass == null)
        {
            noClassLabel.setText("please select a class before continuing");
        }
        else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Class_Screen.fxml"));
                Parent Class_Screen_Parent = loader.load();
                ClassController control = loader.getController();
                control.setClassName(selectedClass);
                Scene Class_Screen_Scene = new Scene(Class_Screen_Parent);
                Stage Class_Stage = (Stage) ((Node) start.getSource()).getScene().getWindow();
                Class_Stage.setScene(Class_Screen_Scene);
                Class_Stage.show();
                Class_Stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        control.close();
                        Class_Stage.close();
                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    @FXML
    public void addClass(ActionEvent add)
    {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/AddClass_Screen.fxml"));
            AddStage.setTitle("Add a Class");
            AddStage.setScene(new Scene(root, 304, 100));
            AddStage.show();
            AddClassController.setController(this);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }


    @FXML
    public void DisplayClass()
    {

        if(sql.isInitialize())
        {
            List<String[]> classesValues = sql.getClasses();
            for (String[] s : classesValues) {
                classesCodes.put(s[0], s[1]);
            }
            List<String> list = new ArrayList<>(classesCodes.keySet());
            ObservableList<String> Classes = FXCollections.observableArrayList(list);
            listClasses.setItems(Classes);
        }

    }

    @FXML
    public void initialize(URL location, ResourceBundle resources)
    {
        DisplayClass();
        listClasses.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String code = classesCodes.get(newValue);
                if (code != null) {
                    noClassLabel.setText("Class Code: " + code);
                }
            }
        });
    }
    @FXML
    public void removeClass(ActionEvent r)
    {

        String remove = listClasses.getSelectionModel().getSelectedItem();
        if(remove == null)
        {
            noClassLabel.setText("Please select a class to remove");
        }
        else
        {
            if (sql.removeClass(remove)) {
                classesCodes.remove(remove);
                DisplayClass();
            }
        }

    }
}
