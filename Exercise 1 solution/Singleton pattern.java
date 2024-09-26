class ConfigurationManager {
    private static ConfigurationManager instance;
    private String databaseUrl;
    private String apiKey;

    private ConfigurationManager() {
        // Private constructor to prevent instantiation
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    public void setDatabaseUrl(String url) {
        this.databaseUrl = url;
    }

    public void setApiKey(String key) {
        this.apiKey = key;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }
}

public class ConfigDemo {
    public static void main(String[] args) {
        ConfigurationManager config1 = ConfigurationManager.getInstance();
        config1.setDatabaseUrl("jdbc:mysql://localhost:3306/mydb");
        config1.setApiKey("abcdef123456");

        ConfigurationManager config2 = ConfigurationManager.getInstance();
        System.out.println("Database URL: " + config2.getDatabaseUrl());
        System.out.println("API Key: " + config2.getApiKey());
    }
}