package bot.listener;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author Eaky0 A listener called whenever a user joins the guild. Mainly used for assigning
 *     initial roles such as 'Member'.
 */
public class JoinListener extends ListenerAdapter {

    /**
     * @param event The event that takes place when a User/Member joins the guild.
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();
        try {
            Role role = event.getGuild().getRoleById(1120239610920976563L);
            if (role != null) {
                // Assign the member role to the new user.
                event.getGuild().addRoleToMember(user, role).queue();
            } else {
                throw new NullPointerException();
            }

        } catch (NullPointerException e) {
            System.out.println("This role doesn't exist!");
            return;
        }
    }
}
