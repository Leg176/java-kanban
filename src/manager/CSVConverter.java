package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVConverter {

    // Преобразование Задачи в строку в зависимости от типа
    public static String transformationString(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        String startTime;
        long minutes;
        if (task.getDuration() != null) {
            minutes = task.getDuration().toMinutes();
        } else {
            minutes = 0;
        }
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(formatter);
        } else {
            startTime = "";
        }
        if (task.getType() == TaskType.SUBTASK) {
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), ((Subtask) task).getIdEpic(),
                    minutes, startTime);
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                    task.getDescription(), minutes, startTime);
        }
    }

    // Создание задачи из строки
    public static Task fromString(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        Task task = null;
        if (!value.isEmpty() && !value.isBlank()) {
            String[] elements = value.split(",",-1);
            switch (TaskType.valueOf(elements[1])) {
                case EPIC:
                    int idEpic = Integer.parseInt(elements[0]);
                    Duration duration;
                    LocalDateTime startTime;
                    if (Integer.parseInt(elements[5]) != 0) {
                        duration = Duration.ofMinutes(Long.parseLong((elements[5])));
                    } else {
                        duration = null;
                    }
                    if (!elements[6].equals("")) {
                        startTime = LocalDateTime.parse(elements[6], formatter);
                    } else {
                        startTime = null;
                    }
                    task = new Epic(elements[2], elements[4], Status.valueOf(elements[3]), idEpic,
                            duration, startTime);
                    break;
                case SUBTASK:
                    int idSubtask = Integer.parseInt(elements[0]);
                    int idEpicInSubtask = Integer.parseInt(elements[5]);
                    Duration durationSub;
                    LocalDateTime startTimeSub;
                    if (Integer.parseInt(elements[6]) != 0) {
                        durationSub = Duration.ofMinutes(Long.parseLong(elements[6]));
                    } else {
                        durationSub = null;
                    }
                    if (!elements[7].equals("")) {
                        startTimeSub = LocalDateTime.parse(elements[7], formatter);
                    } else {
                        startTimeSub = null;
                    }
                    task = new Subtask(elements[2], elements[4], Status.valueOf(elements[3]),
                            idSubtask, idEpicInSubtask, durationSub, startTimeSub);
                    break;
                case TASK:
                    int idTask = 0;
                    if (!elements[0].equals("")) {
                    idTask = Integer.parseInt(elements[0]);
                    }
                    if (Integer.parseInt(elements[5]) != 0) {
                        duration = Duration.ofMinutes(Long.parseLong((elements[5])));
                    } else {
                        duration = null;
                    }
                    if (!elements[6].equals("")) {
                        startTime = LocalDateTime.parse(elements[6], formatter);
                    } else {
                        startTime = null;
                    }
                    task = new Task(elements[2], elements[4], Status.valueOf(elements[3]), idTask, duration, startTime);
                    break;
            }
        }
        return task;
    }
}
