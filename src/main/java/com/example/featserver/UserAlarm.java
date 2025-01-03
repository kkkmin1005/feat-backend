package com.example.featserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.HashMap;
import java.util.Map;

@Entity
public class UserAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String userId;

    public String friendRequest;
    public String friendAlarm;
    public String entireAlarm;


    // UserAlarm 데이터를 Map으로 변환하는 메서드
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("userId", this.userId);
        map.put("friendRequest", this.friendRequest);
        map.put("freindAlarm", this.friendAlarm);
        map.put("entireAlarm", this.entireAlarm);
        return map;
    }
}
