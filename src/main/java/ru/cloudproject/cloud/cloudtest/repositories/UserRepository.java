package ru.cloudproject.cloud.cloudtest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cloudproject.cloud.cloudtest.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Boolean existsByEmail(String email);
    void deleteUserByEmail(String email);
    User  findUserByEmail(String email);
}
