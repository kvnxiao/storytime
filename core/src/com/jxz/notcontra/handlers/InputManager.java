package com.jxz.notcontra.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.states.GameState;
import com.jxz.notcontra.states.LoadState;
import com.jxz.notcontra.states.PauseState;
import com.jxz.notcontra.states.PlayState;

/**
 * Created by Samuel on 2015-03-27.
 */
public class InputManager implements InputProcessor {
    private Game game;
    private GameStateManager gsm;
    private static InputManager manager;

    private InputManager(Game g) {
        game = g;
        gsm = GameStateManager.getInstance();
    }

    public static InputManager getInstance(Game game) {
        if (manager == null) {
            manager = new InputManager(game);
        }
        return manager;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Movement controls only operational if in play state
        if (GameStateManager.getInstance().getStateInstance() instanceof PlayState) {
            // Update sprinting state
            if (keycode == Input.Keys.SHIFT_LEFT && game.getPlayer().getJumpState() == 0) {
                game.getPlayer().setSprinting(true);
                return true;
            }

            // Standard WASD Movement
            if (keycode == Input.Keys.A) {
                game.getPlayer().getMovementState().add(-1, 0);
            }
            if (keycode == Input.Keys.D) {
                game.getPlayer().getMovementState().add(1, 0);
            }
            if (keycode == Input.Keys.W) {
                game.getPlayer().getMovementState().add(0, 1);
            }
            if (keycode == Input.Keys.S) {
                game.getPlayer().getMovementState().add(0, -1);
            }

            // Attack | melee keys
            if (keycode == Input.Keys.J) {
                game.getPlayer().melee(0);
            }
            if (keycode == Input.Keys.K) {
                game.getPlayer().melee(1);
            }
            if (keycode == Input.Keys.L) {
                game.getPlayer().melee(2);
            }

            // Jump if max jumps is not reached
            if (keycode == Input.Keys.SPACE && game.getPlayer().getJumpCounter() < game.getPlayer().getMaxJumps() && !game.getPlayer().isJumping()) {
                // Reduced jump height and disabled double jumping when climbing
                if (game.getPlayer().isClimbing()) {
                    game.getPlayer().setIsClimbing(false);
                    game.getPlayer().setJumpState(game.getPlayer().getJumpTime() * (float) 0.75);
                    game.getPlayer().setJumpCounter(game.getPlayer().getMaxJumps());
                } else {
                    game.getPlayer().setJumpState(Math.round(game.getPlayer().getJumpTime()));
                    AudioHelper.playSoundEffect("jump");
                }
                game.getPlayer().resetGravity();

                game.getPlayer().setJumpCounter(game.getPlayer().getJumpCounter() + 1);
                game.getPlayer().setIsGrounded(false);
                game.getPlayer().setIsJumping(true);

            }

            // PLAY STATE SWITCH STATE TEST
            if (keycode == Input.Keys.ESCAPE) {
                gsm.setState(GameStateManager.State.PAUSE);
                return true;
            }
        }
        // PAUSE STATE SWITCH STATE TEST
        if (GameStateManager.getInstance().getStateInstance() instanceof PauseState) {
            if (keycode == Input.Keys.ESCAPE) {
                gsm.setState(GameStateManager.State.PLAY);
                return true;
            }
        }
        // LOAD STATE SWITCH STATE TEST
        if (GameStateManager.getInstance().getStateInstance() instanceof LoadState) {
            if (keycode == Input.Keys.ESCAPE) {
                game.load();
                gsm.setState(GameStateManager.State.PLAY);
                return true;
            }
        }
        if (keycode == Input.Keys.P) {
            Gdx.graphics.setVSync(false);
            return true;
        }
        if (keycode == Input.Keys.O) {
            Gdx.graphics.setVSync(true);
            return true;
        }

        if (keycode == Input.Keys.M) {
            AudioHelper.muteMusic();
            return true;
        }

        if (keycode == Input.Keys.PAGE_UP) {
            AudioHelper.setMusicVolume(AudioHelper.getMusicVolume() + 0.1f);
            return true;
        }

        if (keycode == Input.Keys.PAGE_DOWN) {
            AudioHelper.setMusicVolume(AudioHelper.getMusicVolume() - 0.1f);
            return true;
        }

        if (keycode == Input.Keys.ALT_LEFT) {
            game.getPlayerCam().zoom += 1;
        }

        if (keycode == Input.Keys.ALT_RIGHT) {
            game.getPlayerCam().zoom -= 1;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Again, movement controls in play mode only
        if (GameStateManager.getInstance().getStateInstance() instanceof PlayState) {
            // Released keys signal end of movement
            if (keycode == Input.Keys.SHIFT_LEFT) {
                game.getPlayer().setSprinting(false);
            }

            if (keycode == Input.Keys.A) {
                game.getPlayer().getMovementState().add(1, 0);
            }
            if (keycode == Input.Keys.D) {
                game.getPlayer().getMovementState().add(-1, 0);
            }
            if (keycode == Input.Keys.W) {
                game.getPlayer().getMovementState().add(0, -1);
            }
            if (keycode == Input.Keys.S) {
                game.getPlayer().getMovementState().add(0, 1);
            }

            // Resets jump flag if space bar is released - ready to jump again
            if (keycode == Input.Keys.SPACE) {
                game.getPlayer().setIsJumping(false);
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
}
