package org.springframework.samples.petclinic.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.io.IOException;
import java.util.concurrent.TimeUnit; // <-- Add this import!

/**
 * Controller used to showcase different types of exceptions and scenarios.
 *
 * @author Michael Isvy
 * <p/>
 * Also see how a view that resolves to "error" has been added ("error.html").
 */
@Controller
class CrashController {

    // --- Deadlock Scenario Locks ---
    // These objects will be used as locks for our deadlock simulation
    private final Object lockA = new Object();
    private final Object lockB = new Object();

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
        // You'll likely need to revert this to the "broken" (infinite recursion) version for the AI test:
        // return recursiveMethod(0);
        // Or if you want it to always cause StackOverflow:
        // recursiveMethod(0); // This will cause the error
        // return "error"; // This line is generally not reached
        
        // For the AI test of the StackOverflow, you want it to actually overflow.
        // So revert to:
        return recursiveMethodForSOE(0);
    }

    // Helper method for StackOverflowError, intentionally without base case for the demo
    private String recursiveMethodForSOE(int count) {
        // This method calls itself indefinitely, leading to StackOverflowError
        return recursiveMethodForSOE(count + 1);
    }


    /**
     * Simulates one part of a potential deadlock scenario.
     * Accessible via /deadlock/one
     * A thread hitting this endpoint will try to acquire Lock A, then Lock B.
     */
    @GetMapping("/deadlock/one")
    public String triggerDeadlockScenarioOne() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " trying to acquire lockA (scenario one)...");
        synchronized (lockA) { // Acquire lockA
            System.out.println(Thread.currentThread().getName() + " acquired lockA (scenario one). Sleeping...");
            TimeUnit.SECONDS.sleep(2); // Simulate work while holding lockA

            System.out.println(Thread.currentThread().getName() + " trying to acquire lockB (scenario one)...");
            synchronized (lockB) { // Try to acquire lockB
                System.out.println(Thread.currentThread().getName() + " acquired lockB (scenario one). Releasing locks.");
            }
        }
        System.out.println(Thread.currentThread().getName() + " released all locks (scenario one).");
        return "error"; // Return an error page for consistent behavior
    }

    /**
     * Simulates the other part of a potential deadlock scenario.
     * Accessible via /deadlock/two
     * A thread hitting this endpoint will try to acquire Lock B, then Lock A.
     */
    @GetMapping("/deadlock/two")
    public String triggerDeadlockScenarioTwo() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " trying to acquire lockB (scenario two)...");
        synchronized (lockB) { // Acquire lockB
            System.out.println(Thread.currentThread().getName() + " acquired lockB (scenario two). Sleeping...");
            TimeUnit.SECONDS.sleep(2); // Simulate work while holding lockB

            System.out.println(Thread.currentThread().getName() + " trying to acquire lockA (scenario two)...");
            synchronized (lockA) { // Try to acquire lockA
                System.out.println(Thread.currentThread().getName() + " acquired lockA (scenario two). Releasing locks.");
            }
        }
        System.out.println(Thread.currentThread().getName() + " released all locks (scenario two).");
        return "error"; // Return an error page for consistent behavior
    }
}