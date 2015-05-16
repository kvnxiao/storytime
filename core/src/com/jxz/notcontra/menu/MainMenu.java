package com.jxz.notcontra.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.jxz.notcontra.game.Game;
import com.jxz.notcontra.handlers.GameStateManager;
import com.jxz.notcontra.menu.buttons.Button;
import com.jxz.notcontra.menu.buttons.SpriteButton;
import com.jxz.notcontra.states.MenuState;

/**
 * Created by Andrew on 2015-05-10.
 */
public class MainMenu extends Menu {

    public MainMenu(MenuState menuState, String filePath) {
        this(menuState);
        this.file = Gdx.files.internal(filePath); // For when we implement parsing from XML
    }

    public MainMenu(MenuState menuState) {
        // Button set up for main menu
        this.menuState = menuState;
        menuButtons = menuState.getMenuButtonTextures();
        setUpButton("Play", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_play").getRegionWidth() / 2, 275), "button_play");
        setUpButton("Options", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_options").getRegionWidth() / 2, 175), "button_options");
        setUpButton("Exit", new Vector2(Game.VID_WIDTH / 2 - menuButtons.findRegion("button_quit").getRegionWidth() / 2, 75), "button_quit");

        // Background texture setup
        this.background = (Texture) assetHandler.getByName("menu_background");
    }

    protected void setUpButton(String name, Vector2 position, String atlasRegion) {
        final SpriteButton button = new SpriteButton(name, menuButtons.createSprite(atlasRegion, 0), menuButtons.createSprite(atlasRegion, 1), menuButtons.createSprite(atlasRegion, 2), position);
        Button.InputListener listener;

        if (button.getName().equalsIgnoreCase("Play")) {
            listener = new Button.InputListener() {

                Game game = GameStateManager.getInstance().getGame();

                @Override
                public void onClick() {
                    Menu menu = new PlayMenu(menuState);
                    menu.setPrevMenu(MainMenu.this);
                    GameStateManager.getInstance().getMenuState().setCurrentMenu(menu);
                }

                @Override
                public void onHover() {
                }
            };
            button.setInputListener(listener);
        } else if (button.getName().equalsIgnoreCase("Options")) {
            listener = new Button.InputListener() {

                // TODO: implement options menu
                Game game = GameStateManager.getInstance().getGame();

                @Override
                public void onClick() {
                }

                @Override
                public void onHover() {

                }
            };
            button.setInputListener(listener);
        } else if (button.getName().equalsIgnoreCase("Exit")) {
            listener = new Button.InputListener() {

                Game game = GameStateManager.getInstance().getGame();

                @Override
                public void onClick() {
                    game.executeCommand("Exit");
                }

                @Override
                public void onHover() {

                }
            };
            button.setInputListener(listener);
        }
        buttons.add(button);
    }
}
