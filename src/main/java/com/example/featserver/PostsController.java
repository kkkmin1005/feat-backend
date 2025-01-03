package com.example.featserver;
import java.time.LocalDate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class PostsController {

    private final PostsRepository postsRepository;
    private final S3Service s3Service;
    private final FriendRepository friendRepository;
    private final AlarmsRepository alarmsRepository;

    @PostMapping("/load/dates") // 만약 친구 캘린더를 요청하는 상황이면 userId에 친구 아이디 넣기
    public List<LocalDate> loadPDates(@RequestBody Map<String, String> body) {

        List<Posts> result;
        System.out.println("포스트 겟 요청 들어옴");

        String userId = body.get("userId");

        result = postsRepository.findByUserId(userId);

        List<LocalDate> dates = new ArrayList<>();

        for(Posts p : result) {
            dates.add(p.date);
        }

        return dates;
    }

    @PostMapping("/load/post/bydate")
    public Map<String, String> loadPostsByDate(@RequestBody Map<String, String> body) {
        String date = body.get("date");
        String userId = body.get("userId");

        List<Posts> result = postsRepository.findByUserId(userId);

        Map<String, String> retunResult = new HashMap<>();

        for(Posts p : result) {
            if(p.date.toString().equals(date)) {
                retunResult.put("post" ,p.image);
                retunResult.put("music", p.music);

                return retunResult;
            }
        }
        return null;
    }

    @PostMapping("/upload/post")
    String getURL(@RequestBody Map<String, String> body) {
        String fileName = body.get("fileName");
        String userId = body.get("userId");
        String date = body.get("date");

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String finalFileExtension;

        LocalDate new_date = LocalDate.parse(date);

        if(fileExtension.equals("jpg")){
            finalFileExtension = "image/" + "jpeg";
        }
        else{
            finalFileExtension = "image/" + fileExtension;
        }

        var result = s3Service.createPresignedUrl("test/" + fileName, finalFileExtension);

        var saveResult = result.split("\\?")[0];

        var friend = friendRepository.findByFromUserIdAndIsFriendTrue(userId);

        List<String> toUserIds = new ArrayList<>();

        for(Friend f : friend) {
            toUserIds.add(f.getToUserId());
        }

        if(postsRepository.findByUserIdAndDate(userId, new_date).isEmpty()) {
            // 새로운 포스트 객체 생성 및 저장
            var newPost = new Posts();

            newPost.userId = userId;
            newPost.image = saveResult;
            newPost.date = new_date;

            postsRepository.save(newPost);
        } else {
            // 이미 존재하는 포스트 업데이트
            var optionalPost = postsRepository.findByUserIdAndDate(userId, new_date);

            if (optionalPost.isPresent()) {
                var existingPost = optionalPost.get();
                existingPost.image = saveResult;

                postsRepository.save(existingPost);  // 수정된 포스트를 저장
            }
        }


        for (String toUserId : toUserIds) {
            var newAlarm = new Alarms();

            newAlarm.toUserId = toUserId;
            newAlarm.fromUserId = userId;
            newAlarm.type = "newPosts";

            alarmsRepository.save(newAlarm);
        }

        return result;
    }

    @PostMapping("/load/musics")
    List<String> loadMusics(@RequestBody Map<String, String> body) {

        String userId = body.get("userId");
        String imageUrl = body.get("url");

        String flaskUrl = "http://localhost:5000/ai";
        String jsonData = "{\"url\":\"" + imageUrl + "\"}";

        try(CloseableHttpClient client = HttpClients.createDefault()){
            HttpPost post = new HttpPost(flaskUrl);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(jsonData));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                ObjectMapper objectMapper = new ObjectMapper();
                List<String> result = objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});

                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/select/music")
    ResponseEntity<String> selectMusic(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String date = body.get("date");
        String music = body.get("music");

        LocalDate new_date = LocalDate.parse(date);

        var optionalPost = postsRepository.findByUserIdAndDate(userId, new_date);

        if (optionalPost.isPresent()) {
            var post = optionalPost.get();
            post.music = music;  // 음악 업데이트

            postsRepository.save(post);  // 수정된 포스트 저장

            return ResponseEntity.ok(post.toString());
        } else {
            return ResponseEntity.status(404).body("Post not found for the given userId and date");
        }
    }

    @PostMapping("/load/posts/home")
    List<String> loadPostsHome(@RequestBody Map<String, String> body) {
        System.out.println("요청 들어옴");

        String userId = body.get("userId");

        List<String> HomeImages = new ArrayList<>();

        List<Posts> result = postsRepository.findByUserId(userId);
        for(Posts p : result) {
            HomeImages.add(p.image);
        }

        return HomeImages;
    }
}
