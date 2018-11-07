package com.ao.desktop.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AddClassController implements Initializable
{

    @FXML
    private TextField ClassNameField;
    @FXML
    private Button AddClassButton;
    private String newSection;

    public void newClass(ActionEvent add)
    {
        newSection = ClassNameField.getText();
        //todo ADD SQL IMPLEMENTATION FOR CLASS SAVING ADDING AND DELETING
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
