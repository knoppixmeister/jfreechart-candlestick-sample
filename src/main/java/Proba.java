import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class Proba {
	public static void main(String[] args) {
		//1536788114784
		
		DateTime dt = new DateTime(Long.parseLong("1536788114784"));
		
		System.out.println(		DateTimeFormat.forPattern("dd-MM-YYYY HH:mm").print(dt)	);
	}
}
