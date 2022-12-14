package com.jason.btd7;



import com.jason.btd7.UI.UI;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

import javax.sql.rowset.spi.SyncFactory;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.jason.btd7.Tower.TargetType.*;
import static com.jason.btd7.helpers.Artist.*;

import static com.jason.btd7.helpers.Clock.Delta;

public abstract class Tower implements Entity{

    private float x, y, timeSinceLastShot, firingSpeed, angle;
    private int width, height, range, cost;
    public Enemy target;
    private Texture[] textures;
    private CopyOnWriteArrayList<Enemy> enemies;
    private boolean targeted, leftMouseButtonDown;
    private Texture rangeTexture;
    public ArrayList<Projectile> projectiles;
    public TowerType type;
    public UI.TowerMenu towerMenu;
    public UI targetUI;
    public int turn;
    enum TargetType{
        First,
        Last,
        Close,
        Strong;
    }
    public TargetType[] towerTargeting = {First, Last, Close, Strong};
    public int towerTargetingIndex;

    protected boolean first;

    public Tower(TowerType type, Tile startTile, CopyOnWriteArrayList<Enemy> enemies){
        this.type = type;
        this.textures = type.textures;
        this.rangeTexture = QuickLoad("range");
        this.range = type.range;
        this.cost = type.cost;
        this.x = startTile.getX();
        this.y = startTile.getY();
        this.width = startTile.getWidth();
        this.height = startTile.getHeight();
        this.enemies = enemies;
        this.targeted = false;
        this.towerTargetingIndex = 0;
        this.targetUI = new UI();
        //this.towerTargeting = First;
        this.first = true;

        this.timeSinceLastShot = 0f;
        this.projectiles = new ArrayList<Projectile>();
        this.firingSpeed = type.firingSpeed;
        this.angle = 0f;
        this.turn = 0;
    }

    private Enemy acquireTarget(){

        if(towerTargeting[towerTargetingIndex] == Close) {

            Enemy closest = null;

            // Arbitrary distance (larger than map) to help sort enemy distances
            float closestDistance = 10000;
            // Go through each enemy and return the nearest one
            for (Enemy e : enemies) {
                if (isInRange(e) && findDistance(e) < closestDistance && e.isAlive()) {
                    closestDistance = findDistance(e);
                    closest = e;
                }
            }
            // If an enemy exists and is returned, targeted == true
            if (closest != null)
                targeted = true;
            return closest;
        }else if(towerTargeting[towerTargetingIndex] == First){

            Enemy first = null;
            float farthestP = 0f;
            // Get percent completed, then compare to the oldest percent, if >, then make that enemy 1st
            for (Enemy e : enemies) {
                if(isInRange(e) && e.getPercentComplete() > farthestP ) {
                    first = e;
                    farthestP = e.getPercentComplete();

                }
            }

            if (first != null)
                targeted = true;
            return first;

        }else if(towerTargeting[towerTargetingIndex] == Last){

            Enemy last = null;
            float lastP = 1000f;
            // Same as First, but keeps going to find the last enemy in range

            for (Enemy e : enemies) {
                if(isInRange(e) && e.getPercentComplete() < lastP) {
                    last = e;
                    lastP = e.getPercentComplete();
                }
            }

            if (last != null)
                targeted = true;
            return last;

        }else if(towerTargeting[towerTargetingIndex] == Strong){

            Enemy strongest = null;
            int largestBL = 0;

            // If enemy HP is larger than current largestHP, make largest, run until all enemies in range are checked
            for (Enemy e : enemies) {
                if(isInRange(e) && e.getHealth() > largestBL && e.isAlive()) {
                    largestBL = e.bloonLvl;
                    strongest = e;
                }
            }

            if (strongest != null)
                targeted = true;
            return strongest;
        }
        return null;
    }

    protected boolean isInRange(Enemy e){
        double xDif = e.getX() + 32 - (x + 32);
        double yDif = e.getY() + 32 - (y + 32);
        double distanceSquared = xDif * xDif + yDif * yDif;
        boolean collision = distanceSquared < (range + TILE_SIZE / 3) * (range + TILE_SIZE / 3);

        return collision;
    }

    private float findDistance(Enemy e){
        float xDistance = Math.abs(e.getX() - x);
        float yDistance = Math.abs(e.getY() - y);
        return xDistance + yDistance;
    }

    private float calculateAngle(){
        double angleTemp = Math.atan2(target.getY() - y, target.getX() - x);
        return (float) Math.toDegrees(angleTemp);
    }

    // Abstract method for "shoot", must @Override in subclasses
    public abstract void shoot(Enemy target);


    public void updateEnemyList(CopyOnWriteArrayList<Enemy> newList){
        enemies = newList;
    }

    public void update(){

        if(first) {
            towerMenu = new UI.TowerMenu("Menu", getXPlace());
            first = false;
        }else {

            if (towerMenu.towerMenu.isButtonClicked("XButton") && Mouse.isButtonDown(0))
                towerMenu.towerMenu.hide();

            if (towerMenu.towerMenu.isButtonClicked("LButton") && !Mouse.isButtonDown(0) && leftMouseButtonDown) {
                if (towerTargetingIndex == 0) {
                    towerTargetingIndex = 3;
                } else {
                    towerTargetingIndex -= 1;
                }
            }

            if (towerMenu.towerMenu.isButtonClicked("RButton") && !Mouse.isButtonDown(0) && leftMouseButtonDown) {
                if (towerTargetingIndex == 3) {
                    towerTargetingIndex = 0;
                } else {
                    towerTargetingIndex += 1;
                }
            }

            leftMouseButtonDown = Mouse.isButtonDown(0);

            if (towerMenu.towerMenu.visible) {
                DrawQuadTex(rangeTexture, x + TILE_SIZE / 2 - range, y + TILE_SIZE / 2 - range, range * 2.125f, range * 2.125f);

            }
            target = acquireTarget();
            if (target != null) {

                if (timeSinceLastShot > firingSpeed) {
                    angle = calculateAngle() - turn;
                    shoot(target);
                    timeSinceLastShot = 0;
                }
            }

            if (target == null || target.isAlive() == false || !isInRange(target))
                targeted = false;
            timeSinceLastShot += Delta();


            draw();
        }
    }

    public void draw() {
        DrawQuadTex(textures[0], x, y, width, height);
        // This makes the bullet under the shooting part & not the base
        for (Projectile p : projectiles) {
            if(!p.isOnScreen()){
                p.setAlive(false);
            }
            p.update();
        }
        if(textures.length > 1)
            for (int i = 1; i < textures.length; i++)
                DrawQuadTexRot(textures[i], x, y, width, height, angle);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Enemy getTarget(){
        return target;
    }

    public void setTimeSinceLastShot(int time){
        this.timeSinceLastShot = time;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void setProjectiles(ArrayList<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public int getCost(){
        return cost;
    }

    public CopyOnWriteArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public int getXPlace(){
        return (int) x / TILE_SIZE;
    }

    public int getYPlace(){
        return (int) y / TILE_SIZE;
    }

    public int getRange() {
        return range;
    }

    public Texture getRangeTexture(){
        return rangeTexture;
    }

    public void setEnemies(CopyOnWriteArrayList<Enemy> enemies){
        this.enemies = enemies;
    }
}
