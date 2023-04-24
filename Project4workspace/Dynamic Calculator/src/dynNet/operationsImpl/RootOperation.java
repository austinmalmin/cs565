package dynNet.operationsImpl;

import dynNet.dynCalculator.Operation;



/**
 * Class [AdditionOperation]
 * <p>
 * This is a concrete operation class, that implements the
 * interface <code>Operation</code>.
 *
 * @author Prof. Dr.-Ing. Wolf-Dieter Otte
 * @version May 20002
 */
public class RootOperation implements Operation{
	
	public float calculate(float firstNumber, float secondNumber){
			 return (float) Math.pow(firstNumber, 1.0/secondNumber);
	}
}

   