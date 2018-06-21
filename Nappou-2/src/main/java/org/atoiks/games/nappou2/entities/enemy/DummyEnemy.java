package org.atoiks.games.nappou2.entities.enemy;

import org.atoiks.games.framework2d.IGraphics;

public final class DummyEnemy extends AbstractEnemy {

    private static final long serialVersionUID = 56192645221L;
    //This needs to be different for every enemy...

    private boolean right;

    public DummyEnemy(int hp, float x, float y, float r, boolean right) {
        super(hp, x, y, r);
        this.right = right;
    }

    @Override
    public void update(float dt) {
        if (right && getX() > 700) {
            right = false;
        }
        if (!right && getX() < 50) {
            right = true;
        }

        setX(getX() + (right ? 1 : -1) * 100 * dt);
    }

    @Override
    public void render(IGraphics g) {
        // Convert to drawImage later on?
        super.render(g);
    }

    @Override
    public int getScore() {
        return 0;
    }
}