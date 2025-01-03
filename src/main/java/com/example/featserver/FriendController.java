package com.example.featserver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendRepository friendRepository;
    private final AlarmsRepository alarmsRepository;
    private final UserInfoRepository userInfoRepository;

    @PostMapping("/edit/friendRequest")
    @Transactional
    public ResponseEntity<String> acceptFriendRequest(@RequestBody Map<String, String> body){

        String fromUserId = body.get("fromUserId");
        String toUserId = body.get("toUserId");
        String isFriend = body.get("isFriend");
        Integer id = Integer.valueOf(body.get("id"));

        boolean isFrinedResult;

        var reverseFriend = new Friend();

        if(isFriend.equals("true")) {
            isFrinedResult = true;

            String fromNames = userInfoRepository.findByUserId(fromUserId).get().userName;
            String toNames = userInfoRepository.findByUserId(toUserId).get().userName;

            reverseFriend.fromUserId = toUserId;
            reverseFriend.toUserId = fromUserId;
            reverseFriend.isFriend = true;
            reverseFriend.fromUserName = fromNames;
            reverseFriend.toUserName = toNames;

            friendRepository.save(reverseFriend);
        }
        else{
            isFrinedResult = false;
        }

        List<Friend> result = friendRepository.findByToUserId(toUserId);

        for (Friend friend : result) {
            if(friend.fromUserId.equals(fromUserId)){
                friend.isFriend = isFrinedResult;

                friendRepository.save(friend);

                if (alarmsRepository.existsById(id)) {
                    alarmsRepository.deleteById(id);
                } else {
                    System.out.println("못찾음");
                }

                return ResponseEntity.ok("Friend request accepted");
            }
        }

        return ResponseEntity.status(401).body("fail");
    }

    @PostMapping("/request/friend")
    public ResponseEntity<String> requestFriend(@RequestBody Map<String, String> body){

        String fromUserId = body.get("userId");
        String toUserId = body.get("toUserId");

        Optional<UserInfo> fromUser = userInfoRepository.findByUserId(fromUserId);
        Optional<UserInfo> toUser = userInfoRepository.findByUserId(toUserId);

        String fromUserName = fromUser.get().userName;
        String toUserName = toUser.get().userName;

        var newFriendRequest = new Friend();
        var newFriendRequestAlarm = new Alarms();

        newFriendRequest.fromUserName = fromUserName;
        newFriendRequest.toUserName = toUserName;
        newFriendRequest.fromUserId = fromUserId;
        newFriendRequest.toUserId = toUserId;
        newFriendRequest.isFriend = false;

        newFriendRequestAlarm.fromUserId = fromUserId;
        newFriendRequestAlarm.toUserId = toUserId;
        newFriendRequestAlarm.type = "friendRequest";

        friendRepository.save(newFriendRequest);
        alarmsRepository.save(newFriendRequestAlarm);

        return ResponseEntity.ok("Friend request request accepted");
    }

    @PostMapping("search/friends")
    public List<Map<String, String>> searchFriends(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String name = body.get("name");

        List<Map<String, String>> returnList = new ArrayList<>();

        // userInfoRepository에서 이름으로 검색
        List<UserInfo> result = userInfoRepository.findAllByUserName(name);

        // 검색된 결과 리스트를 돌면서 userId와 userName을 반환 리스트에 추가
        for (UserInfo userInfo : result) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("userId", userInfo.userId);
            userMap.put("userName", userInfo.userName);
            userMap.put("userProfile", userInfo.profileImageUrl);
            returnList.add(userMap);
        }

        return returnList;
    }


    @PostMapping("load/friends")
    public List<Map<String, String>> loadFriends(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");

        System.out.println("서버로 친구 로드 요청 보냄");


        var result = friendRepository.findByFromUserIdAndIsFriendTrue(userId);
        List<Map<String, String>> returnList = new ArrayList<>();

        for (Friend friend : result) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("userId", friend.toUserId);
            userMap.put("userName", friend.fromUserName);
            userMap.put("userProfile", userInfoRepository.findByUserId(friend.toUserId).get().profileImageUrl);
            returnList.add(userMap);
        }

        return returnList;

    }



}
