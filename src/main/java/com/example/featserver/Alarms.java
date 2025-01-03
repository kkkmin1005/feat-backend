package com.example.featserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Alarms {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String type;

    public String fromUserId;

    public String toUserId;

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("type", type);
        map.put("fromUserId", fromUserId);
        map.put("toUserId", toUserId);
        return map;
    }
}
