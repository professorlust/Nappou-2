package org.atoiks.games.seihou2.entities.enemy;

import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.seihou2.entities.bullet.Beam;
import org.atoiks.games.seihou2.entities.bullet.PointBullet;

public final class CAITutorial extends AbstractEnemy {

    private static final long serialVersionUID = 5619264522L;

    private float time;
    private boolean fireGate;
    private int bulletPattern;
    private int enemyTime;
    private double spiralAngle = 0;

    public CAITutorial(int hp, float x, float y, float r) {
        super(hp, x, y, r);
    }

    @Override
    public void update(float dt) {
        time += dt;
        bulletPattern ++;

        enemyTime++;
        if(enemyTime%25000 == 0){
          game.addEnemy(new SingleShotEnemy(1, 250, -10, 8));
          game.addEnemy(new SingleShotEnemy(1, 500, -10, 8));
        }

        if (getY() <= 150) {
            setY(getY() + 300 * dt);
        }

        final float x = getX();
        final float y = getY();

        if (hp < 50 && hp >= 10) {
            if (bulletPattern % 2500 == 0) {
                spiralAngle += Math.PI / 24;
                game.addEnemyBullet(new PointBullet(x, y, 2, (float) (1000 * Math.cos(spiralAngle)), (float) (1000 * Math.sin(spiralAngle))));
            }
        }

        if (hp < 15) {
            if (bulletPattern % 1250 == 0) {
                spiralAngle += Math.PI / 24;
                game.addEnemyBullet(new PointBullet(x, y, 2, (float) (1000 * Math.cos(spiralAngle)), (float) (1000 * Math.sin(spiralAngle))));
                game.addEnemyBullet(new PointBullet(x, y, 2, (float) (1000 * Math.cos(spiralAngle - Math.PI)), (float) (1000 * Math.sin(spiralAngle - Math.PI))));
            }
        }

        if (hp < 25 && hp >= 10) {
            if (bulletPattern % 2500 == 0) {
                game.addEnemyBullet(new PointBullet(x, y, 2, (float) (1000 * Math.cos(spiralAngle - Math.PI)), (float) (1000 * Math.sin(spiralAngle - Math.PI))));
            }
        }

        if (hp <= 15) {
            if (bulletPattern % 1250 == 0) {
                game.addEnemyBullet(new PointBullet(x, y, 2, (float) (1000 * Math.cos(spiralAngle + Math.PI/2)), (float) (1000 * Math.sin(spiralAngle + Math.PI/2))));
                game.addEnemyBullet(new PointBullet(x, y, 2, (float) (1000 * Math.cos(spiralAngle - Math.PI/2)), (float) (1000 * Math.sin(spiralAngle - Math.PI/2))));
            }
        }

        if (bulletPattern % 25000 == 0) {
            final double angle = Math.atan2(game.player.getY() - y, game.player.getX() - x);
            if (Math.random() >= 0.5) {
                game.addEnemyBullet(new PointBullet(x, y, 2, (float) (1000 * Math.cos(angle)), (float) (1000 * Math.sin(angle))));
                game.addEnemyBullet(new Beam(x, y, (float) 2, (float) 10, (float) (angle + Math.PI/4), (float) 500));
                game.addEnemyBullet(new Beam(x, y, (float) 2, (float) 10, (float) (angle - Math.PI/4), (float) 500));
                game.addEnemyBullet(new Beam(x, y, (float) 2, (float) 10, (float) (angle - Math.PI/6), (float) 750));
                game.addEnemyBullet(new Beam(x, y, (float) 2, (float) 10, (float) (angle + Math.PI/6), (float) 750));
                game.addEnemyBullet(new Beam(x, y, (float) 2, (float) 10, (float) (angle - Math.PI/12), (float) 1000));
                game.addEnemyBullet(new Beam(x, y, (float) 2, (float) 10, (float) (angle + Math.PI/12), (float) 1000));
            } else {
                game.addEnemyBullet(new Beam(x, y, (float) 10, (float) 100, (float) (angle), (float) 750));
                game.addEnemyBullet(new Beam(x, y, (float) 100, (float) 100, (float) (angle + Math.PI/4), (float) 500));
                game.addEnemyBullet(new Beam(x, y, (float) 100, (float) 100, (float) (angle - Math.PI/4), (float) 500));
            }
        }

        if (!fireGate && Math.cos(6 * time) < 0.5) {
            fireGate = true;
        }

        if (fireGate && Math.cos(6 * time) > 0.5) {
            fireGate = false;
            final double angle = Math.atan2(game.player.getY() - y, game.player.getX() - x);
            game.addEnemyBullet(new PointBullet(x, y, 2, (float) (1000 * Math.cos(angle)), (float) (1000 * Math.sin(angle))));
        }
    }

    @Override
    public void render(IGraphics g) {
        // Convert to drawImage later on?
        super.render(g);
    }

    @Override
    public int getScore() {
        return 1;
    }
}