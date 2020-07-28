package eu.neufeldt.concoursegithubcredentials.check;

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
        String versions = check.check("{}");

        // then
        assertThat(versions).isEqualTo("[]");
    }

    @Test
    void returnGivenDateAsCurrentVersion() {
        // when
        String versions = check.check("{\"version\":{\"date\":\"someDate\"}}");

        // then
        assertThat(versions).isEqualTo("[{\"date\":\"someDate\"}]");
    }
}
