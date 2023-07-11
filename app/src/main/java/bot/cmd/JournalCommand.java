package bot.cmd;

import bot.entity.JournalDisplay;
import bot.entity.word.JournalWord;
import bot.quiz.FlashcardQuiz;
import bot.quiz.FlashcardQuizFilter;
import bot.quiz.QuizFactory;
import bot.quiz.question.FlashcardQuestion;
import bot.service.UserService;
import bot.view.JournalView;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JournalCommand extends BotCommand {

    public static final String PREFIX = JournalDisplay.PREFIX + "filter-";
    private static final int WORDS_COUNT = 5;
    private final JournalView journalView;

    private final QuizFactory quizFactory;
    private final UserService userService;

    @Autowired
    public JournalCommand(
            JournalView journalView, QuizFactory quizFactory, UserService userService) {
        super("journal", "Check your journal!", true);
        this.journalView = journalView;
        this.quizFactory = quizFactory;
        this.userService = userService;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        ChannelType channelType = event.getChannelType();
        JournalDisplay journalDisplay = journalView.getUserJournalDisplay(user, 0, WORDS_COUNT);

        if (channelType.equals(ChannelType.PRIVATE)) {
            if (journalDisplay.getErrorMessage() != null) {
                event.reply(journalDisplay.getErrorMessage()).setEphemeral(true).queue();
                return;
            }

            event.reply(journalDisplay.getMessage())
                    .addEmbeds(journalDisplay.getWords())
                    .setEphemeral(true)
                    .addActionRow(journalDisplay.getActionButtons())
                    .queue();
        } else {
            event.reply("You can only send this command as a direct message! :blue_book:")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String id = event.getSelectedOptions().get(0).getValue();
        User user = event.getUser();

        if (id.startsWith(JournalDisplay.PREFIX + "filter")) { // Construct quiz with filter.
            String type = id.substring((JournalDisplay.PREFIX + "filter-").length());
            FlashcardQuizFilter filter = FlashcardQuizFilter.getByLabel(type);
            Supplier<Modal> modalSupplier = filter.getMetadataModal();

            if (modalSupplier != null) event.replyModal(modalSupplier.get()).queue();
            else {
                event.deferEdit().queue();
                event.getHook().deleteOriginal().queue();
                FlashcardQuiz quiz =
                        quizFactory.getFlashcardQuiz(
                                user, event.getChannel().asPrivateChannel(), filter, null);
                quiz.start();
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId();
        User user = event.getUser();

        event.deferEdit().queue();

        // Display to the user the dropdown menu asking them
        // what words to include.
        if (id.contains("exercise")) {
            if (FlashcardQuiz.getInstance(user.getId()).isPresent()) {
                event.getHook()
                        .editOriginal("There is already a quiz in progress! ⭐️")
                        .setEmbeds(Collections.emptyList())
                        .queue();
                return;
            }

            StringSelectMenu.Builder menu =
                    StringSelectMenu.create(JournalDisplay.PREFIX + "filter");
            Arrays.stream(FlashcardQuizFilter.values())
                    .forEach(
                            f ->
                                    menu.addOption(
                                            f.getTitle(),
                                            PREFIX + f.getLabel(),
                                            f.getDescription(),
                                            f.getEmoji()));
            event.getHook()
                    .editOriginal("Select the words you want to practice with:")
                    .setEmbeds(Collections.emptyList())
                    .setActionRow(menu.build())
                    .queue();
            return;
        }

        // A "Reveal" button was clicked during a quiz.
        if (id.contains("flashcard-reveal")) {
            FlashcardQuiz.getInstance(user.getId()).ifPresent(FlashcardQuiz::showAnswer);
            return;
        }

        if (id.contains("flashcard-quit")) {
            FlashcardQuiz.getInstance(user.getId()).ifPresent(quiz -> quiz.finish(true));
            return;
        }

        // A ranking button was clicked during a quiz answer.
        if (id.contains("flashcard-answer")) {
            int selectedQuality = Integer.parseInt(id.split(":")[1]);
            FlashcardQuiz.getInstance(user.getId())
                    .ifPresent(
                            quiz -> {
                                FlashcardQuestion question =
                                        (FlashcardQuestion) quiz.getCurrentQuestion();
                                JournalWord word = question.getWord();

                                userService.updateWordSuperMemo(
                                        user.getId(),
                                        word.getWord(),
                                        word.getDefinitionIndex(),
                                        selectedQuality);
                                quiz.showQuestion();
                            });
            return;
        }

        int page = Integer.parseInt(id.split(":")[1]);
        JournalDisplay journalDisplay = journalView.getUserJournalDisplay(user, page, WORDS_COUNT);

        event.getHook()
                .editOriginal(journalDisplay.getMessage())
                .setEmbeds(journalDisplay.getWords())
                .setActionRow(journalDisplay.getActionButtons())
                .queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String id = event.getModalId();
        String type = id.split("-", 3)[2];
        ModalMapping value = event.getValue("practice-input");
        User user = event.getUser();

        FlashcardQuizFilter filter = FlashcardQuizFilter.getByLabel(type);

        if (filter != null) {
            event.deferEdit().queue(hook -> hook.deleteOriginal().queue());
            FlashcardQuiz quiz =
                    quizFactory.getFlashcardQuiz(
                            user, event.getChannel().asPrivateChannel(), filter, value);
            quiz.start();
        }
    }
}
