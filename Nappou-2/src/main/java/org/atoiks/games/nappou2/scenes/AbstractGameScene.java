package org.atoiks.games.nappou2.scenes;

import java.util.Arrays;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;

import se.tube42.lib.tweeny.TweenManager;

import org.atoiks.games.framework2d.Scene;
import org.atoiks.games.framework2d.IGraphics;

import org.atoiks.games.nappou2.ScoreData;
import org.atoiks.games.nappou2.Difficulty;

import org.atoiks.games.nappou2.entities.*;
import org.atoiks.games.nappou2.entities.bullet.*;

public abstract class AbstractGameScene extends Scene {

    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    public static final int GAME_BORDER = 750;

    public static final float DEFAULT_DX = 300f;
    public static final float DEFAULT_DY = 300f;
    public static final Color PAUSE_OVERLAY = new Color(192, 192, 192, 100);

    // Conventionally, continue is always the first option,
    // sceneDest is always one less than the selectorY
    private static final int[] selectorY = {342, 402};
    private static final int[] sceneDest = {1};
    private static final int OPT_HEIGHT = 37;

    private int selector;

    protected final Game game = new Game();

    private byte updatePhase = -1;
    private boolean ignoreDamage = false;

    protected float playerFireTimeout;
    protected Image hpImg;
    protected Image statsImg;
    protected Image skillImg;
    protected Image pauseImg;
    protected boolean pause;
    protected boolean disableInput;
    protected Difficulty difficulty;

    public final int sceneId;

    protected AbstractGameScene(int id) {
        sceneId = id;
    }

    @Override
    public final void resize(int w, int h) {
        // Window size is fixed
    }

    protected final void disableDamage() {
        ignoreDamage = true;
    }

    protected final void enableDamage() {
        ignoreDamage = false;
    }

    @Override
    public void enter(int prevSceneId) {
        hpImg = (Image) scene.resources().get("hp.png");
        statsImg = (Image) scene.resources().get("stats.png");
        skillImg = (Image) scene.resources().get("skill_recharged.png");
        pauseImg = (Image) scene.resources().get("pause.png");
        difficulty = (Difficulty) scene.resources().getOrDefault("difficulty", Difficulty.NORMAL);

        playerFireTimeout = 0f;
        pause = false;
    }

    @Override
    public void leave() {
        if (sceneId >= 0) {
            final ScoreData scoreDat = (ScoreData) scene.resources().get("score.dat");
            final int[] alias = scoreDat.data[sceneId][difficulty.ordinal()];
            final int[] a = Arrays.copyOf(alias, alias.length + 1);
            a[a.length - 1] = game.getScore();
            Arrays.sort(a);
            System.arraycopy(a, 1, alias, 0, a.length - 1);
        }

        game.cleanup();
    }

    public void renderBackground(final IGraphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, GAME_BORDER, HEIGHT);
    }

    public void renderStats(final IGraphics g) {
        if (statsImg != null) {
            g.drawImage(statsImg, GAME_BORDER, 0);
        }

        if (hpImg != null) {
            final int hp = game.player.getHp();
            final int w = hpImg.getWidth(null);
            for (int i = 0; i < hp; ++i) {
                g.drawImage(hpImg, GAME_BORDER + 5 + i * w, 24);
            }
        }

        final String str = game.getScore() == 0 ? "0" : Integer.toString(game.getScore()) + "000";
        g.drawString(str, GAME_BORDER + 5, 72);

        if (game.player.shield.isReady() && skillImg != null) {
            g.drawImage(skillImg, GAME_BORDER, 80);
        }
    }

    @Override
    public final <T> void render(final IGraphics<T> g) {
        // The bullet-curtain part
        renderBackground(g);
        game.render(g);

        // The game stats part
        g.setColor(Color.black);
        g.fillRect(GAME_BORDER, 0, WIDTH, HEIGHT);
        g.setColor(Color.white);
        g.drawLine(GAME_BORDER, 0, GAME_BORDER, HEIGHT);

        renderStats(g);

        if (pause) {
            g.drawImage(pauseImg, 0, 0, PAUSE_OVERLAY);
            g.setColor(Color.black);
            g.drawRect(45, selectorY[selector], 49, selectorY[selector] + OPT_HEIGHT);
        }
    }

    @Override
    public boolean update(final float dt) {
        // Hopefully the only "black magic" in here
        if (!pause) {
            if (scene.keyboard().isKeyPressed(KeyEvent.VK_ESCAPE)) {
                pause = true;
            }
            playerFireTimeout -= dt;
            TweenManager.service((long) (dt * 1000000));
            if (!procPlayerPos(dt)) return false;
            switch (++updatePhase) {
                default: updatePhase = 0;   // FALLTHROUGH!
                case 0: return updateEnemyBulletPos(dt);
                case 1: return updateEnemyPos(dt);
                case 2: return updatePlayerBulletPos(dt);
                case 3: return testCollisions(dt);
                case 4: return postUpdate(dt);
            }
        } else {
            if (scene.keyboard().isKeyPressed(KeyEvent.VK_ENTER) || scene.mouse().isButtonClicked(1, 2)) {
                if (selector == 0) {
                    pause = false;
                } else {
                    scene.switchToScene(sceneDest[selector - 1]);
                }
                return true;
            }
            if (scene.keyboard().isKeyPressed(KeyEvent.VK_DOWN)) {
                if (++selector >= selectorY.length) selector = 0;
            }
            if (scene.keyboard().isKeyPressed(KeyEvent.VK_UP)) {
                if (--selector < 0) selector = selectorY.length - 1;
            }

            final int mouseY = scene.mouse().getLocalY();
            for (int i = 0; i < selectorY.length; ++i) {
                final int selBase = selectorY[i];
                if (mouseY > selBase && mouseY < (selBase + OPT_HEIGHT)) {
                    selector = i;
                    break;
                }
            }
        }
        return true;
    }

    private boolean updateEnemyPos(final float dt) {
        for (int i = 0; i < game.enemies.size(); ++i) {
            final IEnemy enemy = game.enemies.get(i);
            enemy.update(dt);
            if (enemy.isOutOfScreen(GAME_BORDER, HEIGHT)) {
                game.enemies.remove(i);
                if (--i < -1) break;
            }
        }
        return true;
    }

    private boolean updateEnemyBulletPos(final float dt) {
        for (int i = 0; i < game.enemyBullets.size(); ++i) {
            final IBullet bullet = game.enemyBullets.get(i);
            bullet.update(dt);
            if (bullet.isOutOfScreen(GAME_BORDER, HEIGHT)) {
                game.enemyBullets.remove(i);
                if (--i < -1) break;
            }
        }
        return true;
    }

    private boolean updatePlayerBulletPos(final float dt) {
        for (int i = 0; i < game.playerBullets.size(); ++i) {
            final IBullet bullet = game.playerBullets.get(i);
            bullet.update(dt);
            if (bullet.isOutOfScreen(GAME_BORDER, HEIGHT)) {
                game.playerBullets.remove(i);
                if (--i < -1) break;
            }
        }
        return true;
    }

    private boolean procPlayerPos(final float dt) {
        if (disableInput) return true;

        float tmpVal = 0;
        float tmpPos = game.player.getY();
        if (scene.keyboard().isKeyDown(KeyEvent.VK_DOWN)) {
            if (tmpPos + Player.RADIUS < HEIGHT) tmpVal += DEFAULT_DY;
        }
        if (scene.keyboard().isKeyDown(KeyEvent.VK_UP)) {
            if (tmpPos - Player.RADIUS > 0) tmpVal -= DEFAULT_DY;
        }
        game.player.setDy(tmpVal);

        tmpVal = 0;
        tmpPos = game.player.getX();
        if (scene.keyboard().isKeyDown(KeyEvent.VK_RIGHT)) {
            if (tmpPos + Player.RADIUS < GAME_BORDER) tmpVal += DEFAULT_DX;
        }
        if (scene.keyboard().isKeyDown(KeyEvent.VK_LEFT)) {
            if (tmpPos - Player.RADIUS > 0) tmpVal -= DEFAULT_DX;
        }
        game.player.setDx(tmpVal);

        game.player.setSpeedScale(scene.keyboard().isKeyDown(KeyEvent.VK_SHIFT) ? 0.55f : 1);

        game.player.update(dt);

        if (playerFireTimeout <= 0 && scene.keyboard().isKeyDown(KeyEvent.VK_Z)) {
            final float px = game.player.getX();
            final float py = game.player.getY();
            game.addPlayerBullet(new PointBullet(px, py, 5, 0, -DEFAULT_DY * 4.5f));
            playerFireTimeout = 0.2f;  // 0.2 second cap
        }

        if (scene.keyboard().isKeyPressed(KeyEvent.VK_X)) {
            game.player.shield.activate();
        }
        return true;
    }

    private boolean testCollisions(final float dt) {
        if (ignoreDamage) return true;

        final float px = game.player.getX();
        final float py = game.player.getY();

        final boolean shieldActive = game.player.shield.isActive();
        final float sx = game.player.shield.getX();
        final float sy = game.player.shield.getY();
        final float sr = game.player.shield.getR();

        for (int i = 0; i < game.enemyBullets.size(); ++i) {
            final IBullet bullet = game.enemyBullets.get(i);
            if (!game.player.isRespawnShieldActive() && bullet.collidesWith(px, py, Player.COLLISION_RADIUS)) {
                if (game.player.changeHpBy(-1) <= 0) {
                    // Goto title scene
                    scene.switchToScene(1);
                    return true;
                }
                game.player.activateRespawnShield();
                game.enemyBullets.remove(i);
                if (--i < -1) break;
            }

            if (shieldActive && bullet.collidesWith(sx, sy, sr)) {
                game.enemyBullets.remove(i);
                if (--i < -1) break;
            }
        }

        enemy_loop:
        for (int i = 0; i < game.enemies.size(); ++i) {
            final IEnemy enemy = game.enemies.get(i);
            for (int j = 0; j < game.playerBullets.size(); ++j) {
                final IBullet bullet = game.playerBullets.get(j);
                final float r = enemy.getR();
                if (r < 0) continue;
                if (bullet.collidesWith(enemy.getX(), enemy.getY(), enemy.getR())) {
                    game.playerBullets.remove(j);
                    if (enemy.changeHp(-1) <= 0) {
                        game.enemies.remove(i);
                        game.changeScore(enemy.getScore());
                        if (--i < -1) break enemy_loop;
                    }
                    if (--j < -1) break;

                    // Enemy is killed, do not test collision against the player
                    continue enemy_loop;
                }
            }

            if (!game.player.isRespawnShieldActive() && enemy.collidesWith(px, py, Player.COLLISION_RADIUS)) {
                if (game.player.changeHpBy(-1) <= 0) {
                    // Goto title scene
                    scene.switchToScene(1);
                    return true;
                }
                game.player.activateRespawnShield();
                if (enemy.changeHp(-1) <= 0) {
                    game.enemies.remove(i);
                    game.changeScore(enemy.getScore());
                    if (--i < -1) break;
                }
            }
        }

        return true;
    }

    public abstract boolean postUpdate(float dt);
}