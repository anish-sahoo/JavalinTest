import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class Main {
    public static void main(String[] args) throws SQLException{
        Gson gson = new GsonBuilder().create();
        JsonMapper gsonMapper = new JsonMapper() {
            @Override
            public @NotNull String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj, type);
            }

            @Override
            public <T> @NotNull T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                return gson.fromJson(json, targetType);
            }
        };
        Javalin app = Javalin.create(config -> config.jsonMapper(gsonMapper)).start(7070);

        app.get("/description", ctx -> ctx.result("Hi, this response is from Javalin!"));
    }
}