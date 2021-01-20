package fr.milekat.MCPG_Cite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import fr.milekat.MCPG_Core.MainCore;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Test implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MongoCollection<Document> collection = MainCore.getMongoDB().getMongoClient().getDatabase("minecraft").getCollection("test");
        /*
        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
                .create();
        */
        if (args.length == 1 && args[0].equalsIgnoreCase("save")) {
            ItemStack itemStack = ((Player) sender).getInventory().getItemInMainHand();
            MainCite.getInstance().getConfig().set("Item", itemStack.serialize());


            Document document = new Document();
            document.put("_id", 1);
            document.put("item", ymlToDoc(itemStack.serialize()));

            sender.sendMessage(document.toJson());
            ReplaceOptions replaceOptions = new ReplaceOptions();
            replaceOptions.upsert(true);
            collection.replaceOne(new BasicDBObject("_id", 1), document, replaceOptions);

            /*
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("_id", 1);
            //jsonObject.put("item", gson.toJsonTree(itemStack, ItemStack.class));
            jsonObject.put("item", convertToJson(itemStack.serialize()));

            sender.sendMessage(jsonObject.toJSONString());
            collection.insertOne(Document.parse(jsonObject.toJSONString()));
            */
        } else if (args.length == 1 && args[0].equalsIgnoreCase("load")) {

            Document query = new Document();
            query.put("_id", 1);
            Map<String, Object> obj = docToYml((Document) collection.find(query).first().get("item"));

            for (Map.Entry<String, Object> loop : obj.entrySet()) {
                sender.sendMessage(loop.getKey() + " - " + loop.getValue().toString() + " - " + loop.getValue().getClass().getName());
            }

            sender.sendMessage(obj.toString());
            ItemStack itemStack = ItemStack.deserialize(obj);
            ((Player) sender).getInventory().addItem(itemStack);

            //ItemStack itemStack = ItemStack.deserialize(MainCite.getInstance().getConfig().getConfigurationSection("Item").getValues(true));
            ((Player) sender).getInventory().addItem(itemStack);

            /*
            Document query = new Document();
            query.put("_id", 1);
            Document document = (Document) collection.find(query).first().get("item");
            assert document != null;
            try {
                sender.sendMessage(asYaml(document.toJson()).toString());
                ItemStack itemStack = ItemStack.deserialize(asYaml(document.toJson()));
                ((Player) sender).getInventory().addItem(itemStack);
            } catch (IOException e) {
                e.printStackTrace();
            }
             */
        }
        return true;
    }

    /*
    private JSONObject convertToJson(Map<String,Object> yaml) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(yaml);
        return jsonObject;
    }
    */

    private String convertYamlToJson(String yaml) throws JsonProcessingException {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = yamlReader.readValue(yaml, Object.class);

        ObjectMapper jsonWriter = new ObjectMapper();
        return jsonWriter.writeValueAsString(obj);
    }

    public Map<String, Object> asYaml(String jsonString) throws IOException {
        // parse JSON
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
        // save it as YAML
        Yaml yaml = new Yaml();
        return yaml.load(new YAMLMapper().writeValueAsString(jsonNodeTree));
    }

    private Document ymlToDoc(Map<String, Object> yml) {
        Document document = new Document();
        for (Map.Entry<String, Object> loop : yml.entrySet()) {
            if (loop.getValue().getClass().getSimpleName().equalsIgnoreCase("CraftMetaItem")) {

            } else {
                document.put(loop.getKey(), loop.getValue().toString());
            }
        }
        return document;
    }

    private Map<String, Object> docToYml(Document document) {
        return new HashMap<>(document);
    }
}
