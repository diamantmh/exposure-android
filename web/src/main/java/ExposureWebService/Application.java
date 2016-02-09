package ExposureWebService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("Type 'test' to continue");
    	String line = scanner.nextLine();
    	while (!line.equals("test")) {
    		System.out.println("You typed: " + line);
    		System.out.println("Please type 'test'!");
    		line = scanner.nextLine();
    	}
        SpringApplication.run(Application.class, args);
        System.out.println("Now im after the spring application run thing");
        System.out.println("Type 'q' to quit");
        while (true) {
        	line = scanner.nextLine();
        	if (line.equals("q")) {
        		System.out.println("Goodbye!");
        		System.exit(0);
        	} else {
        		System.out.println("You typed: " + line);
        	}
        }
    }
}


