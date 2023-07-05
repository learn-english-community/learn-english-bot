package bot.cmd;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents an abstract slash command used by the bot.
 */
public abstract class BotCommand extends ListenerAdapter {

    /** The name of the command. */
    private final String name;

    /** A description of the command. */
    private final String description;

    /** A map of all the possible command arguments. */
    private final Map<String, CommandArgument> arguments = new HashMap<>();

    @Getter
    private final boolean isGlobal;

    public BotCommand(String name, String description, boolean isGlobal) {
        this.name = name;
        this.description = description;
        this.isGlobal = isGlobal;
    }

    /**
     * @return The name of the command.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The description of the command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The map of the command arguments.
     */
    public Map<String, CommandArgument> getArguments() {
        return arguments;
    }

    /**
     * Handles the command execution.
     * <p>
     * This method should get automatically called whenever a
     * slash command event is received by the JDA client.
     */
    public abstract void execute(SlashCommandInteractionEvent event);

    /**
     * Gets called every time a user executes a slash command
     * registered by the bot.
     *
     * @param event An instance of the event.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(this.getName())) return;
        this.execute(event);
    }

    /**
     * Gets called every time a user has an auto-completion prompt open
     * on their Discord client and are currently using it.
     *
     * @param event An instance of the event.
     */
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (!event.getName().equals(this.getName())) return;

        CommandArgument cmdArg = this.getArguments().get(event.getFocusedOption().getName());
        List<Command.Choice> options = cmdArg.getOptions().stream()
                .filter(word -> {
                    return word.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase());
                })
                .limit(25)
                .map(word -> new Command.Choice(word, word))
                .collect((Collectors.toList()));

        event.replyChoices(options).queue();
    }

    /**
     * Represents a Discord command argument.
     */
    public static class CommandArgument {
        /**
         * The data type that the command argument accepts.
         */
        private final OptionType type;

        /**
         * The name of the command argument.
         */
        private final String name;

        /**
         * A friendly description of the command argument.
         */
        private final String description;

        /**
         * Whether this command argument is required for the
         * command's execution.
         */
        private final boolean required;

        /**
         * Whether this command argument should support
         * autocomplete functionality.
         */
        private final boolean autoComplete;

        /**
         * In case of autocomplete support, a list of options
         * for the user to pick.
         */
        private final List<String> options = new ArrayList<>();

        public CommandArgument(
            OptionType type, String name, String description,
            boolean required, boolean autoComplete) {
            this.type = type;
            this.name = name;
            this.description = description;
            this.required = required;
            this.autoComplete = autoComplete;
        }

        /**
         * @return The data type that the command argument accepts.
         */
        public OptionType getType() {
            return type;
        }

        /**
         * @return The name of the command argument.
         */
        public String getName() {
            return name;
        }

        /**
         * @return The description of the command argument.
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return Whether this command argument is required
         * for overall command's execution.
         */
        public boolean isRequired() {
            return required;
        }

        /**
         * @return Whether this command argument is supposed to
         * have autocomplete capabilities.
         */
        public boolean shouldAutocomplete() {
            return autoComplete;
        }

        /**
         * TODO: This is redundant since the options list
         *  can be accessed without this method.
         * <p>
         * Adds a list of options to the command argument.
         * @return This instance.
         */
        public CommandArgument addOptions(List<String> options) {
            this.options.addAll(options);
            return this;
        }

        /**
         * TODO: This is redundant since the options list
         *  can be accessed without this method.
         * <p>
         * Adds an option to the command argument.
         * @return This instance.
         */
        public CommandArgument addOption(String option) {
            this.options.add(option);
            return this;
        }

        /**
         * @return The list holding all the command argument options.
         */
        public List<String> getOptions() {
            return options;
        }
    }
}
