package ru.petsowner.exception;

public class TooManyLoginAttemptsException extends RuntimeException {
  public TooManyLoginAttemptsException(String msg) {
    super(msg);
  }
}
