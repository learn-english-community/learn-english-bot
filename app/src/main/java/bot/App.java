package bot;

import bot.cmd.*;
import bot.listener.ReadyListener;
import bot.service.UserService;
import bot.task.StreakResetTask;
import com.deepl.api.Translator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import it.sauronsoftware.cron4j.Scheduler;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@Log4j2
public class App implements ApplicationRunner {

    public static final Scheduler scheduler = new Scheduler();

    private static JsonObject config;
    private static Translator translator;

    public static List<EventListener> listeners = new ArrayList<>();
    public static List<BotCommand> commands;

    @Getter private static JDA jda;

    @Autowired private MongoTemplate mongoTemplate;

    @Autowired private WOTDHandler wotdHandler;

    // Listeners
    static {
        listeners.add(new ReadyListener());
    }

    public void launch() {
        // Test DB connection
        try {
            BasicDBObject ping = new BasicDBObject("ping", "1");
            mongoTemplate.getDb().runCommand(ping);
        } catch (Exception e) {
            log.error("Failed to connect to the MongoDB server! Quitting...");
            System.exit(1);
        }

        // Schedule daily translation reset task.
        scheduler.schedule(
                Constants.CRON_DAILY_MORNING,
                () -> {
                    // Clear all translation usages.
                    TranslateCommand.getUsages().clear();
                    log.log(Level.INFO, "Cleaned member daily translation usage!");
                });

        scheduler.start();

        try {
            JDABuilder jdaBuilder = JDABuilder.createDefault(App.getenv("BOT_TOKEN"));

            jdaBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
            commands = SpringContext.getBeansOfType(BotCommand.class);
            // Add event listeners
            listeners.forEach(jdaBuilder::addEventListeners);
            commands.forEach(jdaBuilder::addEventListeners);

            jda = jdaBuilder.build();
            jda.awaitReady();

            Optional<Guild> guildOptional = jda.getGuilds().stream().findFirst();
            if (guildOptional.isEmpty()) {
                log.error(
                        "Unable to find a guild! You need to invite the bot to one"
                                + "before executing this program. Attempting to exit...");
                System.exit(1);
            }
            Guild guild = guildOptional.get();

            // Register slash commands
            Function<BotCommand, CommandData> commandConsumer =
                    (command) -> {
                        SlashCommandData scd =
                                Commands.slash(command.getName(), command.getDescription());

                        command.getArguments()
                                .forEach(
                                        (name, arg) ->
                                                scd.addOption(
                                                        arg.getType(),
                                                        name,
                                                        arg.getDescription(),
                                                        arg.isRequired(),
                                                        arg.shouldAutocomplete()));
                        return scd;
                    };
            log.info("Attempting to register " + commands.size() + " commands...");
            Collection<CommandData> localData =
                    commands.stream()
                            .filter(cmd -> !cmd.isGlobal())
                            .map(commandConsumer)
                            .collect(Collectors.toList());

            Collection<CommandData> globalData =
                    commands.stream()
                            .filter(BotCommand::isGlobal)
                            .map(commandConsumer)
                            .collect(Collectors.toList());

            guild.updateCommands().addCommands(localData).queue();
            jda.updateCommands().addCommands(globalData).queue();

            // TOTD and WOTD stuff
            TOTDHandler.getTotd().createFallbackTopic();
            scheduler.schedule(
                    Constants.CRON_DAILY_MIDDLE,
                    () -> {
                        TOTDHandler.getTotd().executeCron(guild);
                        wotdHandler.executeCron(guild);
                    });

            // Streak reset handling
            scheduler.schedule(
                    Constants.CRON_HOURLY,
                    new StreakResetTask(SpringContext.getBean(UserService.class)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Translator getTranslator() {
        if (translator == null) translator = new Translator(App.getenv("KEY_DEEPL"));

        return translator;
    }

    public static String getenv(String key) {
        if (config == null) {
            try {
                config = JsonParser.parseReader(new FileReader("config.json")).getAsJsonObject();
            } catch (Exception e) {
                config = null;
            }
        }

        // Prioritize config first
        JsonElement value = null;

        System.out.println(key);
        if (config != null) value = config.get(key);

        return value != null ? value.getAsString() : System.getenv(key);
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args).close();
    }

    @Override
    public void run(ApplicationArguments args) {
        launch();
    }
}
