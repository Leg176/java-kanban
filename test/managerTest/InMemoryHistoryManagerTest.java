package managerTest;

import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {

    Task task = new Task("Пробежать 3 км", "Физическая активность необходима", Status.NEW, 1);
    Task task1 = new Task("Написать тесты", "Тесты должны проверять основные" +
            " функциональные возможности", Status.NEW, 1);
    InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault();

    @Test
    void checkingHowTheMethodForAddingTasksToTheViewedListWorks() {
        historyManager.addTask(task);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история " +
                "не должна быть пустой.");
        for (int i = 0; i < 11; i++) {
            historyManager.addTask(task1);
        }
        final ArrayList<Task> historyNew = historyManager.getHistory();
        assertEquals(10, historyNew.size(), "Количество задач в памяти " +
                "не должно превышать 10");
    }

    @Test
    void checkingTheImmutabilityOfClassObjectWhenAddedToTheViewedList() {
        taskManager.createTask(task);
        taskManager.getTaskById(1);
        taskManager.updateTask(task1);

        final ArrayList<Task> history = taskManager.getHistory();
        System.out.println(history.size());
        Task task2 = history.get(0);
        assertEquals(task.getName(), task2.getName(), "Имена задач должны совпадать");
        assertEquals(task.getDescription(), task2.getDescription(), "Описание задач " +
                "должны совпадать");
        assertEquals(task.getStatus(), task2.getStatus(), "Статус должен совпадать");
        assertEquals(task.getId(), task2.getId(), "Id должен совпадать");
    }
}
