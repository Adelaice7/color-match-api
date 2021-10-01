package com.rmeunier.colormatchapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "id", length = 20, nullable = false)
    private String id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "gender_id", columnDefinition = "varchar(3)")
    private GenderId genderId;

    @Column(length = 20)
    private String composition;

    @Column(length = 20)
    private String sleeve;

    @Column(name = "photo", length = 150)
    private String path;

    @Column(length = 200)
    private String url;

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
        this.path = path;
        this.url = url;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
