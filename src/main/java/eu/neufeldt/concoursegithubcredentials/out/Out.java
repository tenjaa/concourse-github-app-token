package eu.neufeldt.concoursegithubcredentials.out;

import com.google.gson.Gson;
import eu.neufeldt.concoursegithubcredentials.model.Version;
import eu.neufeldt.concoursegithubcredentials.model.VersionWrapper;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class Out {

    private final Instant instant;

    public Out(Instant clock) {
        this.instant = clock;
    }

    public String check() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String date = formatter.format(instant);
        return new Gson().toJson(new VersionWrapper(new Version(date)));
    }
}
