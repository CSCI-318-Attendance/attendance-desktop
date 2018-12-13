package com.ao.desktop.controllers;

import com.ao.desktop.data.Student;
import com.ao.desktop.database.SQLManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.smartcardio.*;
import javax.smartcardio.CommandAPDU;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ClassController implements Initializable
{
    @FXML
    private Button returnButton;
    @FXML
    private Button finishButton;
    private boolean done;
    List<Student> students;
    @FXML
    public void finish(ActionEvent e)
    {
        done=true;
    }
    @FXML
    public void Return(ActionEvent r)
    {
        done=true;
        try {
            Parent Main_Screen_Parent = FXMLLoader.load(getClass().getResource("/Main_Screen.fxml"));
            Scene Main_Screen_Scene = new Scene(Main_Screen_Parent);
            Stage Main_Stage = (Stage) ((Node) r.getSource()).getScene().getWindow();
            Main_Stage.setScene(Main_Screen_Scene);
            Main_Stage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private byte[] select_aid = new byte[] {
            (byte) 0x00, //class of command
            (byte) 0xA4, //instruction
            (byte) 0x04, //parameter 1
            (byte) 0x00, //parameter 2
            (byte) 0x07, //length of data
            (byte) 0xF0, (byte) 0x39, (byte) 0x41, (byte) 0x48, (byte) 0x14, (byte) 0x81, (byte) 0x00, (byte) 0x00
            //max length of response
    };

    private byte[] check_id = new byte[] {
            (byte) 0x80, (byte) 0x03, (byte) 0x00, (byte) 0x00
    };

    private long duration;
    private final long THRESHOLD = (1000 * (5 * 60));
    private boolean isLooking;
    private UUID applicationId;

    private void startReader() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            CardTerminals terminals = factory.terminals();
            CardTerminal terminal = terminals.list().get(0);
            duration = 0;
            isLooking = false;

            Thread readThread = new Thread(() -> {
                Timer t = new Timer("NFC Timer");
                t.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if(done)
                        {
                            System.out.println("Finished");
                            cancel();
                        }
                        if (duration != THRESHOLD && !isLooking) {
                            applicationId = readCard(terminal);
                            getID(applicationId);
                        }
                        duration++;
                        System.out.println("Running");
                    }
                }, 0, 1000);
            });
            readThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private UUID readCard(CardTerminal terminal) {
        UUID applicationId = null;
        try {
            isLooking = true;
            terminal.waitForCardPresent(1000 * 60);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Card c = terminal.connect("*");
            CardChannel channel = c.getBasicChannel();
            CommandAPDU command = new CommandAPDU(select_aid);
            ResponseAPDU response = channel.transmit(command);
            byte rec_aid[] = response.getBytes();
            for (byte e : rec_aid) {
                System.out.print(String.format("%02X", e));
            }
            command = new CommandAPDU(check_id);
            response = channel.transmit(command);
            byte[] rec_id = response.getBytes();
            byte[] uuid = new byte[rec_id.length - 4];
            for (byte e : rec_id) {
                System.out.print(String.format("%02X", e));
            }
            System.out.println();
            if (uuid.length >= 0) {
                System.arraycopy(rec_id, 3, uuid, 0, uuid.length);
            }
            applicationId = UUID.fromString(new String(uuid));
            System.out.println(applicationId.toString());
            c.disconnect(true);
            isLooking = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            System.out.println("Card Terminal Issue: " + ex);
            isLooking = false;
        }

        return applicationId;
    }

    public void getID(UUID ApplicationID)
    {
        String id = ApplicationID.toString();
        System.out.println("id : " + id);
        for(int i=0;i<students.size();i++)
        {
            if(id.equals(students.get(i).getStudentId()))
            {
                students.get(i).setPresent(true);
            }
        }

    }
   @FXML
    public void initialize(URL location, ResourceBundle resources)
    {
        students=new ArrayList<>();
        done=false;
        applicationId = null;
        startReader();

        SQLManager manager = new SQLManager();
        if (manager.isInitialize()) {
            students = manager.getStudents();
        }

    }
}
