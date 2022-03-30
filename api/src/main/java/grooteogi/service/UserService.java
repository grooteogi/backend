package grooteogi.service;

import grooteogi.dto.TokenDto;
import grooteogi.domain.User;
import grooteogi.dto.UserDto;
import grooteogi.repository.UserRepository;
import java.util.List;
import java.util.Map;

import grooteogi.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;
  public List<User> getAllUser() {
    return userRepository.findAll();
  }

  public TokenDto login(UserDto userDto ){

    // 알아서 로그인 처리
    return generateToken( 1, userDto.getEmail() );
  }

  private TokenDto generateToken( int id, String email ){
    TokenDto token = new TokenDto();
    token.setAccessToken(jwtProvider.generateAccessToken( id, email ));
    token.setRefreshToken(jwtProvider.generateRefreshToken( id, email ));

    return token;
  }

  // 추후 private 으로 변경 필요
  public Map verify( String authorizationHeader ) {
    return jwtProvider.verifyToken( authorizationHeader );
  }
}
