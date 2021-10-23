package net.malek.blink.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.malek.blink.ClientModInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.malek.blink.ModInit.getConfig;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    /**
     * @author - MalekiRe
     */
//    private static final Identifier TFC_GUI_ICONS_TEXTURE = new Identifier(TerraFabriCraft.MODID, "textures/gui/icons/overlay.png");
//    private static final Identifier EMPTY_GUI_ICONS_TEXTURE = new Identifier(TerraFabriCraft.MODID, "textures/gui/icons/empty.png");
    private static final Identifier ENDERPEARL_TEXTURE = new Identifier("minecraft", "textures/item/ender_pearl.png");
    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private int scaledHeight;

    @Shadow
    private int scaledWidth;

    float i = 0;
    int ticksToLeaveIt = 300;
    int currentLeavingTicks = -2;
    //@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;"))
    @Inject(method = "render", at = @At("RETURN"))
    private void renderPre(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(currentLeavingTicks <= ticksToLeaveIt && currentLeavingTicks != -2) {
            PlayerEntity player = getCameraPlayer();
            RenderSystem.setShaderTexture(0, ENDERPEARL_TEXTURE);
            matrices.push();
            matrices.scale(1f, 1f, 1f);
            drawTexture(matrices, scaledWidth/4, scaledHeight-20, 0, i * 16, 16, 16, 16, 16);
            matrices.pop();
        }
        if(currentLeavingTicks >= 0){
            currentLeavingTicks++;
        }
        if(currentLeavingTicks >= ticksToLeaveIt) {
            currentLeavingTicks = -2;
        }
        if(ClientModInit.renderTime >= 0) {
            currentLeavingTicks = -1;
            int numberOfTicks = ClientModInit.TIME/(ClientModInit.distance+1);
            i = (float)ClientModInit.renderTime / (float)numberOfTicks;
            i /= 3.0;
            ClientModInit.renderTime++;
        }
        if(i >= 1) {
            i = 0;
            ClientModInit.renderTime = -1;
            currentLeavingTicks = 0;
        }

    }





}