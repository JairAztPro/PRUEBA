-- Semana 14: identidades persistentes. No se insertan claves aqui; las cuentas de laboratorio
-- las crea LabUserSeeder (perfil "lab") con hashes BCrypt.
CREATE TABLE app_users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(80)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    enabled       BIT(1)       NOT NULL DEFAULT b'1',
    PRIMARY KEY (id),
    CONSTRAINT uk_app_users_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE app_user_roles (
    user_id BIGINT      NOT NULL,
    role    VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_app_user_roles_user FOREIGN KEY (user_id)
        REFERENCES app_users (id) ON DELETE CASCADE,
    CONSTRAINT ck_app_user_roles_role
        CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
