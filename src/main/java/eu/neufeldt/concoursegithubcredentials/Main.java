package eu.neufeldt.concoursegithubcredentials;

import eu.neufeldt.concoursegithubcredentials.check.Check;
import eu.neufeldt.concoursegithubcredentials.in.In;
import eu.neufeldt.concoursegithubcredentials.out.Out;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.security.GeneralSecurityException;
import java.time.Instant;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, GeneralSecurityException {
        Action action = Action.valueOf(args[0]);
        String stdin = IOUtils.toString(System.in, Charset.defaultCharset());

        String response;
        switch (action) {
            case CHECK:
                response = new Check().check(stdin);
                break;
            case IN:
                response = new In("https://api.github.com/", Instant.now(), FileSystems.getDefault()).getToken(stdin, args[1]);
                break;
            case OUT:
                response = new Out(Instant.now()).check();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }

        System.out.println("RESPONSE: " + response);
    }
}
