package com.jason.btd7;

import com.jason.btd7.Towers.TowerCannonBlue;
import com.jason.btd7.Towers.TowerCannonIce;
import com.jason.btd7.UI.Button;
import com.jason.btd7.UI.UI;
import org.lwjgl.input.Mouse;

import static com.jason.btd7.helpers.Artist.*;

public class Game {

    private TileGrid grid;
    private Player player;
    private WaveManager waveManager;
    private UI towerPickerUI;
    private boolean leftMouseButtonDown;

    public Game(int[][] map){

        grid = new TileGrid(map);
        waveManager = new WaveManager(new Enemy(QuickLoad("bad"), grid.getTile(10, 6), grid, TILE_SIZE, TILE_SIZE, 60, 25), 2, 2);
        player = new Player(grid, waveManager);
        player.setup();
        this.leftMouseButtonDown = false;
        setupUI();

    }

    private void setupUI(){
        towerPickerUI = new UI();
        //towerPickerUI.addButton("CannonIce", "cannonIceGun", 0, 0);
        //towerPickerUI.addButton("CannonBlue", "cannonGunBlue", 64, 0);
        towerPickerUI.createMenu("TowerPicker", 1280, 0, 192, 960, 2, 0);
        towerPickerUI.getMenu("TowerPicker").addButton(new Button("CannonIce", QuickLoad("cannonIceGun"), 0, 0));
        towerPickerUI.getMenu("TowerPicker").addButton(new Button("CannonBlue", QuickLoad("cannonGunBlue"), 0, 0));
        towerPickerUI.getMenu("TowerPicker").addButton(new Button("CannonBlue", QuickLoad("cannonGunBlue"), 0, 0));
        towerPickerUI.getMenu("TowerPicker").addButton(new Button("CannonIce", QuickLoad("cannonIceGun"), 0, 0));
    }

    private void updateUI(){
        towerPickerUI.draw();

        if(Mouse.next()) {
            boolean mouseClicked = Mouse.isButtonDown(0);

            if (mouseClicked && !leftMouseButtonDown) {
                if (towerPickerUI.getMenu("TowerPicker").isButtonClicked("CannonIce"))
                    player.pickTower(new TowerCannonIce(TowerType.CannonIce, grid.getTile(0, 0), waveManager.getCurrentWave().getEnemyList()));
                if (towerPickerUI.getMenu("TowerPicker").isButtonClicked("CannonBlue"))
                    player.pickTower(new TowerCannonBlue(TowerType.CannonBlue, grid.getTile(0, 0), waveManager.getCurrentWave().getEnemyList()));
            }
        }
        leftMouseButtonDown = Mouse.isButtonDown(0);
    }

    public void update(){
        grid.draw();
        waveManager.update();
        player.update();
        towerPickerUI.draw();
        DrawQuadTex(QuickLoad("menu_BG"), 1280, 0, 192, 960);
        updateUI();

    }

}
