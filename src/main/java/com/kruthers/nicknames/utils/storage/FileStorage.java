/*
 * Nickname Ultimate - A comprehensive nickname plugin for spigot
 * Copyright (C) 2020 kruthers
 *
 * This Program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The program  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kruthers.nicknames.utils.storage;

import com.google.gson.JsonArray;
import com.kruthers.nicknames.NicknamesUltimate;
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class FileStorage {
    private static final NicknamesUltimate plugin = NicknamesUltimate.getPlugin(NicknamesUltimate.class);

    private static final String PLUGIN_FILE = plugin.getDataFolder().getPath()+"/";
    private static final Logger LOGGER = NicknamesUltimate.LOGGER;
    private static final File nicknamesFile = new File(PLUGIN_FILE+"nicknames.json");

    private static HashMap<UUID,String> nicknameData = new HashMap<>();


    //File Management
    public static boolean setup(){

        if (nicknamesFile.exists()){
            LOGGER.info("Found nicknames.json, loading data");
            try {
                JSONParser jsonParser = new JSONParser();
                Object parser = jsonParser.parse(new FileReader(nicknamesFile));
                JSONArray jsonArray = (JSONArray) parser;

                Iterator<JSONObject> iterator = jsonArray.iterator();

                while (iterator.hasNext()){
                    JSONObject jsonObject = iterator.next();
                    UUID uuid = UUID.fromString(jsonObject.get("uuid").toString());
                    String nick = jsonObject.get("nickname").toString();

                    nicknameData.put(uuid,nick);
                }

                LOGGER.info("Loaded previous nickname data, storing");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
                LOGGER.severe("Failed to load nicknames.json, previous nicknames will not be loaded");
                return false;
            }
        } else {
            LOGGER.warning("Failed to find nicknames.json, creating new file...");
            try {
                nicknamesFile.createNewFile();
                LOGGER.info("Created new file, nicknames.json");
                writeToFile("nicknames.json",new JsonArray().toString());
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.severe("Failed create nicknames.json! Nickname data will not be saved!");
                return false;
            }
        }
        return true;
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
        JSONArray jsonArray = new JSONArray();
        for (UUID uuid : nicknameData.keySet()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid",uuid.toString());
            jsonObject.put("nickname",nicknameData.get(uuid));

            jsonArray.add(jsonObject);
        }

        writeToFile("nicknames.json",jsonArray.toJSONString());

    }


    //Nickname checks
    public static List<OfflinePlayer> getPlayers(String nick){
        List<OfflinePlayer> players = new ArrayList<>();

        for (UUID uuid : nicknameData.keySet()){
            String checkNick = nicknameData.get(uuid);
            checkNick= Utils.removeAllFormatting(checkNick);
            if (checkNick.equalsIgnoreCase(nick)){
                players.add(Bukkit.getOfflinePlayer(uuid));
            }
        }

        return players;

    }

    public static String getNickname(UUID checkUUID){
        if (nicknameData.size()==0) { return null; }

        return nicknameData.get(checkUUID);
    }

    public static void saveNick(Player player, String newNick){

        nicknameData.put(player.getUniqueId(),newNick);
        LOGGER.info(nicknameData.toString());
    }

    public static void removeNick(Player player){
        nicknameData.remove(player.getUniqueId());
    }

    public static boolean checkNicknameList(String nick,UUID ignored){
        if (!nicknameData.values().contains(nick.toLowerCase())){
            return false;
        }

        for (UUID uuid : nicknameData.keySet()){
            String string=Utils.removeAllFormatting(nicknameData.get(uuid));
            if (nick.equalsIgnoreCase(string) && !ignored.toString().equals(uuid.toString())){
                return true;
            }
        }
        return false;
    }


    public static boolean checkUsername(String nickname,UUID exclude){
        for (World world: Bukkit.getServer().getWorlds()){
            File playerData = new File(world.getWorldFolder()+"/playerdata/");
            for (File file: playerData.listFiles()){
                UUID uuid = UUID.fromString(file.getName().replaceAll(".dat$", ""));
                if(!uuid.equals(exclude)){
                    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    if (p.getName()==null){
                        return false;
                    } else if (p.getName().equalsIgnoreCase(nickname)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static HashMap<UUID, String> getNicknameData() {
        return nicknameData;
    }

    public static Collection<String> getNicknames() {
        return nicknameData.values();
    }
}
