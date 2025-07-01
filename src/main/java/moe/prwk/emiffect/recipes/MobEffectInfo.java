package moe.prwk.emiffect.recipes;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.screen.RecipeScreen;
import moe.prwk.emiffect.EMIffectPlugin;
import moe.prwk.emiffect.mixin.RecipeScreenAccessor;
import moe.prwk.emiffect.util.MobEffectEmiStack;
import moe.prwk.emiffect.util.VersionUtil;
import moe.prwk.emiffect.util.resources.ExtraAppenderLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
//? if >=1.20.6 {
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if >=1.20.6 {
import net.minecraft.world.item.component.SuspiciousStewEffects;
//?}
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
//? if =1.20.4 {
/*import net.minecraft.world.level.block.SuspiciousEffectHolder;
*///?}
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
//? if <1.20.6 {
/*import java.util.Arrays;
*///?}
import java.util.List;

@SuppressWarnings("CommentedOutCode")
public class MobEffectInfo implements EmiRecipe {
    private final List<EmiIngredient> inputs;
    private final List<FormattedCharSequence> desc;
    private final ResourceLocation id;
    private int inputStackRow;
    private final MobEffectEmiStack emiStack;

    public MobEffectInfo(MobEffect effect, MobEffectEmiStack emiStack) {
        this.id = emiStack.getId();
        List<EmiIngredient> inputs0 = new ArrayList<>();

        BuiltInRegistries.POTION.holders().forEach(potion -> {
            for (MobEffectInstance instance : potion.value().getEffects()) {
                if (instance.getEffect()/*? if >=1.20.6 {*/.value()/*?}*/.equals(effect)) {
                    inputs0.addAll(List.of(EmiStack.of(VersionUtil.setPotion(Items.POTION.getDefaultInstance(), potion)),
                            EmiStack.of(VersionUtil.setPotion(Items.SPLASH_POTION.getDefaultInstance(), potion)),
                            EmiStack.of(VersionUtil.setPotion(Items.LINGERING_POTION.getDefaultInstance(), potion)),
                            EmiStack.of(VersionUtil.setPotion(Items.TIPPED_ARROW.getDefaultInstance(), potion))));
                    break;
                }
            }
        });

        flowerLoop: for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof FlowerBlock flower) {
                ItemStack stew = new ItemStack(Items.SUSPICIOUS_STEW);
                //? if >=1.20.6 {
                List<Holder<MobEffect>> effects = flower.getSuspiciousEffects().effects().stream()
                        .map(SuspiciousStewEffects.Entry::effect).toList();
                for (Holder<MobEffect> _effect : effects) {
                    if (_effect.value().equals(effect)) {
                        inputs0.add(EmiStack.of(stew));
                        break flowerLoop;
                    }
                }
                //?} elif >=1.20.4 {
                /*List<MobEffect> effects = flower.getSuspiciousEffects().stream()
                        .map(SuspiciousEffectHolder.EffectEntry::effect).toList();
                for (MobEffect _effect : effects) {
                    if (_effect.equals(effect)) {
                        inputs0.add(EmiStack.of(stew));
                        break flowerLoop;
                    }
                }
                *///?} else {
                /*MobEffect _effect = flower.getSuspiciousEffect();
                if (_effect.equals(effect)) {
                    inputs0.add(EmiStack.of(stew));
                    break flowerLoop;
                }
                *///?}
            }
        }

        for (Item item : BuiltInRegistries.ITEM) {
            //? if >=1.20.6 {
            FoodProperties food = item.components().get(DataComponents.FOOD);
            if (food != null) {
                if (food.effects().stream().map(_effect ->
                        _effect.effect().getEffect().value()).toList().contains(effect)) {
                    inputs0.add(EmiStack.of(item));
                }
            }
            //?} else {
            /*FoodProperties food = item.getFoodProperties();
            if (food != null) {
                if (food.getEffects().stream().map(_effect ->
                        _effect.getFirst().getEffect()).toList().contains(effect)) {
                    inputs0.add(EmiStack.of(item));
                }
            }
            *///?}
        }

        //? if >=1.20.6 {
        for (List<Holder<MobEffect>> effects : BeaconBlockEntity.BEACON_EFFECTS) {
            if (effects.stream().map(Holder::value).toList().contains(effect)) {
                inputs0.add(EmiStack.of(Blocks.BEACON));
            }
        }
        //?} else {
        /*for (MobEffect[] effects : BeaconBlockEntity.BEACON_EFFECTS) {
            if (Arrays.asList(effects).contains(effect)) {
                inputs0.add(EmiStack.of(Blocks.BEACON));
            }
        }
        *///?}

        this.inputs = inputs0;
        this.desc = Minecraft.getInstance().font.split(getDescription(id), 110);
        this.inputStackRow = inputs.isEmpty() ? 0 : 1;
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

    public void addFromAppenders(List<ExtraAppenderLoader.ExtraAppender> appenders) {
        for (ExtraAppenderLoader.ExtraAppender appender : appenders) {
            if (id.equals(appender.effectId())) {
                appender.getIngredients().forEach(ingredient -> {
                    if (!inputs.contains(ingredient)) {
                        inputs.add(ingredient);
                    }
                });
            }
        }
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIffectPlugin.CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return VersionUtil.identifier("emiffect", "/effects/"
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
        int upperOffset = 14 + Math.max(desc.size() * Minecraft.getInstance().font.lineHeight, 30) + 2;
        int backgroundHeight = 200;
        if (Minecraft.getInstance().screen instanceof RecipeScreen screen) {
            backgroundHeight = ((RecipeScreenAccessor) screen).emiffect$getBackgroundHeight();
        }
        int slotsHeight = inputs.isEmpty() ? 0 : ((inputs.size() - 1) / 6 + 1) * 18;
        return Math.min(slotsHeight + upperOffset, backgroundHeight);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void addWidgets(WidgetHolder widgets) {
        int titleColor = 16777215;
        if (emiStack.getEffect() != null) {
            switch (emiStack.getEffect().getCategory()) {
                case BENEFICIAL -> titleColor = ChatFormatting.GREEN.getColor();
                case NEUTRAL -> titleColor = ChatFormatting.GOLD.getColor();
                case HARMFUL -> titleColor = ChatFormatting.RED.getColor();
            }
        }
        FormattedCharSequence title = emiStack.getName().getVisualOrderText();
        int titleX = 31 + ((144 - 31 - Minecraft.getInstance().font.width(title)) / 2);
        widgets.addText(title, titleX, 2, titleColor, true);

        final int lineHeight = Minecraft.getInstance().font.lineHeight;
        int descLine = 0;
        for (FormattedCharSequence text : desc) {
            widgets.addText(text, 31, 14 + lineHeight * descLine, 16777215, true);
            descLine += 1;
        }
        int descHeight = Math.max(descLine * lineHeight, 30);
        descHeight += 12;

        int upperOffset = 14 + Math.max(desc.size() * Minecraft.getInstance().font.lineHeight, 30) + 2;
        int ph = inputs.isEmpty() ? 0 : (widgets.getHeight() - (upperOffset + 14) - 2) / 18 + 1;
        PageManager manager = new PageManager(inputs, 6 * ph);
        if (ph < inputStackRow) {
            widgets.addButton(2, upperOffset, 12, 12, 0, 0, () -> true,
                    (mouseX, mouseY, button) -> manager.scroll(-1));
            widgets.addButton(widgets.getWidth() - 14, upperOffset, 12, 12, 12, 0, () -> true,
                    (mouseX, mouseY, button) -> manager.scroll(1));
        }
        for (int i = 0; i < manager.pageSize; i++) {
            widgets.add(new PageSlotWidget(manager, i, i % 6 * 18 + 18, i / 6 * 18 + upperOffset));
        }

        SlotWidget effectSlot = new SlotWidget(emiStack, 3, (descHeight - 26) / 2).large(true).recipeContext(this);
        widgets.add(effectSlot);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    // modified version of dev.emi.emi.api.recipe.EmiIngredientRecipe$PageManager
    private static class PageManager {
        public final List<EmiIngredient> stacks;
        public final int pageSize;
        public int currentPage;

        public PageManager(List<EmiIngredient> stacks, int pageSize) {
            this.stacks = stacks;
            this.pageSize = pageSize;
        }

        public void scroll(int delta) {
            currentPage += delta;
            int totalPages = (stacks.size() - 1) / pageSize + 1;
            if (currentPage < 0) {
                currentPage = totalPages - 1;
            }
            if (currentPage >= totalPages) {
                currentPage = 0;
            }
        }

        public EmiIngredient getStack(int offset) {
            offset += pageSize * currentPage;
            if (offset < stacks.size()) {
                return stacks.get(offset);
            }
            return EmiStack.EMPTY;
        }

        public EmiRecipe getRecipe(int offset) {
            offset += pageSize * currentPage;
            if (offset < stacks.size()) {
                return EmiApi.getRecipeContext(stacks.get(offset));
            }
            return null;
        }
    }

    // modified version of dev.emi.emi.api.recipe.EmiIngredientRecipe$PageSlotWidget
    private static class PageSlotWidget extends SlotWidget {
        public final PageManager manager;
        public final int offset;

        public PageSlotWidget(PageManager manager, int offset, int x, int y) {
            super(EmiStack.EMPTY, x, y);
            this.manager = manager;
            this.offset = offset;
        }

        @Override
        public EmiIngredient getStack() {
            return manager.getStack(offset);
        }

        @Override
        public EmiRecipe getRecipe() {
            return manager.getRecipe(offset);
        }

        //? if forge {
        /*@Override
        public void m_88315_(GuiGraphics draw, int mouseX, int mouseY, float delta) {
            if (!getStack().isEmpty()) {
                super.m_88315_(draw, mouseX, mouseY, delta);
            }
        }
        *///?} else {
        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
            if (!getStack().isEmpty()) {
                super.render(draw, mouseX, mouseY, delta);
            }
        }
        //?}
    }

    private static MutableComponent getDescription(ResourceLocation id) {
        // Handle the bad omen translation key change manually here
        //? if <1.21 {
        /*if (id.getNamespace().equals("minecraft") && id.getPath().equals("bad_omen")) {
            return EmiPort.translatable("effect.minecraft.bad_omen_legacy.description");
        }
        *///?}

        String firstKey = String.format("effect.%s.%s.description", id.getNamespace(), id.getPath());
        if (I18n.exists(firstKey)) return EmiPort.translatable(firstKey);

        String secondKey = String.format("effect.%s.%s.desc", id.getNamespace(), id.getPath());
        if (I18n.exists(secondKey)) return EmiPort.translatable(secondKey);

        return EmiPort.translatable("info.emiffect.desc_not_found");
    }
}
