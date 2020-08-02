package eu.neufeldt.concoursegithubcredentials;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;

import static java.net.http.HttpRequest.BodyPublishers.noBody;

public class GithubClient {
    private final String baseUrl;
    private final HttpClient client;
    private final HttpRequest.Builder baseRequest;

    public GithubClient(String baseUrl, int appId, String privateKey, Instant now) throws GeneralSecurityException {
        this.baseUrl = baseUrl;
        PrivateKey pkcs1PrivateKey = EncryptionUtils.loadPkcs1PrivateKey(privateKey);
        Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) pkcs1PrivateKey);
        String token = JWT.create()
                .withIssuer(String.valueOf(appId))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(600)))
                .sign(algorithm);
        client = HttpClient.newHttpClient();
        baseRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github.machine-man-preview+json");
    }

    public <T> T call(String method, String path, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = baseRequest.method(method, noBody()).uri(URI.create(baseUrl + path)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() <= 299) {
            return new Gson().fromJson(response.body(), clazz);
        } else {
            throw new RuntimeException(response.statusCode() + " when calling " + baseUrl + path);
        }
    }
}
