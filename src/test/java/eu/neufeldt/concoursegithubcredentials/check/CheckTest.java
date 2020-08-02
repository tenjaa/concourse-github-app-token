package eu.neufeldt.concoursegithubcredentials.check;

import eu.neufeldt.concoursegithubcredentials.model.Version;
import eu.neufeldt.concoursegithubcredentials.model.VersionWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CheckTest {

    private Check check;

    @BeforeEach
    void setUp() {
        check = new Check();
    }

    @Test
    void doNotCreateInitialVersion() {
        // when
        String versions = check.check(null);

        // then
        assertThat(versions).isEqualTo("[]");
    }

    @Test
    void doNotCreateInitialVersion2() {
        // when
        String versions = check.check(new VersionWrapper(null));

        // then
        assertThat(versions).isEqualTo("[]");
    }

    @Test
    void returnGivenDateAsCurrentVersion() {
        // when
        String versions = check.check(new VersionWrapper(new Version("someDate")));

        // then
        assertThat(versions).isEqualTo("[{\"date\":\"someDate\"}]");
    }
}
