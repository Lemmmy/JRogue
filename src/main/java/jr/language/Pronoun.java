package jr.language;

public class Pronoun extends Noun {
    public Pronoun(String word) {
        super(word);
    }
    
    @Override
    public Class<? extends Word> getCloneClass() {
        return Pronoun.class;
    }
}
