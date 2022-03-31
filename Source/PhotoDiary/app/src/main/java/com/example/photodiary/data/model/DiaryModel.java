package com.example.photodiary.data.model;

public class DiaryModel {
    private int id;
    private String title;
    private String date;
    private String time;
    private String location;
    private String description;
    private String fileName;
    private String imageUri;
    private int userId;

    public DiaryModel() {
    }

    public DiaryModel(int id, String title, String date, String time, String location, String description, String fileName, String imageUri, int userId) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.fileName = fileName;
        this.imageUri = imageUri;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DiaryModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", fileName='" + fileName + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", userId=" + userId +
                '}';
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
