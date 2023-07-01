package bot.service;

import bot.entity.User;
import bot.repository.UserRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service component.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Inserts a new user to the database.
     * @param user The user to insert
     */
    public void createUser(@NonNull User user) {
        userRepository.save(user);
    }

    /**
     * @param discordId The Discord ID of the user.
     * @return True if the user exists, false if not
     */
    public boolean userExists(@NonNull String discordId) {
        return userRepository.findUserByDiscordId(discordId) != null;
    }
}
