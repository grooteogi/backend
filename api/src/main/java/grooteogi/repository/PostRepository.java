package grooteogi.repository;

import grooteogi.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

  /*
   * 검색어가 존재하고, ID 내림차순으로 정렬
   * */
  @Query(
      value = "SELECT * FROM post WHERE (title LIKE %:title% OR content LIKE %:content%)"
          + "AND id < :id ORDER BY id desc", nativeQuery = true
  )
  List<Post> findBySearchOrderByIdDesc(@Param("title") String title,
      @Param("content") String content,
      @Param("id") Long id, Pageable pageable);

  /*
   * 마지막 id 기준으로 뒤에 더 있는지 여부 확인
   * */
  Boolean existsByIdLessThan(Long id);

  /*
   * 첫 검색이면서 검색어가 존재하고, ID 내림차순으로 정렬
   * */
  List<Post> findByTitleContainingOrContentContainingOrderByIdDesc(String title,
      String content, Pageable page);

  /*
   * 검색어가 없고, ID 내림차순으로 정렬
   * */
  List<Post> findByIdLessThanOrderByIdDesc(Long id, Pageable page);

  /*
   * 첫 검색이면서 검색어가 없고, ID 내림차순으로 정렬
   * */
  List<Post> findAllByOrderByIdDesc(Pageable page);

  /*
   * 첫 검색이면서 검색어가 없고, createDate, id 내림차순으로 정렬
   * */
  List<Post> findAllByOrderByCreateDateDescIdDesc(Pageable page);

  /*
   * 검색어가 없고, createDate, id 내림차순으로 정렬
   * */
  List<Post> findByIdLessThanOrderByCreateDateDescIdDesc(Long id, Pageable page);

  /*
   * 검색어가 있고, createDate, id 내림차순으로 정렬
   * */
  List<Post> findByTitleContainingOrContentContainingOrderByCreateDateDescIdDesc(String title,
      String content, Pageable page);

  /*
   * 검색어가 있고, createDate, id 내림차순으로 정렬
   * */
  @Query(
      value = "SELECT * FROM post WHERE (title LIKE %:title% OR content LIKE %:content%)"
          + "AND id < :id ORDER BY createDate desc id desc", nativeQuery = true
  )
  List<Post> findBySearchOrderByCreateDateDesc(@Param("title") String title,
      @Param("content") String content, @Param("id") Long id, Pageable pageable);

}
