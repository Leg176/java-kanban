package api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static int PORT = 8080;

    public static void main(String[] args) throws IOException{

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        // связываем сервер с сетевым портом
        httpServer.createContext("/tasks", new BaseHttpHandler.TasksHandler());
        httpServer.createContext("/subtasks", new BaseHttpHandler.SubtasksHandler());
        httpServer.createContext("/epics", new BaseHttpHandler.EpicsHandler());
        httpServer.createContext("/history", new BaseHttpHandler.HistoryHandler());
        httpServer.createContext("/prioritized", new BaseHttpHandler.PrioritizedHandler());
        httpServer.start(); // запускаем сервер

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}
