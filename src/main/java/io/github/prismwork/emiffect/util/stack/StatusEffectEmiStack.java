package io.github.prismwork.emiffect.util.stack;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class StatusEffectEmiStack extends EmiStack {
    @Nullable
    private final StatusEffect effect;
    private final StatusEffectEntry entry;

    protected StatusEffectEmiStack(@Nullable StatusEffect effect) {
        this.effect = effect;
        this.entry = new StatusEffectEntry(effect);
    }

    public static StatusEffectEmiStack of(@Nullable StatusEffect effect) {
        return new StatusEffectEmiStack(effect);
    }

    @Override
    public EmiStack copy() {
        return StatusEffectEmiStack.of(this.effect);
    }

    @Override
    public boolean isEmpty() {
        return effect != null;
    }

    public @Nullable StatusEffect getEffect() {
        return effect;
    }

    @Override
    public void render(MatrixStack matrices, int x, int y, float delta, int flags) {
        StatusEffectSpriteManager sprites = MinecraftClient.getInstance().getStatusEffectSpriteManager();
        if (effect != null) {
            Sprite sprite = sprites.getSprite(effect);
            RenderSystem.clearColor(1.0F, 1.0F,1.0F,1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, sprite.getAtlasId());
            DrawableHelper.drawSprite(matrices, x, y, 0, 18, 18, sprite);
            RenderSystem.applyModelViewMatrix();
        }
    }

    @Override
    public NbtCompound getNbt() {
        return null;
    }

    @Override
    public Object getKey() {
        return effect;
    }

    @Override
    public Entry<?> getEntry() {
        return entry;
    }

    @Override
    public Identifier getId() {
        return Registries.STATUS_EFFECT.getId(effect);
    }

    @Override
    public List<Text> getTooltipText() {
        return List.of(getName());
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        if (effect == null) return List.of();
        List<TooltipComponent> tooltips = new ArrayList<>(getTooltipText().stream().map(EmiPort::ordered).map(TooltipComponent::of).toList());
        switch (effect.getCategory()) {
            case BENEFICIAL -> tooltips.add(TooltipComponent.of(EmiPort.ordered(
                    EmiPort.translatable("tooltip.emiffect.beneficial").formatted(Formatting.GREEN))));
            case NEUTRAL -> tooltips.add(TooltipComponent.of(EmiPort.ordered(
                    EmiPort.translatable("tooltip.emiffect.neutral").formatted(Formatting.GOLD))));
            case HARMFUL -> tooltips.add(TooltipComponent.of(EmiPort.ordered(
                    EmiPort.translatable("tooltip.emiffect.harmful").formatted(Formatting.RED))));
        }
        tooltips.add(TooltipComponent.of(EmiPort.ordered(
                EmiPort.translatable("tooltip.emiffect.color", "#" + String.format("%02x", effect.getColor())).formatted(Formatting.GRAY))));
        Identifier id = Registries.STATUS_EFFECT.getId(effect);
        if (id != null)
            FabricLoader.getInstance().getModContainer(id.getNamespace()).ifPresent(modContainer -> tooltips.add(TooltipComponent.of(EmiPort.ordered(
                    EmiPort.literal(modContainer.getMetadata().getName()).formatted(Formatting.BLUE, Formatting.ITALIC)))));
        return tooltips;
    }

    @Override
    public Text getName() {
        return effect != null ? effect.getName() : EmiPort.literal("missingno");
    }

    public static class StatusEffectEntry extends Entry<StatusEffect> {
        public StatusEffectEntry(StatusEffect value) {
            super(value);
        }

        @Override
        public Class<? extends StatusEffect> getType() {
            return getValue().getClass();
        }
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack stack = super.getItemStack();
        if (effect != null) {
            stack = PotionUtil.setCustomPotionEffects(Items.POTION.getDefaultStack(),
                    Collections.singletonList(new StatusEffectInstance(effect, 600)));
        }
        return stack;
    }
}
