package grooteogi.mapper;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.AuthDto;
import grooteogi.enums.LoginType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BasicMapper<AuthDto, User> {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mappings({
      @Mapping(source = "user.nickname", target = "nickname"),
      @Mapping(source = "userInfo.imageUrl", target = "imageUrl")
  })
  AuthDto.Response toResponseDto(User user, UserInfo userInfo);

  @Mappings({
      @Mapping(source = "dto.email", target = "email"),
      @Mapping(source = "dto.password", target = "password")
  })
  User toEntity(AuthDto.Request dto);

  @Mappings({
      @Mapping(source = "dto.email", target = "email"),
      @Mapping(source = "dto.password", target = "password"),
      @Mapping(source = "general", target = "type"),
      @Mapping(target = "nickname", defaultValue = "groot"),
  })
  User toEntity(AuthDto.Request dto, LoginType general, String nickname);


  @Mappings({
      @Mapping(source = "user.id", target = "id"),
      @Mapping(source = "userInfo.id", target = "userInfo.id"),
      @Mapping(source = "user.nickname", target = "nickname"),
      @Mapping(target = "createAt", ignore = true),
      @Mapping(target = "updateAt", ignore = true)
  })
  User toModify(User user, UserInfo userInfo);
}
