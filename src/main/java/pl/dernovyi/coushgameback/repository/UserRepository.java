package pl.dernovyi.coushgameback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dernovyi.coushgameback.model.User;

import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    User findUserByUserId(String userId);

    void deleteByEmail(String email);

}
