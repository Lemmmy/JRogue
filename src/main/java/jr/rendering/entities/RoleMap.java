package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.entities.player.roles.Role;
import jr.dungeon.entities.player.roles.RoleWizard;
import jr.rendering.utils.ImageLoader;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleMap {
	WIZARD(
		RoleWizard.class,
		ImageLoader.getImageFromSheet(
			"textures/entities.png",
			1, 0,
			EntityMap.ENTITY_WIDTH, EntityMap.ENTITY_HEIGHT,
			false
		)
	);
	
	private Class<? extends Role> roleClass;
	private TextureRegion roleTexture;
}
