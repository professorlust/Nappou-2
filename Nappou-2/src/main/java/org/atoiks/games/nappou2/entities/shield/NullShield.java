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

package org.atoiks.games.nappou2.entities.shield;

import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.nappou2.entities.IShield;

public class NullShield implements IShield {

    private static final long serialVersionUID = -6024720306180805901L;

    @Override
    public void render(IGraphics g) {
        // Do nothing
    }

    @Override
    public void update(float dt) {
        // Do nothing
    }

    @Override
    public float getX() {
        return -1;
    }

    @Override
    public float getY() {
        return -1;
    }

    @Override
    public float getR() {
        return -1;
    }

    @Override
    public void setX(float x) {
        // Do nothing
    }

    @Override
    public void setY(float y) {
        // Do nothing
    }

    @Override
    public void activate() {
        // Do nothing
    }

    @Override
    public void deactivate() {
        // Do nothing
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }
}
