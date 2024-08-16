package pokemon.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotInRepoException extends RuntimeException {
    private String message;

    public NotInRepoException(String msg) {
        this.message = msg;
    }
}
