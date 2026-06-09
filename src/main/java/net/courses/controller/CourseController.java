package net.courses.controller;

import net.courses.model.Course;
import net.courses.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller - maps HTTP requests to service methods and returns appropriate responses.
 *
 * All endpoints are under the base path /api/courses.
 * ResponseEntity lets us control both the response body and the HTTP status code.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    // Spring automatically injects CourseService here (constructor injection)
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ──────────────────────────────────────────────
    // GET /api/courses/stats
    // Returns total count and breakdown by status.
    // NOTE: must be declared BEFORE /{id} so Spring
    // does not try to parse "stats" as a Long id.
    // ──────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCourseStats() {
        return ResponseEntity.ok(courseService.getStats());
    }

    // ──────────────────────────────────────────────
    // GET /api/courses
    // Returns all courses (empty list if none exist)
    // ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // ──────────────────────────────────────────────
    // GET /api/courses/{id}
    // Returns a single course or 404 if not found
    // ──────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourseById(id);
        if (course.isPresent()) {
            return ResponseEntity.ok(course.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Course not found with id: " + id));
    }

    // ──────────────────────────────────────────────
    // POST /api/courses
    // Creates a new course; returns 201 Created on success
    // or 400 Bad Request if validation fails
    // ──────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        try {
            Course created = courseService.createCourse(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            // Validation errors from CourseService.validateCourse()
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ──────────────────────────────────────────────
    // PUT /api/courses/{id}
    // Replaces all fields of an existing course
    // ──────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            Optional<Course> updated = courseService.updateCourse(id, course);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Course not found with id: " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ──────────────────────────────────────────────
    // DELETE /api/courses/{id}
    // Deletes a course; returns 200 on success or 404 if not found
    // ──────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Course with id " + id + " deleted successfully."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Course not found with id: " + id));
    }

    // ──────────────────────────────────────────────
    // Catches unexpected runtime errors (e.g., file I/O failures)
    // and returns a clean 500 response instead of a stack trace
    // ──────────────────────────────────────────────
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error: " + e.getMessage()));
    }
}
