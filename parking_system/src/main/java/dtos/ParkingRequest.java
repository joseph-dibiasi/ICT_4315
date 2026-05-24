package dtos;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*
 * ParkingRequest is a DTO that represents a request to perform a parking office command.
 * It contains a command name and a set of properties that can be used as parameters for the command.
 * These parameters vary based on the command being executed.
 */
public class ParkingRequest {

    private static final Gson GSON = new GsonBuilder().create();

    private String commandName;
    private Properties properties;

    public ParkingRequest() {
        this("", new Properties());
    }

    public ParkingRequest(String commandName, Properties properties) {
        this.commandName = commandName;
        this.properties = properties != null ? properties : new Properties();
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties != null ? properties : new Properties();
    }

    public String toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("commandName", commandName);
        JsonObject propertyObject = new JsonObject();
        for (String propertyName : properties.stringPropertyNames()) {
            propertyObject.addProperty(propertyName, properties.getProperty(propertyName));
        }
        object.add("properties", propertyObject);
        return GSON.toJson(object);
    }

    public static ParkingRequest fromJson(String json) {
        JsonObject object = GSON.fromJson(json, JsonObject.class);
        Properties properties = new Properties();
        JsonObject propertyObject = object.has("properties") && object.get("properties").isJsonObject()
                ? object.getAsJsonObject("properties")
                : new JsonObject();
        for (Map.Entry<String, JsonElement> entry : propertyObject.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue().isJsonNull() ? "" : entry.getValue().getAsString());
        }
        return new ParkingRequest(object.get("commandName").getAsString(), properties);
    }

    @Override
    public String toString() {
        Map<String, String> sorted = new TreeMap<>();
        for (String propertyName : properties.stringPropertyNames()) {
            sorted.put(propertyName, properties.getProperty(propertyName));
        }
        return "ParkingRequest{commandName='" + commandName + "', properties=" + sorted + "}";
    }
}
