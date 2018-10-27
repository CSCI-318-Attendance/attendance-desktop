package com.ao.desktop.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Node;


public class Controller implements Initializable
{
    @FXML
    private Button signInButton;
    @FXML
    private TextField UserNameInput;
    @FXML
    private TextField PasswordInput;
    @FXML
    private Label errorSignIn;

    private String tempUser;
    private String tempPass;
    @FXML
    public void signIn(ActionEvent e)
    {
         String teachUser = "Wenjia";
         String teachPass = "Li";
        tempUser= UserNameInput.getText();
        tempPass = PasswordInput.getText();

        if(!tempUser.equals(teachUser) || !tempPass.equals(teachPass))
        {
            errorSignIn.setText("Wrong username or password");
        }
        else
        {
            try {
                Parent Main_Screen_Parent = FXMLLoader.load(getClass().getResource("/Main_Screen.fxml"));
                Scene Main_Screen_Scene = new Scene(Main_Screen_Parent);
                Stage Main_Stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                Main_Stage.setScene(Main_Screen_Scene);
                Main_Stage.show();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

    }
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }





}
