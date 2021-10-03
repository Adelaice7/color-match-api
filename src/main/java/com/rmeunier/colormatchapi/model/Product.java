package com.rmeunier.colormatchapi.model;

import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @NotNull
    @Column(name = "id", length = 20, nullable = false)
    private String id;

    @NotNull
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_id", columnDefinition = "varchar(3)")
    private GenderId genderId;

    @Column(length = 20)
    private String composition;

    @Column(length = 20)
    private String sleeve;

    @Column(name = "photo", length = 150)
    private String photo;

    @Column(length = 200)
    private String url;

    @Column(name = "dominant_color")
    private String dominantColor;

    public Product() {
        // empty
    }

    public Product(String id, String title, GenderId genderId,
                   String composition, String sleeve, String path, String url) {
        this.id = id;
        this.title = title;
        this.genderId = genderId;
        this.composition = composition;
        this.sleeve = sleeve;
        this.photo = path;
        this.url = url;
    }

    public Product(String id, String title, GenderId genderId,
                   String composition, String sleeve, String path, String url, String dominantColor) {
        this.id = id;
        this.title = title;
        this.genderId = genderId;
        this.composition = composition;
        this.sleeve = sleeve;
        this.photo = path;
        this.url = url;
        this.dominantColor = dominantColor;
    }

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

    public void setPhoto(String path) {
        this.photo = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDominantColor() {
        return dominantColor;
    }

    public void setDominantColor(String dominantColor) {
        this.dominantColor = dominantColor;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", genderId=" + genderId +
                ", composition='" + composition + '\'' +
                ", sleeve='" + sleeve + '\'' +
                ", path='" + photo + '\'' +
                ", url='" + url + '\'' +
                ", dominantColor='" + dominantColor + '\'' +
                '}';
    }
}
