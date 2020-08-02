package eu.neufeldt.concoursegithubcredentials.in;

import eu.neufeldt.concoursegithubcredentials.GithubClient;
import eu.neufeldt.concoursegithubcredentials.model.InstallationResponse;

import java.io.IOException;

public class InstallationIdFinder {

    private final GithubClient client;

    public InstallationIdFinder(GithubClient client) {
        this.client = client;
    }

    public int find(String user, String org) throws IOException, InterruptedException {
        if ((user == null) == (org == null)) {
            throw new RuntimeException("Specify either user or org");
        }
        InstallationResponse installation;
        if (user != null) {
            installation = client.call("GET", "users/" + user + "/installation", InstallationResponse.class);
        } else {
            installation = client.call("GET", "orgs/" + org + "/installation", InstallationResponse.class);
        }
        return installation.id;
    }
}
