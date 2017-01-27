package jr.dungeon.events;

import jr.dungeon.Prompt;

public class PromptEvent extends DungeonEvent {
	private Prompt prompt;
	
	public PromptEvent(Prompt prompt) {
		this.prompt = prompt;
	}
	
	public Prompt getPrompt() {
		return prompt;
	}
}
