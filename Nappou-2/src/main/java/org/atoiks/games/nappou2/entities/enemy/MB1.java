/**
 *  Nappou-2
 *  Copyright (C) 2017-2018  Atoiks-Games <atoiks-games@outlook.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.atoiks.games.nappou2.entities.enemy;

import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.nappou2.entities.bullet.PointBullet;

public final class MB1 extends AbstractEnemy {

    private static final long serialVersionUID = 5619264522L;

    private static final double PI_DIV_6 = Math.PI / 6;
    private static final int ROTATIONS = 7;

    private int enemyTime;

    public MB1(int hp, float x, float y, float r) {
        super(hp, x, y, r);
    }

    @Override
    public void drift(float dx, float dy) {
        // Bosses / Mini bosses do not drift
    }

    @Override
    public void update(float dt) {
        enemyTime++;

        final float x = getX();
        final float y = getY();
        final double angle = Math.atan2(game.player.getY() - y, game.player.getX() - x);

        if (enemyTime % 600 == 0) {
            for (int i = 0; i < ROTATIONS; ++i) {
                final double k = i * PI_DIV_6;
                game.addEnemyBullet(new PointBullet(x, y, 3, (float) (100 * Math.cos(k)), (float) (1000 * Math.sin(k))));
            }
        }

        if ((enemyTime + 300) % 600 == 0) {
            for (int i = 0; i < ROTATIONS; ++i) {
                final int offset = 3 - i;
                final int s = (4 - Math.abs(offset)) * 100;
                final double k = angle - offset * PI_DIV_6;
                game.addEnemyBullet(new PointBullet(x, y, 3, (float) (s * Math.cos(k)), (float) (s * Math.sin(k))));
            }
        }

        // There was no prior setY call, it's safe
        if (y <= 150) {
            setY(y + 300 * dt);
        }
    }

    @Override
    public int getScore() {
        return 1;
    }
}
