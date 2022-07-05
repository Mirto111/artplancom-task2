package ru.petsowner.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LoginAttemptService {

  private final LoadingCache<String, Integer> attemptsCache;

  public LoginAttemptService() {
    super();
    attemptsCache = CacheBuilder.newBuilder().
        expireAfterWrite(1, TimeUnit.HOURS).build(new CacheLoader<>() {
      public Integer load(String key) {
        return 0;
      }
    });
  }

  public void loginSucceeded(String key) {
    attemptsCache.invalidate(key);
  }

  public void loginFailed(String key) {
    int attempts = 0;
    try {
      attempts = attemptsCache.get(key);
    } catch (ExecutionException e) {
      attempts = 0;
    }
    attempts++;
    attemptsCache.put(key, attempts);
  }

  public boolean isBlocked(String ip) {
    try {
      int MAX_ATTEMPT = 10;
      return attemptsCache.get(ip) >= MAX_ATTEMPT;
    } catch (ExecutionException e) {
      return false;
    }
  }

}
