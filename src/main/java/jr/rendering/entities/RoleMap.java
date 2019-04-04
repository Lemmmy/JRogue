package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.entities.player.roles.Role;
import jr.dungeon.entities.player.roles.RoleWizard;
import jr.rendering.assets.Assets;
import jr.rendering.assets.Textures;
import jr.rendering.assets.UsesAssets;
import lombok.Getter;

@Getter
public enum RoleMap implements UsesAssets {
	WIZARD(RoleWizard.class, "player_wizard");
	
	private Class<? extends Role> roleClass;
	private String fileName;
	private TextureRegion roleTexture;
	
	RoleMap(Class<? extends Role> roleClass, String fileName) {
		this.roleClass = roleClass;
		this.fileName = fileName;
	}
	
	@Override
	public void onLoad(Assets assets) {
		assets.textures.loadPacked(Textures.entityFile(fileName), t -> roleTexture = t);
	}
}
