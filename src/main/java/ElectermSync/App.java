package ElectermSync;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static spark.Spark.*;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Gson gson = new Gson();


        Config dotenv = new Config();
        String secretOri = dotenv.getValue("JWT_SECRET");
        byte[] bytesToEncode = secretOri.getBytes(StandardCharsets.UTF_8);

        // Encode the bytes using Base64
        String secret = Base64.getEncoder().encodeToString(bytesToEncode);

        String ids = dotenv.getValue("JWT_USERS");
        String[] idArrStrings = ids.split(",");
        Jwts.parserBuilder().setSigningKey(secret).build();

        port(Integer.parseInt((dotenv.getValue("PORT"))));

        ipAddress(dotenv.getValue("HOST"));

        before("/api/sync", (request, response) -> {
            String authHeader = request.headers("Authorization");
            try {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new JwtException("Missing or invalid token");
                } else {
                    String token = authHeader.substring(7);
                    Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);
                    String id = claimsJws.getBody().get("id").toString();
                    boolean found = Arrays.stream(idArrStrings).anyMatch(element -> element.equals(id));
                    if (!found) {
                        throw new JwtException("Unauthorized access");
                    }
                    request.attribute("jwtId", id);
                }
            } catch (JwtException ex) {
                halt(401, "Unauthorized: " + ex.getMessage());
            }
        });

        get("/api/sync", (request, response) -> {
            String jwtId = request.attribute("jwtId");
            ReadResult r = FileStore.read(jwtId, dotenv);
            response.status(r.statusCode);
            return r.fileData;
        });
        post("/api/sync", (request, response) -> {
            return "test ok";
        });
        put("/api/sync", (request, response) -> {
            String requestBody = request.body();
            String jwtId = request.attribute("jwtId");
            response.type("application/json");
            WriteResult r = FileStore.write(requestBody, jwtId, dotenv);
            response.status(r.statusCode);
            return r.message;
        });

        after((request, response) -> {
            response.type("application/json");
            response.header("Content-Encoding", "gzip");
            log.info(String.format("%s[%s] - [%s]", request.requestMethod(), request.url(), response.status()));
        });
    }
}
