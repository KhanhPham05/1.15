package cofh.thermal.core.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static cofh.thermal.core.common.ThermalFeatures.*;

public class ThermalConfig {

    private static boolean registered = false;

    public static void register() {

        if (registered) {
            return;
        }
        FMLJavaModLoadingContext.get().getModEventBus().register(ThermalConfig.class);
        registered = true;

        genServerConfig();
        genClientConfig();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }

    private ThermalConfig() {

    }

    // region CONFIG SPEC
    private static final ForgeConfigSpec.Builder SERVER_CONFIG = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec serverSpec;

    private static final ForgeConfigSpec.Builder CLIENT_CONFIG = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec clientSpec;

    private static void genServerConfig() {

        //        SERVER_CONFIG.push("Machines");
        //
        //        SERVER_CONFIG.pop();

        SERVER_CONFIG.push("Global Options");

        keepEnergy = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Energy when dropped.")
                .define("Blocks Retain Energy", true);
        keepItems = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Inventory Contents when dropped.")
                .define("Blocks Retain Inventory", false);
        keepFluids = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Tank Contents when dropped.")
                .define("Blocks Retain Tanks", false);
        keepAugments = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Augments when dropped.")
                .define("Blocks Retain Augments", true);
        keepRSControl = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Redstone Control configuration when dropped.")
                .define("Blocks Retain Redstone Control", true);
        keepSideConfig = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Side configuration when dropped.")
                .define("Blocks Retain Side Configuration", true);
        keepTransferControl = SERVER_CONFIG
                .comment("If TRUE, Thermal Blocks will retain Transfer Control configuration when dropped.")
                .define("Blocks Retain Transfer Control", true);

        SERVER_CONFIG.pop();

        SERVER_CONFIG.push("Features");

        flagVanillaBlocks = SERVER_CONFIG
                .comment("If TRUE, various 'Vanilla+' Blocks and Recipes are enabled.")
                .define("Vanilla+", true);
        flagRockwool = SERVER_CONFIG
                .comment("IF TRUE, Rockwool Blocks and Recipes are enabled.")
                .define("Rockwool", true);

        flagMobBasalz = SERVER_CONFIG
                .comment("IF TRUE, the Basalz Mob is enabled.")
                .define("Basalz", true);
        flagMobBlitz = SERVER_CONFIG
                .comment("IF TRUE, the Blitz Mob is enabled.")
                .define("Basalz", true);
        flagMobBlizz = SERVER_CONFIG
                .comment("IF TRUE, the Blizz Mob is enabled.")
                .define("Basalz", true);

        SERVER_CONFIG.pop();

        serverSpec = SERVER_CONFIG.build();
    }

    private static void genClientConfig() {

        clientSpec = CLIENT_CONFIG.build();
    }

    private static void genMachineConfig() {

    }

    private static void refreshServerConfig() {

        setFeature(FLAG_VANILLA_BLOCKS, flagVanillaBlocks.get());
        setFeature(FLAG_ROCKWOOL, flagRockwool.get());

        setFeature(FLAG_MOB_BASALZ, flagMobBasalz.get());
        setFeature(FLAG_MOB_BLITZ, flagMobBlitz.get());
        setFeature(FLAG_MOB_BLIZZ, flagMobBlizz.get());
    }

    private static void refreshClientConfig() {

    }

    // region GLOBALS
    public static final byte[] DEFAULT_MACHINE_SIDES_RAW = new byte[]{0, 0, 0, 0, 0, 0};
    // endregion

    // region SERVER VARIABLES
    public static int deviceAugments = 2;
    public static int dynamoAugments = 4;
    public static int machineAugments = 4;
    public static int workbenchAugments = 2;

    public static int toolAugments = 4;

    public static boolean permanentLava = true;
    public static boolean permanentWater = true;

    public static BooleanValue keepEnergy;
    public static BooleanValue keepItems;
    public static BooleanValue keepFluids;

    public static BooleanValue keepAugments;
    public static BooleanValue keepRSControl;
    public static BooleanValue keepSideConfig;
    public static BooleanValue keepTransferControl;

    private static BooleanValue flagVanillaBlocks;
    private static BooleanValue flagRockwool;

    private static BooleanValue flagMobBasalz;
    private static BooleanValue flagMobBlitz;
    private static BooleanValue flagMobBlizz;

    private static BooleanValue freezePermanentLava;
    private static BooleanValue freezePermanentWater;
    // endregion

    // region CLIENT VARIABLES
    public static boolean jeiBucketTanks = true;
    // endregion

    // region CONFIGURATION
    @SubscribeEvent
    public static void configLoading(final net.minecraftforge.fml.config.ModConfig.Loading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }

    @SubscribeEvent
    public static void configReloading(final net.minecraftforge.fml.config.ModConfig.Reloading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }
    // endregion
}
