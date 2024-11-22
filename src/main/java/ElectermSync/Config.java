package ElectermSync;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private Dotenv dotenv;

    public Config() {
        dotenv = Dotenv.load();
    }

    public String getValue(String name) {
        return dotenv.get(name);
    }
}
