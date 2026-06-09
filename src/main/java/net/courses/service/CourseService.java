package net.courses.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.courses.model.Course;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service layer - contains all business logic and handles reading/writing the JSON file.
 * Controllers call this service; the service never talks directly to controllers.
 */
@Service
public class CourseService {

    // Injected from application.properties (courses.file.path)
    @Value("${courses.file.path:courses.json}")
    private String filePath;

    // The only allowed status values
    private static final List<String> VALID_STATUSES = List.of("Not Started", "In Progress", "Completed");

    // Jackson ObjectMapper converts between Java objects and JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ──────────────────────────────────────────────
    // Public CRUD methods called by the controller
    // ──────────────────────────────────────────────

    /** Returns every course stored in the JSON file. */
    public List<Course> getAllCourses() {
        return loadCourses();
    }

    /** Finds a course by its ID; returns empty Optional if not found. */
    public Optional<Course> getCourseById(Long id) {
        return loadCourses().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    /** Validates, assigns an auto-generated ID and timestamp, then saves the new course. */
    public Course createCourse(Course course) {
        validateCourse(course);

        List<Course> courses = loadCourses();

        // Generate next ID: find the highest existing ID and add 1 (or start at 1)
        Long newId = courses.stream()
                .mapToLong(Course::getId)
                .max()
                .orElse(0L) + 1;

        course.setId(newId);
        course.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        courses.add(course);
        saveCourses(courses);
        return course;
    }

    /** Updates all fields of an existing course; preserves the original created_at timestamp. */
    public Optional<Course> updateCourse(Long id, Course updatedCourse) {
        validateCourse(updatedCourse);

        List<Course> courses = loadCourses();
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(id)) {
                updatedCourse.setId(id);
                // Keep the original creation time - it shouldn't change on update
                updatedCourse.setCreatedAt(courses.get(i).getCreatedAt());
                courses.set(i, updatedCourse);
                saveCourses(courses);
                return Optional.of(updatedCourse);
            }
        }
        return Optional.empty(); // Course with that ID doesn't exist
    }

    /** Returns total course count and a breakdown count per status. */
    public Map<String, Object> getStats() {
        List<Course> courses = loadCourses();

        Map<String, Long> byStatus = new java.util.LinkedHashMap<>();
        for (String status : VALID_STATUSES) {
            byStatus.put(status, courses.stream().filter(c -> status.equals(c.getStatus())).count());
        }

        Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("total", (long) courses.size());
        stats.put("by_status", byStatus);
        return stats;
    }

    /** Removes a course by ID; returns true if deleted, false if it wasn't found. */
    public boolean deleteCourse(Long id) {
        List<Course> courses = loadCourses();
        boolean removed = courses.removeIf(c -> c.getId().equals(id));
        if (removed) {
            saveCourses(courses);
        }
        return removed;
    }

    // ──────────────────────────────────────────────
    // Private helpers for file I/O and validation
    // ──────────────────────────────────────────────

    /**
     * Reads courses.json and deserializes it into a List<Course>.
     * If the file doesn't exist yet, returns an empty list (file is created on first save).
     */
    private List<Course> loadCourses() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<Course>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error reading " + filePath + ": " + e.getMessage(), e);
        }
    }

    /**
     * Serializes the list to pretty-printed JSON and writes it to courses.json.
     * The file is created automatically if it does not exist.
     */
    private void saveCourses(List<Course> courses) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filePath), courses);
        } catch (IOException e) {
            throw new RuntimeException("Error writing " + filePath + ": " + e.getMessage(), e);
        }
    }

    /**
     * Checks that all required fields are present, target_date is YYYY-MM-DD format,
     * and the status is one of the allowed values.
     * Throws IllegalArgumentException (caught by the controller) if validation fails.
     */
    private void validateCourse(Course course) {
        if (course.getName() == null || course.getName().isBlank()) {
            throw new IllegalArgumentException("Field 'name' is required and cannot be blank.");
        }
        if (course.getDescription() == null || course.getDescription().isBlank()) {
            throw new IllegalArgumentException("Field 'description' is required and cannot be blank.");
        }
        if (course.getTargetDate() == null || course.getTargetDate().isBlank()) {
            throw new IllegalArgumentException("Field 'target_date' is required (format: YYYY-MM-DD).");
        }
        // Validate that target_date strictly follows YYYY-MM-DD format using regex + parse
        if (!course.getTargetDate().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException(
                    "Field 'target_date' must be in YYYY-MM-DD format (e.g. 2025-12-31). Got: " + course.getTargetDate());
        }
        try {
            LocalDate.parse(course.getTargetDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Field 'target_date' is not a valid date (e.g. 2025-12-31). Got: " + course.getTargetDate());
        }
        if (course.getStatus() == null || course.getStatus().isBlank()) {
            throw new IllegalArgumentException("Field 'status' is required.");
        }
        if (!VALID_STATUSES.contains(course.getStatus())) {
            throw new IllegalArgumentException(
                    "Invalid status '" + course.getStatus() + "'. Must be one of: " + VALID_STATUSES);
        }
    }
}
