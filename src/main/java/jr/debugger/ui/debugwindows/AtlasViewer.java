package jr.debugger.ui.debugwindows;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectMap;
import jr.rendering.assets.Assets;
import jr.rendering.ui.windows.Window;

public class AtlasViewer extends Window {
	private Assets assets;
	
	private Label currentHoverLabel;
	
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
		getWindowBorder().setSize(580, 640);
		
		currentHoverLabel = new Label(" \n \n ", getSkin(), "windowStyleLoweredMarkup");
		getWindowBorder().getContentTable().add(currentHoverLabel).pad(2).spaceBottom(8).growX().left().top().row();
		
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
				String pageName = String.format(
					"%s - page %,d (%,d x %,d)",
					name, i,
					texture.getWidth(), texture.getHeight()
				);
				pageTable.add(new Label(pageName, getSkin())).left().row();
				
				Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
				image.addListener(new InputListener() {
					@Override
					public boolean mouseMoved(InputEvent event, float x, float y) {
						for (ObjectMap.Entry<String, PixmapPacker.PixmapPackerRectangle> entry : page.getRects()) {
							PixmapPacker.PixmapPackerRectangle rect = entry.value;
							
							if (rect.contains(x, texture.getHeight() - y)) {
								currentHoverLabel.setText(String.format(
									"[P_CYAN_1]%s[]\n%,d, %,d (%,d x %,d)\n[P_GREY_3]in %s[]",
									entry.key,
									(int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height,
									pageName
								));
								break;
							}
						}
						
						return false;
					}
					
					@Override
					public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
						currentHoverLabel.setText(" \n \n ");
					}
				});
				
				pageTable.add(image).left();
			} else {
				pageTable.add(new Label(String.format("%s - page %,d ([P_RED]NULL[])", name, i), getSkin())).left().row();
			}
			
			atlasTable.add(pageTable).left().row();
		}
	}
}
