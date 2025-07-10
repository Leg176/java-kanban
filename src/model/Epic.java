package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> listSubtaskEpic = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public Epic(String name, String description, Status status, int id, Duration duration, LocalDateTime startTime) {
        super(name, description, status, id, duration, startTime);
    }

    public ArrayList<Integer> getListSubtaskEpic() {
        return listSubtaskEpic;
    }

    public void setListSubtaskEpic(ArrayList<Integer> listSubtaskEpic) {
        this.listSubtaskEpic = listSubtaskEpic;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

}
