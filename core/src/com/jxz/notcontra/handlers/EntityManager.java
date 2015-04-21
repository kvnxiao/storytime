package com.jxz.notcontra.handlers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.jxz.notcontra.entity.Entity;
import com.jxz.notcontra.game.Game;

/**
 * Created by Samuel on 2015-03-27.
 */
public class EntityManager implements Disposable {
    // Entity Manager Fields
    private ObjectMap<String, Entity> masterList;
    private Game game;
    private static EntityManager entityManager;

    // Constructor
    private EntityManager(Game game) {
        // Initialize list
        this.game = game;
        masterList = new ObjectMap<String, Entity>();
    }

    public static EntityManager getInstance(Game game) {
        if (entityManager == null) {
            entityManager = new EntityManager(game);
        }
        return entityManager;
    }

    public static EntityManager getInstance() {
        return entityManager;
    }

    public void register(String key, Entity e) {
        // Add entities to master list, and add the appropriate physics body to the world
        masterList.put(key, e);
    }

    public Array<Entity> getEntitiesList() {
        return masterList.values().toArray();
    }

    public ObjectMap<String, Entity> getMasterObjectMap() {
        return masterList;
    }

    @Override
    public void dispose() {
        ObjectMap.Entries entries = masterList.entries();
        while (entries.hasNext()) {
            Entity e = (Entity) entries.next().value;
            e.getSprite().getTexture().dispose();
        }

        masterList.clear();
    }
}
