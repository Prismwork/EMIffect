package moe.prwk.emiffect;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import moe.prwk.emiffect.recipes.MobEffectInfo;
import moe.prwk.emiffect.util.MobEffectEmiStack;
import moe.prwk.emiffect.util.VersionUtil;
//? if >=1.20.6 {
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@EmiEntrypoint
public class EMIffectPlugin implements EmiPlugin {
    public static final String MOD_ID = "emiffect";
    public static final ResourceLocation CATEGORY_ICON = VersionUtil.identifier(MOD_ID, "textures/gui/emi/icon.png");
    public static final EmiRecipeCategory CATEGORY
            = new EmiRecipeCategory(
                    VersionUtil.identifier(MOD_ID, "status_effect_info"),
                    new EmiTexture(CATEGORY_ICON, 0, 0, 16, 16, 16, 16, 16, 16)
            );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CATEGORY);
        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            MobEffectEmiStack stack = new MobEffectEmiStack(effect);
            registry.addRecipe(new MobEffectInfo(effect, stack));
            registry.addEmiStack(stack);
        }
        registry.addWorkstation(CATEGORY, EmiStack.of(Blocks.BEACON));
        registry.addWorkstation(CATEGORY, EmiStack.of(Items.POTION));
        registry.addWorkstation(CATEGORY, EmiStack.of(Items.SPLASH_POTION));
        registry.addWorkstation(CATEGORY, EmiStack.of(Items.LINGERING_POTION));
        registry.addWorkstation(CATEGORY, EmiStack.of(Items.SUSPICIOUS_STEW));
        for (Item item : BuiltInRegistries.ITEM) {
            //? if >=1.20.6 {
            FoodProperties food = item.components().get(DataComponents.FOOD);
            if (food != null) {
                if (!food.effects().isEmpty()) {
                    registry.addWorkstation(CATEGORY, EmiStack.of(item));
                }
            }
            //?} else {
            /*FoodProperties food = item.getFoodProperties();
            if (food != null) {
                if (!food.getEffects().isEmpty()) {
                    registry.addWorkstation(CATEGORY, EmiStack.of(item));
                }
            }
            *///?}
        }
    }
}
