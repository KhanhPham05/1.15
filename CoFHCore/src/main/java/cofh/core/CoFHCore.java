package cofh.core;

import cofh.core.capability.CapabilityArchery;
import cofh.core.capability.CapabilityAreaEffect;
import cofh.core.capability.CapabilityEnchantableItem;
import cofh.core.capability.CapabilityShieldItem;
import cofh.core.client.gui.GoVoteHandler;
import cofh.core.command.CoFHCommand;
import cofh.core.event.AttributeEvents;
import cofh.core.init.*;
import cofh.core.key.CoreKeys;
import cofh.core.network.PacketHandler;
import cofh.core.network.packet.client.*;
import cofh.core.network.packet.server.*;
import cofh.core.registries.DeferredRegisterCoFH;
import cofh.core.util.Proxy;
import cofh.core.util.ProxyClient;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cofh.core.util.constants.Constants.*;

@Mod(ID_COFH_CORE)
public class CoFHCore {

    public static final Logger LOG = LogManager.getLogger(ID_COFH_CORE);
    public static final PacketHandler PACKET_HANDLER = new PacketHandler(new ResourceLocation(ID_COFH_CORE, "general"));
    public static final Proxy PROXY = DistExecutor.runForDist(() -> ProxyClient::new, () -> Proxy::new);

    public static final DeferredRegisterCoFH<Block> BLOCKS = new DeferredRegisterCoFH<>(ForgeRegistries.BLOCKS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Fluid> FLUIDS = new DeferredRegisterCoFH<>(ForgeRegistries.FLUIDS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Effect> EFFECTS = new DeferredRegisterCoFH<>(ForgeRegistries.POTIONS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<Item> ITEMS = new DeferredRegisterCoFH<>(ForgeRegistries.ITEMS, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<ParticleType<?>> PARTICLES = new DeferredRegisterCoFH<>(ForgeRegistries.PARTICLE_TYPES, ID_COFH_CORE);
    public static final DeferredRegisterCoFH<TileEntityType<?>> TILE_ENTITIES = new DeferredRegisterCoFH<>(ForgeRegistries.TILE_ENTITIES, ID_COFH_CORE);

    public CoFHCore() {

        registerPackets();

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);

        BLOCKS.register(modEventBus);
        FLUIDS.register(modEventBus);
        EFFECTS.register(modEventBus);
        ITEMS.register(modEventBus);
        // PARTICLES.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

        CoreConfig.register();

        CoreBlocks.register();
        CoreFluids.register();
        CoreEffects.register();
        CoreItems.register();
        // CoreParticles.register();
    }

    private void registerPackets() {

        PACKET_HANDLER.registerPacket(PACKET_CONTROL, TileControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_GUI, TileGuiPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_REDSTONE, TileRedstonePacket::new);
        PACKET_HANDLER.registerPacket(PACKET_STATE, TileStatePacket::new);

        PACKET_HANDLER.registerPacket(PACKET_CHAT, IndexedChatPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_CONTAINER, ContainerPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_SECURITY, SecurityPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_CONFIG, TileConfigPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_SECURITY_CONTROL, SecurityControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_REDSTONE_CONTROL, RedstoneControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_TRANSFER_CONTROL, TransferControlPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_SIDE_CONFIG, SideConfigPacket::new);
        PACKET_HANDLER.registerPacket(PACKET_STORAGE_CLEAR, StorageClearPacket::new);

        PACKET_HANDLER.registerPacket(PACKET_ITEM_MODE_CHANGE, ItemModeChangePacket::new);
    }

    // region INITIALIZATION
    private void commonSetup(final FMLCommonSetupEvent event) {

        CapabilityAreaEffect.register();
        CapabilityArchery.register();
        CapabilityEnchantableItem.register();
        CapabilityShieldItem.register();

        AttributeEvents.setup();
    }

    private void clientSetup(final FMLClientSetupEvent event) {

        CoreKeys.register();

        GoVoteHandler.init();
    }

    private void serverStarting(final FMLServerStartingEvent event) {

        CoFHCommand.initialize(event.getCommandDispatcher());
    }
    // endregion
}
