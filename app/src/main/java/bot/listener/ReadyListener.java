package bot.listener;

import bot.App;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

/**
 * A listener that is called when the bot is ready.
 */
@Log4j2
public class ReadyListener implements EventListener {

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof ReadyEvent)) return;
        log.info("Bot is ready!");
    }
}
