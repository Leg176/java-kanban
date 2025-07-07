package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetTasks(httpExchange);
                    } else {
                        handleGetTaskId(httpExchange);
                    }
                    break;
                case ("POST"):
                    handleCreateTask(httpExchange);
                    break;
                case ("DELETE"):
                    handleDeleteTask(httpExchange);
                    break;
                default:
                    String response = "Internal Server Error";
                    sendInternalServerError(httpExchange, response);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleGetTasks(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента на вывод задач.");

        ArrayList<Task> tasks = taskManager.fullListTask();
        sendText(httpExchange, gson.toJson(tasks));
    }

    public void handleGetTaskId(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /task/{id} запроса от клиента на вывод задачи по её id.");

        int id = searchIdTask(httpExchange);
        Task task = taskManager.getTaskById(id);
        String response = "";

        if (task == null) {
            response = "Not Found";
            sendNotFound(httpExchange, response);
        } else {
            response = gson.toJson(task);
            sendText(httpExchange, response);
        }
    }

    public void handleCreateTask(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента на создание задачи.");

        InputStream inputStream = httpExchange.getRequestBody();
        String taskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(taskString, Task.class);
        int idTask = task.getId();

        if (idTask == 0) {
            if (taskManager.createTask(task) != null) {
                sendCreated(httpExchange, gson.toJson(task));
            } else {
                String response = "Not Acceptable";
                sendHasInteractions(httpExchange, response);
            }
        } else {
            boolean isContains = taskManager.fullListTask().stream()
                    .map(Task::getId)
                    .anyMatch(i -> i == idTask);
            if (!isContains) {
                sendNotFound(httpExchange, "Задачи с таким id в списках отсутствует");
            } else {
                taskManager.updateTask(task);
                sendCreated(httpExchange, gson.toJson("Задача успешно обновлена"));
            }
        }
    }

    public void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /task/{id} запроса от клиента на удаление задачи.");

        int id = searchIdTask(httpExchange);
        taskManager.deleteById(id);
        sendText(httpExchange, "Task deleted");
    }
}
