package jr.language;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Noun extends Word<Noun> {
	private boolean uncountable;
	
	public Noun(String word) {
		super(word);
	}
	
	@Override
	public Class<? extends Word> getCloneClass() {
		return Noun.class;
	}
	
	@Override
	public Noun clone() {
		return super.clone().setUncountable(uncountable);
	}
}
