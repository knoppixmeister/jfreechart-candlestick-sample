
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Proba {
	public static void main(String[] args) {
		
		
		DateTime dt = new DateTime(2018, 9, 29, 1, 54);

		
	    System.out.println("DT: "+(dt.getMillis())+"; SYS: "+System.currentTimeMillis());
	}
}
