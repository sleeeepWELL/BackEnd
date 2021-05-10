package project.sleepwell.domain.email;

public class CertificationNumberMismatchException extends RuntimeException {

    public CertificationNumberMismatchException(String message) {
        super(message);
    }
}
