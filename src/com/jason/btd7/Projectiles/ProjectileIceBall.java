package com.jason.btd7.Projectiles;

import com.jason.btd7.Enemy;
import com.jason.btd7.Projectile;
import com.jason.btd7.ProjectileType;
import org.newdawn.slick.opengl.Texture;

public class ProjectileIceBall extends Projectile {


    public ProjectileIceBall(ProjectileType type, Enemy target, float x, float y, int width, int height) {
        super(type, target, x, y, width, height);
    }

    @Override
    public void doDamage(){
        super.getTarget().setSpeed(4f);
        super.doDamage();
    }

}
