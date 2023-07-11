package bot.entity.word;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("cache_words")
public class CachedWord extends Word {

    @Getter @Setter private long lastUpdate;

    @SerializedName("success")
    private boolean success;

    @SerializedName("results")
    private List<Definition> results;

    @SerializedName("syllables")
    private Syllables syllables;

    @SerializedName("pronunciation")
    @Getter
    @Setter
    private String pronunciation;

    @SerializedName("frequency")
    private double frequency;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Definition> getResults() {
        return results;
    }

    public void setResults(List<Definition> results) {
        this.results = results;
    }

    public Syllables getSyllables() {
        return syllables;
    }

    public void setSyllables(Syllables syllables) {
        this.syllables = syllables;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public static class Definition {
        @Getter @Setter private int index;

        @SerializedName("definition")
        private String definition;

        @SerializedName("partOfSpeech")
        private String partOfSpeech;

        @SerializedName("synonyms")
        private List<String> synonyms;

        @SerializedName("inRegion")
        private List<String> inRegion;

        @SerializedName("typeOf")
        private List<String> typeOf;

        @SerializedName("hasTypes")
        private List<String> hasTypes;

        @SerializedName("pertainsTo")
        private List<String> pertainsTo;

        @SerializedName("memberOf")
        private List<String> memberOf;

        @SerializedName("derivation")
        private List<String> derivation;

        @SerializedName("examples")
        private List<String> examples;

        @SerializedName("similarTo")
        private List<String> similarTo;

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }

        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public void setPartOfSpeech(String partOfSpeech) {
            this.partOfSpeech = partOfSpeech;
        }

        public List<String> getSynonyms() {
            return synonyms;
        }

        public void setSynonyms(List<String> synonyms) {
            this.synonyms = synonyms;
        }

        public List<String> getInRegion() {
            return inRegion;
        }

        public void setInRegion(List<String> inRegion) {
            this.inRegion = inRegion;
        }

        public List<String> getTypeOf() {
            return typeOf;
        }

        public void setTypeOf(List<String> typeOf) {
            this.typeOf = typeOf;
        }

        public List<String> getHasTypes() {
            return hasTypes;
        }

        public void setHasTypes(List<String> hasTypes) {
            this.hasTypes = hasTypes;
        }

        public List<String> getPertainsTo() {
            return pertainsTo;
        }

        public void setPertainsTo(List<String> pertainsTo) {
            this.pertainsTo = pertainsTo;
        }

        public List<String> getMemberOf() {
            return memberOf;
        }

        public void setMemberOf(List<String> memberOf) {
            this.memberOf = memberOf;
        }

        public List<String> getDerivation() {
            return derivation;
        }

        public void setDerivation(List<String> derivation) {
            this.derivation = derivation;
        }

        public List<String> getExamples() {
            return examples;
        }

        public void setExamples(List<String> examples) {
            this.examples = examples;
        }

        public List<String> getSimilarTo() {
            return similarTo;
        }

        public void setSimilarTo(List<String> similarTo) {
            this.similarTo = similarTo;
        }
    }

    public static class Syllables {

        @SerializedName("count")
        private int count;

        @SerializedName("list")
        private List<String> list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }

    public static class Pronunciation {
        @Getter @Setter private String content;
    }
}
