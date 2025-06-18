//? if neoforge && >=1.20.6 {
/*package moe.prwk.emiffect.loaders.neoforge;

import moe.prwk.emiffect.util.resources.ExtraAppenderLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = "emiffect", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoForgeClient {
    @SubscribeEvent
    public static void registerResourceReloaders(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ExtraAppenderLoader());
    }
}
*///?}
