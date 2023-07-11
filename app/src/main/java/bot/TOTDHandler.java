package bot;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.Random;
import kong.unirest.Unirest;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Random;

/**
 * Manages functions that involve the "topic of the day" feature.
 */
@Log4j2
public class TOTDHandler {
    /** An instance of the TOTDHandler. */
    private static TOTDHandler totd;

    /** An instance of the current topic. */
    private Topic topic;

    // For those, awkward moments when we can't communicate
    // with the API.
    private static final String[] fallbackTopics = {
        "Where’s the most beautiful place you’ve ever been?",
        "What are you going to do for your birthday this year?",
        "What hobby have you always wanted to try?",
        "What do you like to do on weekends?",
        "Where would you go if you could go anywhere?",
        "What’s your biggest goal right now?",
        "What’s your favorite thing about being you?"
    };

    private TOTDHandler() {}

    /** Updates the topic by making an API request to the specified API URL. */
    private void getNewTopic() {
        String response =
                Unirest.get(Constants.TOTD_API_URL)
                        .header("X-RapidAPI-Key", App.getenv("KEY_RAPID_API"))
                        .asString()
                        .getBody();

        this.topic = new GsonBuilder().create().fromJson(response, Topic.class);
    }

    /**
     * Attempts to announce the saved topic to the given channel.
     *
     * <p>It pings the topic of the day role, so use it with caution!
     *
     * @param channel The text channel to make the announcement to
     */
    public void announce(TextChannel channel) {
        Guild guild = channel.getGuild();
        Role totdRole = guild.getRoleById(App.getenv("ROLE_ID_TOTD"));

        if (totdRole == null) {
            log.warn("Tried to announce TOTD but role was not found.");
            return;
        }
        String message = totdRole.getAsMention() + " " + constructTopicMessage();

        channel.sendMessage(message).queue();
    }

    /**
     * Primarily used by the cron scheduler, and it acts as the executing method once triggered.
     *
     * <p>Upon execution, it fetches a new topic from the given API and right after that, it
     * announces it to the chat channel.
     *
     * @param guild A reference of the guild to perform this to
     */
    public void executeCron(Guild guild) {
        TextChannel chatChannel = guild.getTextChannelById(App.getenv("CHANNEL_ID_CHAT"));

        if (chatChannel != null) {
            getNewTopic();
            announce(chatChannel);
        }
    }

    /** Sets the topic to one of the fallback topics, picking one at random. */
    public void createFallbackTopic() {
        topic = new Topic();
        topic.setTopic(fallbackTopics[new Random().nextInt(fallbackTopics.length)]);
    }

    /**
     * @return The final Discord version of the message, excluding the role mention
     */
    public String constructTopicMessage() {
        String topic = totd.getTopic();
        return "**Today's topic is**: " + topic + " :thinking:";
    }

    /**
     * @return An instance of the TOTD handler
     */
    public static TOTDHandler getTotd() {
        if (totd == null) totd = new TOTDHandler();

        return totd;
    }

    /**
     * @return The topic as a string
     */
    public String getTopic() {
        return topic.getTopic();
    }

    /**
     * This class is used as a model for the Gson parser to be able to parse the response received
     * from the API.
     */
    static class Topic {
        @SerializedName("question")
        private String topic;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }
}
