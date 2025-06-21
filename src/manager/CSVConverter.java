package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVConverter {

    // Преобразование Задачи в строку в зависимости от типа
    public static String transformationString(Task task) {
        if (task.getType() == TaskType.SUBTASK) {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), ((Subtask) task).getIdEpic(),
                    task.getDuration().toMinutes(), task.getStartTime());
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription(), task.getDuration().toMinutes(), task.getStartTime());
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
                    Duration durationEpic = Duration.ofMinutes(Integer.parseInt(elements[5]));
                    LocalDateTime startTimeEpic = LocalDateTime.parse(elements[6]);
                    task = new Epic(elements[2], elements[4], Status.valueOf(elements[3]), idEpic,
                            durationEpic, startTimeEpic);
                    break;
                case SUBTASK:
                    int idSubtask = Integer.parseInt(elements[0]);
                    int idEpicInSubtask = Integer.parseInt(elements[5]);
                    Duration durationSub = Duration.ofMinutes(Integer.parseInt(elements[6]));
                    LocalDateTime startTimeSub = LocalDateTime.parse(elements[7]);
                    task = new Subtask(elements[2], elements[4], Status.valueOf(elements[3]),
                            idSubtask, idEpicInSubtask, durationSub, startTimeSub);
                    break;
                case TASK:
                    int idTask = Integer.parseInt(elements[0]);
                    Duration duration = Duration.ofMinutes(Integer.parseInt(elements[5]));
                    LocalDateTime startTime = LocalDateTime.parse(elements[6]);
                    task = new Task(elements[2], elements[4], Status.valueOf(elements[3]), idTask, duration, startTime);
                    break;
            }
        }
        return task;
    }
}
