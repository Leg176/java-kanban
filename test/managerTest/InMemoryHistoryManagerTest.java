package managerTest;

import jdk.jfr.Description;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    String time = "2025.06.14 14:00";
    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    Duration duration = Duration.ofMinutes(15);
    Task task = new Task("Пробежать 3 км", "Физическая активность необходима", Status.NEW, 1, duration, startTime);
    Task task1 = new Task("Написать тесты", "Тесты должны проверять основные" +
            " функциональные возможности", Status.NEW, 1);
    Task task3 = new Task("Сдать ФЗ", "Реализовать LinkedHashMap", Status.NEW, 3);
    InMemoryHistoryManager historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault();

    @Test
    void checkingHowTheMethodForAddingTasksToTheViewedListWorks() {
        historyManager.addTask(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история " +
                "не должна быть пустой.");
        historyManager.addTask(task1);
        historyManager.addTask(task);
        historyManager.addTask(task3);
        historyManager.addTask(task1);
        final List<Task> historyNew = historyManager.getHistory();
        assertEquals(2, historyNew.size(), "Количество задач в памяти " +
                "не должно превышать 3");
    }

    @Test
    void checkingTheImmutabilityOfClassObjectWhenAddedToTheViewedList() {
        taskManager.createTask(task);
        taskManager.getTaskById(1);
        taskManager.updateTask(task1);
        final List<Task> history = taskManager.getHistory();
        System.out.println(history.size());
        Task task2 = history.get(0);
        assertEquals(task.getName(), task2.getName(), "Имена задач должны совпадать");
        assertEquals(task.getDescription(), task2.getDescription(), "Описание задач " +
                "должны совпадать");
        assertEquals(task.getStatus(), task2.getStatus(), "Статус должен совпадать");
        assertEquals(task.getId(), task2.getId(), "Id должен совпадать");
    }

    @Test
    void checkingHowTheMethodRemoveTasksToTheViewedListWorks() {
        historyManager.addTask(task);
        historyManager.addTask(task3);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(2, history.size(), "Количество задач в памяти не должно превышать 2");
        historyManager.remove(1);
        final List<Task> historyNew = historyManager.getHistory();
        assertEquals(1, historyNew.size(), "Количество задач в памяти " +
                "не должно превышать 1");
    }
}
