# PROG5121 POE - Part 1: Registration and Login Feature

## What's in this folder

```
PROG5121_Part1/
├── src/
│   ├── Login.java                 -> the class with all the checking/registering/login logic
│   └── RegistrationLoginApp.java  -> the console app you actually run (has the main method)
├── test/
│   └── LoginTest.java             -> JUnit tests using the exact test data from the brief
└── README.md                      -> this file
```

## How to open this in NetBeans

1. Open NetBeans and create a **new Java Application** project (e.g. call it `PROG5121_Part1`).
2. In the Projects panel, find the `src` folder NetBeans made for you (usually under
   `Source Packages` -> `<default package>`).
3. Copy `Login.java` and `RegistrationLoginApp.java` from this folder into that
   `<default package>` folder (right click -> paste, or just drag them in).
4. Delete the placeholder `Main.java`/class that NetBeans creates automatically, if it
   clashes with `RegistrationLoginApp`.
5. Right click `RegistrationLoginApp.java` -> **Run File** to test the console app.

## How to add and run the unit tests

1. Right click your project -> **Properties** -> **Libraries** -> **Add Library** -> choose
   **JUnit** (pick JUnit 5, also called JUnit Jupiter, if it's listed).
2. Right click the project -> **New** -> **Java Class**, but instead paste in the contents
   of `LoginTest.java` (or right click `Login.java` -> Tools -> Create/Update Tests, then
   replace the generated file's contents with `LoginTest.java`).
3. Right click `LoginTest.java` -> **Run File** to run all the tests.

If you're not sure how to do this, watch the NetBeans unit testing video linked in the
brief: https://www.youtube.com/watch?v=MOhiM2SXZI0

## What the code does

- `checkUserName(username)` – true if the username has an underscore and is 5 characters
  or fewer.
- `checkPasswordComplexity(password)` – true if the password is 8+ characters and has a
  capital letter, a number, and a special character.
- `checkCellPhoneNumber(cellPhoneNumber)` – true if the number starts with `+27` and is
  followed by up to 10 digits.
- `registerUser(...)` – runs the three checks above in order and returns the matching
  message from the brief; only saves the user's details if everything passes.
- `loginUser(username, password)` – true if the details match what was saved when the
  user registered.
- `returnLoginStatus(username, password)` – returns the welcome message on success, or
  the "incorrect" message on failure.

## Before you submit — don't forget

- [ ] Push this project to a GitHub repository with **at least 6 commits**, and submit the
      GitHub link on Arc for Part 1.
- [ ] Record an **unlisted YouTube video** (or similar) with **your own voice** (no AI
      voices) explaining your code, your logic/flow, and showing the app running.
- [ ] Double check every message your program prints matches the wording in the brief
      exactly (including punctuation) — the marker's tests check this.
