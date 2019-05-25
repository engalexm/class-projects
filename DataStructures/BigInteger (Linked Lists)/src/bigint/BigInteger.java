package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;
	
	/**
	 * Number of digits in this integer
	 */
	int numDigits;
	
	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 *    
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 *    5 --> 3 --> 2  (No zeros after the last 2)        
	 */
	DigitNode front;
	
	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}
	
	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * Leading and trailing spaces are ignored. So "  +123  " will still parse 
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12  345" will not parse as
	 * an integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the BigInteger
	 * constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) 
	
	throws IllegalArgumentException {
		if(integer.equals("")) {
			throw new IllegalArgumentException();
		}
		
		BigInteger parsed = new BigInteger(); 						// Create new BigInteger LL
		String cleanedInteger = cleanString(integer);				// Remove leading/trailing spaces and leading zeroes from input string
		
		// Handle negative numbers
		if(cleanedInteger.charAt(0) == '-') {
			parsed.negative = true;							
			cleanedInteger = cleanedInteger.substring(1);			// Delete the negative sign to handle digits only
			cleanedInteger = cleanZeroesOnly(cleanedInteger);			// Remove only leading zeroes - any spaces at this point are errors caught below
			for(int i = 0; i < cleanedInteger.length(); i++) {		// Parse all digits
				if(Character.isDigit(cleanedInteger.charAt(i)) == false) {
					throw new IllegalArgumentException();
				}
				parsed.front = new DigitNode(Character.getNumericValue(cleanedInteger.charAt(i)), parsed.front); 
				parsed.numDigits++;
			}

		} else {
		// Handle positive numbers, with or without sign
			if(cleanedInteger.charAt(0) == '+') {					
				cleanedInteger = cleanedInteger.substring(1);		// Delete the positive sign to handle digits only
				cleanedInteger = cleanZeroesOnly(cleanedInteger);		// Remove only leading zeroes - any spaces at this point are errors caught below
			}
			for(int j = 0; j < cleanedInteger.length(); j++) {		// Parse all digits
				if(Character.isDigit(cleanedInteger.charAt(j)) == false) {
					throw new IllegalArgumentException();
				}
				parsed.front = new DigitNode(Character.getNumericValue(cleanedInteger.charAt(j)), parsed.front); 
				parsed.numDigits++;
			}
		}
		if(cleanedInteger.charAt(0) == '0' && cleanedInteger.length() == 1) {
			parsed.numDigits = 0;
			parsed.negative = false;
		}
		return parsed;
	}
	
	/**
	 * Adds the first and second big integers, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY the input big integers.
	 * 
	 * NOTE that either or both of the input big integers could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return Result big integer
	 */
	public static BigInteger add(BigInteger first, BigInteger second) {	
		
		/* How come there is a try/catch for errors in "parse," 
		 * which is called to do "add,"
		 * but errors in add do not yield the "Incorrect Format" error message?
		 */
		
		/* Pass all inputs to custom-designed BigIntegers that can be manipulated later, as needed. */
		BigInteger sum = new BigInteger(), longer = new BigInteger(), shorter = new BigInteger();
		if(first.numDigits >= second.numDigits) { longer = first; shorter = second; }
		else { longer = second; shorter = first; }
		
		/* Variable definitions: */
		DigitNode ptr1 = longer.front, ptr2 = shorter.front;
		int carryOver = 0;
		boolean borrow = false;
		
		/* ONE AND ONLY ONE INTEGER IS NEGATIVE */
		if((longer.negative && shorter.negative == false) || (longer.negative == false && shorter.negative)) {
			/* BOTH HAVE THE SAME LENGTH
			 * If the integers have the same length,
			 * we want a situation in which the positive number is on "top," 
			 * and the negative number is:
			 * (1) a smaller absolute value than the positive number, i.e. pos < |neg|
			 * (2) on "bottom"
			 * so that the while loops & code from later on can **more or less** be reused here.
			 */
			if(longer.numDigits == shorter.numDigits) {	
				if(compareTo(longer, shorter) == 0) {							// Special case: x = -y <=> x+y = 0
					sum.numDigits = 0;
					sum.negative = false;
					return sum;
				}
				BigInteger pos = new BigInteger(), neg = new BigInteger();
				boolean switched = false;
				if(longer.negative) { neg = longer; pos = shorter; } 				// figure out which are positive/negative
				else { neg = shorter; pos = longer; }							
				/* Make pos = neg, then make it "positive".
				 * Make neg = "old" pos, then make it "negative,"
				 * i.e. switch top & bottom as well as signs.
				 */
				if(compareTo(pos, neg) < 0) {
					BigInteger temp = new BigInteger();
					temp = pos;
					pos = neg;
					pos.negative = false;
					neg = temp;
					neg.negative = true;			
					switched = true;
				} 
				ptr1 = pos.front; ptr2 = neg.front;									// Set up pointers
				while(ptr2 != null) {
					while(ptr2 != null) {		
						if(ptr1.digit == 0 && neg.numDigits == 1) {					// Special case e.g. 0 - 9
							sum.front = addLast(sum.front, 0 - ptr2.digit);
							sum.numDigits++;
						} else if(ptr1.digit >= ptr2.digit && borrow == false) {
							int digitDiff = ptr1.digit - ptr2.digit;
							sum.front = addLast(sum.front, digitDiff);
							sum.numDigits++;
						} else if(ptr1.digit >= ptr2.digit && borrow) {
							int digitDiff;
							if(ptr1.digit - 1 > ptr2.digit) {
								digitDiff = (ptr1.digit - 1) - ptr2.digit;
								borrow = false;
							} else if(ptr1.digit - 1 < ptr2.digit){
								digitDiff = (10 + (ptr1.digit - 1) - ptr2.digit);
								borrow = true;
							} else /* if (ptr1.digit - 1 == ptr2.digit) */ {
								/*if(ptr1.next == null) {
									continue;
								} */
								digitDiff = 0;
								borrow = false;
							} 
							sum.front = addLast(sum.front, digitDiff);
							sum.numDigits++;
						} else if(ptr1.digit < ptr2.digit && borrow == false) {
							int digitDiff = (10 + ptr1.digit) - ptr2.digit;
							sum.front = addLast(sum.front, digitDiff);
							sum.numDigits++;
							borrow = true;
						} else if(ptr1.digit < ptr2.digit && borrow) {
							int digitDiff;
							digitDiff = (10 + ptr1.digit -1) - ptr2.digit;
							sum.front = addLast(sum.front, digitDiff);
							sum.numDigits++;
							borrow = true;
						}
						ptr1 = ptr1.next;
						ptr2 = ptr2.next;
					}	
				}
				
				sum = killLeadingZeroes(sum);
				
				if(switched) {
					sum.negative = true;
				}
				
				return sum;														
			}
				
			/* THE LONGER INT IS POSITIVE & THE SHORTER INT IS NEGATIVE */
			if(longer.negative == false && shorter.negative) {					
				while(ptr2 != null) {		
					if(ptr1.digit >= ptr2.digit && borrow == false) {
						int digitDiff = ptr1.digit - ptr2.digit;
						sum.front = addLast(sum.front, digitDiff);
						sum.numDigits++;
					} else if(ptr1.digit >= ptr2.digit && borrow) {
						int digitDiff;
						if(ptr1.digit - 1 > ptr2.digit) {
							digitDiff = (ptr1.digit - 1) - ptr2.digit;
							borrow = false;
						} else if(ptr1.digit - 1 < ptr2.digit){
							digitDiff = (10 + (ptr1.digit - 1) - ptr2.digit);
							borrow = true;
						} else /* if (ptr1.digit - 1 == ptr2.digit) */ {
							/* if(ptr1.next == null) {
								break;
							} */
							digitDiff = 0;
							borrow = false;
						} 
						sum.front = addLast(sum.front, digitDiff);
						sum.numDigits++;
					} else if(ptr1.digit < ptr2.digit && borrow == false) {
						int digitDiff = (10 + ptr1.digit) - ptr2.digit;
						sum.front = addLast(sum.front, digitDiff);
						sum.numDigits++;
						borrow = true;
					} else if(ptr1.digit < ptr2.digit && borrow) {
						int digitDiff;
						digitDiff = (10 + ptr1.digit -1) - ptr2.digit;
						sum.front = addLast(sum.front, digitDiff);
						sum.numDigits++;
						borrow = true;
					}
					ptr1 = ptr1.next;
					ptr2 = ptr2.next;
				}	
				while(ptr1 != null) {
					if(ptr1.digit == 1 && borrow && ptr1.next == null) {				// borrowed 1 from 1, so don't add a digit
						borrow = false;
					} else if(ptr1.digit >= 1 && borrow) {
						sum.front = addLast(sum.front, ptr1.digit-1);
						sum.numDigits++;
						borrow = false;
					} else if (ptr1.digit == 0 && borrow){
						sum.front = addLast(sum.front, 9);
						sum.numDigits++;
						borrow = true;
					} else {
						sum.front = addLast(sum.front, ptr1.digit);
						sum.numDigits++;
						borrow = false;
					}
					ptr1 = ptr1.next;
				}		
				sum = killLeadingZeroes(sum);
				return sum;
				
			/* THE LONGER INT IS NEGATIVE & THE SHORTER INT IS POSITIVE 
			/* Here, we set the longer int to positive, and the shorter int to negative,
			 * so we can use the exact same code as above,
			 * with the stipulation that we return the negative of the sum. 
			 * e.g. (-100) + 9 = - (100-9)								
			 * */	
			} else if(longer.negative && shorter.negative == false) {						// if longer negative & shorter positive
				longer.negative = false;	shorter.negative = true;
				while(ptr2 != null) {		
					if(ptr1.digit >= ptr2.digit && borrow == false) {
						int digitDiff = ptr1.digit - ptr2.digit;
						sum.front = addLast(sum.front, digitDiff);
						sum.numDigits++;
					} else if(ptr1.digit >= ptr2.digit && borrow) {
						int digitDiff;
						if(ptr1.digit - 1 > ptr2.digit) {
							digitDiff = (ptr1.digit - 1) - ptr2.digit;
							borrow = false;
						} else if(ptr1.digit - 1 < ptr2.digit){
							digitDiff = (10 + (ptr1.digit - 1) - ptr2.digit);
							borrow = true;
						} else /* if (ptr1.digit - 1 == ptr2.digit) */ {
							/* if(ptr1.next == null) {
								break;
							} */
							digitDiff = 0;
							borrow = false;
						} 
						sum.front = addLast(sum.front, digitDiff);
						sum.numDigits++;
					} else if(ptr1.digit < ptr2.digit && borrow == false) {
						int digitDiff = (10 + ptr1.digit) - ptr2.digit;
						sum.front = addLast(sum.front, digitDiff);
						sum.numDigits++;
						borrow = true;
					} else if(ptr1.digit < ptr2.digit && borrow) {
						int digitDiff;
						digitDiff = (10 + ptr1.digit -1) - ptr2.digit;
						sum.front = addLast(sum.front, digitDiff);
						sum.numDigits++;
						borrow = true;
					}
					ptr1 = ptr1.next;
					ptr2 = ptr2.next;
				}	
				while(ptr1 != null) {
					if(ptr1.digit == 1 && borrow && ptr1.next == null) {
						borrow = false;
					} else if(ptr1.digit >= 1 && borrow) {
						sum.front = addLast(sum.front, ptr1.digit-1);
						sum.numDigits++;
						borrow = false;
					} else if (ptr1.digit == 0 && borrow){
						sum.front = addLast(sum.front, 9);
						sum.numDigits++;
						borrow = true;
					} else {
						sum.front = addLast(sum.front, ptr1.digit);
						sum.numDigits++;
						borrow = false;
					}
					ptr1 = ptr1.next;
				}	
				sum = killLeadingZeroes(sum);
				sum.negative = true;
				return sum;
			}

		}
		
		/* EITHER:
		 * (1) BOTH INTS ARE POSITIVE
		 * (2) BOTH INTS ARE NEGATIVE */
		// Sum over all the digits of the shorter BigInteger
		while(ptr2 != null) {
			if(ptr1 == null) {ptr1 = new DigitNode(0, null);}
			int digitSum = (ptr2.digit + ptr1.digit + carryOver) % 10;
			carryOver = (int) ((ptr2.digit + ptr1.digit + carryOver) / 10);
			sum.front = addLast(sum.front, digitSum);
			sum.numDigits++;
			ptr1 = ptr1.next;
			ptr2 = ptr2.next;
		}
		// Sum over all the remaining digits of the longer BigInteger
		while(ptr1 != null) {
			int digitSum = (ptr1.digit + carryOver) % 10;
			carryOver = (int) ((ptr1.digit + carryOver) / 10);
			sum.front = addLast(sum.front, digitSum);
			sum.numDigits++;
			ptr1 = ptr1.next;
		}		
		if(carryOver != 0) {									// Handles any additional carryovers
			sum.front = addLast(sum.front, carryOver);
			sum.numDigits++;
		}
		if(longer.negative && shorter.negative) {			// Slaps a negative sign on if both integers are negative
			sum.negative = true;
		}
		if(sum.front.digit == 0 && sum.numDigits == 1) {
			sum.numDigits = 0;
			sum.negative = false;
		}
		return sum; 
	}
	
	/*
	 * Returns the BigInteger obtained by multiplying the first big integer
	 * with the second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {
		BigInteger product = new BigInteger(), longer = new BigInteger(), shorter = new BigInteger();
		if(first.numDigits >= second.numDigits) { longer = first; shorter = second; }
		else { longer = second; shorter = first; }
		if(((first.front).digit == 0 && first.numDigits == 1) || ((second.front).digit == 0 && second.numDigits == 1)) {	// Return 0 if 0 is in the expression
			return product;
		}
		int n = 0; 
		int carryOver = 0;
		DigitNode ptr1 = longer.front;
		DigitNode ptr2 = shorter.front;
		/* Loop through shorter BigInt, multiplying each digit of "shorter"
		 * by each digit of "longer."
		 * Build a BigInteger "partial" which contains each "row" product of manual multiplication.
		 */
		while(ptr2 != null) {													
			ptr1 = longer.front;
			BigInteger partial = new BigInteger();
			while(ptr1 != null) {
				if( ((ptr2.digit * ptr1.digit) + carryOver) >= 10) {
					partial.front = addLast(partial.front, (((ptr2.digit * ptr1.digit) + carryOver)) % 10);
					partial.numDigits++;
					carryOver = (int) ((ptr2.digit * ptr1.digit) + carryOver) / 10;
				} else {
					partial.front = addLast(partial.front, (((ptr2.digit * ptr1.digit) + carryOver)) % 10);
					partial.numDigits++;
					carryOver = 0;
				}
				if(ptr1.next == null && carryOver != 0) {
					partial.front = addLast(partial.front, carryOver);
					partial.numDigits++;
					carryOver = 0;
				} 	
				ptr1 = ptr1.next;
			}
			/* Then, add "partial" times 10^n to the product. 
			 * Row 0 has n=0 since you don't append any zeroes there.
			 */
			for(int i = 0; i < n; i++) {
				partial.front = new DigitNode(0, partial.front);
				partial.numDigits++;
			}
			product = add(product, partial); 
			n++;
			ptr2 = ptr2.next;
		}
		
		/* Handle basic multiplication sign rules. */
		if((longer.negative && shorter.negative == false) || (longer.negative == false && shorter.negative)) {
			product.negative = true;
		} else if (longer.negative && shorter.negative){
			product.negative = false;
		}
		product = killLeadingZeroes(product);
		if(product.numDigits <= 1 && product.front.digit == 0) {
			product.numDigits = 0;
			product.negative = false;
		}
		return product;		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
				retval = curr.digit + retval;
		}
		
		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}
	
	private static String cleanString(String s) {
		if(s.equals("")) {
			throw new IllegalArgumentException();
		}
		String cleaned = new String();
		cleaned = s.trim();									// Delete leading & trailing spaces
		cleaned = cleaned.replaceFirst("^0+(?!$)", "");		// Delete leading zeroes
		return cleaned;
	}
	
	private static String cleanZeroesOnly(String s) {
		if(s.equals("")) {
			throw new IllegalArgumentException();
		}
		return s.replaceFirst("^0+(?!$)", "");				// Delete leading zeroes
	}	
	
	private static DigitNode addLast(DigitNode front, int digit) {
		if(front == null) {
			return new DigitNode(digit, null);
		}
		DigitNode ptr = front;
		while(ptr.next != null) {
			ptr = ptr.next;
		}
		ptr.next = new DigitNode(digit, null);
		return front; 			
	}
	
	private static int compareTo(BigInteger first, BigInteger second) {
		// Assumes both BigIntegers are the same size.
		int greater = 0;
		DigitNode ptr1 = first.front;
		DigitNode ptr2 = second.front;
		while(ptr1 != null) {
			if(ptr1.digit > ptr2.digit) {
				greater = 1;
			} else if(ptr1.digit < ptr2.digit){
				greater = -1;
			}
			ptr1 = ptr1.next;
			ptr2 = ptr2.next;
		}
		return greater;
	}
	
	private static BigInteger killLeadingZeroes(BigInteger bigInt) {
		DigitNode ptr1 = bigInt.front;
		DigitNode ptr2 = bigInt.front.next;
		while(ptr2 != null) {
			if(ptr2.digit == 0 && ptr2.next == null) {
				ptr1.next = null;
				ptr1 = bigInt.front;
				ptr2 = bigInt.front.next;
				bigInt.numDigits--;
				continue;
			} else if(ptr2.digit != 0 && ptr2.next == null) {
				break;
			}
			ptr1 = ptr1.next;
			ptr2 = ptr2.next;
		}
		return bigInt;
	}
	
	
}
