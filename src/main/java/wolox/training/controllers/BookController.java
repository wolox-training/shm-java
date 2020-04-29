package wolox.training.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.dto.OpenLibraryBook;
import wolox.training.exceptions.BookIdMismatchException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

@RestController
@RequestMapping("/api/books")
@Api(tags = "Book")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OpenLibraryService openLibraryService;

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World")
        String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a book", response = Book.class)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Book created successfully"),
        @ApiResponse(code = 400, message = "A bad request was sent"),
        @ApiResponse(code = 401, message = "You are not authorized to access this resource"),
        @ApiResponse(code = 404, message = "The resource you are trying to access was not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @GetMapping
    @ApiOperation(value = "Retrieve list of created books", response = Iterable.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved list"),
        @ApiResponse(code = 400, message = "A bad request was sent"),
        @ApiResponse(code = 401, message = "You are not authorized to access this resource"),
        @ApiResponse(code = 404, message = "The resource you are trying to access was not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Iterable<Book> findAll() {
        return bookRepository.findAll();
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates a book", response = Book.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated book"),
        @ApiResponse(code = 400, message = "A bad request was sent"),
        @ApiResponse(code = 401, message = "You are not authorized to access this resource"),
        @ApiResponse(code = 404, message = "The resource you are trying to access was not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) {
        if (book.getId() != id) {
            throw new BookIdMismatchException();
        }
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes a book", response = Book.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully deleted book"),
        @ApiResponse(code = 400, message = "A bad request was sent"),
        @ApiResponse(code = 401, message = "You are not authorized to access this resource"),
        @ApiResponse(code = 404, message = "The resource you are trying to access was not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public void delete(@PathVariable Long id) {
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Retrieve a book by id", response = Book.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved book"),
        @ApiResponse(code = 400, message = "A bad request was sent"),
        @ApiResponse(code = 401, message = "You are not authorized to access this resource"),
        @ApiResponse(code = 404, message = "The resource you are trying to access was not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public Book findOne(@PathVariable Long id) {
        return bookRepository.findById(id).orElseThrow(BookIdMismatchException::new);
    }

    @GetMapping("/author/{bookAuthor}")
    @ApiOperation(value = "Retrieve a book by author", response = List.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved book"),
        @ApiResponse(code = 400, message = "A bad request was sent"),
        @ApiResponse(code = 401, message = "You are not authorized to access this resource"),
        @ApiResponse(code = 404, message = "The resource you are trying to access was not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public List findByAuthor(@PathVariable String bookAuthor) {
        return bookRepository.findByAuthor(bookAuthor);
    }

    @GetMapping("/isbn/{isbn}")
    @ApiOperation(value = "Retrieve a book by ISBN", response = Optional.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved book"),
        @ApiResponse(code = 201, message = "Book created successfully"),
        @ApiResponse(code = 400, message = "A bad request was sent"),
        @ApiResponse(code = 401, message = "You are not authorized to access this resource"),
        @ApiResponse(code = 404, message = "The resource you are trying to access was not found"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Book> findByIsbn(@PathVariable String isbn)
        throws JsonProcessingException {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(book.get());
        }
        OpenLibraryBook openLibraryBook = openLibraryService.bookInfo(isbn);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(openLibraryService.saveBook(openLibraryBook));
    }

    @GetMapping("/find_by_publisher_genre_and_year")
    public List<Book> findByPublisherAndGenreAndYear(
        @RequestParam(name = "publisher") String publisher,
        @RequestParam(name = "genre") String genre, @RequestParam(name = "year") String year) {
        return bookRepository.findByPublisherAndGenreAndYear(publisher, genre, year);
    }
}
