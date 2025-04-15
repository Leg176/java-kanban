import java.util.HashMap;
import java.util.ArrayList;

public class Epic extends Task {
    private int subEpic = 1;
    private static ArrayList<Integer> listSubEpic = new ArrayList();
    private static Status status = Status.NEW;

    public Epic(String name, String description) {
        super(name, description, status);
        listSubEpic.add(subEpic);
    }

    public static boolean inStock (int number) {
        return listSubEpic.contains(number);
    }

    public static Epic newEpic() {
        System.out.println("Введите имя новой эпической задачи:");
        String taskName = Manager.scanner.nextLine();
        System.out.println("Введите описание эпической задачи:");
        String taskDescription = Manager.scanner.nextLine();
        Epic epic = new Epic(taskName, taskDescription);
        return epic;
    }



}
