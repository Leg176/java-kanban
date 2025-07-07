package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.TreeSet;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("Началась обработка /prioritized запроса от клиента на вывод списка отсортированных задач.");

            TreeSet<? extends Task> prioritized = taskManager.getPrioritizedTasks();
            String response = gson.toJson(prioritized);
            sendText(httpExchange, response);

        } catch (IOException e) {
            sendInternalServerError(httpExchange, "Internal Server Error");
        }
    }
}
