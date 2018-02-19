package util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** Tuple holding two values of Generic Types */
@AllArgsConstructor public class Tuple<GenericA, GenericB> {
	
	/** The A value of the Tuple */
	@Setter @Getter private GenericA a;
	/** The B value of the Tuple */
	@Setter @Getter private GenericB b;
	
	/** Returns deep clone of this Tuple */
	public Tuple<GenericA, GenericB> clone(){
		return new Tuple<GenericA, GenericB>(a, b);
	}
	
	/** Returns String of this Tuple */
	public String toString(){
		return "["+a+"|"+b+"]";
	}
	
}
