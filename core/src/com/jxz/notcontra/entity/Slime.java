package com.jxz.notcontra.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.jxz.notcontra.world.Level;

/**
 * Created by Kevin Xiao on 2015-04-23.
 * Slime. Exemplifies "grunt" AI.
 */
public class Slime extends Monster {

    public Slime() {
        super("slime");
        // Set up animations
        this.animFrames = (TextureAtlas) assetHandler.getByName("grey_slime");
        animIdle = new Animation(1 / 6f,
                this.animFrames.findRegion("stand", 0),
                this.animFrames.findRegion("stand", 1),
                this.animFrames.findRegion("stand", 2));
        animWalk = new Animation(1 / 6f,
                this.animFrames.findRegion("move", 0),
                this.animFrames.findRegion("move", 1),
                this.animFrames.findRegion("move", 2),
                this.animFrames.findRegion("move", 3),
                this.animFrames.findRegion("move", 4),
                this.animFrames.findRegion("move", 5),
                this.animFrames.findRegion("move", 6));
        animHurt = new Animation(1 / 6f, this.animFrames.findRegion("hit1", 0));
        animJump = new Animation(1 / 6f, this.animFrames.findRegion("jump", 0));
        animDeath = new Animation(1 / 10f,
                this.animFrames.findRegion("die1", 0),
                this.animFrames.findRegion("die1", 1),
                this.animFrames.findRegion("die1", 2),
                this.animFrames.findRegion("die1", 3));

        renderOffset = animIdle.getKeyFrame(0).getRegionWidth();

        // Initialize sprite stuff
        this.sprite = new Sprite(animIdle.getKeyFrame(animStateTime, true));

        // Knock back values
        kbDuration = 0.4f;
        kbDistance = 25f;
        kbThreshold = 15;

        // Combat stats
        damage = 5;
        maxHealth = 50;
        speed = 3 + MathUtils.random(-0.5f, 0.5f);

        // Jump parameters
        maxJumps = 1;
        jumpCounter = 0;
        jumpState = 0;
        jumpMultiplier = 0.9f;
        jumpTime = 3f;

        // Speed parameters
        patrolSpeed = 2.0f;
        chaseSpeed = 3.0f;
    }

    public void init() {
        aabb.set(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        health = 50;
        isVisible = true;
        isActive = true;
        hitboxOffset.set(0, 0);
        state = AIState.PATROLLING;
        aiStateTime = 2.0f; // Start off idle for 2 seconds
        currentAnimation = animIdle;
    }


    @Override
    public void update() {
        switch (state) {
            // Pre-movement checks
            case IDLE:
                // Idle. Don't move.
                movementState.set(0, 0);
                break;
            case CHASING:
                // Approach target if not rooted
                speed = chaseSpeed;
                if (!isRooted) {
                    distToTarget = target.getPosition().cpy().sub(position);
                    if (Math.abs(distToTarget.x) > aabb.getWidth() / 2) {
                        movementState.set(distToTarget.x, 0).nor();
                    } else {
                        movementState.set(0, 0);
                    }
                    // If you're going to run yourself off a platform, at least jump.
                    if (isOnPlatform) {
                        float boundingEdgeDelta = (movementState.x > 0 ? 1 : -1) * aabb.getWidth() / 2;
                        TiledMapTile targetTile = currentLevel.getTileAt(position.x + aabb.getWidth() / 2 + boundingEdgeDelta + (movementState.x * speed), position.y - 1, Level.DYNAMIC_LAYER);
                        if (targetTile == null) {
                            jump();
                        }
                    }
                }
                break;
            case PATROLLING:
                // Reduce speed when patrolling
                speed = patrolSpeed;
                if (aiStateTime <= 0) {
                    if (movementState.isZero()) {
                        // New patrol route
                        movementState.set(MathUtils.randomSign(), 0);
                        aiStateTime = MathUtils.random(2.0f, 4.0f);
                    } else {
                        // Pause between patrol points
                        movementState.set(0, 0);
                        aiStateTime = MathUtils.random(0.5f, 2.5f);
                    }
                } else {
                    // Turn around if the slime is going to run off the platform
                    if (isOnPlatform) {
                        float boundingEdgeDelta = (movementState.x > 0 ? 1 : -1) * aabb.getWidth() / 2;
                        TiledMapTile targetTile = currentLevel.getTileAt(position.x + aabb.getWidth() / 2 + boundingEdgeDelta + (movementState.x * speed), position.y - 1, Level.DYNAMIC_LAYER);
                        if (targetTile == null) {
                            movementState.set(-movementState.x, 0);
                        }
                    }
                    aiStateTime -= Gdx.graphics.getDeltaTime();
                }
                break;
        }

        // Update collisions
        super.update();

        // Post Movement Checks
        switch (state) {
            case CHASING:
                // If slime is blocked on the x axis for some reason, try jumping
                // Although, don't jump if player is beneath the slime. That doesn't make sense.
                if (deltaX == 0 && distToTarget.y >= 0 && forceDuration == 0) {
                    jump();
                }
                // If target is dead, stop chasing them
                if (target instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) target;
                    if (le.getHealth() <= 0) {
                        target = null;
                        state = AIState.PATROLLING;
                    }
                }
                break;
            case PATROLLING:
                // If slime is blocked, stop for a bit
                if (deltaX == 0 && movementState.x != 0) {
                    aiStateTime = MathUtils.random(0.5f, 2.5f);
                    movementState.set(0, 0);
                }
                break;
        }

        // Update states:
        if (health <= 0) {
            health = 0;
            state = AIState.DYING;
            movementState.set(0, 0);
        }
    }

    @Override
    public void die() {
        super.die();
        EntityFactory.free(this);
    }

    public void cast(int skill) {
        this.isCasting = true;
    }

    public void animate() {
        // Update animation time
        animStateTime += Gdx.graphics.getDeltaTime();

        // Hurt > Death > Movement/Idle
        if (forceDuration > 0) {
            if (currentAnimation != animHurt) {
                animStateTime = 0;
            }
            forceDuration -= Gdx.graphics.getDeltaTime();
            sprite.setRegion(animHurt.getKeyFrame(animStateTime, true));
            currentAnimation = animHurt;
        } else if (state == AIState.DYING) {
            if (currentAnimation != animDeath) {
                animStateTime = 0;
            }
            sprite.setRegion(animDeath.getKeyFrame(animStateTime, true));
            currentAnimation = animDeath;
            if (animStateTime > animDeath.getAnimationDuration()) {
                die();
            }
        } else if (movementState.isZero()) {
            if (currentAnimation != animIdle) {
                animStateTime = 0;
            }
            sprite.setRegion(animIdle.getKeyFrame(animStateTime, true));
            currentAnimation = animIdle;
        } else {
            if (currentAnimation != animWalk) {
                animStateTime = 0;
            }
            sprite.setRegion(animWalk.getKeyFrame(animStateTime, true));
            currentAnimation = animWalk;
        }

        if (movementState.x < 0) {
            isFlipped = true;
        } else if (movementState.x > 0) {
            isFlipped = false;
        }

        this.sprite.setSize(this.sprite.getRegionWidth(), this.sprite.getRegionHeight());
    }

    @Override
    public void draw (Batch batch) {
        if (state == AIState.DYING) {
            batch.setColor(1f, 1f, 1f, 1 - (animStateTime / animDeath.getAnimationDuration()));
        }
        super.draw(batch);
        batch.setColor(1f, 1f, 1f, 1f);
    }

}
