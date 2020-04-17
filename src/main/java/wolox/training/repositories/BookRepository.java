package wolox.training.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import wolox.training.models.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findByAuthor(String author);
}
