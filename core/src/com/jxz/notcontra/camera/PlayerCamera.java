package com.jxz.notcontra.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 17/04/2015.
 * Camera will follow player
 */
public class PlayerCamera extends OrthographicCamera {
    // Player Camera Fields
    private Player player;
    private boolean isTracking = false;

    public PlayerCamera() {
        super();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void track() {
        // X-position
        position.x = (player.getPosition().x + Math.round(player.getSprite().getWidth() / 2)) * Game.UNIT_SCALE;

        // Y-position
        float deltaY = position.y - player.getTilePosition().y + player.getSprite().getHeight() * Game.UNIT_SCALE;
        if (!isTracking) {
            // Enables tracking when player is more than 1/4 screen height away
            if (Math.abs(deltaY) > Game.VIEW_HEIGHT / 4) {
                isTracking = true;
            }
        } else {
            // Moves camera closer to the player until within 0.01 tiles
            if (Math.abs(deltaY) > 0.01 && position.y >= Game.VIEW_HEIGHT / 2f && position.y <= player.getCurrentLevel().getHeight() - Game.VIEW_HEIGHT / 2f) {
                position.y += (deltaY > 0 ? -1 : 1) * Math.pow(deltaY, 2) * Game.UNIT_SCALE * Game.fpsTimer;
            } else {
                isTracking = false;
            }
        }

        // Clamp positions to map boundaries
        position.x = MathUtils.clamp(position.x, Game.VIEW_WIDTH / 2f, player.getCurrentLevel().getWidth() - Game.VIEW_WIDTH / 2f);
        position.y = MathUtils.clamp(position.y, Game.VIEW_HEIGHT / 2f, player.getCurrentLevel().getHeight() - Game.VIEW_HEIGHT / 2f);
    }

}
