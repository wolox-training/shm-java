package wolox.training.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.BookIdMismatchException;
import wolox.training.exceptions.UserIdMismatchException;
import wolox.training.exceptions.UserNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.services.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    @GetMapping
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @PutMapping("/{id}")
    public User update(@RequestBody User modifiedUser, @PathVariable Long id) {
        if (modifiedUser.getId() != id) {
            throw new UserIdMismatchException();
        }
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userService.updateUser(user, modifiedUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @PostMapping("{user_id}/books/{book_id}")
    public User addBookToCollection(@PathVariable Long user_id, @PathVariable Long book_id) {
        User user = userRepository.findById(user_id).orElseThrow(UserIdMismatchException::new);
        Book book = bookRepository.findById(book_id).orElseThrow(BookIdMismatchException::new);
        user.addBook(book);
        return userRepository.save(user);
    }

    @DeleteMapping("{user_id}/books/{book_id}")
    public User deleteBookToCollection(@PathVariable Long user_id, @PathVariable Long book_id) {
        User user = userRepository.findById(user_id).orElseThrow(UserIdMismatchException::new);
        Book book = bookRepository.findById(book_id).orElseThrow(BookIdMismatchException::new);
        user.deleteBook(book);
        return userRepository.save(user);
    }

    @PutMapping("/{id}/password")
    public User updatePassword(@RequestHeader(value = "Password") String password,
        @PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userService.updateUserPassword(user, password);
    }

    @GetMapping("/username")
    public String currentUserName(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/find_by_birth_date_and_name")
    public List<User> findByBirthDateBetweenAndNameContaining(
        @RequestParam(name = "startDate") String startDate,
        @RequestParam(name = "endDate") String endDate,
        @RequestParam(name = "name") String name) {
        LocalDate firstDate = LocalDate.parse(startDate);
        LocalDate lastDate = LocalDate.parse(endDate);
        return userRepository.findByBirthDateBetweenAndNameContaining(firstDate, lastDate, name);
    }
}
