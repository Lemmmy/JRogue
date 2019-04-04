package jr.debugger.ui.debugwindows;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.rendering.assets.Assets;
import jr.rendering.ui.windows.Window;

public class AtlasViewer extends Window {
	private Assets assets;
	
	public AtlasViewer(Stage stage, Skin skin, Assets assets) {
		super(stage, skin);
		
		this.assets = assets;
	}
	
	@Override
	public String getTitle() {
		return "Atlas Viewer";
	}
	
	@Override
	public void populateWindow() {
		getWindowBorder().setSize(580, 580);
		
		Table atlasTable = new Table();
		
		addFromPacker(atlasTable, assets.textures.getMainPacker(), "Main");
		addFromPacker(atlasTable, assets.textures.getBlobPacker(), "Baked blobs");
		
		ScrollPane scrollPane = new ScrollPane(atlasTable, getSkin());
		getWindowBorder().getContentTable().add(scrollPane).grow().left().top();
	}
	
	private void addFromPacker(Table atlasTable, PixmapPacker pixmapPacker, String name) {
		for (int i = 0; i < pixmapPacker.getPages().size; i++) {
			PixmapPacker.Page page = pixmapPacker.getPages().get(i);
			Texture texture = page.getTexture();
			
			Table pageTable = new Table();
			
			if (texture != null) {
				pageTable.add(new Label(String.format(
					"%s - page %,d (%,d x %,d)",
					name, i,
					texture.getWidth(), texture.getHeight()
				), getSkin())).left().row();
				pageTable.add(new Image(new TextureRegionDrawable(new TextureRegion(texture)))).left();
			} else {
				pageTable.add(new Label(String.format("%s - page %,d ([RED]NULL[])", name, i), getSkin())).left().row();
			}
			
			atlasTable.add(pageTable).left().row();
		}
	}
}
