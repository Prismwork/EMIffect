package io.github.pkstdev.emiffect.recipe;

import com.mojang.datafixers.util.Pair;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.pkstdev.emiffect.EMIffectPlugin;
import io.github.pkstdev.emiffect.util.stack.StatusEffectEmiStack;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusEffectInfo implements EmiRecipe {
    private final List<EmiIngredient> inputs;
    private final List<OrderedText> desc;
    private final Identifier id;
    private int inputStackRow;
    private final StatusEffectEmiStack emiStack;

    public StatusEffectInfo(StatusEffect effect, StatusEffectEmiStack emiStack) {
        this.id = Registry.STATUS_EFFECT.getId(effect) != null ? Registry.STATUS_EFFECT.getId(effect) : new Identifier("emiffect", "missingno");
        List<EmiIngredient> inputs1 = new ArrayList<>(List.of(EmiStack.of(PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potion.byId(id.toString()))),
                EmiStack.of(PotionUtil.setPotion(Items.SPLASH_POTION.getDefaultStack(), Potion.byId(id.toString()))),
                EmiStack.of(PotionUtil.setPotion(Items.LINGERING_POTION.getDefaultStack(), Potion.byId(id.toString()))),
                EmiStack.of(PotionUtil.setPotion(Items.TIPPED_ARROW.getDefaultStack(), Potion.byId(id.toString())))));
        for (Block block : Registry.BLOCK) {
            if (block instanceof FlowerBlock flower) {
                ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
                StatusEffect flowerEffect = flower.getEffectInStew();
                if (flowerEffect.equals(effect)) {
                    SuspiciousStewItem.addEffectToStew(stew, effect, 200);
                    inputs1.add(EmiStack.of(stew));
                    break;
                }
            }
        }
        for (Item item : Registry.ITEM) {
            FoodComponent food = item.getFoodComponent();
            if (food != null) {
                ItemStack stack = new ItemStack(item);
                for (Pair<StatusEffectInstance, Float> pair : food.getStatusEffects()) {
                    if (pair.getFirst().getEffectType().equals(effect)) {
                        inputs1.add(EmiStack.of(stack));
                        break;
                    }
                }
            }
        }
        for (StatusEffect[] effects : BeaconBlockEntity.EFFECTS_BY_LEVEL) {
            if (Arrays.asList(effects).contains(effect)) {
                inputs1.add(EmiStack.of(Blocks.BEACON));
            }
        }
        this.inputs = inputs1;
        this.desc = MinecraftClient.getInstance().textRenderer.wrapLines(EmiPort.translatable("effect." + id.getNamespace() + "." + id.getPath() + ".description"), 110);
        this.inputStackRow = 1;
        int inputColumn = 0;
        for (EmiIngredient ignored : inputs) {
            if (inputColumn >= 6) {
                this.inputStackRow += 1;
                inputColumn = 0;
            }
            inputColumn += 1;
        }
        this.emiStack = emiStack;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIffectPlugin.CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return new Identifier("emi", "emiffect/"
                + id.getNamespace()
                + "/" + id.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(emiStack);
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 2 + Math.max(desc.size() * MinecraftClient.getInstance().textRenderer.fontHeight, 30) + 4 + (inputStackRow * 18) + 2;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int lineHeight = MinecraftClient.getInstance().textRenderer.fontHeight;
        int descLine = 0;
        for (OrderedText text : desc) {
            widgets.addText(text, 31, 2 + lineHeight * descLine, 16777215, true);
            descLine += 1;
        }
        int descHeight = Math.max(descLine * lineHeight, 30);

        int inputRow = 0;
        int inputColumn = 0;
        for (EmiIngredient ingredient : inputs) {
            widgets.addSlot(ingredient, 18 + (inputColumn * 18), descHeight + 4 + (inputRow * 18));
            inputColumn += 1;
            if (inputColumn >= 6) {
                inputRow += 1;
                inputColumn = 0;
            }
        }

        SlotWidget effectSlot = new SlotWidget(getOutputs().get(0), 3, (descHeight - 26) / 2).output(true);
        widgets.add(effectSlot);
    }
}
