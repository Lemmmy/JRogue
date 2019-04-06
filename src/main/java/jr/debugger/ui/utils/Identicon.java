package jr.debugger.ui.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import jr.rendering.assets.Assets;
import jr.rendering.assets.RegisterAssetManager;
import jr.rendering.utils.ImageUtils;

import static jr.rendering.assets.Textures.hudFile;

public class Identicon extends BaseDrawable {
	private static final Color[] COLOURS = new Color[] {
		new Color(0x720D93FF),
		new Color(0x85ED6DFF),
		new Color(0xC90909FF),
		new Color(0x3BBA1EFF),
		new Color(0xB81EABFF),
		new Color(0x025A77FF),
		new Color(0xCD660AFF),
		new Color(0xFFF10CFF),
		new Color(0xF356E6FF),
		new Color(0x0B4FB5FF),
		new Color(0x28B5E3FF),
		new Color(0xF85B5BFF),
		new Color(0x3177E0FF),
		new Color(0xF47F18FF),
		new Color(0x560670FF),
		new Color(0x047CA4FF),
		new Color(0xAE3FD2FF),
		new Color(0xA24E04FF),
		new Color(0xFFFDD7FF),
		new Color(0x980C8CFF),
		new Color(0x0B1B93FF),
		new Color(0xA20404FF),
		new Color(0x52D234FF),
		new Color(0xE7BA06FF),
		new Color(0xFFF88BFF),
		new Color(0xE13ED4FF),
		new Color(0x1A2BACFF),
		new Color(0x8E25B1FF),
		new Color(0x0593C1FF),
		new Color(0x2B9F10FF),
		new Color(0xED1212FF),
		new Color(0xFF9B42FF)
	};
	
	private static final int SHAPE_COUNT = 16;
	private static final int SHAPE_WIDTH = 8;
	private static final int SHAPE_HEIGHT = 8;
	private static final int SHAPE_START_X = 0;
	private static final int SHAPE_START_Y = 200;
	
	public static final int SHAPE_PADDING = 2;
	
	private TextureRegion shape1, shape2;
	private Color bg1, fg1, bg2, fg2, oldColour = new Color();
	
	public Identicon(TextureRegion shape1,
					 Color bg1,
					 Color fg1,
					 TextureRegion shape2,
					 Color bg2,
					 Color fg2) {
		this.shape1 = shape1;
		this.bg1 = bg1;
		this.fg1 = fg1;
		this.shape2 = shape2;
		this.bg2 = bg2;
		this.fg2 = fg2;
		
		this.setMinWidth(SHAPE_WIDTH + SHAPE_PADDING + SHAPE_WIDTH);
		this.setMinHeight(SHAPE_HEIGHT);
	}
	
	public static Identicon getIdenticon(int code) {
		int ibg1 		= (code & 0b11111000000000000000000000000000) >>> 27;
		int ifg1 		= (code & 0b00000111110000000000000000000000) >>> 22;
		int ishape1 	= (code & 0b00000000001111000000000000000000) >>> 18;
		int isecurity1 	= (code & 0b00000000000000110000000000000000) >>> 16;
		
		int ibg2 		= (code & 0b00000000000000001111100000000000) >>> 11;
		int ifg2 		= (code & 0b00000000000000000000011111000000) >>> 6;
		int ishape2 	= (code & 0b00000000000000000000000000111100) >>> 2;
		int isecurity2 	=  code & 0b00000000000000000000000000000011;
		
		TextureRegion shape1 = Shapes.getShape(ishape1);
		Color bg1 = COLOURS[ibg1 % COLOURS.length];
		Color fg1 = COLOURS[ifg1 % COLOURS.length];
		
		if (ibg1 == ifg1) fg1 = COLOURS[(ifg1 + isecurity1) % COLOURS.length];
		
		TextureRegion shape2 = Shapes.getShape(ishape2);
		Color bg2 = COLOURS[ibg2 % COLOURS.length];
		Color fg2 = COLOURS[ifg2 % COLOURS.length];
		
		if (ibg2 == ifg2) fg2 = COLOURS[(ifg2 + isecurity2) % COLOURS.length];
		
		return new Identicon(shape1, bg1, fg1, shape2, bg2, fg2);
	}
	
	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		oldColour.set(batch.getColor());
		
		batch.setColor(bg1);
		batch.draw(Shapes.getBlank(), x, y);
		batch.setColor(fg1);
		batch.draw(shape1, x, y);
		
		batch.setColor(bg2);
		batch.draw(Shapes.getBlank(), x + SHAPE_PADDING + SHAPE_WIDTH, y);
		batch.setColor(fg2);
		batch.draw(shape2, x + SHAPE_PADDING + SHAPE_WIDTH, y);
		
		batch.setColor(oldColour);
	}
	
	@RegisterAssetManager
	public static class Shapes {
		private static TextureRegion[] shapes = new TextureRegion[SHAPE_COUNT];
		
		public static void loadAssets(Assets assets) {
			assets.textures.loadPacked(hudFile("debug/identicon_shapes"), t ->
				ImageUtils.loadSheet(t, shapes, SHAPE_COUNT, 1, SHAPE_WIDTH, SHAPE_HEIGHT));
		}
		
		public static TextureRegion getBlank() {
			return shapes[0];
		}
		
		public static TextureRegion getShape(int index) {
			return shapes[index % shapes.length];
		}
	}
}
