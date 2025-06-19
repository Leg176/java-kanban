package manager;

import model.*;

public class CSVConverter {

    // Преобразование Задачи в строку в зависимости от типа
    public static String transformationString(Task task) {
        if (task.getType() == TaskType.SUBTASK) {
            return String.format("%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription(), ((Subtask) task).getIdEpic());
        } else {
            return String.format("%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription());
        }
    }

    // Создание задачи из строки
    public static Task fromString(String value) {
        Task task = null;
        if (!value.isEmpty() && !value.isBlank()) {
            String[] elements = value.split(",");
            switch (TaskType.valueOf(elements[1])) {
                case EPIC:
                    int idEpic = Integer.parseInt(elements[0]);
                    task = new Epic(elements[2], elements[4], Status.valueOf(elements[3]), idEpic);
                    break;
                case SUBTASK:
                    int idSubtask = Integer.parseInt(elements[0]);
                    int idEpicInSubtask = Integer.parseInt(elements[5]);
                    task = new Subtask(elements[2], elements[4], Status.valueOf(elements[3]),
                            idSubtask, idEpicInSubtask);
                    break;
                case TASK:
                    int idTask = Integer.parseInt(elements[0]);
                    task = new Task(elements[2], elements[4], Status.valueOf(elements[3]), idTask);
                    break;
            }
        }
        return task;
    }
}
