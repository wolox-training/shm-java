package wolox.training.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wolox.training.models.User;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    @Query(
        "SELECT u FROM User u WHERE (u.birthDate BETWEEN :startDate AND :endDate) AND"
            + "(:name = '' OR lower(u.name) LIKE lower(concat('%', :name,'%')) )")
    List<User> findByBirthDateBetweenAndNameContaining(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("name") String name);

    @Query(
        "SELECT u FROM User u WHERE (:id IS NULL OR u.id = :id) AND "
            + "(:userName IS NULL OR u.userName = :userName) AND "
            + "(:name IS NULL OR u.name = :name) AND"
            + "(CAST(:birthDate AS date) IS NULL OR u.birthDate = :birthDate)")
    Page<User> findAllByFilter(
        @Param("id") Long id,
        @Param("userName") String userName,
        @Param("name") String name,
        @Param("birthDate") LocalDate birthDate,
        Pageable pageable);
}