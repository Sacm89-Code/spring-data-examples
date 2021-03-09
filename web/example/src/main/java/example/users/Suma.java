package example.users;

public class Suma {
	Integer sumando1;
	Integer sumando2;
	
	public Suma(Integer sumando1, Integer sumando2) {
		this.sumando1 = sumando1;
		this.sumando2 = sumando2;
	}
	
	public Integer suma() {
		return this.sumando1 + this.sumando2;
	}
	

}
