package apiTest;

import api.HttpTaskServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerConfigTest {

    static TaskManager taskManager = Managers.getDefault();
    static HttpTaskServer server = new HttpTaskServer(taskManager);
    static HttpClient httpClient = HttpClient.newHttpClient();

    String baseUrl = "http://localhost:8080";

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
    void shouldObjectOfInMemoryTaskManagerClassMustBeCreated() {
        assertInstanceOf(HttpTaskServer.class, server, "Созданный объект не является" +
                " объектом класса HttpTaskServer");
    }

    @Test
    void checkHandlerTasks() throws URISyntaxException, IOException, InterruptedException {
        // Тестирование получения всех задач
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Cтатус ответа должен быть равен 200");
            String body = response.body();
            assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }

    @Test
    void checkHandlerSubtasks() throws URISyntaxException, IOException, InterruptedException {
        // Тестирование получения всех задач
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Cтатус ответа должен быть равен 200");
            String body = response.body();
            assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }

    @Test
    void checkHandlerEpics() throws URISyntaxException, IOException, InterruptedException {
        // Тестирование получения всех задач
        URI uri = new URI(baseUrl + "/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Cтатус ответа должен быть равен 200");
            String body = response.body();
            assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }

    @Test
    void checkHandlerHistory() throws URISyntaxException, IOException, InterruptedException {
        // Тестирование получения всех задач
        URI uri = new URI(baseUrl + "/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Cтатус ответа должен быть равен 200");
        String body = response.body();
        assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }

    @Test
    void checkHandlerPrioritized() throws URISyntaxException, IOException, InterruptedException {
        // Тестирование получения всех задач
        URI uri = new URI(baseUrl + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Cтатус ответа должен быть равен 200");
            String body = response.body();
            assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }
}
