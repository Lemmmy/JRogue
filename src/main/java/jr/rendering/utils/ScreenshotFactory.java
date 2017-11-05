package jr.rendering.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotFactory {
	private static final String SCREENSHOT_DIR = "screenshots";
	private static final SimpleDateFormat SCREENSHOT_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
	
	public static void saveScreenshot(String name) {
		String date = SCREENSHOT_DATE_FORMAT.format(new Date());
		String path = String.format("%s/screenshot-%s-%s.png", SCREENSHOT_DIR, date, name);
		
		int i = 0;
		
		while (Gdx.files.local(path).exists()) {
			path = String.format("%s/screenshot-%s-%s-%d.png", SCREENSHOT_DIR, date, name, ++i);
		}
		
		Pixmap pixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		PixmapIO.writePNG(Gdx.files.local(path), pixmap);
		pixmap.dispose();
	}
	
	private static Pixmap getScreenshot(int x, int y, int width, int height) {
		return ScreenUtils.getFrameBufferPixmap(x, y, width, height); // TODO: flipY
	}
}
