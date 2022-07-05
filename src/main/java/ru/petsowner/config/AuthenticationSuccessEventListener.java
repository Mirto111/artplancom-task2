package ru.petsowner.config;

import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AuthenticationSuccessEventListener implements
    ApplicationListener<AuthenticationSuccessEvent> {

  private final HttpServletRequest request;
  private final LoginAttemptService loginAttemptService;

  @Override
  public void onApplicationEvent(final AuthenticationSuccessEvent e) {
    final String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      loginAttemptService.loginSucceeded(request.getRemoteAddr());
    } else {
      loginAttemptService.loginSucceeded(xfHeader.split(",")[0]);
    }
  }

}
