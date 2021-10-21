package net.malek.blink;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static net.malek.blink.ModInit.RENDER_PACKET;
import static net.malek.blink.ModInit.timeoutMap;

@Environment(EnvType.CLIENT)
public class ClientModInit implements ClientModInitializer {
    KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
    "key.blink.teleport", // The translation key of the keybinding's name
    InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
    GLFW.GLFW_KEY_R, // The keycode of the key
    "category.blink.teleport" // The translation key of the keybinding's category.
    ));
    public static int renderTime = -1;
    public static int distance = 1;
    public static int TIME = 1;
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                ClientPlayNetworking.send(ModInit.TELEPORT_PACKET, PacketByteBufs.empty());
                //client.player.sendMessage(new LiteralText("Key 1 was pressed!"), false);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("blink:render"), (client, handler, buf, responseSender) -> {

                System.out.println("SDJFKSDFJSDFSDJ");
                NbtCompound nbtCompound = buf.readNbt();
                distance = nbtCompound.getInt("distance");
                TIME = nbtCompound.getInt("time");
                renderTime = 0;
                System.out.println("distance : " + distance);
                System.out.println("time : " + TIME);
                System.out.println("renderTime : " + renderTime);

        });
    }
}
