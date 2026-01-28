package com.buuz135.simpleclaims;

import com.buuz135.simpleclaims.chat.PlayerChatListener;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.SimpleClaimProtectCommand;
import com.buuz135.simpleclaims.commands.SimpleClaimsPartyCommand;
import com.buuz135.simpleclaims.config.SimpleClaimsConfig;
import com.buuz135.simpleclaims.interactions.ClaimCycleBlockGroupInteraction;
import com.buuz135.simpleclaims.interactions.ClaimPickupBucketInteraction;
import com.buuz135.simpleclaims.interactions.ClaimPlaceBucketInteraction;
import com.buuz135.simpleclaims.interactions.ClaimUseBlockInteraction;
import com.buuz135.simpleclaims.map.SimpleClaimsWorldMapProvider;
import com.buuz135.simpleclaims.systems.events.*;
import com.buuz135.simpleclaims.systems.tick.*;
import com.buuz135.simpleclaims.util.PartyInactivityThread;
import com.buuz135.simpleclaims.util.WindowExtraResourcesState;
import com.buuz135.simpleclaims.util.WindowPacketAdapters;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.IWorldMapProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.chunk.WorldGenWorldMapProvider;
import com.hypixel.hytale.server.core.util.Config;
import dev.unnm3d.codeclib.config.CodecFactory;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.logging.Level;


public class Main extends JavaPlugin {

    public static Config<SimpleClaimsConfig> CONFIG;

    private PartyInactivityThread partyInactivityTickingSystem;

    public Main(@NonNullDecl JavaPluginInit init) {
        super(init);
        CONFIG = this.withConfig("SimpleClaims", CodecFactory.createClassCodec(SimpleClaimsConfig.class));
    }

    @Override
    protected void setup() {
        super.setup();
        CONFIG.save();
        this.getEntityStoreRegistry().registerSystem(new BreakBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(new DamageBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(new PlaceBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(new InteractEventSystem());
        this.getEntityStoreRegistry().registerSystem(new PickupInteractEventSystem());
        this.getEntityStoreRegistry().registerSystem(new TitleTickingSystem(CONFIG.get().getTitleTopClaimTitleText()));
        if (CONFIG.get().isEnableAlloyEntryTesting())
            this.getEntityStoreRegistry().registerSystem(new EntryTickingSystem());
        if (CONFIG.get().isEnableParticleBorders())
            this.getEntityStoreRegistry().registerSystem(new ChunkBordersTickingSystem());
        this.getEntityStoreRegistry().registerSystem(new CustomDamageEventSystem());
        this.getEntityStoreRegistry().registerSystem(new QueuedCraftClaimFilterSystem());
        this.getEntityStoreRegistry().registerSystem(new CraftingUiQuantitiesSystem());

        // Register global (world-level) event systems for block damage. Allows us to block custom item interactions from damaging claims.
        this.getEntityStoreRegistry().registerSystem(new GlobalDamageBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(new GlobalBreakBlockEventSystem());

        this.getChunkStoreRegistry().registerSystem(new WorldMapUpdateTickingSystem());
        this.getCommandRegistry().registerCommand(new SimpleClaimProtectCommand());
        this.getCommandRegistry().registerCommand(new SimpleClaimsPartyCommand());

        IWorldMapProvider.CODEC.register(SimpleClaimsWorldMapProvider.ID, SimpleClaimsWorldMapProvider.class, SimpleClaimsWorldMapProvider.CODEC);

        WindowPacketAdapters.install();
        ClaimManager.getInstance();

        this.getEventRegistry().registerGlobal(AddWorldEvent.class, (event) -> {
            this.getLogger().at(Level.INFO).log("Registered world: " + event.getWorld().getName());

            if (CONFIG.get().isForceSimpleClaimsChunkWorldMap() && !event.getWorld().getWorldConfig().isDeleteOnRemove()) {
                this.getLogger().at(Level.INFO).log("Registered map for world: " + event.getWorld().getName());
                event.getWorld().getWorldConfig().setWorldMapProvider(new SimpleClaimsWorldMapProvider());
            } else {
                event.getWorld().getWorldConfig().setWorldMapProvider(new WorldGenWorldMapProvider());
            }
        });

        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, (event) -> {
            var player = event.getHolder().getComponent(Player.getComponentType());
            var playerRef = event.getHolder().getComponent(PlayerRef.getComponentType());
            ClaimManager.getInstance().setPlayerName(playerRef.getUuid(), player.getDisplayName(), System.currentTimeMillis());

            var ch = playerRef.getPacketHandler().getChannel();
            WindowExtraResourcesState.getOrCreateMap(ch);
        });

        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, (event) -> {
            ClaimManager.getInstance().setPlayerName(event.getPlayerRef().getUuid(), event.getPlayerRef().getUsername(), System.currentTimeMillis());

            var ch = event.getPlayerRef().getPacketHandler().getChannel();
            var m = ch.attr(WindowExtraResourcesState.EXTRA_BY_WINDOW_ID).get();
            if (m != null) m.clear();
        });

        this.getEventRegistry().registerAsyncGlobal(PlayerChatEvent.class, new PlayerChatListener());

        var interaction = getCodecRegistry(Interaction.CODEC);
        interaction.register("UseBlock", ClaimUseBlockInteraction.class, ClaimUseBlockInteraction.CUSTOM_CODEC);
        interaction.register("CycleBlockGroup", ClaimCycleBlockGroupInteraction.class, ClaimCycleBlockGroupInteraction.CUSTOM_CODEC);
        interaction.register("PlaceFluid", ClaimPlaceBucketInteraction.class, ClaimPlaceBucketInteraction.CUSTOM_CODEC);
        interaction.register("RefillContainer", ClaimPickupBucketInteraction.class, ClaimPickupBucketInteraction.CUSTOM_CODEC);

        partyInactivityTickingSystem = new PartyInactivityThread();
        partyInactivityTickingSystem.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        WindowPacketAdapters.uninstall();
    }

}