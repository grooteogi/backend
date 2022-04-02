//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package grooteogi.controller;

import grooteogi.domain.UserHashtag;
import grooteogi.service.UserHashtagService;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserHashtagController {
    private final UserHashtagService userHashtagService;



    @PostMapping("/user/hashtag")
    //저장하고 해당 유저의 전체 hashtag list 확인
    public ResponseEntity<List<UserHashtag>> saveUserHashtag(@RequestParam int userId, int[] hashtagId) {
        List<UserHashtag> saveUserHashtaglist = this.userHashtagService.saveUserHashtag(userId, hashtagId);
        return ResponseEntity.ok(saveUserHashtaglist);
    }

    @DeleteMapping("/user/hashtag")
    //저장하고 해당 유저의 전체 hashtag list 확인
    public ResponseEntity<List<UserHashtag>> deleteUserHashtag(@RequestParam int userId, int[] hashtagId) {
        List<UserHashtag> DeleteUserHashtaglist = this.userHashtagService.deleteUserHashtag(userId, hashtagId);
        return ResponseEntity.ok(DeleteUserHashtaglist);
    }

    @GetMapping("/user/hashtag")
    public List<UserHashtag> getAllUserHashtag() {
        return this.userHashtagService.getAllUserHashtag();
    }

    @GetMapping("/user/{userId}/hashtag")
    public ResponseEntity<List<UserHashtag>> getUserHashtag(@PathVariable int userId) {
        return ResponseEntity.ok(this.userHashtagService.getUserHashtag(userId));
    }

}
