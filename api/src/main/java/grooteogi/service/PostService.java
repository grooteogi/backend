package grooteogi.service;

import grooteogi.domain.Hashtag;
import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.User;
import grooteogi.dto.ModifyingPostDto;
import grooteogi.dto.PostDto;
import grooteogi.repository.HashtagRepository;
import grooteogi.repository.PostHashtagRepository;
import grooteogi.repository.PostRepository;
import grooteogi.repository.UserRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final PostHashtagRepository postHashtagRepository;
  private final UserRepository userRepository;
  private final HashtagRepository hashtagRepository;

  public List<Post> getAllPost() {
    return this.postRepository.findAll();
  }

  public Post getPostDetail(int postId) {

    //조회수 증가
    Optional<Post> post = this.postRepository.findById(postId);
    post.get().setViews(post.get().getViews() + 1);
    this.postRepository.save(post.get());

    return post.get();
  }

  public Post createPost(PostDto postDto) {
    //변수 정의
    Post createdPost = new Post();
    Optional<User> user = this.userRepository.findById(postDto.getUserId());

    //Post 데이터 처리
    createdPost.setUser(user.get());
    createdPost.setTitle(postDto.getTitle());
    createdPost.setContent(postDto.getContent());
    createdPost.setCreateDate(Timestamp.valueOf(LocalDateTime.now()));
    createdPost.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    createdPost.setImageUrl(postDto.getImageUrl());

    this.postRepository.save(createdPost);


    //PostHashtag 데이터 처리
    for (int i = 0; i < postDto.getHashtagId().length; i++) {
      PostHashtag createdPostHashtag = new PostHashtag();

      Optional<Hashtag> hashtag = this.hashtagRepository.findById(postDto.getHashtagId()[i]);

      hashtag.get().setCount(hashtag.get().getCount() + 1);

      createdPostHashtag.setHashTag(hashtag.get());
      createdPostHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
      createdPostHashtag.setPost(createdPost);

      this.postHashtagRepository.save(createdPostHashtag);
    }

    return createdPost;
  }


  public Post modifyPost(ModifyingPostDto modifyingPostDto) {
    Optional<Post> modifiedPost = this.postRepository.findById(modifyingPostDto.getPostId());

    //Post 데이터 처리
    modifiedPost.get().setTitle(modifyingPostDto.getTitle());
    modifiedPost.get().setContent(modifyingPostDto.getContent());
    modifiedPost.get().setImageUrl(modifyingPostDto.getImageUrl());
    modifiedPost.get().setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));

    //hashtag count - 1
    List<PostHashtag> postHashtag =
        this.postHashtagRepository.findByPostId(modifyingPostDto.getPostId());

    for (int i = 0; i < postHashtag.size(); i++) {
      Hashtag beforeHashtag = modifiedPost.get().getPostHashtags().get(i).getHashTag();
      beforeHashtag.setCount(beforeHashtag.getCount() - 1);
    }
    modifiedPost.get().getPostHashtags().removeAll(postHashtag);

    this.postRepository.save(modifiedPost.get());



    //PostId와 mapping 되는 PostHashtag 삭제
    this.postHashtagRepository.deleteAll(postHashtag);


    //PostHashtag 데이터 처리
    for (int i = 0; i < modifyingPostDto.getHashtagId().length; i++) {
      PostHashtag modifiedPostHashtag = new PostHashtag();

      Optional<Hashtag> newHashtag =
          this.hashtagRepository.findById(modifyingPostDto.getHashtagId()[i]);

      //hashtag count + 1
      newHashtag.get().setCount(newHashtag.get().getCount() + 1);
      modifiedPostHashtag.setHashTag(newHashtag.get());
      modifiedPostHashtag.setRegistered(Timestamp.valueOf(LocalDateTime.now()));
      modifiedPostHashtag.setPost(modifiedPost.get());
      this.postHashtagRepository.save(modifiedPostHashtag);
    }


    return modifiedPost.get();
  }

  public List<Post> deletePost(int postId) {
    Optional<Post> post = this.postRepository.findById(postId);

    //hashtag count - 1
    for (int i = 0; i < post.get().getPostHashtags().size(); i++) {
      Optional<Hashtag> hashtag = this.hashtagRepository.findById(
              post.get().getPostHashtags().get(i).getHashTag().getId());
      hashtag.get().setCount(hashtag.get().getCount() - 1);
    }

    this.postRepository.delete(post.get());


    return this.postRepository.findAll();
  }
}
