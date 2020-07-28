package eu.neufeldt.concoursegithubcredentials.check;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.neufeldt.concoursegithubcredentials.model.CheckRequest;

public class Check {

    private final Gson gson = new GsonBuilder().create();

    public String check(String stdin) {

        CheckRequest checkRequest = gson.fromJson(stdin, CheckRequest.class);

        if (checkRequest.version == null) {
            return "[]";
        } else {
            return "[{\"date\":\"" + checkRequest.version.date + "\"}]";
        }
    }
}
