package com.rmeunier.colormatchapi.model;

public class Product {
    private String id;
    private String title;
    private GenderId genderId;
    private String composition;
    private String sleeve;
    private String photo;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GenderId getGenderId() {
        return genderId;
    }

    public void setGenderId(GenderId genderId) {
        this.genderId = genderId;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public String getSleeve() {
        return sleeve;
    }

    public void setSleeve(String sleeve) {
        this.sleeve = sleeve;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
