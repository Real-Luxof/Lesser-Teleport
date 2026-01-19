package com.luxof.lessertp;

import com.luxof.lessertp.init.Patterns;

import at.petrak.hexcasting.api.casting.mishaps.MishapImmuneEntity;
import at.petrak.hexcasting.api.mod.HexTags;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LesserTeleport implements ModInitializer {
	public static final String MOD_ID = "lessertp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Patterns.init();

		LOGGER.info("Luxof says you're welcome.");
	}

    private static boolean cannotTeleport(Entity entity) {
        return entity.getType().isIn(HexTags.Entities.CANNOT_TELEPORT);
    }
	public static void assertEntityMayBeTeleported(Entity teleportee) throws MishapImmuneEntity {
        if (cannotTeleport(teleportee))
            throw new MishapImmuneEntity(teleportee);

        if (teleportee.getType().isIn(HexTags.Entities.STICKY_TELEPORTERS)) {
            var immunePassengers = teleportee.getPassengerList().stream()
                .filter(LesserTeleport::cannotTeleport).toList();
            
            if (!immunePassengers.isEmpty())
                throw new MishapImmuneEntity(immunePassengers.get(0));
        }
	}
}