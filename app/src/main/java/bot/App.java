package bot;

import bot.listener.ReadyListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static final Logger logger = LogManager.getLogger();

    private static final Dotenv dotenv = Dotenv
        .configure()
        .directory("..")
        .load();

    public static List<EventListener> listeners = new ArrayList<>();
    static {
        listeners.add(new ReadyListener());
    }

    public void launch() {
        try {
            String envVar = App.getenv("PROD_BUILD");
            int isProdBuild;

            if (envVar != null) {
                isProdBuild = Integer.parseInt(envVar);
            } else {
               isProdBuild = 0;
            }

            String botToken = isProdBuild == 1 ? "BOT_TOKEN_PROD" : "BOT_TOKEN_DEV";
            JDABuilder jdaBuilder = JDABuilder.createDefault(App.getenv(botToken));

            // Add event listeners
            listeners.forEach(jdaBuilder::addEventListeners);

            JDA jda = jdaBuilder.build();
            jda.awaitReady();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO (@christolis): There must be a better way
    // to get environment variables more easily.
    private static String getenv(String key) {
        // Prioritize .env first
        String value = null;
        try {
            value = dotenv.get(key);
        } catch (Exception e) {} // .env file not found

        return value != null ? value : System.getenv(key);
    }
    public static void main(String[] args) {
        new App().launch();
    }
}
