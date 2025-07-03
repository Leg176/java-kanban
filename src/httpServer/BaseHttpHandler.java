package httpServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.CSVConverter;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class BaseHttpHandler {

    static TaskManager taskManager = Managers.getDefault();

    static class TasksHandler extends BaseHttpHandler implements HttpHandler {

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
            String response = tasks.toString();
            sendText(httpExchange, response);
        }

        public void handleGetTaskId(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /task/{id} запроса от клиента на вывод задачи по её id.");

            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            String[] splitString = path.split("/");
            int id = Integer.parseInt(splitString[2]);
            Task task = taskManager.getTaskById(id);
            String response = "";

            if (task == null) {
                response = "Not Found";
                sendNotFound(httpExchange, response);
            } else {
                response = task.toString();
                sendText(httpExchange, response);
            }
        }

        public void handleCreateTask(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /tasks запроса от клиента на создание задачи.");

            InputStream inputStream = httpExchange.getRequestBody();
            String taskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task task = CSVConverter.fromString(taskString);

            if (taskManager.createTask(task) == null) {
                String response = "Not Acceptable";
                sendHasInteractions(httpExchange, response);
                return;
            }

            if (task.getId() == 0) {
                taskManager.createTask(task);
                httpExchange.sendResponseHeaders(201, 0);
            } else {
                taskManager.updateTask(task);
                httpExchange.sendResponseHeaders(201, 0);
            }
        }

        public void handleDeleteTask(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /task/{id} запроса от клиента на удаление задачи.");
            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            String[] splitString = path.split("/");
            int id = Integer.parseInt(splitString[2]);
            taskManager.deleteById(id);
            sendText(httpExchange, "Task deleted");
        }
    }

    static class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

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
            String response = subtasks.toString();
            sendText(httpExchange, response);
        }

        public void handleGetSubtaskId(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /subtask/{id} запроса от клиента на вывод подзадачи по её id.");

            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            String[] splitString = path.split("/");
            int id = Integer.parseInt(splitString[2]);
            Subtask subtask = taskManager.getSubtaskById(id);
            String response = "";

            if (subtask == null) {
                response = "Not Found";
                sendNotFound(httpExchange, response);
            } else {
                response = subtask.toString();
                sendText(httpExchange, response);
            }
        }

        public void handleCreateSubtask(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /subtasks запроса от клиента на создание подзадачи.");

            InputStream inputStream = httpExchange.getRequestBody();
            String taskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = (Subtask) CSVConverter.fromString(taskString);

            if (taskManager.createSubtask(subtask) == null) {
                String response = "Not Acceptable";
                sendHasInteractions(httpExchange, response);
                return;
            }

            if (subtask.getId() == 0) {
                taskManager.createSubtask(subtask);
                httpExchange.sendResponseHeaders(201, 0);
            } else {
                taskManager.updateSubTask(subtask);
                httpExchange.sendResponseHeaders(201, 0);
            }
        }

        public void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /subtask/{id} запроса от клиента на удаление подзадачи.");
            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            String[] splitString = path.split("/");
            int id = Integer.parseInt(splitString[2]);
            taskManager.deleteById(id);
            sendText(httpExchange, "Subtask deleted");
        }
    }

    static class EpicsHandler extends BaseHttpHandler implements HttpHandler {
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
            String response = epics.toString();
            sendText(httpExchange, response);
        }

        public void handleGetEpicId(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /epics/{id} запроса от клиента на вывод эпической задачи по её id.");

            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            String[] splitString = path.split("/");
            int id = Integer.parseInt(splitString[2]);
            Epic epic = taskManager.getEpicById(id);
            String response = "";

            if (epic == null) {
                response = "Not Found";
                sendNotFound(httpExchange, response);
            } else {
                response = epic.toString();
                sendText(httpExchange, response);
            }
        }

        public void handleGetEpicSubtasks(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /epics/{id}/subtasks запроса от клиента на вывод подзадач " +
                    "входящих в эпическую задачу по id эпической задачи.");

            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            String[] splitString = path.split("/");
            int id = Integer.parseInt(splitString[2]);
            Epic epic = taskManager.getEpicById(id);
            String response = "";

            if (epic == null) {
                response = "Not Found";
                sendNotFound(httpExchange, response);
            } else {
                ArrayList<Subtask> subtask = taskManager.getListSubtask(id);
                response = subtask.toString();
                sendText(httpExchange, response);
            }
        }

        public void handleCreateEpic(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /epics запроса от клиента на создание эпической задачи.");

            InputStream inputStream = httpExchange.getRequestBody();
            String taskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = (Epic) CSVConverter.fromString(taskString);

            if (taskManager.createEpic(epic) == null) {
                String response = "Not Acceptable";
                sendHasInteractions(httpExchange, response);
                return;
            }

            if (epic.getId() == 0) {
                taskManager.createEpic(epic);
                httpExchange.sendResponseHeaders(201, 0);
            } else {
                taskManager.updateEpic(epic);
                httpExchange.sendResponseHeaders(201, 0);
            }
        }

        public void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /epics/{id} запроса от клиента на удаление эпической задачи.");

            URI uri = httpExchange.getRequestURI();
            String path = uri.getPath();
            String[] splitString = path.split("/");
            int id = Integer.parseInt(splitString[2]);
            taskManager.deleteById(id);
            sendText(httpExchange, "Epic deleted");
        }
    }

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "";
            try {
                System.out.println("Началась обработка /history запроса от клиента на вывод истории просмотров.");

                List<? extends Task> tasks = taskManager.getHistory();
                response = tasks.toString();
                sendText(httpExchange, response);

            } catch (IOException e) {
                response = "Internal Server Error";
                sendInternalServerError(httpExchange, response);
            }
        }
    }

    static class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "";
            try {
                System.out.println("Началась обработка /prioritized запроса от клиента на вывод списка отсортированных задач.");

                TreeSet<? extends Task> prioritized = taskManager.getPrioritizedTasks();
                response = prioritized.toString();
                sendText(httpExchange, response);

            } catch (IOException e) {
                response = "Internal Server Error";
                sendInternalServerError(httpExchange, response);
            }
        }
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendInternalServerError(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}