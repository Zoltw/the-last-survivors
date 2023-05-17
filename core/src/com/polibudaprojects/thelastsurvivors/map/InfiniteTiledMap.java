package com.polibudaprojects.thelastsurvivors.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.polibudaprojects.thelastsurvivors.assets.Assets;

public class InfiniteTiledMap {
    private final TiledMapRenderer[][] renderers;
    private final TiledMap[][] maps;
    private final int gridWidth;
    private final int gridHeight;
    private final float zoomLevel;
    private final CollisionDetector collisionDetector;
    private int mapWidth;
    private int mapHeight;

    public InfiniteTiledMap(String mapFileName, int gridWidth, int gridHeight, float zoomLevel) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.zoomLevel = zoomLevel;
        renderers = new TiledMapRenderer[gridWidth][gridHeight];
        maps = new TiledMap[gridWidth][gridHeight];

        TiledMap tiledMap = Assets.get(mapFileName, TiledMap.class);
        collisionDetector = new CollisionDetector(tiledMap);

        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                maps[i][j] = tiledMap;
                renderers[i][j] = new OrthogonalTiledMapRenderer(maps[i][j]);
                mapWidth = maps[i][j].getProperties().get("width", Integer.class) * maps[i][j].getProperties().get("tilewidth", Integer.class);
                mapHeight = maps[i][j].getProperties().get("height", Integer.class) * maps[i][j].getProperties().get("tileheight", Integer.class);
            }
        }
    }

    public TiledMap getTiledMap() {
        return maps[0][0];
    }

    public void update(OrthographicCamera cam, Vector2 playerCenterPosition) {
        int centerX = (int) (playerCenterPosition.x / mapWidth) % gridWidth;
        int centerY = (int) (playerCenterPosition.y / mapHeight) % gridHeight;

        cam.position.set(centerX * mapWidth + playerCenterPosition.x % mapWidth, centerY * mapHeight + playerCenterPosition.y % mapHeight, 0);
        cam.update();
    }

    public void render(OrthographicCamera cam) {
        OrthographicCamera mapCamera = new OrthographicCamera(cam.viewportWidth, cam.viewportHeight);
        mapCamera.zoom = zoomLevel;
        float adjustedX = (cam.position.x - (cam.viewportWidth / 2)) * zoomLevel;
        float adjustedY = (cam.position.y - (cam.viewportHeight / 2)) * zoomLevel;
        mapCamera.position.set(adjustedX, adjustedY, 0);
        mapCamera.update();

        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                renderers[i][j].setView(mapCamera);
                renderers[i][j].render();
            }
        }
    }

    public void dispose() {
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                maps[i][j].dispose();
            }
        }
    }
}
