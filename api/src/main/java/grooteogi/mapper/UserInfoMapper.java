package grooteogi.mapper;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.ProfileDto;
import grooteogi.dto.ProfileDto.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserInfoMapper extends BasicMapper<ProfileDto, UserInfo> {

  UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

  @Mappings({
      @Mapping(source = "user.nickname", target = "nickname"),
      @Mapping(source = "user.email", target = "email"),
      @Mapping(source = "userInfo.name", target = "name", defaultValue = ""),
      @Mapping(source = "userInfo.contact", target = "phone", defaultValue = ""),
      @Mapping(source = "userInfo.address", target = "address", defaultValue = ""),
      @Mapping(source = "userInfo.imageUrl", target = "imageUrl", defaultValue = "")
  })
  ProfileDto.Response toResponseDto(User user, UserInfo userInfo);

  @Mappings({
      @Mapping(source = "dto.name", target = "name"),
      @Mapping(source = "dto.imageUrl", target = "imageUrl"),
      @Mapping(source = "dto.address", target = "address"),
      @Mapping(source = "dto.phone", target = "contact"),
      @Mapping(target = "id", source = "userInfo.id")
  })
  UserInfo toEntity(Request dto, UserInfo userInfo);

}
