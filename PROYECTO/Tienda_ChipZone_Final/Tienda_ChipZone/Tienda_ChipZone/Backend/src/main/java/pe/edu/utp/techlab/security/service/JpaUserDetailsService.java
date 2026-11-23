package pe.edu.utp.techlab.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.techlab.security.model.UserAccount;
import pe.edu.utp.techlab.security.repository.UserAccountRepository;

/**
 * Semana 14: unica fuente de UserDetailsService. Los roles se guardan literalmente como
 * ROLE_USER y ROLE_ADMIN, por eso la configuracion usa hasRole("ADMIN") (agrega el prefijo).
 */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserAccountRepository users;

    public JpaUserDetailsService(UserAccountRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        UserAccount account = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
        return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .authorities(account.getRoles().toArray(String[]::new))
                .disabled(!account.isEnabled())
                .build();
    }
}
