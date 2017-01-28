package jr.dungeon.events;

import jr.dungeon.Prompt;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PromptEvent extends DungeonEvent {
	private Prompt prompt;
}
