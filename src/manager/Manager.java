package manager;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> listTask= new HashMap<>();
    private HashMap<Integer, Subtask> listSubtask= new HashMap<>();
    private HashMap<Integer, Epic> listEpic= new HashMap<>();
    private int counterId = 1;

    // Вывод задачи по номеру Id
    public Task getTaskById(int outputTaskId) {
        return listTask.get(outputTaskId);
    }

    public Subtask getSubtaskById(int outputSubtaskId) {
        return listSubtask.get(outputSubtaskId);
    }

    public Epic getEpicById(int outputEpicId) {
        return listEpic.get(outputEpicId);
    }

    // Удаление задач
    public void fullDelTask() {
        listTask.clear();
    }

    public void fullDelSubtask() {
        listSubtask.clear();
        for (Epic epic : listEpic.values()) {
            epic.getListSubtaskEpic().clear();
            epic.setStatus(Status.NEW);
        }
    }

    public void fullDelEpic() {
        listSubtask.clear();
        listEpic.clear();
    }

    // Удаление задачи по номеру Id
    public void deleteById(int idDel) {
        if (listTask.containsKey(idDel)) {
            listTask.remove(idDel);
        } else if (listSubtask.containsKey(idDel)) {
            ArrayList<Integer> newListSubtaskEpic = listEpic.get(listSubtask.get(idDel).getIdEpic()).getListSubtaskEpic();
            newListSubtaskEpic.remove((Integer) idDel);
            listEpic.get(listSubtask.get(idDel).getIdEpic()).setListSubtaskEpic(newListSubtaskEpic);
            listSubtask.remove(idDel);
            updateStatusEpic(listSubtask.get(idDel).getIdEpic());
        } else if (listEpic.containsKey(idDel)) {
            ArrayList<Integer> idSubtaskEpic = listEpic.get(idDel).getListSubtaskEpic();
            for (int idSubtask : idSubtaskEpic) {
                listSubtask.remove(idSubtask);
            }
            listEpic.remove(idDel);
        }
    }

    // Вывод подзадач по номеру эпичной задачи
    public void getListSubtask (int idEpic) {
        if (listEpic.containsKey(idEpic)) {
            ArrayList<Integer> idSubtaskEpic = listEpic.get(idEpic).getListSubtaskEpic();
            for (int idSubtask : idSubtaskEpic) {
                listSubtask.get(idSubtask);
            }
        }
    }

    // Добавление подзадачи
    public Subtask createSubtask(Subtask subtask) {
        int newId = getCounterId();
        subtask.setId(newId);
        Epic epic = listEpic.get(subtask.getIdEpic());
        if (epic != null) {
            ArrayList<Integer> newListSubtaskEpic = epic.getListSubtaskEpic();
            newListSubtaskEpic.add(subtask.getId());
            epic.setListSubtaskEpic(newListSubtaskEpic);
            listSubtask.put(subtask.getId(), subtask);
            updateStatusEpic(subtask.getIdEpic());
            return subtask;
        } else {
            return null;
        }
    }

    // Добавление задачи
    public Task createTask(Task task) {
        int newId = getCounterId();
        task.setId(newId);
        listTask.put(task.getId(), task);
        return task;
    }

    // Добавление эпической задачи
    public Epic createEpic(Epic epic) {
        int newId = getCounterId();
        epic.setId(newId);
        listEpic.put(epic.getId(), epic);
        return epic;
    }

    // Обновление задачи по номеру Id задачи
    public void updateTask(Task task) {
        listTask.put(task.getId(), task);
    }

    // Обновление подзадачи по номеру Id подзадачи
    public void updateSubTask(Subtask subtask) {
        listSubtask.put(subtask.getId(), subtask);
        updateStatusEpic(subtask.getIdEpic());
    }

    // Обновление эпика по номеру Id эпика
    public void updateEpic(Epic epic, String name, String description) {
        epic.setName(name);
        epic.setDescription(description);
        listEpic.put(epic.getId(), epic);
    }

    // Обновление статуса эпической задачи
    protected void updateStatusEpic(int idEpic) {
        ArrayList<Integer> numbersSubtaskInEpik = getEpicById(idEpic).getListSubtaskEpic();
        int quantitySubtask = numbersSubtaskInEpik.size();
            if (quantitySubtask == 0) {
                getEpicById(idEpic).setStatus(Status.NEW);
            } else if (quantitySubtask == 1) {
                listSubtask.get(numbersSubtaskInEpik.get(0));
                getEpicById(idEpic).setStatus(listSubtask.get(numbersSubtaskInEpik.get(0)).getStatus());
            } else {
                int countDone = 0;
                int countNEW = 0;
                for (int i = 0; i < quantitySubtask; i++) {
                    if (listSubtask.get(numbersSubtaskInEpik.get(i)).getIdEpic() == getEpicById(idEpic).getId() && listSubtask.get(numbersSubtaskInEpik.get(i)).getStatus() == Status.DONE) {
                        countDone++;
                    } else if (listSubtask.get(numbersSubtaskInEpik.get(i)).getIdEpic() == getEpicById(idEpic).getId() && listSubtask.get(numbersSubtaskInEpik.get(i)).getStatus() == Status.NEW) {
                        countNEW++;
                    }
                }
                if (countDone == quantitySubtask) {
                    getEpicById(idEpic).setStatus(Status.DONE);
                } else if (countNEW >= 1 && countNEW != quantitySubtask) {
                    getEpicById(idEpic).setStatus(Status.IN_PROGRESS);
                } else {
                    getEpicById(idEpic).setStatus(Status.NEW);
                }
            }
    }

    // Вывод всех типов задач
    public ArrayList<Task> fullListTask() {
        return new ArrayList<Task>(listTask.values());
    }

    public ArrayList<Subtask> fullListSubtask() {
        return new ArrayList<Subtask>(listSubtask.values());
    }

    public ArrayList<Epic> fullListEpic() {
        return new ArrayList<Epic>(listEpic.values());
    }

    private int getCounterId() {
        return counterId++;
    }

    protected void setCounterId(int counterId) {
        this.counterId = counterId;
    }
}
