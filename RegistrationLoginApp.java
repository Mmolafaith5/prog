import java.util.Scanner;

/**
 * PROG5121 - Part 1: Registration and Login Feature
 *
 * This is the console application. It asks the user for their
 * details, tries to register them, and then lets them try to log in.
 *
 * This is a console app only - no GUI and no JOptionPane, as required
 * by the assignment.
 */
public class RegistrationLoginApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=== Welcome to the Registration and Login App ===");
        System.out.println();

        // ---------- REGISTRATION ----------
        System.out.println("Please enter your first name:");
        String firstName = scanner.nextLine();

        System.out.println("Please enter your last name:");
        String lastName = scanner.nextLine();

        System.out.println("Please enter a username (must contain an underscore and be no more than 5 characters):");
        String username = scanner.nextLine();

        System.out.println("Please enter a password (at least 8 characters, a capital letter, a number, and a special character):");
        String password = scanner.nextLine();

        System.out.println("Please enter your South African cell phone number (e.g. +27831234567):");
        String cellPhoneNumber = scanner.nextLine();

        // Try to register the user with the details they entered.
        String registrationMessage = login.registerUser(username, password, cellPhoneNumber, firstName, lastName);
        System.out.println();
        System.out.println(registrationMessage);
        System.out.println();

        // Only let the user try to log in if registration was successful.
        boolean registeredSuccessfully = registrationMessage.contains("registered successfully");

        if (registeredSuccessfully) {
            // ---------- LOGIN ----------
            System.out.println("=== Please log in ===");

            System.out.println("Enter your username:");
            String loginUsername = scanner.nextLine();

            System.out.println("Enter your password:");
            String loginPassword = scanner.nextLine();

            String loginStatusMessage = login.returnLoginStatus(loginUsername, loginPassword);
            System.out.println();
            System.out.println(loginStatusMessage);
        } else {
            System.out.println("Registration was not successful, so login was not attempted.");
        }

        scanner.close();
    }
}
