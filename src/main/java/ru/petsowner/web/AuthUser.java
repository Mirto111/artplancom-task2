package ru.petsowner.web;

import static java.util.Objects.requireNonNull;

import java.util.List;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.petsowner.model.User;


@Getter
public class AuthUser extends org.springframework.security.core.userdetails.User {

  private final User user;

  public AuthUser(@NonNull User user) {
    super(user.getUserName(), user.getPassword(), List.of(user.getRole()));
    this.user = user;
  }

  private static AuthUser safeGet() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      return null;
    }
    Object principal = auth.getPrincipal();
    return (principal instanceof AuthUser) ? (AuthUser) principal : null;
  }

  public static AuthUser getAuth() {
    return requireNonNull(safeGet(), "Authorized user not found");
  }

  public long id() {
    return user.getId();
  }
}