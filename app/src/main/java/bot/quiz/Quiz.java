package bot.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class Quiz<T> {

    /**
     * Represents the person who is taking the quiz
     */
    protected User user;

    protected Map<Integer, Question<T>> questions;
    /**
     * The question that the user is currently in
     */
    private int currentQuestion;

    public Quiz() {}

    public abstract void showQuestion(int number, InteractionHook hook, List<Button> buttons);

    public abstract void showAnswer(int number, InteractionHook hook, List<Button> buttons);

    public abstract void start();
}
