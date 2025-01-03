package com.example.featserver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UserInfoRepository userInfoRepository;
    private final UserAlarmRepository userAlarmRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {

        System.out.println("로그인 데이터 받음");

        String userId = body.get("userId");
        String password = body.get("password");

        Optional<UserInfo> result = userInfoRepository.findByUserId(userId);

        if(result.isEmpty()) {
            return ResponseEntity.status(401).body("invalid user");
        }
        else{
            if(result.get().userPassword.equals(password)) {
                return ResponseEntity.status(200).body("success");
            }
            else{
                return ResponseEntity.status(401).body("invalid password");
            }
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {


        try{
            String userId = body.get("userId");
            String password = body.get("password");
            String userEmail = body.get("userEmail");
            String userName = body.get("userName");

            var newUser = new UserInfo();

            newUser.userId = userId;
            newUser.userPassword = password;
            newUser.userEmail = userEmail;
            newUser.userName = userName;

            userInfoRepository.save(newUser);

            var newUserAlarm = new UserAlarm();

            newUserAlarm.userId = userId;
            newUserAlarm.entireAlarm = "off";
            newUserAlarm.friendAlarm = "off";
            newUserAlarm.friendRequest = "off";

            userAlarmRepository.save(newUserAlarm);
            return ResponseEntity.status(200).body("success");
        }
        catch(Exception e){
            return ResponseEntity.status(401).body("fail");
        }
    }


}
