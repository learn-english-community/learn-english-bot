package bot.quiz;

import bot.service.UserService;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.Map;

/**
 * Represents flashcard quiz, mostly used by user journals.
 */
@Getter
public class FlashcardQuiz extends Quiz<MessageEmbed> {

    private final UserService userService;

    protected FlashcardQuiz(User user,
                            UserService userService,
                            Map<Integer, Question<MessageEmbed>> questions) {
        this.user = user;
        this.userService = userService;
        this.questions = questions;
    }

    @Override
    public void showQuestion(int number, @NonNull InteractionHook hook, List<Button> buttons) {
        show(getQuestions().get(number).getQuestion(), hook, buttons);
    }

    @Override
    public void showAnswer(int number, @NonNull InteractionHook hook, List<Button> buttons) {
        show(getQuestions().get(number).getAnswer(), hook, buttons);
    }

    private void show(MessageEmbed messageEmbed, InteractionHook hook, List<Button> buttons) {
        if (messageEmbed == null) return;

        hook.editOriginal("")
            .setEmbeds(messageEmbed)
            .setActionRow(buttons)
            .queue();
    }

    @Override
    public void start() {

    }
}
