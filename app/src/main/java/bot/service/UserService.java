package bot.service;

import bot.entity.User;
import bot.repository.UserRepository;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Getter
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public void createUser(@NotNull User user) {
        userRepository.save(user);
    }

    public boolean userExists(@NotNull String discordId) {
        return userRepository.existsById(discordId);
    }
}
