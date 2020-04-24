package wolox.training.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookRepository bookRepository;
    private Book book;

    @Before
    public void setUp() {
        book = new Book(1, "Social science fiction", "George Orwell",
            "image.jpg", "1984", "Nineteen Eighty Four",
            "Debolsillo", "1948", "9788499890944", 309);
    }

    @Test
    public void givenBooks_whenGetBooks_thenReturnJsonArray() throws Exception {
        List<Book> allBooks = Arrays.asList(book);
        given(bookRepository.findAll()).willReturn(allBooks);
        mvc.perform(get("/api/books")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].author", is(book.getAuthor())));
    }

    @Test
    public void givenBook_whenGetBook_thenReturnJson() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        mvc.perform(get("/api/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.author", is(book.getAuthor())));
    }

    @Test
    public void givenAValidBook_whenCreatesABook_thenReturnJson() throws Exception {
        mvc.perform(post("/api/books/")
            .content(objectMapper.writeValueAsString(book))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenBook_whenUpdatesBook_thenReturnOk() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        book.setPages(320);
        mvc.perform(put("/api/books/1")
            .content(objectMapper.writeValueAsString(book))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void givenBook_whenDeletesBook_thenReturnOk() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        mvc.perform(delete("/api/books/1")
            .content(objectMapper.writeValueAsString(book))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void givenNoneBook_whenGetBooks_thenReturnEmptyJsonArray() throws Exception {
        List<Book> allBooks = new ArrayList<Book>();
        given(bookRepository.findAll()).willReturn(allBooks);
        mvc.perform(get("/api/books")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void givenNoneBook_whenCreatesABook_thenReturnJson() throws Exception {
        mvc.perform(post("/api/books/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenAnInvalidId_whenUpdatesBook_thenReturnBadRequest() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        book.setPages(320);
        mvc.perform(put("/api/books/5")
            .content(objectMapper.writeValueAsString(book))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenAnInvalidId_whenDeletesBook_thenReturnNotFound() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        mvc.perform(delete("/api/books/3")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}