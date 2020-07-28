package eu.neufeldt.concoursegithubcredentials;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

    private final InputStream stdinDefault = System.in;
    private final PrintStream stdoutDefault = System.out;
    private ByteArrayInputStream stdin;
    private ByteArrayOutputStream stdout;

    @BeforeEach
    void setUp() {
        stdout = new ByteArrayOutputStream();
        stdin = new ByteArrayInputStream("{}".getBytes());
        System.setIn(stdin);
        System.setOut(new PrintStream(stdout));
    }

    @AfterEach
    void tearDown() {
        System.setOut(stdoutDefault);
        System.setIn(stdinDefault);
    }

    @Test
    void printOutputToStdout() throws InterruptedException, GeneralSecurityException, IOException {
        // when
        Main.main(new String[]{"CHECK"});

        // then
        assertThat(stdout.toString()).isEqualTo("[]");
    }
}
