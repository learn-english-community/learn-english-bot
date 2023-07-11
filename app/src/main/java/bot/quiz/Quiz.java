package bot.quiz;

import bot.quiz.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

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
    @Setter
    private int currentQuestionId;

    @Setter
    private Question<T> currentQuestion;

    public Quiz() {}

    public abstract void showQuestion(int number);

    public abstract void showAnswer(int number);

    public abstract void start();
}
