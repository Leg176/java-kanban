import java.util.ArrayList;
import java.util.HashMap;

public class Subtask extends Task{
static ArrayList<Subtask> subtaskEpic  = new ArrayList<>();;
static private HashMap<Integer, ArrayList<Subtask>> listSubtaskEpic = new HashMap<>();
private static Status status = Status.NEW;

    public Subtask(String name, String description) {
        super(name, description, status);
    }

    public static Subtask newSubtask() {
        System.out.println("Введите имя новой подзадачи:");
        String subtaskName = Manager.scanner.nextLine();
        System.out.println("Введите описание подзадачи:");
        String subtaskDescription = Manager.scanner.nextLine();
        Subtask subtask = new Subtask(subtaskName, subtaskDescription);
        System.out.println("Введите номер эпической задачи к которой относится подзадача:");
        int numberEpic = Integer.parseInt(Manager.scanner.nextLine());
            if (Epic.inStock(numberEpic)) {
                subtaskEpic.add(subtask);
                listSubtaskEpic.put(numberEpic, subtaskEpic);
            } else {
                System.out.println("Эпической задачи с таким номером не существует.");
            }
        return subtask;
    }

    public static void getListSubtask () {
        System.out.println("Введите номер эпичной задачи:");
        int numberEpic = Integer.parseInt(Manager.scanner.nextLine());

        if (listSubtaskEpic.containsKey(numberEpic)) {
            ArrayList<Subtask> arrayListSubTask = listSubtaskEpic.get(numberEpic);
            int size = arrayListSubTask.size();
            for (int i = 0; i < size; i++) {
                System.out.println(arrayListSubTask.get(i));
            }
        } else {
            System.out.println("Введенная задача не содержит подзадач.");
        }
    }
}
