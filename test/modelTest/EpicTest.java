package modelTest;

import model.Epic;
import model.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void shouldComparedClassObjectsEpicInputId() {
        Epic epic1 = new Epic("Пройти обучение", "Получить новые знания", Status.NEW, 3);
        Epic epic2 = new Epic("Собрать ребёнка в школу", "Составить список необходимых вещей", Status.NEW, 3);
        assertEquals(epic1, epic2, "Объекты не равны");
    }
}
