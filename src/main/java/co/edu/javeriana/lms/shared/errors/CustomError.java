package co.edu.javeriana.lms.shared.errors;

public class CustomError extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private ErrorCode code;

    public CustomError(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }  
}
