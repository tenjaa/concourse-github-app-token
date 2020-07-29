package eu.neufeldt.concoursegithubcredentials.check;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.neufeldt.concoursegithubcredentials.model.VersionWrapper;

public class Check {

    private final Gson gson = new GsonBuilder().create();

    public String check(String stdin) {

        VersionWrapper checkRequest = gson.fromJson(stdin, VersionWrapper.class);

        if (checkRequest == null || checkRequest.version == null) {
            return "[]";
        } else {
            return "[{\"date\":\"" + checkRequest.version.date + "\"}]";
        }
    }
}
