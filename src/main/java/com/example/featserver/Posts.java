package com.example.featserver;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Posts {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String userId;
    public LocalDate date;

    @Column(columnDefinition = "TEXT")
    public String image;
    public String music;

}
