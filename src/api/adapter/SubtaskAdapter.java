package api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import model.Status;
import model.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubtaskAdapter extends TypeAdapter<Subtask> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    @Override
    public void write(final JsonWriter jsonWriter, final Subtask subtask) throws IOException {
        String strStart = subtask.getStartTime() == null ? ""
                : subtask.getStartTime().format(DATE_FORMATTER);
        String strDuration = subtask.getDuration() == null ? "0"
                : String.valueOf(subtask.getDuration().toMinutes());
        jsonWriter.beginObject();
        jsonWriter.name("id").value(subtask.getId());
        jsonWriter.name("name").value(subtask.getName());
        jsonWriter.name("description").value(subtask.getDescription());
        jsonWriter.name("status").value(subtask.getStatus().name());
        jsonWriter.name("idEpic").value(subtask.getIdEpic());
        jsonWriter.name("duration").value(strDuration);
        jsonWriter.name("startTime").value(strStart);
        jsonWriter.endObject();
    }

    @Override
    public Subtask read(final JsonReader jsonReader) throws IOException {
        String name = null;
        String description = null;
        Status status = null;
        int id = 0;
        int idEpic = 0;
        Duration duration = null;
        LocalDateTime startTime = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String fieldName = jsonReader.nextName();
            switch (fieldName) {
                case "id":
                    if (jsonReader.peek() == JsonToken.NUMBER) {
                        id = jsonReader.nextInt();
                    } else {
                        jsonReader.skipValue();
                    }
                    break;
                case "idEpic":
                    idEpic = jsonReader.nextInt();
                    break;
                case "name":
                    name = jsonReader.nextString();
                    break;
                case "description":
                    description = jsonReader.nextString();
                    break;
                case "status":
                    status = Status.valueOf(jsonReader.nextString());
                    break;
                case "startTime":
                    String startTimeStr = jsonReader.nextString();
                    if (!startTimeStr.isEmpty()) {
                        startTime = LocalDateTime.parse(startTimeStr, DATE_FORMATTER);
                    }
                    break;
                case "duration":
                    long durationMinutes = jsonReader.nextLong();
                    duration = Duration.ofMinutes(durationMinutes);
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        jsonReader.endObject();
        return new Subtask(name, description, status, id, idEpic, duration, startTime);
    }
}


