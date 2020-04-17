package wolox.training.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The book is already associated with the collection")
public class BookAlreadyOwnedException extends RuntimeException {

}
