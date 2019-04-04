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
		getWindowBorder().setSize(580, 400);
		
		Table atlasTable = new Table();
		PixmapPacker pixmapPacker = assets.textures.getPixmapPacker();
		
		for (int i = 0; i < pixmapPacker.getPages().size; i++) {
			PixmapPacker.Page page = pixmapPacker.getPages().get(i);
			Texture texture = page.getTexture();
			
			Table pageTable = new Table();
			
			pageTable.add(new Label(String.format(
				"Page %,d (%,d x %,d)",
				i,
				texture.getWidth(), texture.getHeight()
			), getSkin())).left().row();
			pageTable.add(new Image(new TextureRegionDrawable(new TextureRegion(texture)))).left();
			
			atlasTable.add(pageTable);
		}
		
		ScrollPane scrollPane = new ScrollPane(atlasTable, getSkin());
		getWindowBorder().getContentTable().add(scrollPane).grow().left().top();
	}
}
