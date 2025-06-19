package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> listSubtaskEpic = new ArrayList();

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public ArrayList<Integer> getListSubtaskEpic() {
        return listSubtaskEpic;
    }

    public void setListSubtaskEpic(ArrayList<Integer> listSubtaskEpic) {
        this.listSubtaskEpic = listSubtaskEpic;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
