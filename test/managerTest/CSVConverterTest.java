/*package managerTest;

import model.Status;
import model.Subtask;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.Test;

import static manager.CSVConverter.fromString;
import static manager.CSVConverter.transformationString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CSVConverterTest {

    Subtask subtask = new Subtask("Записаться на курсы", "Необходимо совмещать с работой", Status.NEW, 5, 3, 15, 15.08.24);
    String text = "5,SUBTASK,Записаться на курсы,NEW,Необходимо совмещать с работой,3";

    @Test
    void checkingTheConversionOfTaskToString() {
        String line = transformationString(subtask);
        assertEquals(line, text, "Текст и формат вывода задачи должен совпадать.");
    }

    @Test
    void checkingTheConversionOfStringToTask() {
        Task task = fromString(text);
        assertNotNull(task, "После преобразования текста в задачу, объект не должен быть пустой.");
        assertEquals(5, subtask.getId(), "Id подзадачи должено быть равено 5.");
        assertEquals("Записаться на курсы", subtask.getName(), "Имя подзадачи должно совпадать с \"Записаться на курсы\".");
        assertEquals("Необходимо совмещать с работой", subtask.getDescription(), "Описание подзадачи должно совпадать с \"Необходимо совмещать с работой\".");
        assertEquals(TaskType.SUBTASK, subtask.getType(), "Тип подзадачи должен быть SUBTASK.");
        assertEquals(Status.NEW, subtask.getStatus(), "Статус подзадачи должен быть NEW.");
        assertEquals(3, subtask.getIdEpic(), "Id эпика должено быть равено 3.");
}
    }*/
