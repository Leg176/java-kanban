package modelTest;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    public void shouldComparedClassObjectsTaskInputId() {
        Task task1 = new Task("Сходить в магазин", "Купить продукты", Status.NEW, 1);
        Task task2 = new Task("Обработать газон", "Достать опрыскиватель", Status.NEW, 1);
        assertEquals(task1, task2, "Объекты не равны");
    }
}
