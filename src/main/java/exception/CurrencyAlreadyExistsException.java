package exception;

public class CurrencyAlreadyExistsException extends RuntimeException {
    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
    public CurrencyAlreadyExistsException(Throwable cause) {super(cause);}
}
