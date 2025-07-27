package com.luxof.lessertp.init;

import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.common.lib.hex.HexActions;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.luxof.lessertp.LesserTeleport;
import com.luxof.lessertp.actions.LesserTPAction;

public class Patterns {
    public static void init() {
        register("lesser_tp", "edqdewqaeaq", HexDir.NORTH_EAST, new LesserTPAction());
    }

    private static void register(
        String name,
        String signature,
        HexDir startDir,
        Action action
    ) {
        Registry.register(HexActions.REGISTRY, new Identifier(LesserTeleport.MOD_ID, name), new ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action));
    }
}
