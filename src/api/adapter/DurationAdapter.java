package api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.time.Duration;
import java.io.IOException;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration != null) {
            jsonWriter.value(duration.toMinutes());
        } else {
            jsonWriter.value(0);
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
            long minutes = Long.parseLong(jsonReader.nextString());
            return Duration.ofMinutes(minutes);
    }
}
