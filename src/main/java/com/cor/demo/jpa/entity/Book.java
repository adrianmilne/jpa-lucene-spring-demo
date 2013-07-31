package com.cor.demo.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 * Book JPA Entity.
 */
@Entity
@Indexed
public class Book {

    @Id
    @GeneratedValue
    private Long id;
    
    @Field
    @Boost(value = 1.5f)
    private String title;
    
    @Field
    @Lob
    private String description;
    
    @Field
    @Enumerated(EnumType.STRING)
    private BookCategory category;
    
    public Book(){
        
    }
    
    public Book(String title, BookCategory category, String description){
        this.title = title;
        this.category = category;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BookCategory getCategory() {
        return category;
    }

    public void setCategory(BookCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", title=" + title + ", description=" + description + ", category=" + category + "]";
    }

}
