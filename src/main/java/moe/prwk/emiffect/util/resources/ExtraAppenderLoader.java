package moe.prwk.emiffect.util.resources;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import moe.prwk.emiffect.EMIffectPlugin;
import moe.prwk.emiffect.util.VersionUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExtraAppenderLoader extends SimplePreparableReloadListener<List<ExtraAppenderLoader.ExtraAppender>> {
    private static final Gson GSON = new Gson();
    private static List<ExtraAppender> APPENDERS = List.of();

    @Override
    protected @NotNull List<ExtraAppender> prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<ExtraAppender> ret = new ArrayList<>();

        for (ResourceLocation id : EmiPort.findResources(manager, "emiffect/extra_stacks", i -> i.endsWith(".json"))) {
            try {
                for (Resource resource : manager.getResourceStack(id)) {
                    InputStreamReader reader = new InputStreamReader(EmiPort.getInputStream(resource));
                    JsonObject json = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                    ResourceLocation effectId = VersionUtil.identifier(json.get("id").getAsString());
                    List<JsonElement> ingredients = json.get("stacks").getAsJsonArray().asList();
                    ret.add(new ExtraAppender(effectId, ingredients));
                    EMIffectPlugin.LOGGER.info("Added extra stack appender for effect with ID: {}", effectId);
                }
            } catch (Exception e) {
                EMIffectPlugin.LOGGER.error("Failed to load extra stack appender", e);
            }
        }

        return ret;
    }

    @Override
    protected void apply(List<ExtraAppender> prepared, ResourceManager manager, ProfilerFiller profiler) {
        APPENDERS = prepared;
    }

    public static List<ExtraAppender> getAppenders() {
        return APPENDERS;
    }

    public record ExtraAppender(ResourceLocation effectId, List<JsonElement> ingredients) {
        public List<EmiIngredient> getIngredients() {
            return ingredients.stream().map(EmiIngredientSerializer::getDeserialized).toList();
        }
    }
}
