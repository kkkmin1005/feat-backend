package com.example.featserver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class UserAlarmController {
    private final UserAlarmRepository userAlarmRepository;
    private final FriendRepository friendRepository;
    private final UserInfoRepository userInfoRepository;

    @PostMapping("/load/alarmSetting")
    public Map<String, Object> loadAlarm(@RequestBody Map<String, String> body) {

        String userId = body.get("userId");

        Optional<UserAlarm> result = userAlarmRepository.findUserAlarmByUserId(userId);

        if(result.isPresent()) {
            return result.get().toMap();
        }
        return null;
    }

    @PostMapping("/edit/entire/alarm")
    public ResponseEntity<String> editEntireAlarm(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");

        Optional<UserAlarm> result = userAlarmRepository.findUserAlarmByUserId(userId);

        if(result.isPresent()) {

            if (result.get().entireAlarm.equals("on")){
                result.get().entireAlarm = "off";

                userAlarmRepository.save(result.get());
                return ResponseEntity.status(200).body("ok");
            }
            else{
                result.get().entireAlarm = "on";

                userAlarmRepository.save(result.get());
                return ResponseEntity.status(200).body("ok");
            }
        }
        return ResponseEntity.status(401).body("bad");
    }

    @PostMapping("/edit/friend/alarm")
    public ResponseEntity<String> editFriendAlarm(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");

        Optional<UserAlarm> result = userAlarmRepository.findUserAlarmByUserId(userId);

        if(result.isPresent()) {

            if (result.get().friendAlarm.equals("on")){
                result.get().friendAlarm = "off";

                userAlarmRepository.save(result.get());
                return ResponseEntity.status(200).body("ok");
            }
            else{
                result.get().friendAlarm = "on";

                userAlarmRepository.save(result.get());
                return ResponseEntity.status(200).body("ok");
            }
        }
        return ResponseEntity.status(401).body("bad");
    }

    @PostMapping("/edit/friend/request")
    public ResponseEntity<String> editFriendRequest(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");

        Optional<UserAlarm> result = userAlarmRepository.findUserAlarmByUserId(userId);

        if(result.isPresent()) {

            if (result.get().friendRequest.equals("on")){
                result.get().friendRequest = "off";

                userAlarmRepository.save(result.get());
                return ResponseEntity.status(200).body("ok");
            }
            else{
                result.get().friendRequest = "on";

                userAlarmRepository.save(result.get());
                return ResponseEntity.status(200).body("ok");
            }
        }
        return ResponseEntity.status(401).body("bad");
    }

    @PostMapping("/load/friendRequestAlarm")
    public List<Map<String, Object>> loadFriendRequests(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");

        // 친구 요청 데이터를 조회
        List<Friend> friendRequests = friendRepository.findByToUserIdAndIsFriendFalse(userId);

        // fromUserId 리스트 생성
        List<String> fromUserIds = friendRequests.stream()
                .map(Friend::getFromUserId)
                .collect(Collectors.toList());

        // fromUSerId 이용해서 해당 유저의 이름, 프로필을 리스트에 담아서 반환
        List<Map<String, Object>> result = new ArrayList<>();

        for(String id : fromUserIds){
            UserInfo search = userInfoRepository.findByUserId(id).orElse(null);
            if (search != null) {
                result.add(search.toMap());
            }
        }
        return result;
    }
}
