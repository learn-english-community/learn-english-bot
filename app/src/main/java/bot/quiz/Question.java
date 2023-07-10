package bot.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Question<T> implements Comparable<Question<T>> {

    private final int id;
    private T question;
    private T answer;

    public Question(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(Question o) {
        return o.id - this.id;
    }
}
