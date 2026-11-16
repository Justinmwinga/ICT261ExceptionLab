import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarksImporter {

    // Logger records technical errors
    private static final Logger LOGGER =
            Logger.getLogger(MarksImporter.class.getName());


    // Import student marks from a text file
    public static void importMarks(String filename) throws IOException {

        // Store student IDs that have already been imported
        Set<String> seen = new HashSet<>();

        // Automatically closes BufferedReader when finished
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filename))) {

            String line;
            int lineNumber = 0;

            // Read the file one line at a time
            while ((line = reader.readLine()) != null) {

                lineNumber++;

                try {

                    // Separate student ID and mark
                    String[] parts = line.split(",");

                    // Check that exactly two fields exist
                    if (parts.length != 2) {
                        throw new IllegalArgumentException(
                                "Each record must contain exactly two fields.");
                    }

                    String studentId = parts[0].trim();
                    String markText = parts[1].trim();

                    // Validate student ID
                    if (studentId.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Student ID cannot be empty.");
                    }

                    // Convert mark into an integer
                    int mark = Integer.parseInt(markText);

                    // Validate mark range
                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException(
                                "Mark must be between 0 and 100.");
                    }

                    // Detect duplicate student IDs
                    if (!seen.add(studentId)) {
                        throw new DuplicateStudentException(
                                "Duplicate student ID: " + studentId);
                    }

                    // Display accepted record
                    System.out.println(
                            "Accepted: " + studentId + " = " + mark);

                } catch (DuplicateStudentException e) {

                    // Log duplicate record
                    LOGGER.log(Level.WARNING,
                            "Duplicate record at line " + lineNumber, e);

                    System.out.println(
                            "Duplicate record at line " + lineNumber
                                    + ": " + e.getMessage());

                } catch (IllegalArgumentException e) {

                    // Handles NumberFormatException and invalid data
                    LOGGER.log(Level.WARNING,
                            "Invalid record at line " + lineNumber, e);

                    System.out.println(
                            "Invalid record at line " + lineNumber
                                    + ": " + e.getMessage());
                }
            }
        }
    }


    // Main method: program entry point
    public static void main(String[] args) {

        FileHandler fileHandler = null;

        try {

            // Create or append to the technical log file
            fileHandler = new FileHandler("import-errors.log", true);

            LOGGER.addHandler(fileHandler);

            // Prevent technical logs from appearing in the console
            LOGGER.setUseParentHandlers(false);

            try {

                // Use supplied filename or default to marks.txt
                String filename = args.length > 0
                        ? args[0]
                        : "marks.txt";

                importMarks(filename);

            } catch (FileNotFoundException e) {

                LOGGER.log(Level.SEVERE,
                        "Marks file not found", e);

                System.out.println(
                        "Marks file not found; check the file name.");

            } catch (IOException e) {

                LOGGER.log(Level.SEVERE,
                        "File reading failed", e);

                System.out.println(
                        "Unable to read the marks file.");
            }

        } catch (IOException e) {

            System.out.println(
                    "Unable to initialize error logging.");

        } finally {

            // Close the logging handler manually
            if (fileHandler != null) {

                LOGGER.removeHandler(fileHandler);

                fileHandler.close();
            }

            // Always display completion message
            System.out.println("Import attempt finished");
        }
    }
}

