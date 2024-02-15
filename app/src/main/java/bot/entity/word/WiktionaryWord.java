package bot.entity.word;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;

@Getter
public class WiktionaryWord extends Word {
    /* The word's definitions exactly as they are from Wiktionary */
    @SerializedName("definitions")
    private List<WordDefinition> definitions;
}
