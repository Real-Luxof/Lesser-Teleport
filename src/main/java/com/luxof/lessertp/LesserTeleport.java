package com.luxof.lessertp;

import com.luxof.lessertp.init.Patterns;

import net.fabricmc.api.ModInitializer;

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
}