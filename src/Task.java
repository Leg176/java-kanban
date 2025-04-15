import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private final Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public static void fullListTask() {
        System.out.println(Manager.listTask.values());
        System.out.println(Manager.listSubtask.values());
        System.out.println(Manager.listEpic.values());
    }

    public static void getForId(int id) {
        if(Manager.listTask.containsKey(id)) {
            System.out.println(Manager.listTask.get(id));
        } else if (Manager.listSubtask.containsKey(id)) {
            System.out.println(Manager.listSubtask.get(id));
        } else if (Manager.listEpic.containsKey(id)) {
            System.out.println(Manager.listEpic.get(id));
        } else {
            System.out.println("Задачи с таким id не найдено.");
        }
    }

    public static void fullDelTask() {
        Manager.listTask.clear();
        Manager.listSubtask.clear();
        Manager.listEpic.clear();
    }
    public static Task newTask() {
        System.out.println("Введите имя новой задачи:");
        String taskName = Manager.scanner.nextLine();
        Task task = null;
        System.out.println("Введите описание задачи:");
        String taskDescription = Manager.scanner.nextLine();
        System.out.println("Введите статус задачи:");
        String taskStatus = Manager.scanner.nextLine();
        Status status = Status.toString(taskStatus);
        if (status != null) {
            task  = new Task(taskName, taskDescription, status);
        }
        return task;
    }
    public static void delTask() {
        System.out.println("Введите номер задачи:");
        int id = Integer.parseInt(Manager.scanner.nextLine());
        if (Manager.listTask.containsKey(id) || Manager.listSubtask.containsKey(id) || Manager.listEpic.containsKey(id)) {
            Manager.listTask.remove(id);
            Manager.listSubtask.remove(id);
            Manager.listEpic.remove(id);
            System.out.println("Задача успешно удалена.");
        } else {
            System.out.println("Задача с таким номером в списках отсутствует.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(name, task.name);
    }
    @Override
    public String toString() {
        return "Task " + name + ", Описание: " + description + " , Статус: " + status + "\n";
    }
}