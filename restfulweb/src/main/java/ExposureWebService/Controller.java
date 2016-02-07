package ExposureWebService;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
public class Controller {

	private static DriverManagerDataSource dataSource;
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private int i = 15;
    
    static {
    	dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dataSource.setUrl("jdbc:sqlserver://vyl5xz64ek.database.windows.net;database=Exposure");
		dataSource.setUsername("kekonat@vyl5xz64ek.database.windows.net");
		dataSource.setPassword("N0REGRETs");
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(String.format(template, name));
    }
    
    @RequestMapping("/addUser")
    public User addUser(@RequestParam(value="name", defaultValue="World") String name) {
    	JdbcTemplate insert = new JdbcTemplate(dataSource);
    	insert.update("INSERT INTO USER VALUES('testname', 'kekona');");
    	return new User();
    }
}


