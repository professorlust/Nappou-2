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

import se.tube42.lib.tweeny.Item;

import org.atoiks.games.nappou2.entities.bullet.PointBullet;

public final class TrackPointEnemy extends TweenEnemy {

    private static final long serialVersionUID = -2145973374641410758L;

    private final int score;
    private final float radius;
    private final float speed;
    private final float fireInterval;
    private final float delay;
    private final float[] angleOffsets;

    private float time;
    private int bulletId;

    private boolean firstRun = true;

    public TrackPointEnemy(int hp, int score, Item tweenInfo, final float fireInterval, boolean immediateFire, float delay, float[] angleOffsets, float radius, float speed) {
        super(hp, tweenInfo);
        this.score = score;
        this.radius = radius;
        this.speed = speed;
        this.angleOffsets = angleOffsets;
        this.delay = delay;
        this.fireInterval = fireInterval;
        if (!immediateFire) {
            bulletId = angleOffsets.length;
        }
    }

    @Override
    public void update(float dt) {
        if (firstRun) {
            firstRun = false;
            return;
        }

        time += dt;
        if (bulletId >= angleOffsets.length) {
            if (time >= fireInterval) bulletId = 0;
        } else if (time > delay) {
            final float x = getX();
            final float y = getY();
            final double angle = Math.atan2(game.player.getY() - y, game.player.getX() - x) + angleOffsets[bulletId];
            game.addEnemyBullet(new PointBullet(x, y, radius, speed * (float) Math.cos(angle), speed * (float) Math.sin(angle)));
            ++bulletId;
            time = 0;
        }
    }

    @Override
    public int getScore() {
        return score;
    }
}
