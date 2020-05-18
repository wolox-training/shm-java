package wolox.training.controllers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
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
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.models.Book;
import wolox.training.providers.CustomAuthenticationProvider;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @MockBean
    private CustomAuthenticationProvider customAuthenticationProvider;

    @MockBean
    private OpenLibraryService openLibraryService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookRepository bookRepository;
    private Book book;

    @Before
    public void setUp() {
        book = new Book(1, "1984", "George Orwell",
            "Social science fiction", "image.jpg", "Nineteen Eighty Four",
            "1948", "New American Library", "0451521234", 309);
    }

    @WithMockUser
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
    public void givenAValidBook_whenCreatesABook_thenReturnCreated() throws Exception {
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

    @WithMockUser
    @Test
    public void givenAIsbn_whenSearchingForExistingBook_thenReturnOk() throws Exception {
        given(bookRepository.findByIsbn("0451521234")).willReturn(Optional.of(book));

        mvc.perform(get("/api/books/isbn/" + book.getIsbn())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.publisher", is(book.getPublisher())));
    }

    @WithMockUser
    @Test
    public void givenAIsbn_whenSearchForNonExistingBook_thenReturnCreated() throws Exception {
        given(bookRepository.save(book)).willReturn(book);
        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.givenThat(
            WireMock.get(urlEqualTo("/api/books?bibkeys=ISBN:0451521234&format=json&jscmd=data"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("response_ok_book.json")));

        wireMockServer.start();

        mvc.perform(get("/api/books/isbn/" + book.getIsbn())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
        wireMockServer.stop();
    }

    @WithMockUser
    @Test
    public void givenAIsbn_whenSearchForInvalidBook_thenReturnCreated()
        throws Exception {
        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.givenThat(
            WireMock.get(urlEqualTo("/api/books?bibkeys=ISBN:0451521234&format=json&jscmd=data"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBodyFile("response_book_not_found.json")));

        wireMockServer.start();

        mvc.perform(get("/api/books/isbn/" + book.getIsbn())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        wireMockServer.stop();
    }
}