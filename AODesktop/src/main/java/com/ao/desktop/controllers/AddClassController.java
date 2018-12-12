package com.ao.desktop.controllers;

import com.ao.desktop.database.SQLManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
                    save.saveClass(newSection);
                }
            }
        });
        savethread.start();
        main.DisplayClass();
        main.AddStage.close();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
