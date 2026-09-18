/**
 * PROG5121 - Part 1: Registration and Login Feature
 *
 * This class stores one user's details and checks whether the
 * username, password and cell phone number are entered correctly.
 * It also handles registering the user and logging the user in.
 *
 * I kept the code simple and used lots of comments so that it is
 * easy to follow, since this is a first year project.
 */
public class Login {

    // These are the details of the user that will be stored once
    // they register successfully.
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // Empty constructor - used before the user has registered.
    public Login() {
    }

    // Constructor that lets us create a Login object with all the details at once.
    public Login(String username, String password, String cellPhoneNumber, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // ---------- Simple getters (used mostly for testing / printing) ----------
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Checks that the username:
     * - contains an underscore ( _ )
     * - is no more than five characters long
     *
     * @param username the username to check
     * @return true if the username is correctly formatted, false if not
     */
    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        boolean containsUnderscore = username.contains("_");
        boolean correctLength = username.length() <= 5;
        return containsUnderscore && correctLength;
    }

    /**
     * Checks that the password meets ALL of the following rules:
     * - at least eight characters long
     * - contains a capital letter
     * - contains a number
     * - contains a special character
     *
     * @param password the password to check
     * @return true if the password meets all the rules, false if not
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            return false;
        }

        boolean longEnough = password.length() >= 8;
        boolean hasCapitalLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        // Go through the password one character at a time and check what it contains.
        for (int i = 0; i < password.length(); i++) {
            char currentChar = password.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                hasCapitalLetter = true;
            } else if (Character.isDigit(currentChar)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(currentChar)) {
                // Anything that is not a letter and not a digit counts as a special character.
                hasSpecialCharacter = true;
            }
        }

        return longEnough && hasCapitalLetter && hasNumber && hasSpecialCharacter;
    }

    /**
     * Checks that the South African cell phone number:
     * - starts with the international country code (+27)
     * - is followed by a number that is no more than ten characters long
     *
     * Example of a correct number: +27838968976
     *
     * @param cellPhoneNumber the cell phone number to check
     * @return true if the number is correctly formatted, false if not
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) {
            return false;
        }
        // ^\+27      -> must start with the South African country code
        // \d{1,10}$  -> followed by 1 to 10 digits, and nothing else
        String regex = "^\\+27\\d{1,10}$";
        return cellPhoneNumber.matches(regex);
    }

    /**
     * Registers a new user by checking the username, password and cell
     * phone number, and returns a message explaining what happened.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @param cellPhoneNumber the cell phone number entered by the user
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @return a message describing whether registration worked or not
     */
    public String registerUser(String username, String password, String cellPhoneNumber, String firstName, String lastName) {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        // If we get here, everything passed, so we save the user's details.
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;

        return "Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.\nUser " + username + " has been registered successfully.";
    }

    /**
     * Checks whether the username and password entered at login match
     * the username and password that were stored when the user registered.
     *
     * @param enteredUsername the username typed in at login
     * @param enteredPassword the password typed in at login
     * @return true if both match, false if not
     */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (username == null || password == null) {
            // Nobody has registered yet, so login cannot succeed.
            return false;
        }
        return username.equals(enteredUsername) && password.equals(enteredPassword);
    }

    /**
     * Returns the correct message to show the user after they try to log in.
     *
     * @param enteredUsername the username typed in at login
     * @param enteredPassword the password typed in at login
     * @return a welcome message if login worked, or an error message if not
     */
    public String returnLoginStatus(String enteredUsername, String enteredPassword) {
        if (loginUser(enteredUsername, enteredPassword)) {
            return "Welcome " + firstName + ", " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
