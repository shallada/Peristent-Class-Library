package dictionary;

public class Dog {
	public double weight;
	public int age;
	
	public Dog() {
		
	}
	
	public Dog(double weight, int age) {
		this.weight = weight;
		this.age = age;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Dog && weight == ((Dog)o).weight && age == ((Dog)o).age;
	}
	
	@Override
	public int hashCode() {
		return (int) (age * 117 + weight * 31);
		
	}
}
