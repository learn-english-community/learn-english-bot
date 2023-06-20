package bot;

import bot.cmd.BotCommand;
import bot.cmd.TranslateCommand;
import bot.listener.ReadyListener;
import com.deepl.api.Translator;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App {

    public static final Logger logger = LogManager.getLogger();

    private static Dotenv dotenv;
    public static Translator translator;

    public static List<EventListener> listeners = new ArrayList<>();
    public static List<BotCommand> commands = new ArrayList<>();

    static {
        // Listeners
        listeners.add(new ReadyListener());
    }

    public static final List<String> languages = new ArrayList<>();

    public void launch() {
        try {
            dotenv = Dotenv.configure().directory("..").load();
        } catch (Exception e) {
            dotenv = null;
        }

        // Add DeepL supported languages and construct translate command.
        try {
            // Slash commands
            App.translator = new Translator(App.getenv("KEY_DEEPL"));
            TranslateCommand cmd = new TranslateCommand();

            commands.add(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            commands.forEach(jdaBuilder::addEventListeners);

            JDA jda = jdaBuilder.build();
            jda.awaitReady();

            Optional<Guild> guildOptional = jda.getGuilds().stream().findFirst();
            if (guildOptional.isEmpty()) {
                logger.error("Unable to find a guild! You need to invite the bot to one" +
                    "before executing this program. Attempting to exit...");
                System.exit(1);
            }
            Guild guild = guildOptional.get();

            // Register slash commands
            commands.forEach(command -> {
                SlashCommandData scd = Commands.slash(command.getName(), command.getDescription());

                command.getArguments().forEach((name, arg) -> scd.addOption(
                    arg.getType(), name, arg.getDescription(),
                    arg.isRequired(), arg.shouldAutocomplete()
                ));
                guild.updateCommands().addCommands(scd).queue();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO (@christolis): There must be a better way
    // to get environment variables more easily.
    public static String getenv(String key) {
        // Prioritize .env first
        String value = null;

        if (dotenv != null)
            value = dotenv.get(key);

        return value != null ? value : System.getenv(key);
    }
    public static void main(String[] args) {
        new App().launch();
    }
}
