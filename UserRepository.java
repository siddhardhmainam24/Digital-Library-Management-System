package learning.outcome.Repository;

import learning.outcome.Entity.appUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<appUser, Long> {

    boolean existsByEmail(String email);
    appUser findByEmail(String email);
    @Query("SELECT u FROM appUser u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<appUser> findByNameContainingIgnoreCase(@Param("name") String name);

    Optional<appUser> getUserByEmail(String email);

    Optional<appUser> getUserById(Long userid);

    List<appUser> getAllUser();


    // Find all users ordered by creation date
  //  List<appUser> findAllByOrderByCreatedAtDesc();
}

