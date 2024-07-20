package io.github.haykam821.electricfloor.game.phase;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import io.github.haykam821.electricfloor.game.ElectricFloorConfig;
import io.github.haykam821.electricfloor.game.map.ElectricFloorMap;
import io.github.haykam821.electricfloor.game.map.ElectricFloorMapBuilder;
import io.github.haykam821.electricfloor.game.map.ElectricFloorGuideText;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class ElectricFloorWaitingPhase {
	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final ElectricFloorMap map;
	private final ElectricFloorConfig config;

	private HolderAttachment guideText;

	public ElectricFloorWaitingPhase(GameSpace gameSpace, ServerWorld world, ElectricFloorMap map, ElectricFloorConfig config) {
		this.gameSpace = gameSpace;
		this.world = world;
		this.map = map;
		this.config = config;
	}

	public static GameOpenProcedure open(GameOpenContext<ElectricFloorConfig> context) {
		ElectricFloorMapBuilder mapBuilder = new ElectricFloorMapBuilder(context.config());
		ElectricFloorMap map = mapBuilder.create();

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
			.setGenerator(map.createGenerator(context.server()));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			ElectricFloorWaitingPhase phase = new ElectricFloorWaitingPhase(activity.getGameSpace(), world, map, context.config());

			GameWaitingLobby.addTo(activity, context.config().getPlayerConfig());
			ElectricFloorActivePhase.setRules(activity);

			// Listeners
			activity.listen(GameActivityEvents.ENABLE, phase::enable);
			activity.listen(GamePlayerEvents.OFFER, phase::offerPlayer);
			activity.listen(PlayerDeathEvent.EVENT, phase::onPlayerDeath);
			activity.listen(GameActivityEvents.REQUEST_START, phase::requestStart);
		});
	}

	private void enable() {
		// Spawn guide text
		Vec3d guideTextPos = this.map.getGuideTextPos();

		if (guideTextPos != null) {
			ElementHolder holder = ElectricFloorGuideText.createElementHolder();
			this.guideText = ChunkAttachment.of(holder, world, guideTextPos);
		}
	}

	public GameResult requestStart() {
		ElectricFloorActivePhase.open(this.gameSpace, this.world, this.map, this.config, this.guideText);
		return GameResult.ok();
	}

	public PlayerOfferResult offerPlayer(PlayerOffer offer) {
		return offer.accept(this.world, this.map.getWaitingSpawnPos()).and(() -> {
			offer.player().changeGameMode(GameMode.ADVENTURE);
		});
	}

	public ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		// Respawn player at the start
		this.spawnPlayer(player);
		return ActionResult.SUCCESS;
	}

	private void spawnPlayer(ServerPlayerEntity player) {
		Vec3d spawnPos = this.map.getWaitingSpawnPos();
		player.teleport(this.world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0);
	}
}
