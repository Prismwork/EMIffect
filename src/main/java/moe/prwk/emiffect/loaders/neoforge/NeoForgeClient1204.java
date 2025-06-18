//? if neoforge && =1.20.4 {
/*package moe.prwk.emiffect.loaders.neoforge;

import moe.prwk.emiffect.util.resources.ExtraAppenderLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@Mod.EventBusSubscriber(modid = "emiffect", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoForgeClient1204 {
    @SubscribeEvent
    public static void registerResourceReloaders(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ExtraAppenderLoader());
    }
}
*///?}
