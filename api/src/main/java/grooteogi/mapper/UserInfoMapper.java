package grooteogi.mapper;

import grooteogi.domain.User;
import grooteogi.domain.UserInfo;
import grooteogi.dto.ProfileDto;
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
      @Mapping(source = "userInfo.name", target = "name"),
      @Mapping(source = "userInfo.contact", target = "phone"),
      @Mapping(source = "userInfo.address", target = "address"),
      @Mapping(source = "userInfo.imageUrl", target = "imageUrl")
  })
  ProfileDto.Response toResponseDto(User user, UserInfo userInfo);

  @Mappings({
      @Mapping(source = "dto.name", target = "name"),
      @Mapping(source = "dto.imageUrl", target = "imageUrl"),
      @Mapping(source = "dto.address", target = "address"),
      @Mapping(source = "dto.phone", target = "contact"),
      @Mapping(target = "id", ignore = true)
  })
  UserInfo toEntity(ProfileDto.Request dto);

}
