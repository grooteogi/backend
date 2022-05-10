package grooteogi;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import grooteogi.domain.Hashtag;
import grooteogi.domain.Post;
import grooteogi.domain.PostHashtag;
import grooteogi.domain.Schedule;
import grooteogi.dto.ScheduleDto;
import grooteogi.enums.CreditType;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

public interface ApiDocumentUtils {

  static OperationRequestPreprocessor getDocumentRequest() {
    return preprocessRequest(modifyUris().scheme("https").host("docs.api.com").removePort(),
        prettyPrint());
  }

  static OperationResponsePreprocessor getDocumentResponse() {
    return preprocessResponse(prettyPrint());
  }

  static Schedule getSchedule() {
    return Schedule.builder()
        .date(Date.valueOf("2022-05-07"))
        .startTime(Time.valueOf("11:00:00"))
        .endTime(Time.valueOf("12:00:00"))
        .region("서대문구")
        .place("명지대")
        .build();
  }

  static List<Schedule> getSchedules() {
    List<Schedule> schedules = new ArrayList<>();
    schedules.add(getSchedule());
    return schedules;
  }

  static List<PostHashtag> getPostHashtags() {
    List<PostHashtag> tags = new ArrayList<>();
    PostHashtag tag = PostHashtag.builder()
        .hashTag(Hashtag.builder().tag("해시태그").build())
        .build();
    tags.add(tag);
    return tags;
  }

  static Post getPost() {
    return Post.builder()
        .content("내용이다.")
        .title("제목이다")
        .credit(CreditType.DIRECT)
        .imageUrl("이미지 주소다")
        .postHashtags(getPostHashtags())
        .schedules(getSchedules())
        .build();
  }

  static String[] getPostHashtagStrings() {
    return new String[] {"해시태그", "문자열"};
  }

  static ScheduleDto.Request getScheduleReq() {
    return ScheduleDto.Request.builder()
        .date("2022-05-07")
        .startTime("11:00:00")
        .endTime("12:00:00")
        .region("서대문구")
        .place("명지대")
        .build();
  }

  static List<ScheduleDto.Request> getScheduleReqs() {
    List<ScheduleDto.Request> schedules = new ArrayList<>();
    schedules.add(getScheduleReq());
    return schedules;
  }
}
