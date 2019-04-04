package jr.rendering.items;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MaterialMap {
	WOOD("wood"),
	STONE("stone"),
	BRONZE("bronze"),
	IRON("iron"),
	STEEL("steel"),
	SILVER("silver"),
	GOLD("gold"),
	MITHRIL("mithril"),
	ADAMANTITE("adamantite");
	
	private String fileName;
}
