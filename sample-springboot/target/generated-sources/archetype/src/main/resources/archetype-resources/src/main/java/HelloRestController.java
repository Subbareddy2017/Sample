#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.Calendar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRestController {

	@GetMapping("/hello")
	public String sayGreetings() {
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.AM_PM) == Calendar.PM) {
			return "Hello Good After NOON!";
		}
		return "Hello Good Morning!";
	}

}
