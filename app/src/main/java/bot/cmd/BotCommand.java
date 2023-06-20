package bot.cmd;

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

public abstract class BotCommand extends ListenerAdapter {

    private final String name;
    private final String description;

    private final Map<String, CommandArgument> arguments = new HashMap<>();

    public BotCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, CommandArgument> getArguments() {
        return arguments;
    }

    public abstract void execute(SlashCommandInteractionEvent event);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(this.getName())) return;
        this.execute(event);
    }

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
        private final OptionType type;
        private final String name;
        private final String description;

        private final boolean required;

        private final boolean autoComplete;
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

        public OptionType getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isRequired() {
            return required;
        }

        public boolean shouldAutocomplete() {
            return autoComplete;
        }

        public CommandArgument addOptions(List<String> options) {
            this.options.addAll(options);
            return this;
        }

        public CommandArgument addOption(String option) {
            this.options.add(option);
            return this;
        }

        public List<String> getOptions() {
            return options;
        }
    }
}
