package server.database.dataHandlers.courseware.course;

import server.database.MySQLHandler;
import shared.model.courseware.educationalMaterial.Item;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.util.config.Config;
import shared.util.config.ConfigType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EducationalMaterialDataHandler {
    private final MySQLHandler databaseHandler;

    public EducationalMaterialDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public ArrayList<String> getItemsList(String eduMaterialCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "items", "educational_material") + " educationalMaterialCode = " +
                getStringFormat(eduMaterialCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                String itemsString = resultSet.getString("items");
                if (itemsString != null && !itemsString.equals("[]")) {
                    String list = itemsString.substring(1, itemsString.length() - 1);
                    return new ArrayList<>(Arrays.asList(list.split(", ")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Item> getItems(String eduMaterialCode) {
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<String> itemList = getItemsList(eduMaterialCode);
        for (String itemCode : itemList) {
            Item item = getItem(itemCode);
            if (item != null) items.add(item);
        }
        return items;
    }

    public Item getItem(String itemCode) {
        ArrayList<Item> items = new ArrayList<>();
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "*", "item") + " itemCode = " + getStringFormat(itemCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                ItemType type = ItemType.valueOf(resultSet.getString("itemType"));
                String item = resultSet.getString("item");
                return new Item(itemCode, type, item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getName(String eduMaterialCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "name", "educational_material") +
                " educationalMaterialCode = " + getStringFormat(eduMaterialCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        try {
            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean removeEduMaterial(String eduMaterialCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "removeData");
        query = String.format(query, "educational_material") + " educationalMaterialCode = " +
                getStringFormat(eduMaterialCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean updateItems(ArrayList<String> items, String eduMaterialCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "educational_material", " items = " + getStringFormat(items.toString()))
                + " educationalMaterialCode = " + getStringFormat(eduMaterialCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean removeItem(String itemCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "removeData");
        query = String.format(query, "item") + " itemCode = " + getStringFormat(itemCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean addItem(String itemCode, ItemType type, String item) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "insertData");
        query = String.format(query, "item", "itemCode, itemType, item", getStringFormat(itemCode) + ", " +
                getStringFormat(type.toString()) + ", " + getStringFormat(item));
        return this.databaseHandler.updateData(query);
    }

    public boolean editItem(String itemCode, String item) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "item", " item = " + getStringFormat(item)) + " itemCode = " +
                getStringFormat(itemCode);
        return this.databaseHandler.updateData(query);
    }

    public boolean updateCourseEduMaterials(String courseCode, String eduMaterialCode) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "getOneData");
        query = String.format(query, "educationalMaterials", "course") + " courseCode = " + getStringFormat(courseCode);
        ResultSet resultSet = this.databaseHandler.getResultSet(query);
        ArrayList<String> eduMaterialList = new ArrayList<>();
        try {
            if (resultSet.next()) {
                String eduMaterials = resultSet.getString("educationalMaterials");
                if (eduMaterials != null && !eduMaterials.equals("[]")) {
                    String list = eduMaterials.substring(1, eduMaterials.length() - 1);
                    eduMaterialList = new ArrayList<>(Arrays.asList(list.split(", ")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        eduMaterialList.remove(eduMaterialCode);
        query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "course", " educationalMaterials = " + getStringFormat(eduMaterialList.toString()))
                + " courseCode = " + getStringFormat(courseCode);
        return this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
