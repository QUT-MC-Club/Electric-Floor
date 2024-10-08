package io.github.haykam821.electricfloor.game;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.haykam821.electricfloor.game.map.ElectricFloorMapConfig;
import net.minecraft.SharedConstants;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.stats.GameStatisticBundle;

public class ElectricFloorConfig {
	public static final Codec<ElectricFloorConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
			ElectricFloorMapConfig.CODEC.fieldOf("map").forGetter(ElectricFloorConfig::getMapConfig),
			PlayerConfig.CODEC.fieldOf("players").forGetter(ElectricFloorConfig::getPlayerConfig),
			Codec.INT.optionalFieldOf("guide_ticks", SharedConstants.TICKS_PER_SECOND * 10).forGetter(ElectricFloorConfig::getGuideTicks),
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("ticks_until_close", ConstantIntProvider.create(SharedConstants.TICKS_PER_SECOND * 5)).forGetter(ElectricFloorConfig::getTicksUntilClose),
			Codec.INT.optionalFieldOf("spawn_platform_delay", 20 * 2).forGetter(ElectricFloorConfig::getSpawnPlatformDelay),
			Codec.INT.optionalFieldOf("delay", 5).forGetter(ElectricFloorConfig::getDelay),
			Codec.BOOL.optionalFieldOf("night", true).forGetter(ElectricFloorConfig::isNight),
			GameStatisticBundle.NAMESPACE_CODEC.optionalFieldOf("statistic_bundle_namespace").forGetter(ElectricFloorConfig::getStatisticBundleNamespace)
		).apply(instance, ElectricFloorConfig::new);
	});

	private final ElectricFloorMapConfig mapConfig;
	private final PlayerConfig playerConfig;
	private final int guideTicks;
	private final IntProvider ticksUntilClose;
	private final int spawnPlatformDelay;
	private final int delay;
	private final boolean night;
	private final Optional<String> statisticBundleNamespace;

	public ElectricFloorConfig(ElectricFloorMapConfig mapConfig, PlayerConfig playerConfig, int guideTicks, IntProvider ticksUntilClose, int spawnPlatformDelay, int delay, boolean night, Optional<String> statisticBundleNamespace) {
		this.mapConfig = mapConfig;
		this.playerConfig = playerConfig;
		this.guideTicks = guideTicks;
		this.ticksUntilClose = ticksUntilClose;
		this.spawnPlatformDelay = spawnPlatformDelay;
		this.delay = delay;
		this.night = night;
		this.statisticBundleNamespace = statisticBundleNamespace;
	}

	public ElectricFloorMapConfig getMapConfig() {
		return this.mapConfig;
	}

	public PlayerConfig getPlayerConfig() {
		return this.playerConfig;
	}

	public int getGuideTicks() {
		return this.guideTicks;
	}

	public IntProvider getTicksUntilClose() {
		return this.ticksUntilClose;
	}

	public int getSpawnPlatformDelay() {
		return this.spawnPlatformDelay;
	}

	public int getDelay() {
		return this.delay;
	}

	public boolean isNight() {
		return this.night;
	}

	public Optional<String> getStatisticBundleNamespace() {
		return this.statisticBundleNamespace;
	}

	public GameStatisticBundle getStatisticBundle(GameSpace gameSpace) {
		return this.statisticBundleNamespace
			.map(namespace -> {
				return gameSpace.getStatistics().bundle(namespace);
			})
			.orElse(null);
	}
}