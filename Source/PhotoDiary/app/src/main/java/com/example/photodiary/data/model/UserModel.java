package com.example.photodiary.data.model;

import java.util.Date;

public class UserModel {
    private int id;
    private String email;
    private String name;
    private String password;
    private String gender;
    private Date dob;
    private String profilePhotoPath;

    public UserModel() {}

    public UserModel(int id, String email, String name, String password, String gender, Date dob, String profilePhotoPath) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.dob = dob;
        this.profilePhotoPath = profilePhotoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", dob=" + dob +
                ", profilePhotoPath='" + profilePhotoPath + '\'' +
                '}';
    }
}
