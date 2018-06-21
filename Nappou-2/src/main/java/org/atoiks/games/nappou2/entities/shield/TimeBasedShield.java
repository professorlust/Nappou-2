package org.atoiks.games.nappou2.entities.shield;

import java.awt.Color;

import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.nappou2.entities.IShield;

public abstract class TimeBasedShield implements IShield {

    private static final long serialVersionUID = 172635916L;

    protected final float reloadTime;
    protected final float timeout;
    
    protected boolean active = false;
    protected float time = 0;
    protected float x, y, r;

    protected TimeBasedShield(final float timeout, final float reloadTime, final float r) {
        this.timeout = timeout;
        this.reloadTime = reloadTime;
        this.time = timeout + reloadTime;
        this.r = r;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getR() {
        return r;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void render(IGraphics g) {
        if (active) {
            g.setColor(Color.orange);
            // x, y are the center of the shield
            g.drawOval(x - r, y - r, x + r, y + r);
        }
    }

    @Override
    public void update(float dt) {
        time += dt;
        if (active && time >= timeout) {
            deactivate();
        }
    }
    
    @Override
    public void activate() {
        if (!active && isReady()) {
            active = true;
            time = 0;
        }
    }
    
    @Override
    public void deactivate() {
        active = false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isOutOfScreen(int width, int height) {
        // Being out of screen does not qualify it for deallocation
        return false;
    }

    @Override
    public boolean collidesWith(float x1, float y1, float r1) {
        if (active) {
            final float sumRadius = r + r1;
            return (Math.abs(x1 - x) < sumRadius)
                && (Math.abs(y1 - y) < sumRadius);
        }
        return false;
    }

    @Override
    public boolean isReady() {
        return time > timeout + reloadTime;
    }
}