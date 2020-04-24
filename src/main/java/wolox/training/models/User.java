package wolox.training.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
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
import wolox.training.exceptions.BookAlreadyOwnedException;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq")
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @NotNull
    @NonNull
    private String userName;

    @NotNull
    @NonNull
    private String name;

    @NotNull
    @NonNull
    private LocalDate birthDate;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private List<Book> books = new ArrayList<Book>();

    public User(long id, String userName, String name, LocalDate birthDate) {
        this.id = id;
        setUserName(userName);
        setName(name);
        setBirthDate(birthDate);
    }

    public User(String userName, String name, LocalDate birthDate) {
        setUserName(userName);
        setName(name);
        setBirthDate(birthDate);
    }

    public void addBook(Book book) {
        if (books.contains(book)) {
            throw new BookAlreadyOwnedException();
        }
        this.books.add(book);
    }

    public void deleteBook(Book book) {
        books.remove(book);
    }

    public List<Book> getBooks() {
        return (List<Book>) Collections.unmodifiableList(books);
    }
}
