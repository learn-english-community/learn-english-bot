package bot.entity.word;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WordPronunciation {
    @SerializedName("audio")
    private List<String> audio;

    @SerializedName("text")
    private List<String> text;
}
