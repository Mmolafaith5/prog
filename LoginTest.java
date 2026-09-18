import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PROG5121 - Part 1: Unit Tests for the Login class.
 *
 * These tests use the exact test data given in the assignment brief,
 * so that the marker can check the methods return the correct results.
 *
 * NOTE: In NetBeans, add the JUnit 5 library to the project
 * (right click project -> Properties -> Libraries -> Add Library -> JUnit)
 * before running these tests.
 */
public class LoginTest {

    private Login login;

    @BeforeEach
    public void setUp() {
        // A fresh Login object before every test, so tests don't affect each other.
        login = new Login();
    }

    // ---------- Tests for checkUserName ----------

    @Test
    public void testUserName_correctlyFormatted() {
        // "kyl_1" contains an underscore and is 5 characters long.
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testUserName_incorrectlyFormatted() {
        // "kyle!!!!!" does not contain an underscore.
        assertFalse(login.checkUserName("kyle!!!!!"));
    }

    // ---------- Tests for checkPasswordComplexity ----------

    @Test
    public void testPassword_meetsComplexityRules() {
        // "Ch&&sec@ke99!" has 8+ characters, a capital letter, a number and special characters.
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testPassword_doesNotMeetComplexityRules() {
        // "password" has no capital letter, no number and no special character.
        assertFalse(login.checkPasswordComplexity("password"));
    }

    // ---------- Tests for checkCellPhoneNumber ----------

    @Test
    public void testCellPhoneNumber_correctlyFormatted() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCellPhoneNumber_incorrectlyFormatted() {
        // No + and no country code.
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    // ---------- Tests for registerUser (assertEquals on the returned message) ----------

    @Test
    public void testRegisterUser_usernameIncorrectlyFormatted() {
        String result = login.registerUser("kyle!!!!!", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.", result);
    }

    @Test
    public void testRegisterUser_passwordIncorrectlyFormatted() {
        String result = login.registerUser("kyl_1", "password", "+27838968976", "Kyle", "Smith");
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.", result);
    }

    @Test
    public void testRegisterUser_allDetailsCorrect() {
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(result.contains("registered successfully"));
    }

    // ---------- Tests for loginUser (assertTrue / assertFalse) ----------

    @Test
    public void testLoginUser_successful() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginUser_failed() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertFalse(login.loginUser("kyl_1", "WrongPassword1!"));
    }

    // ---------- Tests for returnLoginStatus (assertEquals on the returned message) ----------

    @Test
    public void testReturnLoginStatus_successful() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        String result = login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle, Smith, it is great to see you again.", result);
    }

    @Test
    public void testReturnLoginStatus_failed() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        String result = login.returnLoginStatus("kyl_1", "WrongPassword1!");
        assertEquals("Username or password incorrect, please try again.", result);
    }
}
