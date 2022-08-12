package server.logic.managers.courseware.course;

import server.database.dataHandlers.courseware.course.EducationalMaterialDataHandler;
import server.network.ClientHandler;
import shared.model.courseware.educationalMaterial.Item;
import shared.model.courseware.educationalMaterial.ItemType;
import shared.request.Request;
import shared.response.Response;
import shared.response.ResponseStatus;
import shared.util.config.Config;
import shared.util.config.ConfigType;
import shared.util.media.MediaHandler;

import java.util.ArrayList;
import java.util.List;

public class EducationalMaterialManager {
    private final ClientHandler client;
    private final EducationalMaterialDataHandler dataHandler;

    public EducationalMaterialManager(ClientHandler clientHandler) {
        this.client = clientHandler;
        this.dataHandler = new EducationalMaterialDataHandler(clientHandler.getDataHandler());
    }

    public Response showItems(Request request) {
        String courseCode = (String) request.getData("courseCode");
        String eduMaterialCode = (String) request.getData("educationalMaterialCode");
        List<Item> items = this.dataHandler.getItems(eduMaterialCode);
        CourseManager manager = new CourseManager(this.client);
        boolean isAssistant = manager.isAssistant(courseCode);
        String name = this.dataHandler.getName(eduMaterialCode);
        Response response = new Response(ResponseStatus.OK);
        response.addData("isAssistant", isAssistant);
        response.addData("name", name);
        MediaHandler handler = new MediaHandler();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getItemType() == ItemType.MEDIA_FILE) item.setText(handler.encode(item.getText()));
            response.addData("items" + i, item);
        }
        return response;
    }

    public Response deleteEducationalMaterial(Request request) {
        String courseCode = (String) request.getData("courseCode");
        String eduMaterialCode = (String) request.getData("educationalMaterialCode");
        boolean result1 = this.dataHandler.removeEduMaterial(eduMaterialCode);
        boolean result2 = this.dataHandler.updateCourseEduMaterials(courseCode, eduMaterialCode);
        if (result1 && result2) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        else {
            String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
            return sendErrorResponse(error);
        }
    }

    public Response removeItem(Request request) {
        String itemCode = (String) request.getData("itemCode");
        String eduMaterialCode = (String) request.getData("educationalMaterialCode");
        boolean result1 = this.dataHandler.removeItem(itemCode);
        if (result1) {
            ArrayList<String> items = this.dataHandler.getItemsList(eduMaterialCode);
            items.remove(itemCode);
            boolean result2 = this.dataHandler.updateItems(items, eduMaterialCode);
            if (result2) {
                Response response = new Response(ResponseStatus.OK);
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
                response.setNotificationMessage(note);
                return response;
            }
        }
        String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return sendErrorResponse(error);
    }

    public Response addTextItem(Request request) {
        String text = (String) request.getData("text");
        String eduMaterialCode = (String) request.getData("educationalMaterialCode");
        ArrayList<String> items = this.dataHandler.getItemsList(eduMaterialCode);
        String itemCode = generateCode(eduMaterialCode, items);
        boolean result1 = this.dataHandler.addItem(itemCode, ItemType.TEXT, text);
        if (result1) {
            items.add(itemCode);
            boolean result2 = this.dataHandler.updateItems(items, eduMaterialCode);
            if (result2) {
                Response response = new Response(ResponseStatus.OK);
                String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
                response.setNotificationMessage(note);
                return response;
            }
        }
        String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return sendErrorResponse(error);
    }

    public Response editTextItem(Request request) {
        String itemCode = (String) request.getData("itemCode");
        String text = (String) request.getData("text");
        String eduMaterialCode = (String) request.getData("educationalMaterialCode");
        boolean result = this.dataHandler.editItem(itemCode, text);
        if (result) {
            Response response = new Response(ResponseStatus.OK);
            String note = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "done");
            response.setNotificationMessage(note);
            return response;
        }
        String error = Config.getConfig(ConfigType.SERVER_MESSAGES).getProperty(String.class, "error");
        return sendErrorResponse(error);
    }

    private String generateCode(String code, ArrayList<String> items) {
        for (int i = 1; i <= items.size() + 1; i++) {
            String itemCode = code + "-" + i;
            if (!items.contains(itemCode)) return itemCode;
        }
        return "";
    }

    private Response sendErrorResponse(String errorMessage) {
        Response response = new Response(ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
