import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        Duration duration = Duration.ofMinutes(15);
        LocalDateTime loclDate = LocalDateTime.now();
        System.out.println(duration);
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Сходить в магазин", "Купить продукты", Status.NEW, 1);
        Task task2 = new Task("Обработать газон", "Достать опрыскиватель", Status.NEW, 2);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Пройти обучение", "Получить новые знания", Status.NEW, 3);
        Epic epic2 = new Epic("Собрать ребёнка в школу", "Составить список необходимых вещей", Status.NEW, 4);
        Epic epic3 = new Epic("ребёнка в школу", "Составить список необходимых вещей", Status.NEW, 4);
        Epic epic4 = new Epic("Собрать  в школу", "Составить список необходимых вещей", Status.NEW, 4);
        Epic epic5 = new Epic("Собрать ребёнка в ", "Составить список необходимых вещей", Status.NEW, 4);
        Epic epic6 = new Epic("Собрать ребёнка в школу", "список необходимых вещей", Status.NEW, 4);
        Epic epic7 = new Epic("Собрать ребёнка в школу", "Составить необходимых вещей", Status.NEW, 4);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        manager.createEpic(epic4);
        manager.createEpic(epic5);
        manager.createEpic(epic6);
        manager.createEpic(epic7);


        Subtask subtask1 = new Subtask("Записаться на курсы", "Необходимо совмещать с работой", Status.IN_PROGRESS, 5, 3);
        Subtask subtask2 = new Subtask("Пройти первый спринт", "Обучение поделено на спринты", Status.NEW, 6, 3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

//        manager.fullListTask();

//        Subtask subtask3 = new Subtask("Записаться", "Необходимо", Status.DONE, 5, 3);
//        Subtask subtask4 = new Subtask("Пройти спринт", "Обучение поделено", Status.IN_PROGRESS, 5, 3);
//        manager.updateSubTask(subtask3);
//        manager.updateSubTask(subtask4);
//        manager.fullListTask();

        Task task3 = new Task("Купить хлеб", "Сходить в магазин", Status.IN_PROGRESS, 1);
//        manager.updateTask(task3);
//        manager.getListSubtask(4);

//        manager.deleteById(2);

//        manager.fullListTask();

//        manager.getEpicById(3);

        printAllTasks(manager);
    }

    private static void printAllTasks(InMemoryTaskManager manager) {
        System.out.println("Задачи:");
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getTaskById(2));

        System.out.println("Эпики:");
        System.out.println(manager.getEpicById(3));
        System.out.println(manager.getEpicById(4));
        System.out.println(manager.getEpicById(5));
        System.out.println(manager.getEpicById(6));
        System.out.println(manager.getEpicById(7));
        System.out.println(manager.getEpicById(8));
        System.out.println(manager.getEpicById(9));

        System.out.println("Подзадачи:");
        System.out.println(manager.getSubtaskById(10));
        System.out.println(manager.getSubtaskById(11));


        System.out.println("История:");
        System.out.println(manager.getHistory());
    }

}
