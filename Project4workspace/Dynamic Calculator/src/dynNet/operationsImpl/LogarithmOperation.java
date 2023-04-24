package dynNet.operationsImpl;

import dynNet.dynCalculator.Operation;

public class LogarithmOperation implements Operation{
	
	public float calculate(float firstNumber, float secondNumber){
			return (float) (Math.log(firstNumber) / Math.log(secondNumber));
	}
}