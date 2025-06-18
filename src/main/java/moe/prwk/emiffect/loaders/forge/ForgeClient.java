//? if forge {
/*package moe.prwk.emiffect.loaders.forge;

import moe.prwk.emiffect.util.resources.ExtraAppenderLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "emiffect", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClient {
    @SubscribeEvent
    public static void registerResourceReloaders(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ExtraAppenderLoader());
    }
}
*///?}
