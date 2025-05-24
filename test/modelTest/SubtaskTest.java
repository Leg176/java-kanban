package modelTest;

import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    @Test
    public void shouldComparedClassObjectsSubtaskInputId() {
        Subtask subtask1 = new Subtask("Записаться на курсы", "Необходимо совмещать с работой", Status.NEW, 5, 3);
        Subtask subtask2 = new Subtask("Пройти первый спринт", "Обучение поделено на спринты", Status.NEW, 5, 3);
        assertEquals(subtask1, subtask2, "Объекты не равны");
    }
}
