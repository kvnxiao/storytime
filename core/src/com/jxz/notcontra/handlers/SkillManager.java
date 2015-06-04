package com.jxz.notcontra.handlers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Pools;
import com.jxz.notcontra.buff.Buff;
import com.jxz.notcontra.buff.FrozenBuff;
import com.jxz.notcontra.entity.BossMonster;
import com.jxz.notcontra.entity.LivingEntity;
import com.jxz.notcontra.entity.Player;
import com.jxz.notcontra.skill.LinearProjectileSkill;
import com.jxz.notcontra.skill.MeleeAttackSkill;
import com.jxz.notcontra.skill.Skill;

import java.util.ArrayList;

/**
 * Created by Samuel on 24/04/2015.
 * Where skills are defined.
 */

public class SkillManager {
    private static ArrayList<Skill> skillList;
    private static ArrayList<Buff> buffList;
    private static AssetHandler assetManager = AssetHandler.getInstance();

    // Gets skill from skill array
    public static Skill getSkill(String name) {
        for (Skill s : skillList) {
            if (s.getName().equals(name)) {
                return s.clone();
            }
        }
        return null;
    }

    public static Buff getBuff(String name) {
        for (Buff b : buffList) {
            if (b.getName().equals(name)) {
                return Pools.obtain(b.getClass());
            }
        }
        return null;
    }

    public static Class getBuffClass(String name) {
        for (Buff b : buffList) {
            if (b.getName().equals(name)) {
                return b.getClass();
            }
        }
        return null;
    }

    public static void applyBuff(Class buffClass, LivingEntity afflicted, float duration) {
        Buff buff = (Buff) Pools.obtain(buffClass);
        // Disables cannot affect players or boss mobs
        if (afflicted instanceof Player || afflicted instanceof BossMonster) {
            if (!buff.isDisable()) {
                buff.setDuration(duration);
                buff.cast(afflicted);
            }
        } else {
            buff.setDuration(duration);
            buff.cast(afflicted);
        }

    }

    public static void applyBuff(String buffName, LivingEntity afflicted, float duration) {
        applyBuff(getBuffClass(buffName), afflicted, duration);
    }

    // Populates skill array - call before using any skills
    public static void init() {
        initBuffs();
        initSkills();
    }

    public static void initSkills() {
        // Initialize list
        skillList = new ArrayList<Skill>();

        // Skill 1: Melee Attack
        MeleeAttackSkill basicMeleeAttack = new MeleeAttackSkill("testmelee");
        basicMeleeAttack.setRootWhileCasting(true);
        TextureAtlas animFrames = (TextureAtlas) assetManager.getByName("swing");
        basicMeleeAttack.setVfx(animFrames);
        basicMeleeAttack.setTime(0.1f);
        basicMeleeAttack.setAnimName("0.swingD1.1");
        basicMeleeAttack.setHitboxSize(98, 89);
        basicMeleeAttack.setHitboxOffset(55, 0);
        basicMeleeAttack.setAnimation(new Animation(1 / 11.0f, animFrames.findRegions("0.swingD1.1")));
        basicMeleeAttack.setDamage(20);
        basicMeleeAttack.setDamageScaling(1.2f);
        skillList.add(basicMeleeAttack);

        // Skill 2: Second Melee Attack
        MeleeAttackSkill secondMeleeAttack = new MeleeAttackSkill("melee2");
        secondMeleeAttack.setRootWhileCasting(true);
        animFrames = (TextureAtlas) assetManager.getByName("swing");
        secondMeleeAttack.setVfx(animFrames);
        secondMeleeAttack.setTime(0.2f);
        secondMeleeAttack.setAnimName("0.swingD2.1");
        secondMeleeAttack.setHitboxSize(98, 89);
        secondMeleeAttack.setHitboxOffset(30, 0);
        secondMeleeAttack.setAnimation(new Animation(1 / 7.0f, animFrames.findRegions("0.swingD2.1")));
        secondMeleeAttack.setDamage(10);
        secondMeleeAttack.setDamageScaling(1.5f);
        skillList.add(secondMeleeAttack);

        // Skill 3: Iceball thing
        LinearProjectileSkill iceball = new LinearProjectileSkill("iceball");
        iceball.setRootWhileCasting(true);
        animFrames = (TextureAtlas) assetManager.getByName("iceball");
        iceball.setVfx(animFrames);
        iceball.setAnimName("ball");
        iceball.setSpeed(8.5f);
        iceball.setRange(8.0f);
        iceball.setHitboxSize(184, 103); // orig 184-103
        iceball.setAnimation(new Animation(1 / 10f, animFrames.findRegions("ball")));
        iceball.setDamage(30);
        iceball.setCastName("effect");
        iceball.setCastAnimation(new Animation(1 / 25f, animFrames.findRegions(iceball.getCastName())));
        iceball.setDamageScaling(2.0f);
        iceball.setHitEffect("hit.0");
        iceball.setHitAnimation(new Animation(1 / 3f, animFrames.findRegion(iceball.getHitEffect())));
        iceball.setStatusEffect("FrozenBuff");
        iceball.setStatusDuration(1.5f);
        skillList.add(iceball);
    }

    public static void initBuffs() {
        buffList = new ArrayList<Buff>();

        buffList.add(Pools.obtain(FrozenBuff.class));
    }
}
