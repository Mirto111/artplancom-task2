package ru.petsowner.repository;

import java.util.Optional;
import ru.petsowner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> getByUserName(String username);
}
