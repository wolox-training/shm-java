package wolox.training.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wolox.training.dto.OpenLibraryBook;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;

@Service
public class OpenLibraryService {

    @Autowired
    private BookRepository bookRepository;
    private ObjectMapper objectMapper;

    @Value("${openLibrary.baseUrl}")
    private String baseUrl;

    public OpenLibraryBook bookInfo(String isbn) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
        String url =
            baseUrl + String.format("/api/books?bibkeys=ISBN:%s&format=json&jscmd=data", isbn);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JsonNode result = objectMapper.readTree(response.getBody());
        if (result.isEmpty()) {
            throw new BookNotFoundException();
        }
        return buildLibraryBookDto(result.path("ISBN:" + isbn), isbn);
    }

    public OpenLibraryBook buildLibraryBookDto(JsonNode jsonNode, String isbn) {
        OpenLibraryBook book = new OpenLibraryBook();
        book.setIsbn(isbn);
        book.setTitle(jsonNode.path("title").asText());
        book.setSubtitle(jsonNode.path("subtitle").asText());
        book.setPublisher(jsonNode.path("publishers").findPath("name").asText());
        book.setPublishDate(jsonNode.path("publish_date").asText());
        book.setNumberOfPages(Integer.parseInt(jsonNode.path("number_of_pages").asText()));
        book.setAuthors((jsonNode.path("authors").findPath("name").asText()));
        book.setImage(jsonNode.path("cover").path("medium").asText());
        return book;
    }

    public Book saveBook(OpenLibraryBook openLibraryBook) {
        Book book = new Book(openLibraryBook.getTitle(), openLibraryBook.getAuthors(), "No genre",
            openLibraryBook.getImage(), openLibraryBook.getSubtitle(),
            openLibraryBook.getPublishDate(),
            openLibraryBook.getPublisher(), openLibraryBook.getIsbn(),
            openLibraryBook.getNumberOfPages());
        bookRepository.save(book);
        return book;
    }
}
