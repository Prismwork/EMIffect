//? if fabric {
package moe.prwk.emiffect.loaders.fabric;

import moe.prwk.emiffect.util.VersionUtil;
import moe.prwk.emiffect.util.resources.ExtraAppenderLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FabricEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final ExtraAppenderLoader loader = new ExtraAppenderLoader();

            @Override
            public @NotNull String getName() {
                return loader.getName();
            }

            @Override
            public ResourceLocation getFabricId() {
                return VersionUtil.identifier("emiffect:extra_stack_appender");
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(
                    PreparationBarrier preparationBarrier,
                    ResourceManager resourceManager,
                    ProfilerFiller preparationsProfiler,
                    ProfilerFiller reloadProfiler,
                    Executor backgroundExecutor,
                    Executor gameExecutor)
            {
                return loader.reload(
                        preparationBarrier,
                        resourceManager,
                        preparationsProfiler,
                        reloadProfiler,
                        backgroundExecutor,
                        gameExecutor
                );
            }
        });
    }
}
//?}
