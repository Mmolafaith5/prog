import java.util.Scanner;

/**
 * Part 1: registration and login. Reused by QuickChat (Part 2) so that
 * messages can only be sent after a successful login.
 */
public class Login {
    private static final int MAX_USERS = 1;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final String CELLPHONE_REGEX = "0\\d{9}";

    private final String[] usernames = new String[MAX_USERS];
    private final String[] passwords = new String[MAX_USERS];
    private final String[] cellphones = new String[MAX_USERS];
    private int registeredCount = 0;

    /** Registers the users. */
    public void registerUsers(Scanner input) {
        for (int i = 0; i < MAX_USERS; i++) {
            System.out.println("Register User " + (i + 1));

            String username = readNonEmptyInput(input, "Enter username: ");
            while (isUsernameTaken(username)) {
                System.out.println("That username is already taken. Please choose another.");
                username = readNonEmptyInput(input, "Enter username: ");
            }

            String password = readNonEmptyInput(input, "Enter password: ");

            String cellphone = readCellphone(input);
            while (isCellphoneTaken(cellphone)) {
                System.out.println("That cellphone number is already registered. Please enter another.");
                cellphone = readCellphone(input);
            }

            usernames[registeredCount] = username;
            passwords[registeredCount] = password;
            cellphones[registeredCount] = cellphone;
            registeredCount++;
            System.out.println();
        }
    }

    /** Allows up to 3 attempts. Returns true only if login succeeded. */
    public boolean loginUser(Scanner input) {
        System.out.println("Login");
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            String identifier = readNonEmptyInput(input, "Enter username or cellphone number: ");
            String password = readNonEmptyInput(input, "Enter password: ");

            if (authenticate(identifier, password)) {
                System.out.println("Login successful.\n");
                return true;
            }
            int remaining = MAX_LOGIN_ATTEMPTS - attempt;
            if (remaining > 0) {
                System.out.println("Invalid credentials. Attempts remaining: " + remaining);
            }
        }
        System.out.println("Invalid credentials. Account locked.");
        return false;
    }

    public boolean authenticate(String identifier, String password) {
        for (int i = 0; i < registeredCount; i++) {
            boolean idMatches = identifier.equals(usernames[i]) || identifier.equals(cellphones[i]);
            if (idMatches && password.equals(passwords[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isUsernameTaken(String username) {
        for (int i = 0; i < registeredCount; i++) {
            if (username.equals(usernames[i])) return true;
        }
        return false;
    }

    private boolean isCellphoneTaken(String cellphone) {
        for (int i = 0; i < registeredCount; i++) {
            if (cellphone.equals(cellphones[i])) return true;
        }
        return false;
    }

    private static String readNonEmptyInput(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private static String readCellphone(Scanner input) {
        while (true) {
            System.out.print("Enter cellphone number (10 digits, e.g. 0821234567): ");
            String value = input.nextLine().trim();
            if (value.matches(CELLPHONE_REGEX)) return value;
            System.out.println("Invalid cellphone number. It must be 10 digits starting with 0.");
        }
    }
}
