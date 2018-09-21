public class BNCandleResult {
	public String stream;
	public BNCandleData data; 
}

class BNCandleData {
	public long E;
	public String e, s;
	public BNKlineData k;
}

class BNKlineData {
	public long t, T;
	public String s, f, L, o, c, h, l, v, n, i;
	public boolean x;
}
