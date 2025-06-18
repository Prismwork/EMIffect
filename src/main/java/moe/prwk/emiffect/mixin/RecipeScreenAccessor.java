package moe.prwk.emiffect.mixin;

import dev.emi.emi.screen.RecipeScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeScreen.class, remap = false)
public interface RecipeScreenAccessor {
    @Accessor("backgroundHeight")
    int emiffect$getBackgroundHeight();
}
