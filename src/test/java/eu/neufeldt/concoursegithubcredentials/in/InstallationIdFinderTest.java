package eu.neufeldt.concoursegithubcredentials.in;

import eu.neufeldt.concoursegithubcredentials.GithubClient;
import eu.neufeldt.concoursegithubcredentials.model.InstallationResponse;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class InstallationIdFinderTest {

    private GithubClient client;
    private InstallationIdFinder finder;

    private static Stream<Arguments> invalidParamsSource() {
        return Stream.of(
                Arguments.arguments(null, null),
                Arguments.arguments("user", "org")
        );
    }

    @BeforeEach
    void setUp() {
        client = Mockito.mock(GithubClient.class);
        finder = new InstallationIdFinder(client);

    }

    @Test
    void byUser() throws IOException, InterruptedException {
        // given
        when(client.call("GET", "users/tenjaa/installation", InstallationResponse.class)).thenReturn(new InstallationResponse(456));

        // when
        int installationId = finder.find("tenjaa", null);

        // then
        assertThat(installationId).isEqualTo(456);
    }

    @Test
    void byOrg() throws IOException, InterruptedException {
        // given
        when(client.call("GET", "orgs/my-org/installation", InstallationResponse.class)).thenReturn(new InstallationResponse(456));

        // when
        int installationId = finder.find(null, "my-org");

        // then
        assertThat(installationId).isEqualTo(456);
    }

    @MethodSource("invalidParamsSource")
    @ParameterizedTest
    void invalidParams(String user, String org) throws IOException, InterruptedException {
        // when
        var throwableAssert = assertThatThrownBy(() -> finder.find(user, org));

        // then
        throwableAssert
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Specify either user or org");
    }
}