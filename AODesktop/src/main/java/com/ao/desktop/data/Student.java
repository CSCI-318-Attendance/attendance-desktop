package com.ao.desktop.data;

public class Student {

    private String studentId;
    private String name;
    private boolean present;
    private String deviceID;

    public Student(String studentId, String name,String deviceID)
    {
        this.deviceID=deviceID;
        this.studentId = studentId;
        this.name = name;
        present=false;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public boolean getPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getDeviceID() {return deviceID;}


}
