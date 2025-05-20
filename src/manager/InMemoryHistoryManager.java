package manager;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;
    private ArrayList<Task> listViewedTask = new ArrayList<>(MAX_HISTORY_SIZE);

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(listViewedTask);
    }

    @Override
    public <T extends Task> void addTask(Task task) {
        if (task == null) {
            return;
        }
        Task copyTask = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getId());
        if (listViewedTask.size() == MAX_HISTORY_SIZE) {
            listViewedTask.remove(0);
        }
        listViewedTask.add(copyTask);
    }
}
