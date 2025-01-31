package net.mcbrincie212.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHUDMixin {
    @Unique
    private static final Identifier MY_VIGNETTE = Identifier.of(
            MyMod.MOD_ID, "textures/vignette.png"
    );

    @Inject(method = "render", at = @At("TAIL"))
    private void myMod$drawVignette(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        int color = ColorHelper.getArgb(INTENSITY, r, g, b); // r, g, b and intensity are controlled by you
        context.drawTexture(
                RenderLayer::getGuiTexturedOverlay,
                MY_VIGNETTE,
                0,
                0,
                0.0f,
                0.0f,
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight(),
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight(),
                color
        );
    }
}
