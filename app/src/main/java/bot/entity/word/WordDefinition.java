package bot.entity.word;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WordDefinition {
    @SerializedName("examples")
    private List<String> examples;

    @SerializedName("partOfSpeech")
    private String partOfSpeech;

    @SerializedName("relatedWords")
    private List<RelatedWord> relatedWords;

    @SerializedName("text")
    private List<String> text;

    @Getter
    public static class RelatedWord {
        @SerializedName("relationshipType")
        private String relationshipType;

        @SerializedName("words")
        private List<String> words;
    }
}
