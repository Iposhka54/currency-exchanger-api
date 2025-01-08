package exception;

public class ExchangeRateAlreadyExistsException extends RuntimeException {
  public ExchangeRateAlreadyExistsException(String message) {super(message);}
  public ExchangeRateAlreadyExistsException(Throwable cause) {super(cause);}
}
