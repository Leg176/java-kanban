package apiTest.handlerTest;

import api.HttpTaskServer;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EpicsHandlerTest {

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

    Epic epic = new Epic("name", "description",
            Status.NEW, 1);
    Epic epic1 = new Epic("name1", "description1",
            Status.NEW, 0);
    Epic epic2 = new Epic("name2", "description2",
            Status.NEW, 3);

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
    public void checkingTheAdditionEpics() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(epic1);
        URI uri = new URI(baseUrl + "/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была " +
                "добавлена эпическая задача, в Epic указан id = 0");
        assertEquals(1, taskManager.fullListEpic().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(2)
    public void checkingForIssueUpdates() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(epic);
        URI uri = new URI(baseUrl + "/epics/");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Cтатус ответа 201, эпическая задача " +
                "была обновлена, в Epic указан id = 1.");
        assertEquals(1, taskManager.fullListEpic().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(3)
    public void checkingForUpdatesToNonExistingEpics() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(epic2);
        URI uri = new URI(baseUrl + "/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Cтатус ответа 404, эпическая задача " +
                "добавлена не была, в Epic указан id > 0, но обновлять не чего");
        assertEquals(1, taskManager.fullListEpic().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    @Order(4)
    public void checkingGetIdEpic() throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(baseUrl + "/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Cтатус ответа 200, " +
                "эпическая задача успешно выведена в теле ответа");
        assertEquals(1, taskManager.fullListEpic().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            assertEquals("name", name, "Имея эпической задачи должно совпадать");
            assertEquals("description", description, "Описание эпической задачи должно совпадать");
        } else {
            assertInstanceOf(JsonArray.class, jsonElement, "Элемент является массивом");
        }
    }

    @Test
    @Order(5)
    public void checkingDeleteEpic() throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(baseUrl + "/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Cтатус ответа 200, эпическая задача с id = 1 была " +
                "успешно удалена");
        assertEquals(0, taskManager.fullListEpic().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        assertEquals(0, taskManager.getHistory().size());
    }
}
