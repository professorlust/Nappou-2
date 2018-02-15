package org.atoiks.games.seihou2.entities;

import java.io.Serializable;

import java.awt.Color;

import org.atoiks.games.framework2d.IRender;
import org.atoiks.games.framework2d.IUpdate;
import org.atoiks.games.framework2d.IGraphics;

public final class Player implements IRender, IUpdate, Serializable {

    private static final long serialVersionUID = 293042L;

    private static final float RESPAWN_SHIELD_TIME = 3f;
    private static final float RESPAWN_SHIELD_OFF = -1f;

    public static final int RADIUS = 8;
    public static final int COLLISION_RADIUS = 2;
    public static final int HINT_COL_RADIUS = COLLISION_RADIUS + 2;

    public final IShield shield;

    private float x, y, dx, dy;
    private float speedScale = 1;
    private int hp = 5;
    private float respawnShieldTime = RESPAWN_SHIELD_OFF;

    public Player(float x, float y, IShield shield) {
        this.x = x;
        this.y = y;
        this.shield = shield;
    }

    @Override
    public <T> void render(final IGraphics<T> g) {
        this.shield.render(g);
        g.setColor(Color.cyan);
        if (isRespawnShieldActive()) {
            g.drawOval((int) (x - RADIUS), (int) (y - RADIUS), x + RADIUS, y + RADIUS);
        } else {
            g.fillOval((int) (x - RADIUS), (int) (y - RADIUS), x + RADIUS, y + RADIUS);
        }
        if (speedScale != 1) {
            g.setColor(Color.red);
            g.fillOval((int) (x - HINT_COL_RADIUS), (int) (y - HINT_COL_RADIUS), x + HINT_COL_RADIUS, y + HINT_COL_RADIUS);
        }
    }

    @Override
    public void update(final float dt) {
        this.shield.update(dt);
        this.shield.setX(this.x += this.dx * this.speedScale * dt);
        this.shield.setY(this.y += this.dy * this.speedScale * dt);

        if (respawnShieldTime >= 0) {
            if ((respawnShieldTime += dt) >= RESPAWN_SHIELD_TIME) respawnShieldTime = RESPAWN_SHIELD_OFF;
        }
    }

    public void deactivateRespawnShield() {
        respawnShieldTime = RESPAWN_SHIELD_OFF;
    }

    public void activateRespawnShield() {
        respawnShieldTime = 0;
    }

    public boolean isRespawnShieldActive() {
        return respawnShieldTime >= 0;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int changeHpBy(int delta) {
        return this.hp += delta;
    }

    public void setSpeedScale(float scale) {
        this.speedScale = scale;
    }

    public void resetSpeedScale() {
        this.speedScale = 1;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
