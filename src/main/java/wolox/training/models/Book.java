package wolox.training.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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

    @ManyToMany(mappedBy = "books")
    private List<User> user;
}
