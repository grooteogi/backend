package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.dto.PwDto;
import grooteogi.exception.ApiException;
import grooteogi.exception.ApiExceptionEnum;
import grooteogi.repository.UserRepository;
import grooteogi.utils.Validator;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PwService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final Validator validator;

  public void modifyUserPw(Integer userId, PwDto pwDto) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      throw new ApiException(ApiExceptionEnum.USER_NOT_FOUND_EXCEPTION);
    }

    if (passwordEncoder.matches(pwDto.getPassword(), user.get().getPassword())) {
      throw new ApiException(ApiExceptionEnum.BAD_REQUEST_EXCEPTION);
    }

    validator.confirmPasswordVerification(pwDto.getPassword());
    user.get().setPassword(passwordEncoder.encode(pwDto.getPassword()));
    user.get().setModified(Timestamp.valueOf(LocalDateTime.now()));
    userRepository.save(user.get());
  }
}
