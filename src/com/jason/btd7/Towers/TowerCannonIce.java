package com.jason.btd7.Towers;

import com.jason.btd7.Enemy;
import com.jason.btd7.Tile;
import com.jason.btd7.Tower;
import com.jason.btd7.TowerType;
import com.jason.btd7.UI.UI;
import java.util.concurrent.CopyOnWriteArrayList;


public class TowerCannonIce extends Tower {

    private float freezeTime, time;
    private CopyOnWriteArrayList<Enemy> enemiesInRange;

    public TowerCannonIce(TowerType type, Tile startTile, CopyOnWriteArrayList<Enemy> enemies) {
        super(type, startTile, enemies);
        this.enemiesInRange = new CopyOnWriteArrayList<>();
        this.freezeTime = 0.7f;
        this.time = 0;

    }


    @Override
    public void shoot(Enemy target) {
        for (Enemy e : enemiesInRange) {
            if(super.isInRange(e)) {
                e.setSpeed(0);
                e.setFrozen(true);
            }
        }
    }

    @Override
    public void update(){

        for (Enemy e : getEnemies()) {
            if(super.isInRange(e))
                enemiesInRange.add(e);
            if(e.freezeClock > freezeTime) {
                e.setFrozen(false);
                e.setSpeed(e.originalSpeed);
                e.freezeClock = 0;
            }
        }

        super.update();

    }



}
