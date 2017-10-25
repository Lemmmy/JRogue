package jr.rendering.gdxvox.debugger;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import jr.rendering.base.ui.windows.Window;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.objects.AbstractObjectRendererManager;
import jr.rendering.gdxvox.objects.entities.EntityRendererManager;
import jr.rendering.gdxvox.objects.tiles.TileRendererManager;
import jr.rendering.gdxvox.screens.VoxGameScreen;

import java.util.Arrays;

public class VoxProfiler extends Window {
	private static final float STATS_UPDATE_SECONDS = 0.25f;
	
	private VoxGameScreen gameScreen;
	private SceneContext sceneContext;
	
	private TileRendererManager tileRendererManager;
	private EntityRendererManager entityRendererManager;
	
	private Camera camera;
	private Cell<Table> statsTableCell;
	
	public VoxProfiler(Stage stage, Skin skin, VoxGameScreen gameScreen) {
		super(stage, skin);
		
		this.gameScreen = gameScreen;
	}
	
	@Override
	public String getTitle() {
		return "Vox Profiler";
	}
	
	@Override
	public void populateWindow() {
		if (gameScreen.getSceneContext() == null) return;
		
		initialiseSceneContext();
		initialiseWindowSettings();
		initialiseMainTable();
	}
	
	private void initialiseSceneContext() {
		sceneContext = gameScreen.getSceneContext();
		
		tileRendererManager = sceneContext.tileRendererManager;
		entityRendererManager = sceneContext.entityRendererManager;
		
		camera = sceneContext.sceneCamera;
	}
	
	private void initialiseWindowSettings() {
		getWindowBorder().setSize(580, 96);
	}
	
	private void initialiseMainTable() {
		Table mainTable = new Table();
		
		initialiseStatsTables(mainTable);
		initialiseStatsTimer(mainTable);
		
		getWindowBorder().getContentTable().add(mainTable).grow().left().top();
	}
	
	private void initialiseStatsTables(Table container) {
		Table statsTable = new Table();
		
		initialiseStatsHeaders(statsTable);
		
		initialiseStatsRow(statsTable, "Tiles", tileRendererManager);
		initialiseStatsRow(statsTable, "Entities", entityRendererManager);
		initialiseStatsRow(statsTable, "Total", tileRendererManager, entityRendererManager);
		
		if (statsTableCell == null) {
			statsTableCell = container.add(statsTable).grow().left().top();
		} else {
			statsTableCell.setActor(statsTable);
		}
	}
	
	private void initialiseStatsHeaders(Table container) {
		addLabel(container, "manager");
		addLabel(container, "static");
		addLabel(container, "/");
		addLabel(container, "dynamic");
		addLabel(container, "total");
		addLabel(container, "/");
		addLabel(container, "batches");
		addLabel(container, "/");
		
		container.row();
	}
	
	private void initialiseStatsRow(Table container, String name, AbstractObjectRendererManager... managers) {
		initialiseStatsRow(
			container, name,
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getStaticVisibleVoxelCount).sum(),
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getStaticVoxelCount).sum(),
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getDynamicVoxelCount).sum(),
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getVisibleBatchCount).sum(),
			Arrays.stream(managers).mapToInt(AbstractObjectRendererManager::getBatchCount).sum()
		);
	}
	
	private void initialiseStatsRow(Table container,
									String name,
									int staticVisibleVoxels,
									int staticVoxels,
									int dynamicVoxels,
									int visibleBatches, int batches) {
		addLabel(container, name);
		addLabel(container, "P_GREEN_3", staticVisibleVoxels);
		addLabel(container, "P_GREEN_2", staticVoxels);
		addLabel(container, "P_ORANGE_3", dynamicVoxels);
		addLabel(container, "P_ORANGE_1", staticVisibleVoxels + dynamicVoxels);
		addLabel(container, "P_RED", staticVoxels + dynamicVoxels);
		addLabel(container, "P_BLUE_2", visibleBatches);
		addLabel(container, "P_BLUE_1", batches);
		
		container.row();
	}
	
	private void initialiseStatsTimer(Table container) {
		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				initialiseStatsTables(container);
			}
		}, STATS_UPDATE_SECONDS, STATS_UPDATE_SECONDS);
	}
	
	private void addLabel(Table container, String label) {
		container.add(new Label(label, getSkin())).left().padRight(4);
	}
	
	private void addLabel(Table container, String colour, String label) {
		container.add(new Label(String.format("[%s]%s[]", colour, label), getSkin())).left().padRight(4);
	}
	
	private void addLabel(Table container, String colour, int label) {
		container.add(new Label(String.format("[%s]%,d[]", colour, label), getSkin())).left().padRight(16);
	}
}
