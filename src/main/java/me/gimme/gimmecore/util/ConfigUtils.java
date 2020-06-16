package me.gimme.gimmecore.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ConfigUtils {

    /**
     * Gets the YAML config at the specified file path, or creates a new one at that path from the resource with the
     * same name embedded with the specified plugin's .jar file.
     *
     * @param plugin   the plugin that has the resource with the defaults
     * @param filePath the path to the config
     * @return the loaded YAML config
     * @throws IOException
     * @throws InvalidConfigurationException
     */
    public static YamlConfiguration getYamlConfig(Plugin plugin, String filePath) throws IOException, InvalidConfigurationException {
        File languageFile = new File(plugin.getDataFolder(), filePath);
        if (!languageFile.isFile()) {
            Files.createDirectories(languageFile.toPath().getParent());
            plugin.saveResource(filePath, false);
        }

        YamlConfiguration config = new YamlConfiguration();
        config.load(languageFile);

        return config;
    }

    /**
     * Serializes and saves the specified object to the json file at the specified file path.
     *
     * @param pluginDataFolder the plugin data folder where the json file should be created
     * @param filePath         the file path of the json file
     * @param object           the object to save
     * @throws IOException
     * @throws IllegalArgumentException if the file path has the wrong extension
     */
    public static void saveToJson(File pluginDataFolder, String filePath, Object object) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        saveToJson(pluginDataFolder, filePath, object, gson);
    }

    /**
     * Serializes and saves the specified object to the json file at the specified file path.
     * The gson can be used to register type adapters or add other serialization settings.
     *
     * @param pluginDataFolder the plugin data folder where the json file should be created
     * @param filePath         the file path of the json file
     * @param object           the object to save
     * @param gson             the Gson object to use in the serializing
     * @throws IOException
     * @throws IllegalArgumentException if the file path has the wrong extension
     */
    public static void saveToJson(File pluginDataFolder, String filePath, Object object, Gson gson) throws IOException {
        if (!filePath.endsWith(".json")) {
            if (filePath.contains("."))
                throw new IllegalArgumentException("The file path needs to end with \".json\" or have no extension.");

            filePath += ".json";
        }

        String json = gson.toJson(object);

        File jsonFile = new File(pluginDataFolder, filePath);
        Files.createDirectories(jsonFile.toPath().getParent());
        Files.write(jsonFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Deserializes and returns an object from the json file at the specified file path.
     *
     * @param pluginDataFolder the plugin data folder where the json file exists
     * @param filePath         the file path of the json file
     * @param <T>              the type of the loaded object
     * @return the loaded object
     * @throws IOException
     */
    @Nullable
    public static <T> T loadFromJson(File pluginDataFolder, String filePath) throws IOException {
        Gson gson = new GsonBuilder().create();
        return loadFromJson(pluginDataFolder, filePath, gson);
    }

    /**
     * Deserializes and returns an object from the json file at the specified file path.
     *
     * @param pluginDataFolder the plugin data folder where the json file exists
     * @param filePath         the file path of the json file
     * @param gson             the Gson object to use in the deserializing
     * @param <T>              the type of the loaded object
     * @return the loaded object
     * @throws IOException
     */
    @Nullable
    public static <T> T loadFromJson(File pluginDataFolder, String filePath, Gson gson) throws IOException {
        File jsonFile = new File(pluginDataFolder, filePath);

        if (!jsonFile.isFile()) return null;
        Reader reader = new FileReader(jsonFile);

        T o = gson.fromJson(reader, new TypeToken<T>(){}.getType());

        reader.close();
        return o;
    }

}