package eu.neufeldt.concoursegithubcredentials.in;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.Gson;
import eu.neufeldt.concoursegithubcredentials.EncryptionUtils;
import eu.neufeldt.concoursegithubcredentials.model.InRequest;
import eu.neufeldt.concoursegithubcredentials.model.TokenResponse;
import eu.neufeldt.concoursegithubcredentials.model.VersionWrapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;

public class In {

    private final static Gson GSON = new Gson();

    private final String baseUrl;
    private final Instant now;
    private final FileSystem fileSystem;

    public In(String baseUrl, Instant now, FileSystem fileSystem) {
        this.baseUrl = baseUrl;
        this.now = now;
        this.fileSystem = fileSystem;
    }

    public String getToken(String stdin, String destinationDirectory) throws IOException, InterruptedException, GeneralSecurityException {
        InRequest inRequest = GSON.fromJson(stdin, InRequest.class);

        PrivateKey privateKey = EncryptionUtils.loadPkcs1PrivateKey(inRequest.source.privateKey);
        String token;
        try {
            Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) privateKey);
            token = JWT.create()
                    .withIssuer(String.valueOf(inRequest.source.appId))
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(now.plusSeconds(600)))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException();
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "app/installations/" + inRequest.source.installationId + "/access_tokens"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github.machine-man-preview+json")
                .build();
        HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
        TokenResponse tokenResponse = GSON.fromJson(send.body(), TokenResponse.class);

        Path path = fileSystem.getPath(destinationDirectory);
        Files.createDirectories(path);
        Path tokenFile = path.resolve("token");
        Files.writeString(tokenFile, tokenResponse.token);

        return GSON.toJson(new VersionWrapper(inRequest.version));
    }
}
