package com.jason.btd7;

import org.newdawn.slick.opengl.Texture;

import static com.jason.btd7.helpers.Artist.QuickLoad;

public enum TowerType {

    CannonBlue(new Texture[] {QuickLoad("cannonBaseBlue"), QuickLoad("cannonGunBlue")}, ProjectileType.CannonBall,10, 1000, 3f),
    Cannon(new Texture[] {QuickLoad("cannonBase"), QuickLoad("cannonGun")}, ProjectileType.CannonBall, 30, 1000, 3),
    CannonIce(new Texture[] {QuickLoad("cannonIceBase"), QuickLoad("cannonIceGun")}, ProjectileType.IceBall,2, 1000, 0.25f);

    Texture[] textures;
    public ProjectileType projectileType;
    int damage, range;
    float firingSpeed;

    TowerType(Texture[] textures, ProjectileType projectileType, int damage, int range, float firingSpeed){
        this.textures = textures;
        this.projectileType = projectileType;
        this.damage = damage;
        this.range = range;
        this.firingSpeed = firingSpeed;

    }

}
