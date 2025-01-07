package exception;

public class BadParameterException extends RuntimeException {
    public BadParameterException(String message) {
        super(message);
    }
    public BadParameterException(Throwable cause) {super(cause);}
    public BadParameterException() {super();}
}
