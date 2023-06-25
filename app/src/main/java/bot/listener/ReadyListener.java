package bot.listener;

import bot.App;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

/**
 * A listener that is called when the bot is ready.
 */
public class ReadyListener implements EventListener {

    @Override
    public void onEvent(GenericEvent event) {
        if (!(event instanceof ReadyEvent)) return;
        App.logger.info("Bot is ready!");
    }
}
