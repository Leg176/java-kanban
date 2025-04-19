import manager.Manager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("Сходить в магазин", "Купить продукты", Status.NEW, 1);
        Task task2 = new Task("Обработать газон", "Достать опрыскиватель", Status.NEW, 2);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Пройти обучение", "Получить новые знания", Status.NEW, 3);
        Epic epic2 = new Epic("Собрать ребёнка в школу", "Составить список необходимых вещей", Status.NEW,4);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Записаться на курсы", "Необходимо совмещать с работой", Status.NEW, 5, 3);
        Subtask subtask2 = new Subtask("Пройти первый спринт", "Обучение поделено на спринты", Status.NEW, 5, 3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.fullListTask();

        Subtask subtask3 = new Subtask("Записаться", "Необходимо", Status.DONE, 5, 3);
        Subtask subtask4 = new Subtask("Пройти спринт", "Обучение поделено", Status.IN_PROGRESS, 5, 3);
        manager.updateSubTask(subtask3);
        manager.updateSubTask(subtask4);
        manager.fullListTask();

        Task task3 = new Task("Купить хлеб", "Сходить в магазин", Status.IN_PROGRESS, 1);
        manager.updateTask(task3);
        manager.getListSubtask(4);

        manager.deleteById(2);

        manager.fullListTask();

        manager.getEpicById(3);

    }
}
