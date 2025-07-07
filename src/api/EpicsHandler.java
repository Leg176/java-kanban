package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler (TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String method = httpExchange.getRequestMethod();
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitString = path.split("/");
        try {
            switch (method) {
                case ("GET"):
                    if (splitString.length == 2) {
                        handleGetEpics(httpExchange);
                    } else if (splitString.length == 3) {
                        handleGetEpicId(httpExchange);
                    } else {
                        handleGetEpicSubtasks(httpExchange);
                    }
                    break;
                case ("POST"):
                    handleCreateEpic(httpExchange);
                    break;
                case ("DELETE"):
                    handleDeleteEpic(httpExchange);
                    break;
                default:
                    String response = "Internal Server Error";
                    sendInternalServerError(httpExchange, response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleGetEpics(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента на вывод эпических задач.");

        ArrayList<Epic> epics = taskManager.fullListEpic();
        String response = gson.toJson(epics);
        sendText(httpExchange, response);
    }

    public void handleGetEpicId(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics/{id} запроса от клиента на вывод эпической задачи по её id.");


        int id = searchIdTask(httpExchange);
        Epic epic = taskManager.getEpicById(id);
        String response = "";

        if (epic == null) {
            response = "Not Found";
            sendNotFound(httpExchange, response);
        } else {
            response = gson.toJson(epic);
            sendText(httpExchange, response);
        }
    }

    public void handleGetEpicSubtasks(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics/{id}/subtasks запроса от клиента на вывод подзадач " +
                "входящих в эпическую задачу по id эпической задачи.");

        int id = searchIdTask(httpExchange);
        Epic epic = taskManager.getEpicById(id);
        String response = "";

        if (epic == null) {
            response = "Not Found";
            sendNotFound(httpExchange, response);
        } else {
            ArrayList<Subtask> subtask = taskManager.getListSubtask(id);
            response = subtask.toString();
            sendText(httpExchange, gson.toJson(response));
        }
    }

    public void handleCreateEpic(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента на создание эпической задачи.");

        InputStream inputStream = httpExchange.getRequestBody();
        String epicString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(epicString, Epic.class);
        int idEpic = epic.getId();

        if (idEpic == 0) {
            if (taskManager.createEpic(epic) != null) {
                sendCreated(httpExchange, gson.toJson(epic));
            } else {
                String response = "Not Acceptable";
                sendHasInteractions(httpExchange, response);
            }
        } else {
            boolean isContains = taskManager.fullListEpic().stream()
                    .map(Epic::getId)
                    .anyMatch(i -> i == idEpic);
            if (!isContains) {
                sendNotFound(httpExchange, "Эпическая задача с таким id в списках отсутствует");
            } else {
                taskManager.updateEpic(epic);
                sendCreated(httpExchange, gson.toJson("Эпическая задача успешно обновлена"));
            }
        }
    }

    public void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics/{id} запроса от клиента на удаление эпической задачи.");

        int id = searchIdTask(httpExchange);
        taskManager.deleteById(id);
        sendText(httpExchange, "Epic deleted");
    }
}
