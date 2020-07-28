package eu.neufeldt.concoursegithubcredentials.out;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OutTest {

    @Test
    void producesCurrentTimeAsNewVersion() {
        // given
        Instant now = Instant.ofEpochSecond(1595785039);
        Out out = new Out(now);

        // when
        String check = out.check();

        // then
        assertThat(check).isEqualTo("{\"date\":\"2020-07-26T17:37:19Z\"}");
    }
}
