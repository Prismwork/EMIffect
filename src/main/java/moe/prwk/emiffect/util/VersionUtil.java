package moe.prwk.emiffect.util;

import net.minecraft.core.Holder;
//? if >=1.20.6 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
//?} else {
/*import net.minecraft.world.item.alchemy.PotionUtils;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

public final class VersionUtil {
    private VersionUtil() {}

    public static ResourceLocation identifier(String namespace, String path) {
        //? if >=1.21 {
        return ResourceLocation.parse(namespace + ":" + path);
        //?} else {
        /*return new ResourceLocation(namespace, path);
         *///?}
    }

    public static ResourceLocation identifier(String id) {
        String[] parts = id.split(":", 2);
        try {
            return identifier(parts[0], parts[1]);
        } catch (IndexOutOfBoundsException ignored) {
            return identifier("minecraft", parts[0]);
        }
    }

    public static ItemStack setPotion(ItemStack stack, Holder<Potion> potion) {
        //? if >=1.20.6 {
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
        //?} else {
        /*return PotionUtils.setPotion(stack, potion.value());
        *///?}
    }
}
