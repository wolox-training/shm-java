package wolox.training.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.repositories.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @MockBean
    private User user;

    @Before
    public void setUp() {
        user = new User("Zurdo", "Santiago", LocalDate.parse("1993-08-11"));
    }

    @Test
    public void whenFindByName_thenReturnUser() {
        entityManager.persist(user);
        entityManager.flush();
        Optional<User> userFound = userRepository.findByUserName(user.getUserName());
        System.out.println(userFound.get());
        assertThat(userFound.get().getUserName()).isEqualTo(user.getUserName());
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateUserWithoutUserName_thenThrowException() {
        user.setUserName(null);
        userRepository.save(user);
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateUserWithoutName_thenThrowException() {
        user.setName(null);
        userRepository.save(user);
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateUserWithoutBirthDate_thenThrowException() {
        user.setBirthDate(null);
        userRepository.save(user);
    }

}
