package bot.repository;

import bot.entity.word.CachedWord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordCacheRepository extends MongoRepository<CachedWord, String> {
    CachedWord getCachedWordByWord(String word);

    void deleteCachedWordByWord(String word);
}
