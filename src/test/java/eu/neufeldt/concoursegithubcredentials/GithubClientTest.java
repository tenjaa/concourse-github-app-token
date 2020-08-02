package eu.neufeldt.concoursegithubcredentials;

import eu.neufeldt.concoursegithubcredentials.model.Version;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GithubClientTest {

    private final static int APP_ID = 123;
    private final static String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEogIBAAKCAQEAsmtsbdRtPV89pVbCDpU+oZH33AoyYhJunkiF4bCgdNDed+tt\n" +
            "hsxO40sjvjcZfUaTy1DVjiHInbHBUd8Ng95k6+PdWVbzNABfsBWzMVi8TTZxKrnl\n" +
            "6OZ3l6nSUoGr8TP7s0hIvycnogAYT3bxKFqMSVUq+rjLZhQOHkIiTq9UIYXlbDGS\n" +
            "WQPa7w0Ic9iVyEVsF7x4ceyB2eXxkYBcifQWEZ8f5v0nFLfBbNzEk+bD93y0Dozm\n" +
            "J+Eo+OJQr0S939al/nb9KQnobPESMIii9HT6qME6gRdYoB3QTEzPiqqzefEFrE+/\n" +
            "p5IN61gsuMNihP1+jGiE4bBsDdqFDmQsRz29qQIDAQABAoIBAGyExBVlL/A1HnSS\n" +
            "+UMPaWGyO5Q9K3nVBi0FpVCpMl76r2U2744edBjxGdNXBgzZQYlXKBzj1XZ8qD2Y\n" +
            "skqzmKSVGot9RD0rwQeDm1L3SsiXxWscWIc4riKz6rW+Iwt4962K2JxTP1rcPe0P\n" +
            "RgyqTktIHvUSMrxqlE+5H87F+yDLn6ht0gm4R/MQza5Pc8F7GBCrx7Uqg3jR6fcH\n" +
            "jJMMxSKLA62Otn/MVqU2F7bVUODCoez0InWX7r4CGBxCsk4P9QkZWz2IFaoGcxDr\n" +
            "Lt6eTUriZ7lP7pyy8dwKQ2JAoUAaQ1too1EHKCJY0viB6t3zFeNT+5JaJ7iurIN3\n" +
            "svf/aNECgYEA6rqph+DMMqbQRrvANvg73OCMiZ1u+ibvmaf1V1ikYAYhfplrwDsm\n" +
            "+/Q4Wtuu4BADtghpPdnG571wZB9EMushFJTicMX8up+l8MAU/RfTBTofwLJnRwgi\n" +
            "+URPgB/Zlleg+vKPy4rPXyUEsseFI0InJlx2uwExFBsrLX9F2LvfBf0CgYEAwpZ4\n" +
            "muXK/m3NlOP/T1+y+gbSXsPUViElwsEa1nkL4+eWypNYDNMwbwrn4UCTp3g9wT1J\n" +
            "jy7HRRenjLasEQEJ1al61Myc70/j/NRmksuWMzUM2mGvGXT7Jqyp0T9dBVC8fhxt\n" +
            "wzO+L6D8q5WxmJBziCCR/nKoheGmgO/oaH73UB0CgYAhzMEutLsSrByfotd50uLj\n" +
            "2l3CTy40zTiBOsNEUZpRzXAzR6pRYvMpIrCevLWAxC2Un79vzZUrt4aglDQA1QTN\n" +
            "bubwcEIFKYn+kdoz26NiRE1gNRBUFSLcG+8Ktwtg77tZh2YXXCSiQbVcfQh53EPA\n" +
            "7V2XbgOALq1X/tSy8dKajQKBgBybiKGlNQHQ3M+N8YQW2rY9eIIsIjSDbQzD3RE4\n" +
            "/JGGA7pGjURdbBp3LP4Fw290kwes3fqp8uvqfVyHoqZhU7PQ1dkhN9VLFoaeJ3Jf\n" +
            "cfaXTwtwHxEMDLxz2AeGXEOiygN0ZIK1Qbm43kzPliuK2SFjQHN7IeGE+jisjKuI\n" +
            "Nw3VAoGAPknPsCt5TploR4yC5P5+v2W9qXIOmUevBY6pIqPe17LKbXcsdGfpqeTx\n" +
            "6FTnKiSSvO/dDkFZIAbvoikESSGi+yGR5+rU7bIkti77wsGCS25ZesrlR6x44rn2\n" +
            "xEkvEtWQS22vHYRDId+f+4oxwsX1zhQqc+FvoNvAz5XYT24me78=\n" +
            "-----END RSA PRIVATE KEY-----\n";
    private final static Instant NOW = Instant.ofEpochMilli(1595921059197L);

    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void not2xx() throws GeneralSecurityException {
        // given
        server.enqueue(new MockResponse().setBody("{\"date\":\"some-date\"}").setResponseCode(404));
        String baseUrl = server.url("").toString();
        GithubClient client = new GithubClient(baseUrl, APP_ID, PRIVATE_KEY, NOW);

        // when
        var throwableAssert = assertThatThrownBy(() -> client.call("GET", "path", Version.class));

        // then
        throwableAssert
                .isInstanceOf(RuntimeException.class)
                .withFailMessage("404 when calling " + baseUrl + "path")
                .hasNoCause();
    }

    @ValueSource(strings = {"GET", "POST"})
    @ParameterizedTest
    void success(String method) throws GeneralSecurityException, IOException, InterruptedException {
        // given
        server.enqueue(new MockResponse().setBody("{\"date\":\"some-date\"}"));
        GithubClient client = new GithubClient(server.url("").toString(), APP_ID, PRIVATE_KEY, NOW);

        // when
        Version version = client.call(method, "app/installations/123/access_tokens", Version.class);
        RecordedRequest recordedRequest = server.takeRequest();

        // then
        assertThat(version.date).isEqualTo("some-date");

        assertThat(recordedRequest.getPath()).isEqualTo("/app/installations/123/access_tokens");
        assertThat(recordedRequest.getBody()).isEqualTo(new Buffer());
        assertThat(recordedRequest.getMethod()).isEqualTo(method);
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiIxMjMiLCJleHAiOjE1OTU5MjE2NTksImlhdCI6MTU5NTkyMTA1OX0.ZGyKEiy-lB9FZ5p3ipzyVFW0UvJ6tWqLubuBOMYtrc4WC_NW3IftaEqVqvyVsKtdZbvsYpnf1s3rqb6MCpncyroKkQ0lXWBS2xRjpN2_RpqKZQUTO6QeJdV2oRzRNZsGaVcCzDH26y4v5fZPyFVczO76FtDhXD01XOFK1Yq4yA0QB20_QleohALaSFrkpLF2QKUlDkH2Ipbbad5Q_1mcCwb8QIFTyuk5v0rJ883GpO5NywMFsdB23iwIvevGem8GInfCH4aXa7AqA52odUnAMZCD32E_JloUXL5x9BWkkZwvDfX7USJrFqaHl4EZyAES5fwmxHanFNUNOrX5WMF9aw");
        assertThat(recordedRequest.getHeader("Accept")).isEqualTo("application/vnd.github.machine-man-preview+json");
    }
}
