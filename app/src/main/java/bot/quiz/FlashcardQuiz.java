package bot.quiz;

import bot.Constants;
import bot.quiz.question.FlashcardQuestion;
import bot.quiz.question.Question;
import bot.service.UserService;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/** Represents flashcard quiz, mostly used by user journals. */
@Getter
public class FlashcardQuiz extends Quiz<MessageEmbed> {

    private final UserService userService;

    private final PrivateChannel channel;

    private String lastMessageId;

    private static final Map<String, FlashcardQuiz> quizes = new HashMap<>();

    protected FlashcardQuiz(
            User user,
            UserService userService,
            PrivateChannel channel,
            Map<Integer, Question<MessageEmbed>> questions) {
        this.user = user;
        this.userService = userService;
        this.channel = channel;
        this.questions = questions;

        quizes.put(user.getId(), this);
    }

    @Override
    public void showQuestion(int number) {
        Button revealButton = Button.success("flashcard-reveal", "Reveal");
        FlashcardQuestion question = (FlashcardQuestion) getQuestions().get(number);

        this.setCurrentQuestion(question);
        show(question.getQuestion(), List.of(revealButton));
    }

    @Override
    public void showAnswer(int number) {
        List<Button> buttons = new ArrayList<>();
        FlashcardQuestion question = (FlashcardQuestion) getQuestions().get(number);

        for (int i = 1; i <= 5; i++) {
            Button button = Button.primary("flashcard-answer:" + i, String.valueOf(i));
            buttons.add(button);
        }
        show(question.getAnswer(), buttons);
    }

    public void showQuestion() {
        this.setCurrentQuestionId(this.getCurrentQuestionId() + 1);

        if (getQuestions() == null || getQuestions().get(getCurrentQuestionId()) == null) {
            // We didn't even get started. Let the user know.
            EmbedBuilder embed = new EmbedBuilder();
            if (getCurrentQuestionId() == 1) {
                embed.setTitle("No words");
                embed.setDescription("There are no words for you to practice using this filter!");
                embed.setColor(Color.red);

                channel.sendMessageEmbeds(embed.build())
                        .queue(
                                success -> {
                                    channel.deleteMessageById(success.getId())
                                            .queueAfter(10L, TimeUnit.SECONDS);
                                });
            } else {
                // We reached the end of questions.
                embed.setTitle("End of exercise");
                embed.setDescription("You reached the end of your exercise! 💪");
                embed.setColor(Constants.EMBED_COLOR);
                embed.setImage("https://media.tenor.com/MDTYbqilAxgAAAAC/ogvhs-high-five.gif");

                channel.sendMessageEmbeds(embed.build())
                        .queue(
                                success -> {
                                    channel.deleteMessageById(success.getId())
                                            .queueAfter(10L, TimeUnit.SECONDS);
                                });
            }

            removeLastMessage();
            quizes.remove(getUser().getId());
            return;
        }

        showQuestion(this.getCurrentQuestionId());
    }

    public void showAnswer() {
        showAnswer(this.getCurrentQuestionId());
    }

    private void show(MessageEmbed messageEmbed, List<Button> buttons) {
        if (messageEmbed == null) return;

        removeLastMessage();

        channel.sendMessage("")
                .setEmbeds(messageEmbed)
                .setActionRow(buttons)
                .queue(message -> lastMessageId = message.getId());
    }

    private void removeLastMessage() {
        if (this.getLastMessageId() != null)
            channel.deleteMessageById(this.getLastMessageId()).queue();
    }

    @Override
    public void start() {
        showQuestion();
    }

    public static Optional<FlashcardQuiz> getInstance(String discordId) {
        return Optional.ofNullable(quizes.get(discordId));
    }
}
