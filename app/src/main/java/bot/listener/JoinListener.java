package bot.listener;

import bot.Constants;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author Eaky0 A listener called whenever a user joins the guild. Mainly used for assigning
 *     initial roles such as 'Member'.
 */
@Log4j2
public class JoinListener extends ListenerAdapter {
    /**
     * @param event The event that takes place when a User/Member joins the guild.
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        Role role = guild.getRoleById(Constants.MEMBER_ROLE_ID);

        if (role == null) {
            log.warn("Role ID (%s) does not match to the member role!");
            return;
        }

        if (event.getMember().getRoles().contains(role)) {
            String message =
                    String.format(
                            "Member %s (ID: %s) already seems to have the member role.",
                            user.getName(), user.getId());

            log.info(message);
            return;
        }

        // Assign the member role to the new user.
        guild.addRoleToMember(user, role).queue();
    }
}
