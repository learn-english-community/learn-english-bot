package bot.config;

import bot.App;
import bot.Constants;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/** Configuration class for the MongoDB connection. */
@Configuration
@EnableMongoRepositories
@Log4j2
public class ApplicationConfig extends AbstractMongoClientConfiguration {

    @Override
    public MongoClient mongoClient() {
        String url = App.getenv("MONGODB_URI");
        String database = App.getenv("MONGODB_DATABASE");

        try {
            ConnectionString connectionString = new ConnectionString(url + "/" + database);
            MongoClientSettings settings =
                    MongoClientSettings.builder().applyConnectionString(connectionString).build();
            return MongoClients.create(settings);
        } catch (MongoException e) {
            log.error("Failed to connect to the MongoDB server.");
            System.exit(1);
            return null;
        }
    }

    /**
     * Retrieves the name of the MongoDB database.
     *
     * @return The name of the MongoDB database.
     */
    @Override
    protected String getDatabaseName() {
        return Constants.DATABASE_NAME;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
