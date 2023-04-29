package appserver.job.impl;

import appserver.job.Tool;

public class Fibonacci implements Tool{

	Integer number = null;
   
	/**
	 * returns fibonacci number via getResult() function
	 */
	public Object go(Object parameters) {
		number = (Integer) parameters;
		return getResult(number);
	}
	
	/**
	 * recursively calculates fibonacci number
	 * @param n
	 * @return
	 */
	public Integer getResult(int n) {
		if(n <= 1) {
			return n;
	   	}
	    return getResult(n-1) + getResult(n-2);
	}
}
