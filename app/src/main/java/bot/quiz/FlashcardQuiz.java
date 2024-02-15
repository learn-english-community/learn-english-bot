package bot.quiz;

import bot.Constants;
import bot.entity.session.Session;
import bot.quiz.question.FlashcardQuestion;
import bot.quiz.question.Question;
import bot.service.UserService;
import bot.view.StreakView;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/** Represents flashcard quiz, mostly used by user journals. */
public class FlashcardQuiz extends Quiz<MessageEmbed> {

    private final UserService userService;

    private final PrivateChannel channel;

    private String lastMessageId;

    private static final Map<String, FlashcardQuiz> quizes =
            Collections.synchronizedMap(new HashMap<>());

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

        Button cancelButton = Button.danger("flashcard-quit", "Quit");

        this.setCurrentQuestion(question);
        show(question.getQuestion(), List.of(revealButton, cancelButton));
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
            if (getCurrentQuestionId() == 1) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("No words");
                embed.setDescription("There are no words for you to practice using this filter!");
                embed.setColor(Color.red);

                channel.sendMessageEmbeds(embed.build())
                        .queue(
                                success -> {
                                    channel.deleteMessageById(success.getId())
                                            .queueAfter(10L, TimeUnit.SECONDS);
                                });
                finish(false, false);
            } else {
                Session session =
                        Session.builder()
                                .timestamp(System.currentTimeMillis())
                                .type(Session.Type.JOURNAL_QUIZ)
                                .build();
                userService.saveSession(getUser().getId(), session);
                finish(true, true);
            }

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
        if (this.lastMessageId != null) channel.deleteMessageById(this.lastMessageId).queue();
    }

    @Override
    public void start() {
        showQuestion();
    }

    public void finish(boolean announce, boolean complete) {
        if (announce) {
            EmbedBuilder embed = new EmbedBuilder();
            StreakView streakView = new StreakView();

            embed.setTitle("End of exercise");
            embed.setDescription("You reached the end of your exercise! 💪");
            embed.setColor(Constants.EMBED_COLOR);
            embed.setImage("https://media.tenor.com/MDTYbqilAxgAAAAC/ogvhs-high-five.gif");

            if (complete) userService.addDayPoints(user.getId(), 20);

            bot.entity.User savedUser = userService.getUser(user.getId());

            channel.sendMessageEmbeds(List.of(embed.build(), streakView.getStreak(savedUser)))
                    .queue(
                            success -> {
                                channel.deleteMessageById(success.getId())
                                        .queueAfter(30L, TimeUnit.SECONDS);
                            });
        }

        removeLastMessage();
        quizes.remove(getUser().getId());
    }

    public static Optional<FlashcardQuiz> getInstance(String discordId) {
        return Optional.ofNullable(quizes.get(discordId));
    }
}
