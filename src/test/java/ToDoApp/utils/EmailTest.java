package ToDoApp.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateEmailWhenValid() {
        assertDoesNotThrow(() -> new Email("user@mail.com"));
        assertDoesNotThrow(() -> new Email("test.123@mail.org"));
        assertDoesNotThrow(() -> new Email("a@mail.pl"));
    }

    @Test
    void shouldThrowExceptionWhenInvalid() {
        assertThrows(InvalidEmailAdressException.class, () -> new Email("user"));
        assertThrows(InvalidEmailAdressException.class, () -> new Email("user.mail"));
    }

}