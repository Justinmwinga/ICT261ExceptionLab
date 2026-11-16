# ICT261 – Unit 4: Java Exception Handling

## Project
ExceptionLab

## Requirements
- Java 11+
- Java compiler (JDK)

## Files
- MarksImporter.java
- DuplicateStudentException.java
- marks.txt

## Recovery Strategy
Invalid records are reported and skipped.
The importer continues processing valid records.

## Testing
T1: Valid mark
T2: Invalid numeric value
T3: Duplicate student ID
T4: Out-of-range mark
T5: Missing file