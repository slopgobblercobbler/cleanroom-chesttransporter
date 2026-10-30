package com.cleanroom.chesttransporter;

import com.cleanroom.common.api.CommonModInitializer;
import com.cleanroom.common.telemetry.CleanRoomTelemetry;

public class ChestTransporterCore implements CommonModInitializer {
    public static final String MOD_ID = "cleanroom_chesttransporter";
    public static final String MOD_NAME = "Chest Transporter CleanRoom";

    private static ChestTransporterCore instance;
    private final ChestTransporterEngine engine = new ChestTransporterEngine();

    public static ChestTransporterCore getInstance() {
        return instance;
    }

    public ChestTransporterEngine getEngine() {
        return engine;
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public void onInitializeCommon() {
        instance = this;
        System.out.println("[CleanRoom] Initialized core logic for " + MOD_NAME + " (" + MOD_ID + ")");
        CleanRoomTelemetry.record("INIT", "ChestTransporter initialized with 5 tiers (Wood to Netherite)");
    }
}
