package bot;

import bot.listener.ReadyListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static final Logger logger = LoggerFactory.getLogger(App.class.getName());

    private static final Dotenv dotenv = Dotenv.load();

    public static List<EventListener> listeners = new ArrayList<>();
    static {
        listeners.add(new ReadyListener());
    }

    public void launch() {
        try {
            String envVar = System.getenv("PROD_BUILD");
            int isProdBuild;

            if (envVar != null) {
                isProdBuild = Integer.parseInt(envVar);
            } else {
               isProdBuild = 0;
            }

            String botToken = isProdBuild == 1 ? "BOT_TOKEN_PROD" : "BOT_TOKEN_DEV";
            JDABuilder jdaBuilder = JDABuilder.createDefault(dotenv.get(botToken));

            // Add event listeners
            listeners.forEach(jdaBuilder::addEventListeners);

            JDA jda = jdaBuilder.build();
            jda.awaitReady();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new App().launch();
    }
}
