package ru.cloudproject.cloud.cloudtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cloudproject.cloud.cloudtest.entities.UserToken;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken,Long> {
    UserToken findByEmail(String email);

    void deleteTokenByEmail(String email);
}
