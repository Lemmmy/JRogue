package jr.rendering.gdxvox.models.magicavoxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParseException;
import jr.rendering.gdxvox.models.magicavoxel.parser.VoxParser;
import jr.rendering.gdxvox.objects.tiles.TileRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelConverter {
	private static final ModelBuilder modelBuilder = new ModelBuilder();
	
	private static final Map<String, FramedModel> modelCache = new HashMap<>();
	
	public static FramedModel loadModel(String path) {
		if (modelCache.containsKey(path)) {
			return modelCache.get(path);
		}
		
		try {
			VoxelModel voxelModel = new VoxParser().parse(Gdx.files.internal(path).read());
			int[] palette = voxelModel.getPalette();
			
			Model[] frames = new Model[voxelModel.getFrames().size()];
			
			Texture paletteTexture = buildPalette(voxelModel);
			Material paletteMaterial = new Material(TextureAttribute.createDiffuse(paletteTexture));
			
			for (int i = 0; i < voxelModel.getFrames().size(); i++) {
				VoxelModel.Frame frame = voxelModel.getFrames().get(0);
				int[] volume = frame.getVoxels();
				int[] dims = new int[] {frame.getSizeX(), frame.getSizeY(), frame.getSizeZ()};
				int[] indexedVoxelCounts = frame.getIndexedVoxelCounts();
				
				modelBuilder.begin();
				
				MeshPartBuilder builder = modelBuilder.part("part", GL20.GL_TRIANGLES,
					VertexAttributes.Usage.Position |
						VertexAttributes.Usage.TextureCoordinates |
						VertexAttributes.Usage.Normal,
					paletteMaterial);
				
				for (int colour = 1; colour < palette.length; colour++) {
					if (indexedVoxelCounts[colour] == 0) continue;
					greedyMesh(volume, dims, colour, builder, path);
				}
				
				frames[i] = modelBuilder.end();
			}
			
			return new FramedModel(frames);
		} catch (VoxParseException | IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Texture buildPalette(VoxelModel model) {
		Pixmap pixmap = new Pixmap(256, 1, Pixmap.Format.RGBA8888);
		
		int[] palette = model.getPalette();
		for (int i = 0; i < palette.length; i++) {
			int colour = palette[i];
			pixmap.setColor(colour);
			pixmap.drawPixel(i, 0);
		}
		
		return new Texture(pixmap);
	}
	
	public static void greedyMesh(int[] volume, int[] dims, int colour, MeshPartBuilder builder, String path) {
		List<Float> vertices = new ArrayList<>();
		
		for (int d = 0; d < 3; ++d) {
			int i, j, k, l, w, h;
			int u = (d + 1) % 3;
			int v = (d + 2) % 3;
			int[] x = new int[] {0, 0, 0};
			int[] q = new int[] {0, 0, 0};
			boolean[] mask = new boolean[dims[u] * dims[v]];
			
			q[d] = 1;
			
			for (x[d] = -1; x[d] < dims[d];) {
				int n = 0;
				
				for (x[v] = 0; x[v] < dims[v]; ++x[v]) {
					for (x[u] = 0; x[u] < dims[u]; ++x[u]) {
						mask[n++] =
							(0 <= x[d] && getVoxel(volume, dims,
								x[0], x[1], x[2]) == colour) !=
							(x[d] < dims[d] - 1 && getVoxel(volume, dims,
								x[0] + q[0], x[1] + q[1], x[2] + q[2]) == colour);
					}
				}
				
				++x[d];
				
				n = 0;
				for (j = 0; j < dims[v]; ++j) {
					for (i = 0; i < dims[u];) {
						if (mask[n]) {
							for (w = 1; mask[n + w] && i + w < dims[u]; ++w) {}
							
							boolean done = false;
							for (h = 1; j + h < dims[v]; ++h) {
								for (k = 0; k < w; ++k) {
									if (!mask[n + k + h * dims[u]]) {
										done = true;
										break;
									}
								}
								
								if (done) break;
							}
							
							x[u] = i; x[v] = j;
							
							int[] du = new int[] {0, 0, 0}; du[u] = w;
							int[] dv = new int[] {0, 0, 0}; dv[v] = h;
							
							boolean airBehind = getVoxel(volume, dims,
								x[0] - q[0], x[1] - q[1], x[2] - q[2]) == 0;
							boolean airFront = getVoxel(volume, dims,
								x[0] + q[0], x[1] + q[1], x[2] + q[2]) == 0;
							
							if (!airFront && !airBehind) {
								airBehind = getVoxel(volume, dims,
									x[0] + du[0] + dv[0] - q[0],
									x[1] + du[1] + dv[1] - q[1],
									x[2] + du[2] + dv[2] - q[2]) == 0;
								
								airFront = getVoxel(volume, dims,
									x[0] + du[0] + dv[0] + q[0],
									x[1] + du[1] + dv[1] + q[1],
									x[2] + du[2] + dv[2] + q[2]) == 0;
							}
							
							if (airBehind) {
								Vector3 normal = new Vector3(+q[0], +q[1], +q[2]);
								
								builder.rect(
									addVertex(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2],
										normal, colour),
									addVertex(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2],
										normal, colour),
									addVertex(x[0] + du[0], x[1] + du[1], x[2] + du[2],
										normal, colour),
									addVertex(x[0], x[1], x[2],
										normal, colour)
								);
							}
							
							if (airFront) {
								Vector3 normal = new Vector3(-q[0], -q[1], -q[2]);
								
								builder.rect(
									addVertex(x[0], x[1], x[2],
										normal, 1),
									addVertex(x[0] + du[0], x[1] + du[1], x[2] + du[2],
										normal, 1),
									addVertex(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2],
										normal, 1),
									addVertex(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2],
										normal, 1)
								);
							}
							
							for (l = 0; l < h; ++l) {
								for (k = 0; k < w; ++k) {
									mask[n + k + l * dims[u]] = false;
								}
							}
							
							i += w; n += w;
						} else {
							++i; ++n;
						}
					}
				}
			}
		}
	}
	
	private static int getVoxel(int[] volume, int[] dims, int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 || x >= dims[0] || y >= dims[1] || z >= dims[2]) return 0;
		return volume[x + dims[0] * (y + dims[1] * z)];
	}
	
	private static MeshPartBuilder.VertexInfo addVertex(float x, float y, float z, Vector3 normal, int colour) {
		return new MeshPartBuilder.VertexInfo()
				.setUV(colour / 256f, 0)
				.setNor(normal)
				.setPos(x / TileRenderer.TILE_WIDTH, y / TileRenderer.TILE_HEIGHT, z / TileRenderer.TILE_DEPTH);
	}
}
