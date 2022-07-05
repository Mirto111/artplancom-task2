package ru.petsowner.config;


import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.petsowner.exception.TooManyLoginAttemptsException;
import ru.petsowner.model.Role;
import ru.petsowner.model.User;
import ru.petsowner.repository.UserRepository;
import ru.petsowner.web.AuthUser;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  public static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories
      .createDelegatingPasswordEncoder();
  private final UserRepository userRepository;
  private final LoginAttemptService loginAttemptService;
  private final HttpServletRequest request;

  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return name -> {
      String ip = getClientIP();
      if (loginAttemptService.isBlocked(ip)) {
        throw new TooManyLoginAttemptsException("Too many login attempts wait an hour");
      }
      Optional<User> optionalUser = userRepository.getByUserName(name);
      if (optionalUser.isEmpty()) {
        throw new UsernameNotFoundException("User " + name + " is not found");
      }

      return new AuthUser(optionalUser.get());
    };
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService())
        .passwordEncoder(PASSWORD_ENCODER);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/api/users/**").permitAll()
        .antMatchers("/api/pets/**").hasRole(Role.USER.name())
        .and().httpBasic()
        .and().csrf().disable();
  }

  private String getClientIP() {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}