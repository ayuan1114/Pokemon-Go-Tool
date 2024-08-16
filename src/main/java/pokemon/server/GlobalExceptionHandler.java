package pokemon.server;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody ErrorResponse handleNoSuchElement(NoSuchElementException e) {
		return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

	@ExceptionHandler(value = NotInRepoException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody ErrorResponse handleNotInRepo(NotInRepoException e) {
		return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}
}
