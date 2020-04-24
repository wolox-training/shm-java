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
import java.time.LocalDate;
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
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private UserRepository userRepository;
    private User user;
    private Book book;

    @Before
    public void setUp() {
        book = new Book(1, "Social science fiction", "George Orwell",
            "image.jpg", "1984", "Nineteen Eighty Four",
            "Debolsillo", "1948", "9788499890944", 309);
        user = new User((long) 1, "Zurdo", "Santiago", LocalDate.parse("1993-08-11"));
    }

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        List<User> allUsers = Arrays.asList(user);
        given(userRepository.findAll()).willReturn(allUsers);
        mvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(user.getName())));
    }

    @Test
    public void givenUser_whenGetUser_thenReturnJson() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        mvc.perform(get("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(user.getName())));
    }

    @Test
    public void givenAValidUser_whenCreatesAUser_thenReturnJson() throws Exception {
        mvc.perform(post("/api/users/")
            .content(objectMapper.writeValueAsString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    public void givenUser_whenUpdatesUser_thenReturnOk() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        System.out.println(user);
        user.setName("Santiago Hernandez Mejia");
        mvc.perform(put("/api/users/1")
            .content(objectMapper.writeValueAsString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void givenUser_whenDeletesUser_thenReturnOk() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        mvc.perform(delete("/api/users/1")
            .content(objectMapper.writeValueAsString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void givenUser_whenAddingBook_thenReturnOk() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        mvc.perform(post("/api/users/1/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void givenUserAndNoneBook_whenAddingBook_thenReturnBadRequest() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        mvc.perform(post("/api/users/1/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenBookAndNoneUser_whenAddingBook_thenReturnBadRequest() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        mvc.perform(post("/api/users/1/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenUser_whenDeletingBook_thenReturnOk() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        mvc.perform(delete("/api/users/1/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void givenUserAndNoneBooK_whenDeletingBook_thenReturnBadRequest() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        mvc.perform(delete("/api/users/1/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenBookAndNoneUser_whenDeletingBook_thenReturnBadRequest() throws Exception {
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        mvc.perform(delete("/api/users/1/books/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNoneUser_whenGetUsers_thenReturnEmptyJsonArray() throws Exception {
        List<User> allUsers = new ArrayList<User>();
        given(userRepository.findAll()).willReturn(allUsers);
        mvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void givenNoneUser_whenCreatesAUser_thenReturnJson() throws Exception {
        mvc.perform(post("/api/users/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenAnInvalidId_whenUpdatesUser_thenReturnBadRequest() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        user.setName("Santiago");
        mvc.perform(put("/api/users/10")
            .content(objectMapper.writeValueAsString(user))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenAnInvalidId_whenDeletesUser_thenReturnNotFound() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        mvc.perform(delete("/api/users/12")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

}
