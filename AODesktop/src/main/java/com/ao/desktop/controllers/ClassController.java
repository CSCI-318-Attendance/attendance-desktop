package com.ao.desktop.controllers;

import com.ao.desktop.data.Student;
import com.ao.desktop.database.SQLManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.smartcardio.*;
import javax.smartcardio.CommandAPDU;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClassController implements Initializable
{
    String className;
    Student s = new Student();
    MainController main= new MainController();
    Date date = new Date();
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH.mm.ssZ");
    String fdate = format.format(date);
    private String path=System.getProperty("user.home") + "/desktop/attendance-overview";
    private String fileName;
    private String newline="/n";
    @FXML
    private TextArea outputArea;
    @FXML
    private Button returnButton;
    @FXML
    private Button finishButton;
    private boolean done;
    List<Student> students;
    private Thread readThread;
    private Thread innerThread;

    public void setClassName(String name)
    {
        className = name;
        fileName = className + " " + fdate + ".txt";

    }
    @FXML
    public void finish(ActionEvent e)
    {
        close();
        done=true;
        fileWriter();

    }
    @FXML
    public void Return(ActionEvent r)
    {
        try {
            close();
            done=true;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main_Screen.fxml"));
            Parent Main_Screen_Parent = loader.load();
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
            innerThread = new Thread();
            readThread = new Thread(() -> {
                Timer t = new Timer("NFC Timer");
                t.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if(done)
                        {
                            cancel();

                            return;
                        } else {
                            if (duration != THRESHOLD && !isLooking) {
                                innerThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        applicationId = readCard(terminal);
                                        if (applicationId != null) {
                                            getID(applicationId);
                                        }
                                    }
                                });
                                innerThread.start();
                            }
                        }
                        duration++;
                        System.out.println("Still running...");
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
            if (done) {
                return null;
            }
            isLooking = true;
            terminal.waitForCardPresent(1000);
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
            for (byte e : rec_id) {
                System.out.print(String.format("%02X", e));
            }
            byte[] bytes = new byte[rec_id.length];
            if (rec_id.length - 2 >= 0) {
                System.arraycopy(rec_id, 2, bytes, 0, rec_id.length - 2);
            }
            System.out.println();
            String uid = new String(bytes).trim();
            System.out.println(uid);
            applicationId = UUID.fromString(uid);
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
            s = students.get(i);
            if(id.equals(s.getDeviceID()))
            {
                s.setPresent(true);
                Platform.runLater(() -> {
                    if (!outputArea.getText().contains(s.getStudentId())) {
                        outputArea.appendText(s.getName() + " ID:" + s.getStudentId());
                        outputArea.appendText(System.lineSeparator());
                    }
                });
            }
            students.set(i, s);
        }

    }
   @FXML
    public void initialize(URL location, ResourceBundle resources)
    {
        outputArea.appendText("Present Students:");
        outputArea.appendText(System.lineSeparator());
        students=new ArrayList<>();
        done=false;
        applicationId = null;
        SQLManager manager = new SQLManager();
        if (manager.isInitialize()) {
            students = manager.getStudents();
        }
        startReader();
    }

    public void fileWriter()
    {
        File pathF = new File(path);
        if (!pathF.exists()) {
            if (!pathF.mkdir()) {
                return;
            }
        }
        System.out.println(fileName);
        File file=new File(path,fileName);
        try
        {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return;
                }
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("Student Attendance Roster");
            bw.write(System.lineSeparator());
            for(int i=0; i<students.size(); i++)
            {
                s=students.get(i);
                if(s.getPresent()) {
                    bw.write(s.getName() + " ID: " + s.getStudentId() + " status: Present");
                    bw.write(System.lineSeparator());
                }
                else
                {
                    bw.write(s.getName() + " ID: " + s.getStudentId() + " status: Absent");
                    bw.write(System.lineSeparator());
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        done = true;
        try {
            if (readThread != null) {
                readThread = null;
            }
            if (innerThread != null) {
                innerThread = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
