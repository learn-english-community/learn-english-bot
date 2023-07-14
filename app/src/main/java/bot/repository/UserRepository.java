package bot.repository;

import bot.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** User repository for user entities. */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findUserByDiscordId(String discordId);
}
