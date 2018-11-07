package com.ao.desktop.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
    ObservableList<String> Classes = FXCollections.observableArrayList("CSCI 318 Section M01", "CSCI 318 Section M02", "CSCI 318 Section M03");

    public String selectedClass;
    Stage AddStage = new Stage();
    AddClassController get = new AddClassController();

    public void StartButton(ActionEvent start) throws IOException
    {

        selectedClass = listClasses.getSelectionModel().getSelectedItem();
        if(selectedClass == null)
        {
            noClassLabel.setText("please select a class before continuing");
        }
        else {
            try {
                Parent Class_Screen_Parent = FXMLLoader.load(getClass().getResource("/Class_Screen.fxml"));
                Scene Class_Screen_Scene = new Scene(Class_Screen_Parent);
                Stage Class_Stage = (Stage) ((Node) start.getSource()).getScene().getWindow();
                Class_Stage.setScene(Class_Screen_Scene);
                Class_Stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void AddClass(ActionEvent add)
    {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/AddClass_Screen.fxml"));
            AddStage.setTitle("Add a Class");
            AddStage.setScene(new Scene(root, 304, 100));
            AddStage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }


    @FXML
    public void DisplayClass()
    {
        listClasses.setItems(Classes);
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources)
    {
        DisplayClass();
    }

    public void removeClass()
    {
        //Todo add sql implementation for deleting an element
    }
}
