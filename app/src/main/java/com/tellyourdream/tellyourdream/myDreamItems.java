package com.tellyourdream.tellyourdream;

import java.util.Date;

public class myDreamItems {

    /*initialize parameters
    * @Param owner the dream owner*/
    private String owner;
    private String dreamDetails;
    private int age;
    private String maritalStatus;
    private String gender;
    private String dreamTime;
    private String replystatus;
    private String openedstatus;
    private String ownerEmail;
    private String reply;
    private String parentKey ;

    /*constructores*/
    public myDreamItems(){}

    public myDreamItems(String owner, String dreamDetails, int age, String maritalStatus, String gender, String dreamTime, String replystatus, String openedstatus, String ownerEmail, String reply, String parentKey) {
        this.owner = owner;
        this.dreamDetails = dreamDetails;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.gender = gender;
        this.dreamTime = dreamTime;
        this.replystatus = replystatus;
        this.openedstatus = openedstatus;
        this.ownerEmail = ownerEmail;
        this.reply = reply;
        this.parentKey = parentKey;
    }

    /*setters and getters*/

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDreamDetails() {
        return dreamDetails;
    }

    public void setDreamDetails(String dreamDetails) {
        this.dreamDetails = dreamDetails;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDreamTime() {
        return dreamTime;
    }

    public void setDreamTime(String dreamTime) {
        this.dreamTime = dreamTime;
    }

    public String getReplystatus() {
        return replystatus;
    }

    public void setReplystatus(String replystatus) {
        this.replystatus = replystatus;
    }

    public String getOpenedstatus() {
        return openedstatus;
    }

    public void setOpenedstatus(String openedstatus) {
        this.openedstatus = openedstatus;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public void setValues(myDreamItems newDreamItem) {
        owner = newDreamItem.owner;
        dreamDetails = newDreamItem.dreamDetails;
        age = newDreamItem.age;
        maritalStatus = newDreamItem.maritalStatus;
        gender = newDreamItem.gender;
        dreamTime = newDreamItem.dreamTime;
        replystatus = newDreamItem.replystatus;
        openedstatus = newDreamItem.openedstatus;
        ownerEmail = newDreamItem.ownerEmail;
        reply = newDreamItem.reply;
        parentKey = newDreamItem.parentKey;
    }
}
