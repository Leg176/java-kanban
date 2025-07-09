package apiTest.handlerTest;

import api.HttpTaskServer;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TasksHandlerTest {

    static TaskManager taskManager = Managers.getDefault();
    static HttpTaskServer server = new HttpTaskServer(taskManager);
    static HttpClient httpClient = HttpClient.newHttpClient();
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    String baseUrl = "http://localhost:8080";
    String time = "2025.06.14 14:00";
    String time1 = "2025.06.14 15:00";
    String time2 = "2025.06.14 14:15";
    String time3 = "2025.06.14 16:15";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    LocalDateTime startTime1 = LocalDateTime.parse(time1, formatter);
    LocalDateTime startTime2 = LocalDateTime.parse(time2, formatter);
    LocalDateTime startTime3 = LocalDateTime.parse(time3, formatter);
    Duration duration = Duration.ofMinutes(15);

    Task task = new Task("name", "description",
            Status.NEW, 1, duration, startTime);
    Task task1 = new Task("name1", "description1",
            Status.NEW, 0, duration, startTime1);
    Task task2 = new Task("name2", "description2",
            Status.NEW, 3, duration, startTime);
    Task task3 = new Task("name3", "description3",
            Status.NEW, 0, duration, startTime2);
    Task task4 = new Task("name4", "description4",
            Status.DONE, 0, duration, startTime3);

    @BeforeAll
    static void start() {
        server.startServer();
        taskManager.fullDelEpic();
        taskManager.fullDelTask();
    }

    @AfterAll
    public static void stop() {
        server.stopServer();
    }

    @Test
    @Order(1)
    public void checkingTheAdditionTasks() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task1);
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была " +
                "добавлена задача, в Task указан id = 0");
        assertEquals(1, taskManager.fullListTask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(2)
    public void checkingForIssueUpdates() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task);
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Cтатус ответа 201, задача " +
                "была обновлена, в Task указан id = 1.");
        assertEquals(1, taskManager.fullListTask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(3)
    public void checkingForUpdatesToNonExistingTask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task2);
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Cтатус ответа 404, задача " +
                "добавлена не была, в Task указан id > 0, но обновлять не чего");
        assertEquals(1, taskManager.fullListTask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(4)
    public void checkingForAddingAnIntersectingTask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task3);
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Cтатус ответа 406, добавляемая задача " +
                "пересекается по времени с другими задачами.");
        assertEquals(1, taskManager.fullListTask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(5)
    public void checkingGetIdTask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task4);
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была " +
                "добавлена задача, в Task указан id = 0");
        assertEquals(2, taskManager.fullListTask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        URI uri1 = new URI(baseUrl + "/tasks/2");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri1)
                .GET()
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "Cтатус ответа 200, " +
                "задача успешно выведена в теле ответа");
        assertEquals(2, taskManager.fullListTask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
        JsonElement jsonElement = JsonParser.parseString(response1.body());
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            assertEquals("name4", name, "Имена задач должны совпадать");
            assertEquals("description4", description, "Описание задач должны совпадать");
        } else {
            assertInstanceOf(JsonArray.class, jsonElement, "Элемент является массивом");
        }
    }

    @Test
    @Order(6)
    public void checkingDeleteTask() throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(baseUrl + "/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Cтатус ответа 200, задача с id = 1 была " +
                "успешно удалена");
        assertEquals(1, taskManager.fullListTask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
        String nameTask = taskManager.fullListTask().stream()
                .map(task -> task.getName())
                .findFirst()
                .orElse(null);

        assertEquals("name4", nameTask, "Удалена не верная задача");
    }
}


