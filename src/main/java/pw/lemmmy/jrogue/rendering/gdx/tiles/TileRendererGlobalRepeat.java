package pw.lemmmy.jrogue.rendering.gdx.tiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;

public class TileRendererGlobalRepeat extends TileRenderer {
    private final TextureRegion texture;
    private static ShaderProgram program;

    private float offsetX, offsetY;
    private float scaleX = 0.2f, scaleY = 0.2f;

    public TileRendererGlobalRepeat(TextureRegion texture) {
        this.texture = texture;
        texture.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        if (program == null) {
            program = new ShaderProgram(
                Gdx.files.classpath("shaders/global_repeat.vert.glsl"),
                Gdx.files.classpath("shaders/global_repeat.frag.glsl")
            );

            if (!program.isCompiled()) {
                JRogue.getLogger().error("Shader compilation failed: {}", program.getLog());
            }
        }
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetX(float x) {
        this.offsetX = x;
    }

    public void setOffsetY(float y) {
        this.offsetY = y;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    @Override
    public void draw(SpriteBatch batch, Dungeon dungeon, int x, int y) {
        ShaderProgram previousShader = batch.getShader();
        batch.setShader(program);

        final Matrix4 levelProjMat = new Matrix4();
        levelProjMat.setToOrtho2D(
            getOffsetX(), getOffsetY(),
            dungeon.getLevel().getWidth() * TileMap.TILE_WIDTH * scaleX,
            dungeon.getLevel().getHeight() * TileMap.TILE_HEIGHT * scaleY
        );

        program.setUniformMatrix("u_proj", levelProjMat);
        drawTile(batch, texture, x, y);
        batch.setShader(previousShader);
    }

    public static void dispose() {
        if (program != null) {
            program.dispose();
        }
    }
}
