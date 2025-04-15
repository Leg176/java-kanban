
import java.util.HashMap;
import java.util.Scanner;

public class Manager {
    static Scanner scanner = new Scanner(System.in);
    static HashMap<Integer, Task> listTask= new HashMap<>();
    static HashMap<Integer, Subtask> listSubtask= new HashMap<>();
    static HashMap<Integer, Epic> listEpic= new HashMap<>();
    private static int taskId = 1;


    public static void main(String[] args) {

        while (true) {
            printMenu();
            System.out.println("Введите номер команды:");
            int number = Integer.parseInt(scanner.nextLine());
            switch (number) {
                case 1:
                    Task.fullListTask();
                    break;
                case 2:
                    Task.fullDelTask();
                    break;
                case 3:
                    System.out.println("Введите номер id:");
                    int id = Integer.parseInt(scanner.nextLine());
                    Task.getForId(id);
                    break;
                case 4:
                    System.out.println("Введите тип задачи: \n 1. Task \n 2. Subtask \n 3. Epic");
                    int type = Integer.parseInt(scanner.nextLine());
                    if (type == 1) {
                        listTask.put(getId(), Task.newTask());
                        taskId++;
                    } else if (type == 2) {
                        listSubtask.put(getId(), Subtask.newSubtask());
                        taskId++;
                    } else if (type == 3) {
                        listEpic.put(getId(), Epic.newEpic());
                        taskId++;
                    }
                    break;
                case 5:
                    updateTask();
                    break;
                case 6:
                    Task.delTask();
                    break;
                case 7:
                    Subtask.getListSubtask();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Введена неизвестная комнада.");
            }
        }
    }

    public static void printMenu() {
        System.out.println("1. Получение списка всех задач.");
        System.out.println("2. Удаление всех задач.");
        System.out.println("3. Получение по идентификатору.");
        System.out.println("4. Создание задачи.");
        System.out.println("5. Обновление задачи.");
        System.out.println("6. Удаление задачи по идентификатору.");
        System.out.println("7. Получение списка всех подзадач определённого эпика");
        System.out.println("8. Выход из приложения.");
    }

    public static void updateTask() {
        System.out.println("Введите номер задачи которую необходимо обновить:");
        int idNumber = Integer.parseInt(scanner.nextLine());
            listTask.put(idNumber, Task.newTask());
        }
    public static int getId() {
        return taskId;
    }

}
