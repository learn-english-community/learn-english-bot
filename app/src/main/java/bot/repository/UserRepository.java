package bot.repository;

import bot.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * User repository for user entities.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    public User findUserById(String id);
    public User findUserByDiscordId(String discordId);
}
