package com.ao.desktop.controllers;

import com.ao.desktop.database.SQLManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AddClassController implements Initializable
{
    private static MainController main;
    SQLManager save;
    List<String> section = new ArrayList();
    @FXML
    private TextField ClassNameField;
    @FXML
    private Button AddClassButton;
    @FXML
    private Label errorLabel;
    private String newSection;


    public static void setController(MainController mains)
    {
        main=mains;
    }

    public void addClass(ActionEvent add)
    {

        save=new SQLManager();
        newSection = ClassNameField.getText();
        Thread savethread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                if(save.isInitialize())
                {
                    List<String[]> list= save.getClasses();
                    boolean contains=false;
                    for(String[] s : list)
                    {
                        if(s[0].equalsIgnoreCase(newSection))
                        {
                            contains=true;
                            break;
                        }
                    }
                    if(!contains)
                    {
                        if (save.saveClass(newSection)) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    main.DisplayClass();
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Information Dialog");
                                    alert.setHeaderText("Students code to join class on their phones: " + save.getCode());
                                    alert.setContentText("To access a code later, right click on the class in the table.");

                                    alert.showAndWait();

                                    main.AddStage.close();
                                }
                            });
                        }

                    }
                   else
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run()
                            {
                                errorLabel.setText("Class already exists");
                            }
                        });

                    }
                }
            }
        });
        savethread.start();

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
