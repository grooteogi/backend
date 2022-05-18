package grooteogi.mapper;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.AuthDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends BasicMapper<AuthDto, User> {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mappings({
      @Mapping(source = "dto.email", target = "email"),
      @Mapping(source = "dto.password", target = "password")
  })
  User toEntity(AuthDto.Request dto);

  @Mappings({
      @Mapping(source = "user.nickname", target = "nickname"),
      @Mapping(source = "userInfo.imageUrl", target = "imageUrl")
  })
  AuthDto.Response toResponseDto(User user, UserInfo userInfo);

}
