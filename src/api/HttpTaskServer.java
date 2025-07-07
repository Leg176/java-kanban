package api;

import api.handler.*;
import api.adapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .create();
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

            httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        } catch (IOException e) {
            System.err.println("Ошибка при создании сервера: " + e.getMessage());
        }
    }

    private void startServer() {
        httpServer.start();
    }

    private void stopServer() {
        httpServer.stop(30);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.startServer();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    //    httpTaskServer.stopServer();
    }
}
