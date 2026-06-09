I want to create a simple personalized learning platform called CodeCraftHub where developers can track courses they want to learn.

Requirements:
- Use Java with Spring Boot framework
- Store course data in a simple JSON text file (NO database needed)
- No authentication or user management needed
- Focus on learning REST API basics

Each course should track:
- Course name
- Description
- Target completion date
- Current status (Not Started, In Progress, Completed)

Recommend:
1. A simple project structure for beginners
2. The REST API endpoints to be created
3. How to store and retrieve data from a JSON file


Creating the application structure:

Now, create the complete Java Spring Boot code with these requirements:

1. Create a Spring Boot REST API with all CRUD operations for courses
2. Store data in a JSON file called "courses.json"
3. Include these endpoints:
    - POST /api/courses - Add a new course
    - GET /api/courses - Get all courses
    - GET /api/courses/{id} - Get a specific course
    - GET /api/courses/stats - Get statistics (total count + count by status)
    - PUT /api/courses/{id} - Update a course
    - DELETE /api/courses/{id} - Delete a course

4. Each course must have these JSON fields:
    - id (auto-generated, starting from 1)
    - name (required)
    - description (required)
    - target_date (required in JSON requests, format YYYY-MM-DD)
    - status (required, must be exactly one of: "Not Started", "In Progress", or "Completed")
    - created_at (auto-generated timestamp in JSON responses)

   In the Java model, you may use camelCase field names such as `targetDate` and `createdAt` with Jackson annotations to map them to the JSON field names.

5. Include proper error handling for:
    - Missing required fields
    - Course not found
    - Invalid status values
    - File read/write errors

6. Add helpful comments throughout the code for beginners
7. Make sure the app creates courses.json automatically if it doesn't exist
8. Use Jackson for JSON processing

Please provide:
- Course.java (model class)
- CourseService.java (service layer with file operations)
- CourseController.java (REST controller)
- CodeCraftHubApplication.java (main class)
- pom.xml (Maven dependencies)


#Readme md : 

Create a README.md file for the CodeCraftHub project that includes:
1. Project overview and features
2. Installation instructions
3. How to run the application
4. API endpoint documentation with examples
5. Troubleshooting guide





