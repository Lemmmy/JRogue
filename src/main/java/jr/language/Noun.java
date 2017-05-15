package jr.language;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class Noun extends Word<Noun> {
	public Noun(String word) {
		super(word);
	}
	
	@Override
	public Class<? extends Word> getCloneClass() {
		return Noun.class;
	}
}
