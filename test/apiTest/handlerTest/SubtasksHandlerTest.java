package apiTest.handlerTest;

import api.HttpTaskServer;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
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
public class SubtasksHandlerTest {

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
    String time4 = "2025.01.14 16:15";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    LocalDateTime startTime1 = LocalDateTime.parse(time1, formatter);
    LocalDateTime startTime2 = LocalDateTime.parse(time2, formatter);
    LocalDateTime startTime3 = LocalDateTime.parse(time3, formatter);
    LocalDateTime startTime4 = LocalDateTime.parse(time4, formatter);
    Duration duration = Duration.ofMinutes(15);

    Subtask subtask = new Subtask("name", "description",
            Status.NEW, 2, 1, duration, startTime);
    Subtask subtask1 = new Subtask("name1", "description1",
            Status.NEW, 0, 1, duration, startTime1);
    Subtask subtask2 = new Subtask("name2", "description2",
            Status.NEW, 3, 1, duration, startTime);
    Subtask subtask3 = new Subtask("name3", "description3",
            Status.NEW, 0, 1, duration, startTime2);
    Subtask subtask4 = new Subtask("name4", "description4",
            Status.DONE, 0, 1, duration, startTime3);
    Subtask subtask5 = new Subtask("name5", "description5",
            Status.DONE, 0, 4, duration, startTime4);
    Epic epic = new Epic("nameEpic", "descriptionEpic",
            Status.NEW, 0);

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
    public void checkingTheAdditionSubtasks() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(epic);
        URI uri = new URI(baseUrl + "/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была " +
                "добавлена эпическая задача, в Epic указан id = 0");
        String requestBody1 = gson.toJson(subtask1);
        URI uri1 = new URI(baseUrl + "/subtasks/");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri1)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody1))
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Cтатус ответа 201, была " +
                "добавлена подзадача, в Subtask указан id = 0");
        assertEquals(1, taskManager.fullListEpic().size());
        assertEquals(1, taskManager.fullListSubtask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(2)
    public void checkingForIssueUpdates() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response1 = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Cтатус ответа 201, подзадача " +
                "была обновлена, в Subtask указан id = 2.");
        assertEquals(1, taskManager.fullListSubtask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(3)
    public void checkingForUpdatesToNonExistingSubtask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask2);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Cтатус ответа 404, подзадача " +
                "добавлена не была, в Subtask указан id > 0, но обновлять не чего");
        assertEquals(1, taskManager.fullListSubtask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(4)
    public void checkingForAddingAnIntersectingSubtask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask3);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Cтатус ответа 406, добавляемая подзадача " +
                "пересекается по времени с другими задачами.");
        assertEquals(1, taskManager.fullListSubtask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(5)
    public void checkingForAddingSubtaskWithNonExistentIdEpic() throws IOException,
            InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask5);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Cтатус ответа 404, idEpic в добавляемой подзадаче " +
                "не соответствует id эпической задачи.");
        assertEquals(1, taskManager.fullListSubtask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(6)
    public void checkingGetIdSubtask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask4);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была " +
                "добавлена подзадача, в Subtask указан id = 0");
        assertEquals(2, taskManager.fullListSubtask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());

        URI uri1 = new URI(baseUrl + "/subtasks/3");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri1)
                .GET()
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "Cтатус ответа 200, " +
                "подзадача успешно выведена в теле ответа");
        assertEquals(2, taskManager.fullListSubtask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
        JsonElement jsonElement = JsonParser.parseString(response1.body());
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            assertEquals("name4", name, "Имя подзадачи должно совпадать");
            assertEquals("description4", description, "Описание подзадачи должно совпадать");
        } else {
            assertInstanceOf(JsonArray.class, jsonElement, "Элемент является массивом");
        }
    }

    @Test
    @Order(7)
    public void checkingDeleteTask() throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(baseUrl + "/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Cтатус ответа 200, подзадача с id = 2 была " +
                "успешно удалена");
        assertEquals(1, taskManager.fullListSubtask().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
        String nameSubtask = taskManager.fullListSubtask().stream()
                .map(Task::getName)
                .findFirst()
                .orElse(null);

        assertEquals("name4", nameSubtask, "Удалена не верная задача");
    }
}
