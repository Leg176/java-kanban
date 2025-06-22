package managerTest;

import model.Status;
import model.Subtask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static manager.CSVConverter.fromString;
import static manager.CSVConverter.transformationString;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CSVConverterTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    String time = "2025.06.14 14:00";
    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    Duration duration = Duration.ofMinutes(15);
    Subtask subtask = new Subtask("Записаться на курсы", "Необходимо совмещать с работой",
            Status.NEW, 5, 3, duration, startTime);
    String text = "5,SUBTASK,Записаться на курсы,NEW,Необходимо совмещать с работой,3,15,2025.06.14 14:00";
    Subtask subtask1 = new Subtask("Записаться на курсы", "Необходимо совмещать с работой",
            Status.NEW, 5, 3);
    String text1 = "5,SUBTASK,Записаться на курсы,NEW,Необходимо совмещать с работой,3,0,null";

    @Test
    void checkingTheConversionOfTaskToString() {
        String line = transformationString(subtask);
        assertEquals(line, text, "Текст и формат вывода задачи должен совпадать.");
    }

    @Test
    void checkingTheConversionOfTaskWithAnEmptyDurationAndTimeToString() {
        String line = transformationString(subtask1);
        assertEquals(line, text1, "Текст и формат вывода задачи должен совпадать.");
    }

    @Test
    void checkingTheConversionOfStringToTask() {
        Task task = fromString(text);
        assertNotNull(task, "После преобразования текста в задачу, объект не должен быть пустой.");
        assertEquals(5, subtask.getId(), "Id подзадачи должено быть равено 5.");
        assertEquals("Записаться на курсы", subtask.getName(), "Имя подзадачи должно совпадать с \"Записаться на курсы\".");
        assertEquals("Необходимо совмещать с работой", subtask.getDescription(), "Описание " +
                "подзадачи должно совпадать с \"Необходимо совмещать с работой\".");
        assertEquals(TaskType.SUBTASK, subtask.getType(), "Тип подзадачи должен быть SUBTASK.");
        assertEquals(Status.NEW, subtask.getStatus(), "Статус подзадачи должен быть NEW.");
        assertEquals(3, subtask.getIdEpic(), "Id эпика должено быть равено 3.");
        assertEquals(15, subtask.getDuration().toMinutes());
        assertEquals("2025.06.14 14:00", subtask.getStartTime().format(formatter));
    }

    @Test
    void checkingTheConversionOfStringWithAnEmptyDurationAndTimeToTask() {
        Task task = fromString(text1);
        assertNotNull(task, "После преобразования текста в задачу, объект не должен быть пустой.");
        assertEquals(5, subtask1.getId(), "Id подзадачи должено быть равено 5.");
        assertEquals("Записаться на курсы", subtask1.getName(), "Имя подзадачи должно совпадать с \"Записаться на курсы\".");
        assertEquals("Необходимо совмещать с работой", subtask1.getDescription(), "Описание " +
                "подзадачи должно совпадать с \"Необходимо совмещать с работой\".");
        assertEquals(TaskType.SUBTASK, subtask1.getType(), "Тип подзадачи должен быть SUBTASK.");
        assertEquals(Status.NEW, subtask1.getStatus(), "Статус подзадачи должен быть NEW.");
        assertEquals(3, subtask1.getIdEpic(), "Id эпика должено быть равено 3.");
        assertNull(subtask1.getDuration());
        assertNull(subtask1.getStartTime());
    }
}

