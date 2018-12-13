package com.ao.desktop.controllers;
import com.ao.desktop.database.SQLManager;
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
    public String selectedClass;
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

    public String getSelected()
    {
        return selectedClass;
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
            List<String> list = sql.getClasses();
            ObservableList<String> Classes = FXCollections.observableArrayList(list);
            listClasses.setItems(Classes);
        }



    }

    @FXML
    public void initialize(URL location, ResourceBundle resources)
    {
        DisplayClass();
    }
    @FXML
    public void removeClass(ActionEvent r)
    {

        String remove =listClasses.getSelectionModel().getSelectedItem();
        if(remove == null)
        {
            noClassLabel.setText("Please select a class to remove");
        }
        else
        {
            sql.removeClass(remove);
            DisplayClass();
        }

    }
}
