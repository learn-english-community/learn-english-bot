package bot.repository;

import bot.entity.User;
import bot.entity.word.JournalWord;
import bot.entity.word.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User repository for user entities.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findUserByDiscordId(String discordId);
}
