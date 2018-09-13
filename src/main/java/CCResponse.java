import java.util.List;

public class CCResponse {
	public String Response;

	public List<Data> Data;
}

class Data {
	public long time;
	public double close, high, low, open, volumefrom, volumeto;
}
