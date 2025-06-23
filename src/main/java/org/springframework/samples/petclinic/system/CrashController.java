package org.springframework.samples.petclinic.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.io.IOException;

/**
 * Controller used to showcase different types of exceptions and scenarios.
 *
 * @author Michael Isvy
 * <p/>
 * Also see how a view that resolves to "error" has been added ("error.html").
 */
@Controller
class CrashController {

    /**
     * Triggers a generic RuntimeException.
     * Accessible via /oups
     */
    @GetMapping("/oups")
    public String triggerGenericException() {
        throw new RuntimeException("This is a generic runtime exception. Something unexpected went wrong!");
    }

    /**
     * Triggers a NullPointerException, simulating accessing an uninitialized object.
     * Accessible via /null-pointer
     */
    @GetMapping("/null-pointer")
    public String triggerNullPointerException() {
        String data = null;
        // This will throw a NullPointerException when trying to call a method on 'null'
        data.length();
        return "error"; // This line won't be reached
    }

    /**
     * Triggers an IllegalArgumentException, simulating invalid input.
     * Accessible via /invalid-arg
     */
    @GetMapping("/invalid-arg")
    public String triggerIllegalArgumentException() {
        validateInput(-5); // Example of invalid input
        return "error"; // This line won't be reached if exception is thrown
    }

    private void validateInput(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Input value cannot be negative!");
        }
        // Proceed with logic if valid
    }

    /**
     * Triggers an ArrayIndexOutOfBoundsException, common when working with arrays/lists.
     * Accessible via /out-of-bounds
     */
    @GetMapping("/out-of-bounds")
    public String triggerArrayIndexOutOfBoundsException() {
        int[] numbers = {1, 2, 3};
        // Trying to access an index that doesn't exist
        System.out.println(numbers[5]);
        return "error"; // This line won't be reached
    }

    /**
     * Triggers a custom exception with a specific HTTP status code.
     * This demonstrates how to map custom exceptions to HTTP responses.
     * Accessible via /forbidden-access
     */
    @GetMapping("/forbidden-access")
    public String triggerForbiddenAccessException() {
        throw new ForbiddenAccessSimulationException("You do not have permission to access this resource!");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN) // Maps this exception to a 403 Forbidden status
    private static class ForbiddenAccessSimulationException extends RuntimeException {
        public ForbiddenAccessSimulationException(String message) {
            super(message);
        }
    }

    /**
     * Simulates an IOException that might occur during file operations or network calls.
     * Note: IOException is a checked exception, so it must be declared or caught.
     * Accessible via /io-error
     */
    @GetMapping("/io-error")
    public String triggerIOException() throws IOException {
        simulateFileReadError(); // This method might throw an IOException
        return "error";
    }

    private void simulateFileReadError() throws IOException {
        // In a real application, this might be a file not found or permission issue
        throw new IOException("Failed to read data from simulated file. Permission denied or file corrupt.");
    }

    /**
     * Triggers a NumberFormatException when trying to parse an invalid number from a path variable.
     * Accessible via /parse-error/{value} (e.g., /parse-error/abc)
     */
    @GetMapping("/parse-error/{value}")
    public String triggerNumberFormatException(@PathVariable String value) {
        Integer.parseInt(value); // This will throw NumberFormatException if 'value' is not a valid integer
        return "error"; // This line won't be reached
    }

    /**
     * Triggers a StackOverflowError by calling itself recursively without a base case.
     * This simulates an infinite recursion scenario, consuming the call stack.
     * Accessible via /stack-overflow
     */
    @GetMapping("/stack-overflow")
    public String triggerStackOverflowError() {
        return recursiveMethod(0); // Start the infinite recursion
    }

    private String recursiveMethod(int count) {
        // This method calls itself indefinitely.
        // It's designed to not have a base case to stop the recursion,
        // which will eventually lead to a StackOverflowError as the call stack fills up.
        return recursiveMethod(count + 1);
    }
}