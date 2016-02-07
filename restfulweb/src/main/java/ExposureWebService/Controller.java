package ExposureWebService;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private int i = 15;

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="ControllerGreeting") String name) {
    	test();
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    @RequestMapping("/probing")
    public Probe probing(@RequestParam(value="name", defaultValue="ControllerProbing") String name) {
        return new Probe(counter.incrementAndGet(),
                            String.format(template, name));
    }
    @RequestMapping("/list")
    public GreetingList list(@RequestParam(value="name", defaultValue="ControllerList") String name) {
        return new GreetingList(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
    private void test() {
    	System.out.println("yay test");
    	System.out.println(i);
    }
}


