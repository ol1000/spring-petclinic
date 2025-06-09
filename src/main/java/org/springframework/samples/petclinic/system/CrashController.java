package org.springframework.samples.petclinic.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import PathVariable

/**
 * Controller used to showcase what happens when an exception is thrown
 *
 * @author Michael Isvy
 * <p/>
 * Also see how a view that resolves to "error" has been added ("error.html").
 */
@Controller
class CrashController {

    // Existing "boring" exception, good for basic testing
    @GetMapping("/oups")
    public String triggerRuntimeException() {
        throw new RuntimeException(
                "Expected: controller used to showcase what " + "happens when an exception is thrown");
    }

    // New: Simulating a NullPointerException
    @GetMapping("/causeNPE/{param}") // Endpoint now expects a parameter
    public String triggerNullPointerException(@PathVariable String param) {
        String data = null; // Intentionally make this null

        if ("trigger".equals(param)) { // Only trigger NPE if "trigger" is in the path
            System.out.println(data.length()); // This line will throw NullPointerException
        } else if ("simulateDbIssue".equals(param)) {
            // Option to trigger a custom exception for a database issue
            throw new CustomDatabaseException("Failed to connect to the database (simulated)");
        }
        
        return "welcome"; // Return a view if no error is triggered
    }

    // Optional: Define a custom exception for more specific error types
    static class CustomDatabaseException extends RuntimeException {
        public CustomDatabaseException(String message) {
            super(message);
        }
    }
}