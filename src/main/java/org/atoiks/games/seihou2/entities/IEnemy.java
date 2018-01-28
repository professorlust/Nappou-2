package org.atoiks.games.seihou2.entities;

import java.io.Serializable;

import org.atoiks.games.framework.IRender;
import org.atoiks.games.framework.IUpdate;

public abstract class IEnemy implements ICollidable, IRender, IUpdate, Serializable {

    private static final long serialVersionUID = 8123472652L;

    protected int hp;

    protected IEnemy(int hp) {
        this.hp = hp;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public int changeHp(int delta) {
        return this.hp += delta;
    }

    public abstract float getX();
    public abstract float getY();

    public abstract void attachGame(Game game);

    public abstract int getScore();
}