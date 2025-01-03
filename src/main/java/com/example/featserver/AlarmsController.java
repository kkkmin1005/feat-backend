package com.example.featserver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AlarmsController {

    private final AlarmsRepository alarmsRepository;
    private final UserInfoRepository userInfoRepository;

    @PostMapping("/load/alarms")
    public List<Map<String, Object>> LoadAlarms(@RequestBody Map<String, String> body) {
        List<Map<String, Object>> alarms = new ArrayList<>();

        var userId = body.get("userId");

        var alarm = alarmsRepository.findByToUserId(userId);

        for (Alarms a : alarm) {
            Map<String, Object> alarmMap = a.toMap();
            alarmMap.put("fromUserName", userInfoRepository.findByUserId(a.fromUserId).get().userName);
            alarmMap.put("toUserName", userInfoRepository.findByUserId(a.toUserId).get().userId);
            alarmMap.put("fromUserProfile", userInfoRepository.findByUserId(a.fromUserId).get().profileImageUrl);
            alarms.add(alarmMap);
        }
        return alarms;
    }

    @PostMapping("/delete/alarm")
    public ResponseEntity<String> deleteAlarm(@RequestBody Map<String, String> body) {

        Integer alarmId = Integer.valueOf(body.get("alarmId"));

        var result = alarmsRepository.findById(alarmId);

        result.ifPresent(alarmsRepository::delete);

        return ResponseEntity.ok("OK");
    }
}
