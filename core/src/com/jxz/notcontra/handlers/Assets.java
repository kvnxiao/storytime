package com.jxz.notcontra.handlers;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by Kevin Xiao on 2015-04-09.
 */
public class Assets {

    public static final AssetManager assetManager = new AssetManager();

    public static final AssetDescriptor<Texture> player = new AssetDescriptor<Texture>("p1_duck2.png", Texture.class);
    public static final AssetDescriptor<TiledMap> level1 = new AssetDescriptor<TiledMap>("Maps/samplelevel.tmx", TiledMap.class);

    public static void load() {
        // Load sprites
        assetManager.load(player);

        // Loads map last
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(level1);
    }

    public static void dispose() {
        assetManager.dispose();
    }

    // TO BE COMPLETED
}