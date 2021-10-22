package net.malek.blink;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

public class ModInit implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("modid");
	public static final String MOD_ID = "maleks_blink";
	private static final Supplier<Path> CONFIG_ROOT = () -> FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toAbsolutePath();
	private static final ConfigHolder<ModConfig> CONFIG_MANAGER = AutoConfig.register(ModConfig.class, ModConfig.SubRootJanksonConfigSerializer::new);
	public static final Identifier TELEPORT_PACKET = new Identifier("blink:teleport");
	public static final Identifier RENDER_PACKET = new Identifier("blink:render");
	public static HashMap<UUID, Float> timeoutMap = new HashMap<>();
	public static int TIME = getConfig().madness.defaultTime;
	public static int TIME_LAST_ARMOR_CHECK = 0;
	public static int distanceIncreaseAmount = 10;
	private static Enchantment BLINK = Registry.register(Registry.ENCHANTMENT, new Identifier("maleks_blink", "blink"), new BlinkEnchantment());
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ServerPlayNetworking.registerGlobalReceiver(TELEPORT_PACKET, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				if(getConfig().madness.needsEnchantment) {
					if (TIME_LAST_ARMOR_CHECK + 10000 < server.getTicks()) {
						TIME_LAST_ARMOR_CHECK = server.getTicks();
						int distanceIncreaseAmount1 = 0;
						for (int i = 0; i < player.getInventory().armor.size(); i++) {
							ItemStack stack = player.getInventory().armor.get(i);
							if (stack.hasEnchantments()) {
								for (int i2 = 0; i2 < stack.getEnchantments().size(); i2++) {
									NbtCompound compound = (NbtCompound) stack.getEnchantments().get(i2);
									if (compound.getString("id").equals("maleks_blink:blink")) {
										distanceIncreaseAmount1 += compound.getInt("lvl");
									}
								}
							}
						}
						distanceIncreaseAmount = distanceIncreaseAmount1;
					}
				}
			if(timeoutMap.get(player.getUuid()) == null || timeoutMap.get(player.getUuid()) + (TIME/(distanceIncreaseAmount+1)) < server.getTicks()) {
				if (!getConfig().madness.needsEnchantment) {
					HitResult hitResult = player.raycast(125, 0.0f, false);
					World world = player.getWorld();
					for (BlockPos pos : BlockPos.iterateOutwards(new BlockPos(hitResult.getPos()), 4, 4, 4)) {
						if (world.isAir(pos) && world.isAir(pos.up())) {
							world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
							player.teleport(pos.getX(), pos.getY(), pos.getZ());
							timeoutMap.put(player.getUuid(), (float) server.getTicks());
							world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
							PacketByteBuf packetByteBufs = PacketByteBufs.create();
							NbtCompound compound = new NbtCompound();
							compound.putInt("distance", distanceIncreaseAmount);
							compound.putInt("time", TIME);
							packetByteBufs.writeNbt(compound);
							ServerPlayNetworking.send(player, RENDER_PACKET, packetByteBufs);
							return;
						}
					}
				} else {
					if(distanceIncreaseAmount == 0) {
						return;
					}
					HitResult hitResult = player.raycast(distanceIncreaseAmount*8, 0.0f, false);
					World world = player.getWorld();
					for (BlockPos pos : BlockPos.iterateOutwards(new BlockPos(hitResult.getPos()), 4, 4, 4)) {
						if (world.isAir(pos) && world.isAir(pos.up()) /*&& !world.isAir(new BlockPos(player.raycast(distanceIncreaseAmount*8 + 1, 0.0f, false).getPos()*/) {
							world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
							player.teleport(pos.getX(), pos.getY(), pos.getZ());
							timeoutMap.put(player.getUuid(), (float) server.getTicks());
							world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);
							PacketByteBuf packetByteBufs = PacketByteBufs.create();
							NbtCompound compound = new NbtCompound();
							compound.putInt("distance", distanceIncreaseAmount);
							compound.putInt("time", TIME);
							packetByteBufs.writeNbt(compound);
							ServerPlayNetworking.send(player, RENDER_PACKET, packetByteBufs);
							return;
						}
					}
				}
			}
			});
			//LOGGER.info(server.getTicks());
		});

	}
	public static ModConfig getConfig() {
		return CONFIG_MANAGER.get();
	}
	public static Path getConfigRoot() {
		return CONFIG_ROOT.get();
	}
}
