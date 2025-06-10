package model;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String name, String description, Status status, int id, int idEpic) {
        super(name, description, status, id);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
