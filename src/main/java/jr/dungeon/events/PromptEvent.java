package jr.dungeon.events;

import jr.dungeon.io.Prompt;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PromptEvent extends Event {
	private Prompt prompt;
}
