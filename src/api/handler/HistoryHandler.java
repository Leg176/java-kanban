package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("Началась обработка /history запроса от клиента на вывод истории просмотров.");

            List<? extends Task> tasks = taskManager.getHistory();
            String response = gson.toJson(tasks);
            sendText(httpExchange, response);

        } catch (IOException e) {
            sendInternalServerError(httpExchange, "Internal Server Error");
        }
    }
}
