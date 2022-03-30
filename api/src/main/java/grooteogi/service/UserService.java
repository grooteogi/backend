package grooteogi.service;

import grooteogi.dto.TokenDto;
import grooteogi.domain.User;
import grooteogi.dto.UserDto;
import grooteogi.repository.UserRepository;
import java.util.List;

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
    token.setAccessToken(jwtProvider.generateToken( id, email ));
    token.setRefreshToken(jwtProvider.generateToken( id, email ));

    return token;
  }
}
