package com.ao.desktop.controllers;

import com.ao.desktop.database.SQLManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
                    List<String> list= save.getClasses();
                    boolean contains=false;
                    for(int i=0; i<list.size(); i++)
                    {
                        if(list.get(i).equalsIgnoreCase(newSection))
                        {
                            contains=true;
                            break;
                        }
                    }
                    if(!contains)
                    {
                        save.saveClass(newSection);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run()
                            {
                                main.DisplayClass();
                                main.AddStage.close();
                            }
                        });

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
