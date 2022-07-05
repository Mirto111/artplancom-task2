package ru.petsowner.web;

import static ru.petsowner.config.WebSecurityConfig.PASSWORD_ENCODER;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.petsowner.exception.IllegalRequestDataException;
import ru.petsowner.model.User;
import ru.petsowner.repository.UserRepository;

@RestController
@RequestMapping(value = UserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UserController {

  static final String REST_URL = "/api/users";

  private final UserRepository repository;
  private final AuthenticationManager authenticationManager;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<User> register(@Valid @RequestBody User user, HttpServletRequest request) {
    if (user.getId() != null) {
      throw new IllegalRequestDataException("User" + " must be new (id=null)");
    }
    if (repository.getByUserName(user.getUserName()).isPresent()) {
      throw new IllegalRequestDataException(
          "User with username " + user.getUserName() + " already exists");
    }
    String pass = user.getPassword();
    user.setPassword(PASSWORD_ENCODER.encode(user.getPassword()));
    User created = repository.save(user);
    URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(REST_URL + "/{id}")
        .buildAndExpand(created.getId()).toUri();
    authenticateUserAndSetSession(user.getUserName(), pass, request);
    return ResponseEntity.created(uriOfNewResource).body(created);
  }

  @GetMapping("/check-username")
  public boolean checkUserName(@RequestParam String userName) {
    return repository.getByUserName(userName).isPresent();
  }

  private void authenticateUserAndSetSession(String username, String password,
      HttpServletRequest request) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
        password);
    request.getSession();
    token.setDetails(new WebAuthenticationDetails(request));
    Authentication authenticatedUser = authenticationManager.authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
  }

}
