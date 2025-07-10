package api.handler;

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

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetSubtasks(httpExchange);
                    } else {
                        handleGetSubtaskId(httpExchange);
                    }
                    break;
                case ("POST"):
                    handleCreateSubtask(httpExchange);
                    break;
                case ("DELETE"):
                    handleDeleteSubtask(httpExchange);
                    break;
                default:
                    String response = "Internal Server Error";
                    sendInternalServerError(httpExchange, response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента на вывод подзадач.");

        ArrayList<Subtask> subtasks = taskManager.fullListSubtask();
        sendText(httpExchange, gson.toJson(subtasks));
    }

    public void handleGetSubtaskId(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtask/{id} запроса от клиента на вывод подзадачи по её id.");

        int id = searchIdTask(httpExchange);
        Subtask subtask = taskManager.getSubtaskById(id);

        if (subtask == null) {
            sendNotFound(httpExchange, "Not Found");
        } else {
            String response = gson.toJson(subtask);
            sendText(httpExchange, response);
        }
    }

    public void handleCreateSubtask(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента на создание подзадачи.");

        InputStream inputStream = httpExchange.getRequestBody();
        String subtaskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(subtaskString, Subtask.class);
        int idSubtask = subtask.getId();
        int idSubtaskInEpic = subtask.getIdEpic();

        boolean isContainsIdEpic = taskManager.fullListEpic().stream()
                .map(Epic::getId)
                .anyMatch(i -> i == idSubtaskInEpic);

        if (idSubtask == 0 && isContainsIdEpic) {
            if (taskManager.createSubtask(subtask) != null) {
                sendCreated(httpExchange, gson.toJson(subtask));
            } else {
                sendHasInteractions(httpExchange, "Not Acceptable");
            }
        } else if (isContainsIdEpic) {
            boolean isContains = taskManager.fullListSubtask().stream()
                    .map(Subtask::getId)
                    .anyMatch(i -> i == idSubtask);
            if (!isContains) {
                sendNotFound(httpExchange, "Подзадача с таким id в списках отсутствует");
            } else {
                taskManager.updateSubTask(subtask);
                String response = "Подзадача успешно обновлена";
                sendCreated(httpExchange, gson.toJson(response));
            }
        } else {
            sendNotFound(httpExchange, "Эпическая задача с id указанном в подзадаче отсутствует");
        }
    }

    public void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtask/{id} запроса от клиента на удаление подзадачи.");

        int id = searchIdTask(httpExchange);
        taskManager.deleteById(id);
        sendText(httpExchange, "Subtask deleted");
    }
}
