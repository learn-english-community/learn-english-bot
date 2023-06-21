package bot;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TOTD {
    private static TOTD totd;

    private Topic topic;

    private TOTD() {}

    public void getNewTopic() {
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
        String message = totdRole.getAsMention() + " " + topic.getTopic();

        channel.sendMessage(message).queue();
    }

    public void executeCron(Guild guild) {
        TextChannel chatChannel = guild.getTextChannelById(App.getenv("CHANNEL_ID_CHAT"));

        if (chatChannel != null) {
            getNewTopic();
            announce(chatChannel);
        }
    }

    public static TOTD getTotd() {
        return totd != null ? totd : new TOTD();
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
