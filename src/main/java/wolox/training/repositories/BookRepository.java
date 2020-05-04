package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wolox.training.models.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findByAuthor(String author);

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE (:publisher IS NULL OR b.publisher = :publisher) AND "
        + "(:genre IS NULL OR b.genre = :genre) AND (:year IS NULL OR b.year = :year)")
    List<Book> findByPublisherAndGenreAndYear(
        @Param("publisher") String publisher,
        @Param("genre") String genre,
        @Param("year") String year);

    @Query("SELECT b FROM Book b WHERE "
        + "(:genre IS NULL OR b.genre = :genre) AND "
        + "(:author IS NULL OR b.author = :author) AND"
        + "(:image IS NULL OR b.image = :image) AND"
        + "(:title IS NULL OR b.title = :title) AND"
        + "(:subtitle IS NULL OR b.subtitle = :subtitle) AND"
        + "(:publisher IS NULL OR b.publisher = :publisher) AND"
        + "(:year IS NULL OR b.year = :year) AND"
        + "(:pages IS NULL OR b.pages = :pages) AND"
        + "(:isbn IS NULL OR b.isbn = :isbn)")
    Page<Book> findAllByFilter(
        @Param("genre") String genre,
        @Param("author") String author,
        @Param("image") String image,
        @Param("title") String title,
        @Param("subtitle") String subtitle,
        @Param("publisher") String publisher,
        @Param("year") String year,
        @Param("pages") Integer pages,
        @Param("isbn") String isbn,
        Pageable pageable);
}
