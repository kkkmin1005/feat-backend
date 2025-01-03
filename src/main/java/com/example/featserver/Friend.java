package com.example.featserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Friend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String fromUserId;
    public String fromUserName;
    public String toUserId;
    public String toUserName;
    public Boolean isFriend;

}
