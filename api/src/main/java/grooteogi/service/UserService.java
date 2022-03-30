package grooteogi.service;

import grooteogi.domain.User;
import grooteogi.repository.UserRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  public List<User> getAllUser() {
    return userRepository.findAll();
  }
}
