package pw.lemmmy.jrogue.rendering.gdx.utils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import pw.lemmmy.jrogue.JRogue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MultiTextureRegionDrawable extends BaseDrawable {
    private final TextureRegionDrawable[] drawables;
    private float width, height;

    public MultiTextureRegionDrawable(TextureRegionDrawable[] drawables, float width, float height) {
        this.drawables = drawables;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        if (drawables != null) {
            for (TextureRegionDrawable d : drawables) {
                d.draw(batch, x, y, width, height);
            }
        }
    }

    @Override
    public float getMinWidth() {
        return width;
    }

    @Override
    public float getMinHeight() {
        return height;
    }
}
