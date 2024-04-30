package ru.cloudproject.cloud.cloudtest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cloudproject.cloud.cloudtest.entities.UserToken;
import ru.cloudproject.cloud.cloudtest.repositories.UserTokenRepository;

import ru.cloudproject.cloud.cloudtest.utils.TokenNotFoundException;

import javax.transaction.Transactional;

@Service
public class UserTokenService {
    private final UserTokenRepository userTokenRepository;

    @Autowired
    public UserTokenService(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }
    @Transactional
    public UserToken saveToken(String email, String token) {
        // Проверяем, существует ли запись для этого email в базе данных
        UserToken existingToken = userTokenRepository.findByEmail(email);


        // Если запись существует, обновляем ее токен
        if (existingToken != null) {
            existingToken.setToken(token);

        } else {
            // Если записи не существует, создаем новую запись
            existingToken = new UserToken();
            existingToken.setEmail(email);
            existingToken.setToken(token);
        }

        // Сохраняем или обновляем токен в базе данных
        return userTokenRepository.save(existingToken);

    }

    public String getTokenByEmail(String email){
        return userTokenRepository.findByEmail(email).getToken();
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteToken(String email){
        UserToken existingToken = userTokenRepository.findByEmail(email);
        if (existingToken != null) {
            userTokenRepository.deleteTokenByEmail(email);
        } else {

           throw new TokenNotFoundException("Token not existed" );
        }

    }
}
