package managerTest;

import manager.FileBackedTaskManager;
import manager.Managers;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    FileBackedTaskManager fileBackedTaskManager;

    Task task = new Task("Сходить в магазин", "Купить продукты", Status.NEW, 5);
    Task task1 = new Task("Обработать газон", "Необходима фунгицидная обработка", Status.NEW, 2);

    Epic epic = new Epic("Пройти обучение", "Получить новые знания", Status.NEW, 13);
    Epic epic1 = new Epic("Собрать ребёнка в школу", "Составить список необходимых вещей", Status.NEW, 3);

    Subtask subtask = new Subtask("Записаться на курсы", "Необходимо совмещать с работой", Status.NEW, 5, 3);
    Subtask subtask1 = new Subtask("Пройти первый спринт", "Обучение поделено на спринты", Status.NEW, 6, 1);

    @BeforeEach
    void createManager() {
        fileBackedTaskManager = (FileBackedTaskManager) Managers.getDefault();
    }

    @Test
    void checkingTheSavingOfTasksToFile() {
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);
        ArrayList<Task> listTask = fileBackedTaskManager.fullListTask();
        ArrayList<Epic> listEpic = fileBackedTaskManager.fullListEpic();
        ArrayList<Subtask> listSubtask = fileBackedTaskManager.fullListSubtask();

        assertEquals(2, listTask.size(), "Количество добавленных задач должно быть равно 2-ум.");
        assertEquals(1, listEpic.size(), "Количество добавленных эпических задач должно быть равно 1-ой.");
        assertEquals(1, listSubtask.size(), "Количество добавленных подзадач должно быть равно 1-ой.");

        Path source = Paths.get("resources/TaskBacked.csv");
        Path destination = Paths.get("test/exceptionsForTest/TaskBackedCopy.csv");

        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileBackedTaskManager.fullDelTask();
        fileBackedTaskManager.fullDelEpic();

        assertTrue(fileBackedTaskManager.fullListTask().isEmpty(), "После удаления задач не должно быть.");
        assertTrue(fileBackedTaskManager.fullListEpic().isEmpty(), "После удаления эпических задач не должно быть.");
        assertTrue(fileBackedTaskManager.fullListSubtask().isEmpty(), "После удаления подзадач не должно быть.");

        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("test/exceptionsForTest/TaskBackedCopy.csv"));

        ArrayList<Task> listTask1 = fileBackedTaskManager.fullListTask();
        ArrayList<Epic> listEpic1 = fileBackedTaskManager.fullListEpic();
        ArrayList<Subtask> listSubtask1 = fileBackedTaskManager.fullListSubtask();

        assertEquals(2, listTask1.size(), "Количество восстановленных задач должно быть равно 2-ум.");
        assertEquals(1, listEpic1.size(), "Количество восстановленных эпических задач должно быть равно 1-ой.");
        assertEquals(1, listSubtask1.size(), "Количество восстановленных подзадач должно быть равно 1-ой.");
    }

    @Test
    void downloadingDataFromAnEmptyFile() {
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("test/exceptionsForTest/emptyFile.csv"));
        assertTrue(fileBackedTaskManager.fullListTask().isEmpty(), "Файл был пустой, задач не должно быть.");
        assertTrue(fileBackedTaskManager.fullListEpic().isEmpty(), "Файл был пустой, эпических задач не должно быть.");
        assertTrue(fileBackedTaskManager.fullListSubtask().isEmpty(), "Файл был пустой, подзадач не должно быть.");
        assertEquals(1, fileBackedTaskManager.getCounterId(), "Счётчик задач должен быть равен 0");
    }

    @Test
    void downloadingDataFromAnFile() {
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("test/exceptionsForTest/completedFile.csv"));
        ArrayList<Task> listTask = fileBackedTaskManager.fullListTask();
        ArrayList<Epic> listEpic = fileBackedTaskManager.fullListEpic();

        assertEquals(2, listTask.size(), "Количество добавленных задач должно быть равно 2-ум.");
        assertEquals(2, listEpic.size(), "Количество добавленных эпических задач должно быть равно 2-ум.");
    }
}
