package managerTest;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    String time = "2025.06.14 14:00";
    String time1 = "2025.06.14 14:05";
    String time2 = "2025.06.14 13:50";
    String time3 = "2025.06.14 14:30";

    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    LocalDateTime startTime1 = LocalDateTime.parse(time1, formatter);
    LocalDateTime startTime2 = LocalDateTime.parse(time2, formatter);
    LocalDateTime startTime3 = LocalDateTime.parse(time3, formatter);

    Duration duration = Duration.ofMinutes(15);
    Duration duration1 = Duration.ofMinutes(5);


    Task task = new Task("Сходить в магазин", "Купить продукты", Status.NEW, 5, duration, startTime);
    Task task1 = new Task("Обработать газон", "Необходима фунгицидная обработка", Status.NEW,
            2, duration1, startTime2);
    Task task2 = new Task("Task2", "Description2", Status.NEW, 2);
    Task task3 = new Task("Task3", "Description2", Status.DONE, 2);

    Epic epic = new Epic("Пройти обучение", "Получить новые знания", Status.NEW, 13);
    Epic epic1 = new Epic("Собрать ребёнка в школу", "Составить список необходимых вещей", Status.NEW, 3);

    Subtask subtask = new Subtask("Записаться на курсы", "Необходимо совмещать с работой", Status.NEW,
            5, 1, duration, startTime2);
    Subtask subtask1 = new Subtask("Пройти первый спринт", "Обучение поделено на спринты", Status.NEW,
            6, 1, duration1, startTime1);
    Subtask subtask2 = new Subtask("Name2", "Description2", Status.DONE,
            6, 1, duration, startTime1);
    Subtask subtask3 = new Subtask("Name3", "Description3", Status.IN_PROGRESS,
            6, 1, duration1, startTime3);

    @Test
    void shouldAddNewTask() {
        Task taskNewId = taskManager.createTask(task);
        int taskId = taskNewId.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.fullListTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskNewId, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldAddNewEpicAndItsSubtask() {
        Epic epicNewId = taskManager.createEpic(epic);
        int epicId = epicNewId.getId();

        final Epic savedEpic = taskManager.getEpicById(epicId);
        final ArrayList<Epic> epics = taskManager.fullListEpic();

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпик не совпадают.");
        assertNotNull(epics, "Эпик не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epicNewId, epics.get(0), "Эпики не совпадают.");

        Subtask subtaskNewId = taskManager.createSubtask(subtask);
        int subtaskId = subtaskNewId.getId();

        final Task savedSubtask = taskManager.getSubtaskById(subtaskId);
        final ArrayList<Subtask> subtasks = taskManager.fullListSubtask();

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtaskNewId, subtasks.get(0), "Подзадачи не совпадают.");

        ArrayList<Integer> listSubtaskEpic = epic.getListSubtaskEpic();
        int idSubtaskInEpic = listSubtaskEpic.get(0);
        Subtask subtaskInEpic = taskManager.getSubtaskById(idSubtaskInEpic);
        assertEquals(subtaskInEpic, subtask, "Подзадачи не совпадают.");
    }

    @Test
    void makeSureThatTheEpicObjectCannotBeAddedToItselfAsASubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        int idEpic = epic.getId();
        ArrayList<Integer> listSubtaskEpic = epic.getListSubtaskEpic();
        boolean isСontains = false;
        for (Integer numberId : listSubtaskEpic) {
            if (numberId == idEpic) {
                isСontains = true;
            }
        }
        assertFalse(isСontains, "В список id подзадач попал id эпика!");
    }

    @Test
    void checkingTheSubtaskType() {
        assertInstanceOf(Subtask.class, subtask, "Подзадача не может иметь никакой другой тип!");
    }

    @Test
    void shouldFullDeletedTask() {
        taskManager.createTask(task);
        taskManager.createTask(task1);

        final ArrayList<Task> tasks = taskManager.fullListTask();
        assertEquals(2, tasks.size(), "Количество добавленных задач не совпадает с количеством" +
                " хранящихся задач в TaskMenedger!");
        taskManager.fullDelTask();
        final ArrayList<Task> theListAfterDeletion = taskManager.fullListTask();
        assertTrue(theListAfterDeletion.isEmpty(), "Все задачи должны быть удалены!");
    }

    @Test
    void shouldFullDeletedEpic() {
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask3);

        final ArrayList<Epic> epics = taskManager.fullListEpic();
        final ArrayList<Subtask> subtasks = taskManager.fullListSubtask();

        assertEquals(2, epics.size(), "Количество добавленных Эпических задач не совпадает " +
                "с количеством хранящихся Эпических задач в TaskMenedger!");
        assertEquals(2, subtasks.size(), "Количество добавленных Подзадач не совпадает " +
                "с количеством хранящихся Подзадач в TaskMenedger!");

        boolean isContains = false;

        int idEpicInSubtask = subtask.getIdEpic();
        Epic epicIo = taskManager.getEpicById(idEpicInSubtask);
        ArrayList<Integer> listIdNumberSubtasksInEpic = epicIo.getListSubtaskEpic();
        if (listIdNumberSubtasksInEpic != null && listIdNumberSubtasksInEpic.get(0) == subtask.getId() &&
                listIdNumberSubtasksInEpic.get(1) == subtask3.getId()) {
            isContains = true;
        }
        assertTrue(isContains, "Эпик должен содержать в себе Id номера обеих подзадач");
        taskManager.fullDelEpic();
        final ArrayList<Epic> theListEpicAfterDeletion = taskManager.fullListEpic();
        final ArrayList<Subtask> theListSubtaskAfterDeletion = taskManager.fullListSubtask();
        assertTrue(theListEpicAfterDeletion.isEmpty(), "Все Эпические задачи должны быть удалены!");
        assertTrue(theListSubtaskAfterDeletion.isEmpty(), "Все Подзадачи должны быть удалены!");
    }

    @Test
    void shouldFullDeletedEpicAndTaskInHistoryViewedTask() {

        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask3);

        taskManager.createTask(task2);
        taskManager.createTask(task3);

        int idEpic = epic.getId();
        int idEpic1 = epic1.getId();
        int idSubtask = subtask.getId();
        int idSubtask1 = subtask1.getId();
        int idTask = task2.getId();
        int idTask1 = task3.getId();

        Epic lookEpic = taskManager.getEpicById(idEpic);
        Subtask lookSubtask = taskManager.getSubtaskById(idSubtask);
        assertEquals(2, taskManager.getHistory().size(), "Количество задач в списках просмотренных " +
                "в HistoryManager не совпадает с количеством просмотренных задач в TaskManager!");

        taskManager.deleteById(idSubtask);
        assertEquals(1, taskManager.getHistory().size(), "Количество задач в списках просмотренных " +
                "в HistoryManager после удаления одной задачи(подзадачи) по id не верно");

        Subtask lookSubtask2 = taskManager.getSubtaskById(idSubtask1);
        taskManager.fullDelEpic();
        assertEquals(0, taskManager.getHistory().size(), "Количество эпиков и подзадач в списках " +
                "просмотренных в HistoryManager не совпадает с количеством просмотренных задач в TaskManager!");

        Task lookTask = taskManager.getTaskById(idTask);
        Task lookTask1 = taskManager.getTaskById(idTask1);
        assertEquals(2, taskManager.getHistory().size(), "Количество задач в списках просмотренных в " +
                "HistoryManager не совпадает с количеством просмотренных задач в TaskManager!");
        taskManager.deleteById(idTask);
        List<Task> viewTask = taskManager.getHistory();
        boolean isContains = false;
        for (Task task : viewTask) {
            if (task.equals(viewTask)) {
                isContains = true;
            }
        }
        assertFalse(isContains, "В список просмотренных задач попала задача удалённая из HistoryManager!");
    }

    @Test
    void checkingForOverlappingTasks() {
        taskManager.fullDelTask();
        taskManager.fullDelEpic();
        taskManager.createEpic(epic);
        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.createSubtask(subtask3);

        int quantityInListTask = taskManager.fullListTask().size();
        int quantityInListSubtask = taskManager.fullListSubtask().size();
        int quantityInPrioritizedTasks = taskManager.getPrioritizedTasks().size();
        assertEquals(3, quantityInListTask + quantityInListSubtask,
                "Количество задач в listTask и listSubtask, должнобыть равно 2");
        assertEquals(3, quantityInPrioritizedTasks, "Количество задач в PrioritizedTasks, " +
                "должнобыть равно 3");
    }

    @Test
    void checkingIntersectionBetweenEndPointTaskAndBeginningNextOne() {
        taskManager.fullDelTask();
        taskManager.fullDelEpic();
        taskManager.createEpic(epic);
        taskManager.createTask(task);
        taskManager.createSubtask(subtask);

        int quantityInListTask = taskManager.fullListTask().size();
        int quantityInListSubtask = taskManager.fullListSubtask().size();
        int quantityInPrioritizedTasks = taskManager.getPrioritizedTasks().size();
        assertEquals(1, quantityInListTask + quantityInListSubtask,
                "Количество задач в listTask и listSubtask, должнобыть равно 1");
        assertEquals(1, quantityInPrioritizedTasks, "Количество задач в PrioritizedTasks, " +
                "должнобыть равно 1");
    }

    @Test
    void checkingIntersectionPointOfStartAndEndOfTaskDuringExecutionWithAnExistingTask() {
        taskManager.fullDelTask();
        taskManager.fullDelEpic();
        taskManager.createEpic(epic);
        taskManager.createTask(task);
        taskManager.createSubtask(subtask1);

        int quantityInListTask = taskManager.fullListTask().size();
        int quantityInListSubtask = taskManager.fullListSubtask().size();
        int quantityInPrioritizedTasks = taskManager.getPrioritizedTasks().size();
        assertEquals(1, quantityInListTask + quantityInListSubtask,
                "Количество задач в listTask и listSubtask, должнобыть равно 1");
        assertEquals(1, quantityInPrioritizedTasks, "Количество задач в PrioritizedTasks, " +
                "должнобыть равно 1");
    }

    @Test
    void CheckingIntersectionBetweenStartOfTaskAndEndPointTheExisting() {
        taskManager.fullDelTask();
        taskManager.fullDelEpic();
        taskManager.createEpic(epic);
        taskManager.createTask(task);
        taskManager.createSubtask(subtask2);

        int quantityInListTask = taskManager.fullListTask().size();
        int quantityInListSubtask = taskManager.fullListSubtask().size();
        int quantityInPrioritizedTasks = taskManager.getPrioritizedTasks().size();
        assertEquals(1, quantityInListTask + quantityInListSubtask,
                "Количество задач в listTask и listSubtask, должнобыть равно 1");
        assertEquals(1, quantityInPrioritizedTasks, "Количество задач в PrioritizedTasks, " +
                "должнобыть равно 1");
    }

    @Test
    void CheckingForAnIntersectionWhenNewTaskAbsorbsAnExistingOne() {
        taskManager.fullDelTask();
        taskManager.fullDelEpic();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createTask(task);

        int quantityInListTask = taskManager.fullListTask().size();
        int quantityInListSubtask = taskManager.fullListSubtask().size();
        int quantityInPrioritizedTasks = taskManager.getPrioritizedTasks().size();
        assertEquals(1, quantityInListTask + quantityInListSubtask,
                "Количество задач в listTask и listSubtask, должнобыть равно 1");
        assertEquals(1, quantityInPrioritizedTasks, "Количество задач в PrioritizedTasks, " +
                "должнобыть равно 1");
    }

    @Test
    void shouldUpdatingTimeParametersEpic() {
        taskManager.fullDelEpic();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask3);

        int quantitySubtask = taskManager.fullListSubtask().size();
        assertEquals(2, quantitySubtask, "Количество подзадач в listSubtask, должнобыть равно 3");
        Epic epicUpdate = taskManager.getEpicById(1);
        LocalDateTime startTime = epicUpdate.getStartTime();
        LocalDateTime endTime = epicUpdate.getEndTime();
        Long minutes = epicUpdate.getDuration().toMinutes();
        String start = startTime.format(formatter);
        String end = endTime.format(formatter);
        assertEquals(20, minutes, "Продолжительность Epic, должнобыть равно 30 минут");
        assertEquals("2025.06.14 13:50", start, "Время старта Epic, должнобыть равно 2025.05.08 15:05");
        assertEquals("2025.06.14 14:35", end, "Время окончания Epic, должнобыть равно 2025.05.08 15:45");
    }
}
