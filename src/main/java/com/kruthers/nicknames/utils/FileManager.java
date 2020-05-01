package com.kruthers.nicknames.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kruthers.nicknames.Nicknames;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Logger;

public class FileManager {
    private static final Nicknames plugin = Nicknames.getPlugin(Nicknames.class);
    private static final String PLUGIN_FILE = plugin.getDataFolder().getPath()+"/";
    private static final Logger LOGGER = Nicknames.LOGGER;
    private static final File nicknamesFile = new File(PLUGIN_FILE+"nicknames.json");

    public static boolean nicknamesLoaded = false;

    public static void setup(){

        if (nicknamesFile.exists()){
            LOGGER.info("Found nicknames.json, loading data");
            try {
                JSONParser jsonParser = new JSONParser();
                Object parser = jsonParser.parse(new FileReader(nicknamesFile));
                JSONArray jsonArray = (JSONArray) parser;

                Iterator<JSONObject> iterator = jsonArray.iterator();
                HashMap<UUID,String> nicknames = new HashMap<>();

                while (iterator.hasNext()){
                    JSONObject jsonObject = iterator.next();
                    UUID uuid = UUID.fromString(jsonObject.get("uuid").toString());
                    String nick = jsonObject.get("nickname").toString();

                    nicknames.put(uuid,nick);
                }

                LOGGER.info("Loaded previous nickname data, storing");
                Nicknames.setNicknameData(nicknames);
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                LOGGER.severe("Failed to load nicknames.json, previous nicknames will not be loaded");
            }
            nicknamesLoaded=true;
        } else {
            LOGGER.warning("Failed to find nicknames.json, creating new file...");
            try {
                nicknamesFile.createNewFile();
                LOGGER.info("Created new file, nicknames.json");
                writeToFile("nicknames.json",new JsonArray().toString());
                nicknamesLoaded=true;
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.severe("Failed create nicknames.json! Nickname data will not be saved!");
            }
        }
    }

    private static void writeToFile(String fileName, String content){
        try {
            FileWriter fileWriter = new FileWriter(PLUGIN_FILE+fileName);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException err){
            LOGGER.severe("Failed to write to "+fileName);
            err.printStackTrace();
        }
    }

    public static void save(){
        HashMap<UUID,String> nicknameData = Nicknames.getNicknameData();
        JSONArray jsonArray = new JSONArray();
        for (UUID uuid : nicknameData.keySet()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid",uuid.toString());
            jsonObject.put("nickname",nicknameData.get(uuid));

            jsonArray.add(jsonObject);
        }

        writeToFile("nicknames.json",jsonArray.toJSONString());

    }

}
