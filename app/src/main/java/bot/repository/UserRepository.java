package bot.repository;

import bot.entity.User;
import bot.entity.word.JournalWord;
import bot.entity.word.Word;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * User repository for user entities.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findUserByDiscordId(String discordId);
}
