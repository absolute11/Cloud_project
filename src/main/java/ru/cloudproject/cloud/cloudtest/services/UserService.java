package ru.cloudproject.cloud.cloudtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.cloudproject.cloud.cloudtest.entities.User;
import ru.cloudproject.cloud.cloudtest.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUserList(){
        return userRepository.findAll();
    }

    public User getUserByEmail(String email){
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findUserByEmail(email));
        if(optionalUser.isPresent()){
            return  optionalUser.get();
        }
        else{
            throw new UsernameNotFoundException("Username  with email" + email + "not found");
        }
    }

    public User createUser(User user){
        return  userRepository.save(user);
    }

    public void deleteUserByEmail(String email){
        userRepository.deleteUserByEmail(email);
    }
    public boolean existByUserName(String username){
        return userRepository.existsByEmail(username);
    }

    public boolean authenticate(String email,String password){
        User user = userRepository.findUserByEmail(email);
        return user.getEmail().equals(email)&& user.getPassword().equals(password);


    }
}
