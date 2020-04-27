package wolox.training.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@ApiModel(description = "Books from the library")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_generator")
    @SequenceGenerator(name = "book_generator", sequenceName = "book_seq")
    @Setter(AccessLevel.PRIVATE)
    private long id;

    @ApiModelProperty(notes = "The book genre, could be Fictional Novel, Biography, Drama, etc.")
    private String genre;

    @NotNull
    @NonNull
    @ApiModelProperty(notes = "Name of the author of the book")
    private String author;

    @NotNull
    @NonNull
    @ApiModelProperty(notes = "Book cover image url")
    private String image;

    @NotNull
    @NonNull
    @ApiModelProperty(notes = "Title of the book")
    private String title;

    @NotNull
    @NonNull
    @ApiModelProperty(notes = "Book Subtitle")
    private String subtitle;

    @NotNull
    @NonNull
    @ApiModelProperty(notes = "Name of the company or organization responsible for issuing the publication")
    private String publisher;

    @NotNull
    @NonNull
    @ApiModelProperty(notes = "Year of publication of the book")
    private String year;

    @NotNull
    @NonNull
    @ApiModelProperty(notes = "Number of pages of the book")
    private Integer pages;

    @NotNull
    @NonNull
    @ApiModelProperty(notes = "Unique identification number of the book")
    private String isbn;

    @JsonIgnore
    @ManyToMany(mappedBy = "books")
    private List<User> user = new ArrayList<User>();

    public Book(long id, String bookTitle, String bookAuthor, String bookGenre, String bookImage,
        String bookSubtitle, String bookYear, String bookPublisher, String bookIsbn,
        Integer bookPages) {
        this.id = id;
        setTitle(bookTitle);
        setAuthor(bookAuthor);
        setGenre(bookGenre);
        setImage(bookImage);
        setSubtitle(bookSubtitle);
        setYear(bookYear);
        setPublisher(bookPublisher);
        setIsbn(bookIsbn);
        setPages(bookPages);
    }

    public Book(String bookTitle, String bookAuthor, String bookGenre, String bookImage,
        String bookSubtitle, String bookYear, String bookPublisher, String bookIsbn,
        Integer bookPages) {
        setTitle(bookTitle);
        setAuthor(bookAuthor);
        setGenre(bookGenre);
        setImage(bookImage);
        setSubtitle(bookSubtitle);
        setYear(bookYear);
        setPublisher(bookPublisher);
        setIsbn(bookIsbn);
        setPages(bookPages);
    }

    ;
}
