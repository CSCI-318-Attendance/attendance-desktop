package com.ao.desktop.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javax.smartcardio.*;
import javax.smartcardio.CommandAPDU;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

public class ClassController implements Initializable
{

    private byte[] selectaid = new byte[] {
            (byte) 0x00, //class of command
            (byte) 0xA4, //instruction
            (byte) 0x04, //parameter 1
            (byte) 0x00, //parameter 2
            (byte) 0x07, //length of data
            (byte) 0xF0, (byte) 0x39, (byte) 0x41, (byte) 0x48, (byte) 0x14, (byte) 0x81, (byte) 0x00, (byte) 0x00
            //max length of response
    };

    private byte[] checkid = new byte[] {
            (byte) 0x80, (byte) 0x03, (byte) 0x00, (byte) 0x00
    };
    public String getDeviceUUID() {
        String applicationId = "";
        TerminalFactory factory = TerminalFactory.getDefault();
        try {
            CardTerminals terminals = factory.terminals();
            CardTerminal terminal = terminals.list().get(0);
            terminal.waitForCardPresent(2000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Card c = terminal.connect("*");
            System.out.println("Card: " + c);
            CardChannel channel = c.getBasicChannel();
            System.out.println("Channel: " + channel);
            CommandAPDU command = new CommandAPDU(selectaid);
            ResponseAPDU response = channel.transmit(command);
            byte recv[] = response.getBytes();
            System.out.println("SelectAID: ");
            for (int i = 0; i < recv.length; i++) {
                System.out.print(String.format("%02X", recv[i]));
            }
            System.out.println();
            System.out.println("Check Id: ");
            command = new CommandAPDU(checkid);
            response = channel.transmit(command);
            byte[] recv4 = response.getBytes();
            byte[] uuid = new byte[recv4.length - 4];
            for (int i = 0; i < recv4.length; i++) {
                System.out.print(String.format("%02X", recv4[i]));
            }
            System.out.println();
            for (int i = 0; i < uuid.length; i++) {
                uuid[i] = recv4[i + 3];
            }
            applicationId = new String(uuid);
            System.out.println(new String(uuid));
            c.disconnect(true);
        } catch (CardException ex) {
            System.out.println("Card Terminal Issue: " + ex);
        }
        return applicationId;
    }


   @FXML
    public void initialize(URL location, ResourceBundle resources)
    {
        String id = getDeviceUUID();
        System.out.println("id");

    }
}
