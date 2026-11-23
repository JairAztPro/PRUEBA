package pe.edu.utp.techlab.security.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.techlab.security.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}
