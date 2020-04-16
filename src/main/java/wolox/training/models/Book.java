package wolox.training.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Entity
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_generator")
    @SequenceGenerator(name = "book_generator", sequenceName = "book_seq")
    @Setter(AccessLevel.PRIVATE)
    private long id;

    private String genre;

    @NonNull
    private String author;

    @NonNull
    private String image;

    @NonNull
    private String title;

    @NonNull
    private String subtitle;

    @NonNull
    private String publisher;

    @NonNull
    private String year;

    @NonNull
    private int pages;

    @NonNull
    private String isbn;
}
