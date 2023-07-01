package bot.config;

import bot.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuration class for the MongoDB connection.
 */
@Configuration
@EnableMongoRepositories
public class ApplicationConfig extends AbstractMongoClientConfiguration {

    /**
     * Retrieves the name of the MongoDB database.
     *
     * @return The name of the MongoDB database.
     */
    @Override
    protected String getDatabaseName() {
        return Constants.DATABASE_NAME;
    }
}
