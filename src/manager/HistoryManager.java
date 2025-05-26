package manager;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void remove(int id);

    <T extends Task> void addTask(Task task);

    List<Task> getTasks();
}
