package moe.prwk.emiffect.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import moe.prwk.emiffect.EMIffectPlugin;
import moe.prwk.emiffect.mixin.TextureAtlasHolderInvoker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
//? if >=1.20.6 {
import net.minecraft.core.component.DataComponentPatch;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//? if <1.20.4 {
/*import net.minecraft.network.chat.TextColor;
*///?}
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MobEffectEmiStack extends EmiStack {
    private static final MutableComponent WHITESPACE = EmiPort.literal(" ");

    @Nullable
    private final MobEffect effect;

    public MobEffectEmiStack(@Nullable MobEffect effect) {
        this.effect = effect;
    }

    @Override
    public EmiStack copy() {
        return new MobEffectEmiStack(effect);
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
        MobEffectTextureManager sprites = Minecraft.getInstance().getMobEffectTextures();
        if (effect != null) {
            TextureAtlasSprite sprite = ((TextureAtlasHolderInvoker) sprites).emiffect$invokeGetSprite(getId());
            RenderSystem.clearColor(1.0F, 1.0F,1.0F,1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, sprite.atlasLocation());
            draw.blit(x, y, 0, 18, 18, sprite);
            RenderSystem.applyModelViewMatrix();
        }
    }

    @Override
    public boolean isEmpty() {
        return effect == null;
    }

    //? if >=1.20.6 {
    @Override
    public DataComponentPatch getComponentChanges() {
        return DataComponentPatch.EMPTY;
    }
    //?} else {
    /*@Override
    public CompoundTag getNbt() {
        return new CompoundTag();
    }
    *///?}

    @Override
    public Object getKey() {
        return effect;
    }

    @Override
    public ResourceLocation getId() {
        if (effect == null) return VersionUtil.identifier(EMIffectPlugin.MOD_ID, "missingno");
        return BuiltInRegistries.MOB_EFFECT.getKey(effect);
    }

    @Override
    public List<Component> getTooltipText() {
        return List.of(getName());
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        if (effect == null) return List.of();
        List<ClientTooltipComponent> tooltips = new ArrayList<>(
                getTooltipText().stream()
                        .map(EmiPort::ordered)
                        .map(ClientTooltipComponent::create)
                        .toList()
        );

        switch (effect.getCategory()) {
            case BENEFICIAL -> tooltips.add(ClientTooltipComponent.create(EmiPort.ordered(
                    EmiPort.translatable("tooltip.emiffect.beneficial").withStyle(ChatFormatting.GREEN))));
            case NEUTRAL -> tooltips.add(ClientTooltipComponent.create(EmiPort.ordered(
                    EmiPort.translatable("tooltip.emiffect.neutral").withStyle(ChatFormatting.GOLD))));
            case HARMFUL -> tooltips.add(ClientTooltipComponent.create(EmiPort.ordered(
                    EmiPort.translatable("tooltip.emiffect.harmful").withStyle(ChatFormatting.RED))));
        }

        MutableComponent component = EmiPort.translatable(
                        "tooltip.emiffect.color",
                        "#" + String.format("%02x", effect.getColor()).toUpperCase(Locale.ROOT)
                ).withStyle(ChatFormatting.GRAY).append(WHITESPACE)
                .append(EmiPort.literal("█").withStyle(style -> style.withColor(TextColor.fromRgb(effect.getColor()))));
        tooltips.add(ClientTooltipComponent.create(EmiPort.ordered(component)));

        ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        if (id != null)
            tooltips.add(
                    ClientTooltipComponent.create(
                            EmiPort.ordered(EmiPort.literal(
                                    EmiUtil.getModName(id.getNamespace()),
                                    ChatFormatting.BLUE, ChatFormatting.ITALIC
                            ))
                    ));
        return tooltips;
    }

    @Override
    public Component getName() {
        return effect != null ? effect.getDisplayName() : EmiPort.literal("missingno");
    }

    public @Nullable MobEffect getEffect() {
        return effect;
    }

    public static class Serializer implements EmiStackSerializer<MobEffectEmiStack> {
        //? if >=1.20.6 {
        @Override
        public EmiStack create(ResourceLocation id, DataComponentPatch componentChanges, long amount) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
            if (effect != null) return new MobEffectEmiStack(effect);
            return EmiStack.EMPTY;
        }
        //?} else {
        /*@Override
        public EmiStack create(ResourceLocation id, CompoundTag nbt, long amount) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
            if (effect != null) return new MobEffectEmiStack(effect);
            return EmiStack.EMPTY;
        }
        *///?}

        @Override
        public String getType() {
            return "emiffect:effect";
        }
    }
}