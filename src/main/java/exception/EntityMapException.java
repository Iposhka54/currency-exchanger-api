package exception;

public class EntityMapException extends RuntimeException {
  public EntityMapException(String message) {
    super(message);
  }
  public EntityMapException(Throwable cause) {super(cause);}
  public EntityMapException() {super();}
}
