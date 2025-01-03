package com.example.featserver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoRepository userInfoRepository;
    private final S3Service s3Service;

    @PostMapping("/load/userInfo")
    public Map<String, Object> loadUserInfo(@RequestBody Map<String, String> body){

        String userId = body.get("userId");

        Optional<UserInfo> result = userInfoRepository.findByUserId(userId);

        return result.map(UserInfo::toMap).orElse(null);
    }

    @PostMapping("/upload/profile")
    public String uploadProfile(@RequestBody Map<String, String> body){
        String userId = body.get("userId");
        String fileName = body.get("fileName");

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String finalFileExtension;

        Optional<UserInfo> result = userInfoRepository.findByUserId(userId);

        System.out.println("image/" + fileExtension);

        if(fileExtension.equals("jpg")){
            finalFileExtension = "image/" + "jpeg";
        }
        else{
            finalFileExtension = "image/" + fileExtension;
        }

        var profile = s3Service.createPresignedUrl("test/" + fileName, finalFileExtension);

        var saveProfile = profile.split("\\?")[0];

        result.get().profileImageUrl = saveProfile;

        userInfoRepository.save(result.get());

        return profile;
    }

}
