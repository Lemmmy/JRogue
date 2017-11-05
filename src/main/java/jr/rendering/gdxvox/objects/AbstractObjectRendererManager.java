package jr.rendering.gdxvox.objects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.Level;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.EventPriority;
import jr.dungeon.events.LevelChangeEvent;
import jr.rendering.gdxvox.components.RenderPass;
import jr.rendering.gdxvox.context.SceneContext;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class AbstractObjectRendererManager<
	ObjectK, ObjectV,
	BatchT extends VoxelBatch,
	RendererT extends AbstractObjectRenderer>
	implements EventListener, Disposable {
	public static final int CHUNK_SIZE_X = 8;
	public static final int CHUNK_SIZE_Y = 3; // for bounding boxes
	public static final int CHUNK_SIZE_Z = 8;
	
	protected Map<ObjectK, RendererT> objectRendererMap = new HashMap<>();
	
	public int chunksX, chunksY;
	private BoundingBox[] chunkBounds;
	
	private BatchT[] staticBatches;
	
	private BatchT dynamicBatch;
	
	protected SceneContext scene;
	
	public AbstractObjectRendererManager(SceneContext scene) {
		this.scene = scene;
	}
	
	public void initialise() {
		findLists();
		initialiseBatches();
	}
	
	public void initialiseBatches() {
		Level level = scene.getLevel();
		
		chunksX = (int) Math.ceil((float) level.getWidth() / (float) CHUNK_SIZE_X);
		chunksY = (int) Math.ceil((float) level.getHeight() / (float) CHUNK_SIZE_Z);
		
		staticBatches = initialiseStaticBatchArray(chunksX * chunksY);
		chunkBounds = new BoundingBox[staticBatches.length];
		
		for (int i = 0; i < staticBatches.length; i++) {
			int x = i % chunksX;
			int y = i / chunksX;
			
			staticBatches[i] = initialiseBatch();
			chunkBounds[i] = new BoundingBox(
				new Vector3(x * CHUNK_SIZE_X, 0, y * CHUNK_SIZE_Z),
				new Vector3((x + 1) * CHUNK_SIZE_X, CHUNK_SIZE_Y, (y + 1) * CHUNK_SIZE_Z)
			);
		}
		
		dynamicBatch = initialiseBatch();
	}
	
	public abstract BatchT initialiseBatch();
	public abstract BatchT[] initialiseStaticBatchArray(int size);
	
	private void findLists() {
		JRogue.getReflections().getMethodsAnnotatedWith(getListAnnotationClass())
			.forEach(listMethod -> {
				listMethod.setAccessible(true);
				
				if (!listMethod.getParameterTypes()[0].isAssignableFrom(getClass())) {
					throw new RuntimeException("List method " + listMethod + " has wrong parameter types");
				}
				
				try {
					listMethod.invoke(null, this);
				} catch (IllegalAccessException | InvocationTargetException e) {
					ErrorHandler.error("Error finding renderer lists", e);
				}
			});
	}
	
	@SuppressWarnings("unchecked")
	public void addRenderers(RendererT renderer, ObjectK... types) {
		for (ObjectK type : types) {
			renderer.objectKeys.add(type);
			objectRendererMap.put(type, renderer);
			renderer.onAddedToMap(this, scene);
			
			JRogue.getLogger().info("Added renderer for {}", type.toString());
		}
	}
	
	public void checkCulling(Camera camera) {
		for (int i = 0; i < chunkBounds.length; i++) {
			BatchT batch = staticBatches[i];
			BoundingBox bounds = chunkBounds[i];
			
			batch.setCulled(!camera.frustum.boundsInFrustum(bounds));
		}
	}
	
	public void renderAll(RenderPass pass, Camera camera) {
		if (pass == RenderPass.MAIN_PASS) checkCulling(camera);
		
		for (BatchT staticBatch : staticBatches) {
			staticBatch.render(pass, camera, scene);
		}
		
		dynamicBatch.render(pass, camera, scene);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	protected void onLevelChange(LevelChangeEvent e) {
		for (BatchT staticBatch : staticBatches) {
			staticBatch.clear(); // TODO: handle rechunking if level size changes
		}
		
		dynamicBatch.clear();
		
		findObjects(e.getLevel());
	}
	
	public abstract void findObjects(Level level);
	
	public abstract Class<? extends ObjectK> getObjectKeyClass();
	public abstract Class<? extends ObjectV> getObjectValueClass();
	public abstract Class<? extends Annotation> getListAnnotationClass();
	
	public int getStaticVoxelCount() {
		return Arrays.stream(staticBatches)
			.mapToInt(VoxelBatch::getInstanceCount)
			.sum();
	}
	
	public int getStaticVisibleVoxelCount() {
		return Arrays.stream(staticBatches)
			.filter(batch -> !batch.isCulled())
			.mapToInt(VoxelBatch::getInstanceCount)
			.sum();
	}
	
	public int getDynamicVoxelCount() {
		return dynamicBatch.getInstanceCount();
	}
	
	public int getBatchCount() {
		return staticBatches.length + 1; // extra 1 is the dynamic batch
	}
	
	public int getVisibleBatchCount() {
		return (int) Arrays.stream(staticBatches)
			.filter(batch -> !batch.isCulled())
			.count() + 1; // extra 1 is the dynamic batch
	}
	
	public BatchT getStaticBatch(int x, int y) {
		int chunkX = (int) Math.floor(x / CHUNK_SIZE_X);
		int chunkY = (int) Math.floor(y / CHUNK_SIZE_Z);
		
		return staticBatches[chunkY * chunksX + chunkX];
	}
	
	public Optional<BatchT> getBatchContainingObject(ObjectV object) {
		if (dynamicBatch.contains(object)) return Optional.of(dynamicBatch);
		
		for (BatchT staticBatch : staticBatches) {
			if (staticBatch.contains(object)) return Optional.of(staticBatch);
		}
		
		return Optional.empty();
	}
	
	public void removeObjectInstance(ObjectV object) {
		getBatchContainingObject(object).ifPresent(batch -> batch.remove(object));
	}
	
	@Override
	public void dispose() {
		dynamicBatch.dispose();
	}
}
