package manager;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    <T extends Task> void addTask(Task task);

    ArrayList<Task> getHistory();
}
