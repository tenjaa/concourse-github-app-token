package eu.neufeldt.concoursegithubcredentials.in;

import com.google.common.jimfs.Jimfs;
import com.google.gson.Gson;
import eu.neufeldt.concoursegithubcredentials.GithubClient;
import eu.neufeldt.concoursegithubcredentials.model.InRequest;
import eu.neufeldt.concoursegithubcredentials.model.InstallationResponse;
import eu.neufeldt.concoursegithubcredentials.model.TokenResponse;
import eu.neufeldt.concoursegithubcredentials.model.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InRequestTest {

    private final InRequest.Source source = new InRequest.Source(123, "private key", "tenjaa", null);
    private final Version version = new Version("someDate");
    private final InRequest inRequest = new InRequest(source, version);
    private final Gson gson = new Gson();
    private FileSystem fileSystem;
    private GithubClient client;

    @BeforeEach
    void setUp() {
        client = mock(GithubClient.class);
        fileSystem = Jimfs.newFileSystem();
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void test() throws InterruptedException, IOException {
        // given
        when(client.call("GET", "users/tenjaa/installation", InstallationResponse.class)).thenReturn(new InstallationResponse(456));
        when(client.call("POST", "app/installations/456/access_tokens", TokenResponse.class)).thenReturn(new TokenResponse("v1.b71be873ad96e64a84025ae7bee7694a99cb4ba9"));
        In in = new In(client, fileSystem);

        // when
        String output = in.getToken(inRequest, "destination/directory");

        // then
        assertThat(fileSystem.getPath("destination", "directory", "token")).hasContent("v1.b71be873ad96e64a84025ae7bee7694a99cb4ba9");

        assertThat(output).isEqualTo("{\"version\":{\"date\":\"someDate\"}}");
    }
}
