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

package org.atoiks.games.nappou2.entities.bullet;

import java.awt.geom.AffineTransform;

import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.nappou2.entities.IBullet;

import static org.atoiks.games.nappou2.Utils.isPtOutOfScreen;
import static org.atoiks.games.nappou2.Utils.centerSquareCollision;
import static org.atoiks.games.nappou2.Utils.intersectSegmentCircle;
import static org.atoiks.games.nappou2.TrigConstants.*;

public final class Beam extends IBullet {

    private static final long serialVersionUID = 4412375L;
    private static final int ARR_SIZE = 8;

    private final float dx, dy;
    private float x, y;
    private float halfThickness, length;

    private final float[] dest = new float[ARR_SIZE];
    private final AffineTransform mat;

    public Beam(float x, float y, float thickness, float length, float angle, float dmag) {
        this.x = x;
        this.y = y;
        this.halfThickness = thickness / 2;
        this.length = length;

        this.dx = dmag * (float) Math.cos(angle);
        this.dy = dmag * (float) Math.sin(angle);
        this.mat = AffineTransform.getRotateInstance(angle - 3 * PI_DIV_2);
    }

    @Override
    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public void render(final IGraphics g) {
        g.setColor(color);
        g.fillPolygon(dest);
    }

    @Override
    public void update(final float dt) {
        this.x += dx * dt;
        this.y += dy * dt;

        // If we calculate a beam with (x, y) as (0, 0),
        // it will be rectangle with diagonal from (-halfThickness, 0) to (halfThickness, -length).
        // we rotate it around (0, 0) by angle, which has fixed value,
        // then, we translate it by (x, y)

        final float len = -length;
        final float[] input = {
            -halfThickness, 0,
            halfThickness , 0,
            halfThickness , len,
            -halfThickness, len,
        };

        final AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        trans.concatenate(mat);
        trans.transform(input, 0, dest, 0, ARR_SIZE / 2);
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public boolean collidesWith(final float x1, final float y1, final float r1) {
        // Only perform accurate collision if the square formed by center
        // point (x, y) with apothem collides with the circle also
        // approximated as a square with the apothem being its radius.
        final float apothem = Math.max(halfThickness, length * 2) / 2;
        if (centerSquareCollision(x, y, apothem, x1, y1, r1)) {
            // Accurate collision checks if any of the sides intersect with
            // the circle.
            return intersectSegmentCircle(dest[0], dest[1], dest[2], dest[3], x1, y1, r1)
                || intersectSegmentCircle(dest[2], dest[3], dest[4], dest[5], x1, y1, r1)
                || intersectSegmentCircle(dest[4], dest[5], dest[6], dest[7], x1, y1, r1)
                || intersectSegmentCircle(dest[6], dest[7], dest[0], dest[1], x1, y1, r1);
        }
        return false;
    }

    @Override
    public boolean isOutOfScreen(final int w, final int h) {
        return isPtOutOfScreen(dest[0], dest[1], w, h)
            && isPtOutOfScreen(dest[2], dest[3], w, h)
            && isPtOutOfScreen(dest[4], dest[5], w, h)
            && isPtOutOfScreen(dest[6], dest[7], w, h);
    }
}
