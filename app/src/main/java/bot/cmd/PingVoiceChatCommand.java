package bot.cmd;

import bot.App;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PingVoiceChatCommand extends BotCommand {

    private static final Map<String, Integer> usages = new HashMap<>();

    public PingVoiceChatCommand() {
        super("pingvc", "Let other members know that you want to voice chat!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;

        String userId = event.getUser().getId();
        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null) return;
        AudioChannelUnion userVcChannel = voiceState.getChannel();

        if (userVcChannel == null) {
            event.reply("You need to be in a voice channel to execute this command!")
                .setEphemeral(true)
                .queue();
            return;
        }

        Role vcRole = Objects.requireNonNull(
            event.getGuild()).getRoleById(App.getenv("ROLE_ID_VOICE_CHAT")
        );

        if (vcRole == null) {
            App.logger.warn("ROLE_ID_VOICE_CHAT was not found");
            return;
        }

        event.reply(vcRole.getAsMention() + " Join "
            + event.getUser().getAsMention() + " for a conversation in "
            + userVcChannel.getAsMention() + "!")
            .queue();
    }

    public static Map<String, Integer> getUsages() {
        return usages;
    }
}
