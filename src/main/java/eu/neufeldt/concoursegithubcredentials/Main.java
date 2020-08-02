package eu.neufeldt.concoursegithubcredentials;

import com.google.gson.Gson;
import eu.neufeldt.concoursegithubcredentials.check.Check;
import eu.neufeldt.concoursegithubcredentials.in.In;
import eu.neufeldt.concoursegithubcredentials.model.InRequest;
import eu.neufeldt.concoursegithubcredentials.model.VersionWrapper;
import eu.neufeldt.concoursegithubcredentials.out.Out;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.security.GeneralSecurityException;
import java.time.Instant;

public class Main {

    public static final Gson GSON = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException, GeneralSecurityException {
        Action action = Action.valueOf(args[0]);
        String stdin = IOUtils.toString(System.in, Charset.defaultCharset());

        String response;
        switch (action) {
            case CHECK:
                VersionWrapper checkRequest = GSON.fromJson(stdin, VersionWrapper.class);
                response = new Check().check(checkRequest);
                break;
            case IN:
                InRequest inRequest = GSON.fromJson(stdin, InRequest.class);
                GithubClient client = new GithubClient("https://api.github.com/", inRequest.source.appId, inRequest.source.privateKey, Instant.now());
                response = new In(client, FileSystems.getDefault()).getToken(inRequest, args[1]);
                break;
            case OUT:
                response = new Out(Instant.now()).check();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }

        System.out.print(response);
    }
}
