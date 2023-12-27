import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection(
            "jdbc:mariadb://localhost:3306/sampledb",
            Credentials.getUSERNAME(), Credentials.getPASSWORD()
        );
        Statement statement = connection.createStatement();
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

        app.before(ctx -> {
            long startTime = System.currentTimeMillis();
            ctx.attribute("startTime", startTime);
        });

        app.get("/", ctx -> ctx.html("<h1>Hello World</h1> <p>Go to <a href=\"/description\">/description</a> to see the description of the users table</p>"));

        app.get("/description", ctx -> {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users;");
            ArrayList<User> users = new ArrayList<User>();
            while(resultSet.next()) {
                users.add(new User(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email")));
            }
            ctx.json(users);
            
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - (Long)ctx.attribute("startTime");
            System.out.println("[call] Request from "+ ctx.ip() +" took " + timeTaken + "ms");
            System.gc();
        });
    }
}