package bot;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Random;

public class TOTD {
    private static TOTD totd;

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

    private TOTD() {}

    private void getNewTopic() {
        String response = Unirest.get(Constants.TOTD_API_URL)
            .header("X-RapidAPI-Key", App.getenv("KEY_RAPID_API"))
            .asString().getBody();

        System.out.println(response);
        this.topic = new GsonBuilder().create().fromJson(response, Topic.class);
    }

    public void announce(TextChannel channel) {
        Guild guild = channel.getGuild();
        Role totdRole = guild.getRoleById(App.getenv("ROLE_ID_TOTD"));

        if (totdRole == null) {
            App.logger.warn("Tried to announce TOTD but role was not found.");
            return;
        }
        String message = totdRole.getAsMention() + " " + constructTopicMessage();

        channel.sendMessage(message).queue();
    }

    public void executeCron(Guild guild) {
        TextChannel chatChannel = guild.getTextChannelById(App.getenv("CHANNEL_ID_CHAT"));

        if (chatChannel != null) {
            getNewTopic();
            announce(chatChannel);
        }
    }

    public void createFallbackTopic() {
        topic = new Topic();
        topic.setTopic(fallbackTopics[new Random().nextInt(fallbackTopics.length)]);
        System.out.println(topic.getTopic());
    }

    public String constructTopicMessage() {
        String topic = totd.getTopic();
        return "**Today's topic is**: " + topic + " :thinking:";
    }

    public static TOTD getTotd() {
        if (totd == null)
            totd = new TOTD();

        return totd;
    }

    public String getTopic() {
        return topic.getTopic();
    }

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
