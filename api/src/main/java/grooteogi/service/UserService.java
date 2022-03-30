package grooteogi.service;

import grooteogi.domain.EmailCodeRequest;
import grooteogi.domain.EmailRequest;
import grooteogi.domain.User;
import grooteogi.domain.UserDto;
import grooteogi.repository.UserRepository;
import java.util.List;
import java.util.Random;

import grooteogi.utils.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;


@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  public List<User> getAllUser() {
    return userRepository.findAll();
  }
}
