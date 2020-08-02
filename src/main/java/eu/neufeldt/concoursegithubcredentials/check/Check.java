package eu.neufeldt.concoursegithubcredentials.check;


import eu.neufeldt.concoursegithubcredentials.model.VersionWrapper;

public class Check {

    public String check(VersionWrapper request) {
        if (request == null || request.version == null) {
            return "[]";
        } else {
            return "[{\"date\":\"" + request.version.date + "\"}]";
        }
    }
}
