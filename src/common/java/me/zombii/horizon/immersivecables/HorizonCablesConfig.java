package me.zombii.horizon.immersivecables;

import com.google.gson.Gson;
import finalforeach.cosmicreach.util.SaveLocation;

import java.io.*;

public class HorizonCablesConfig {

    public static final File configFile = new File(SaveLocation.getSaveFolder(), "horizon-cables-config.json");
    public static final HorizonCablesConfig DEFAULT = new HorizonCablesConfig();
    public static final HorizonCablesConfig INSTANCE;
    private static final Gson GSON = new Gson();

    static {
        if (!configFile.exists()) {
            try {
                FileWriter writer = new FileWriter(configFile);
                writer.write(GSON.toJson(DEFAULT, HorizonCablesConfig.class));
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            INSTANCE = GSON.fromJson(new FileReader(configFile), HorizonCablesConfig.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int perTickEventBudget = 30;

    public HorizonCablesConfig() {
    }

}
