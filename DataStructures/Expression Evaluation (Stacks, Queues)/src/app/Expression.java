package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
	  	String nums = "1234567890"; String nonarr = " \t*+-/()]";
	    	Stack<String> tokenStack = new Stack<String>();	
	    	StringTokenizer st = new StringTokenizer(expr, delims, true);
	    	while(st.hasMoreTokens()) {  tokenStack.push(st.nextToken());  }
	    	while(tokenStack.isEmpty() == false) {
	    		if(arrays.contains(new Array (tokenStack.peek())) || vars.contains(new Variable(tokenStack.peek()))) {  tokenStack.pop();  }
	    		else if(tokenStack.peek().equals("[")) { tokenStack.pop(); arrays.add(new Array(tokenStack.pop()));  }
	    		else if(nums.contains(tokenStack.peek().substring(0,1)) || nonarr.contains(tokenStack.peek().substring(0,1))) {  tokenStack.pop();  } 
	    		else {  vars.add(new Variable(tokenStack.pop()));  }
	    	}    	
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    		StringTokenizer st = new StringTokenizer(expr, delims, true);
    		Stack<Float> values = new Stack<Float>(); 
    		Stack<String> ops = new Stack<String>(); 
     	while(st.hasMoreTokens()) {
     		String tok = st.nextToken();
     		// If it's a number, then push it to values
     		if(numSearch(tok)) {   values.push(Float.valueOf(tok));   }
     		// If it's a simple variable, then get its value and push it to values
     		else if(varSearch(vars, tok) != null ) {   values.push( (float) varSearch(vars, tok).value );   }
     		/* If it's "(", then we need to get the entire sub-expression, i.e. until the matching ")"
     		 * and then recursively evaluate what's inside.
     		 * We create a parens Stack to grab the whole sub-expression into a String,
     		 * call evaluate on that String, and then push the value.
     		 */
     		else if(tok.equals("(")) { 
     			Stack<String> parens = new Stack<String>();
     			String subexpr = "";
     			parens.push(tok);
     			while(!parens.isEmpty()) {
     				String toAdd = st.nextToken();
     				if(toAdd.equals("(")) { parens.push(toAdd); subexpr += toAdd; }
     				else if(toAdd.equals(")")) { parens.pop(); subexpr += toAdd; } 
     				else { subexpr += toAdd; }
     			}
     			subexpr = subexpr.substring(0, subexpr.length()-1);
     			values.push(evaluate(subexpr, vars, arrays));
     		}
     		/* Similarly, if it's "[", then we need to get the entire sub-expression, i.e. until the matching "]"
     		 * and then recursively evaluate what's inside.
     		 * We create a bracks Stack to grab the whole sub-expression into a String,
     		 * call evaluate on that String, cast it as an int to get an index,
     		 * grab the array's value at that index, and then push the value.
     		 */
     		else if(arrSearch(arrays, tok) != null) {
     			Array arr = arrSearch(arrays, tok);
     			Stack<String> bracks = new Stack<String>();
     			String subexpr = "";
     			bracks.push(st.nextToken());
     			while(!bracks.isEmpty()) {
     				String toAdd = st.nextToken();
     				if(toAdd.equals("[")) { bracks.push(toAdd); subexpr += toAdd; }
     				else if(toAdd.equals("]")) { bracks.pop(); subexpr +=toAdd; } 
     				else { subexpr += toAdd; }
     			}
     			subexpr = subexpr.substring(0, subexpr.length()-1);
     			values.push( (float) arr.values[(int)evaluate(subexpr, vars, arrays)]  );
     		}
     		// Finally, push any operators onto the ops Stack.
     		else if(tok.equals("+") || tok.equals("-") || tok.equals("*") || tok.equals("/")) {
     			ops.push(tok);
     		}
     	}
     	/* Now, we use a recursive helper method to evaluate the expression represented by two stacks:
     	 * (1) a values stack, which at this point should only have values (and not variable names or array indices), and
     	 * (2) an operands stack, which at this point should only have operands represented as Strings.
     	 */
     	return stkEval(values, ops);
    }
    
   private static boolean numSearch(String str) {
	   String nums = "1234567890";
	   for(int i = 0; i < str.length(); i++) {
		   if(!nums.contains(str.substring(i,i+1))) {   return false;   }
	   }
	   return true;
   }
    
    private static Variable varSearch(ArrayList<Variable> list, String target) {
	   if(!list.isEmpty()) { 
		   for(int i = 0; i < list.size(); i++) {
			   if(target.equals(list.get(i).name)) {   return list.get(i);   }
		   }
	   }
	   return null;
    }
    
    private static Array arrSearch(ArrayList<Array> list, String target) {
	    	if(!list.isEmpty()) { 
		    	for(int i = 0; i < list.size(); i++) {
					if(target.equals(list.get(i).name)) {   return list.get(i);   }
		    	}
	    	}
		return null;
    }
    
    private static float stkEval(Stack<Float> values, Stack<String> ops) {
    		// Base case: There's only one value and no operands. Just return the value.
    		if(values.size() == 1 && ops.size() == 0) {   return values.pop();   }
    		
    		/* For multiplication or division:
    		 * Pop one value (num2), the next value (num1). Pop the operation.
       	 * Do the operation with num1 and num2, and push it back onto the stack.
       	 * Then, re-evaluate the values and ops stacks. 
       	 */
    		else if(ops.peek().equals("*") || ops.peek().equals("/")) {
    			String op = ops.pop();
    			float num2 = values.pop();
    			float num1;
    			
    			if(ops.isEmpty()) {   num1 = values.pop();   }
    			else if(!ops.peek().equals("*") && !ops.peek().equals("/")) {    num1 = values.pop();   }
    			/* If the next op is * or /, set num1 to be the evaluation of the next sub-expression only involving / or *
    			 * See the fakeStkEval method comments below to see what it does.
    			 */
    			else {   num1 = fakeStkEval(values, ops);   }
    			
    			if(op.equals("*")) {   values.push(num1 * num2);   }
    			else if(op.equals("/")) {   values.push(num1 / num2);   }
    		}
    		/* For addition or subtraction:
    		 * Pull one value (num2). Pop the operation.
       	 * Then, let the other value (num1) be the result of recursively evaluating the remaining stack.
       	 * Do the operation with num1 and num2, and push it back onto the stack.
       	 * Then, re-evaluate the values and ops stacks. 
       	 */
    		else {
    			String op = ops.pop();
    			float num2 = values.pop();
    			float num1 = stkEval(values, ops);
    			if(op.equals("+")) {   values.push(num1 + num2);   }
    			else if(op.equals("-")) {   values.push(num1 - num2);   }
    		}
		
		return stkEval(values, ops);  	
		
    }
    
    /* Use this method to evaluate the next subexpression of / or *
     * on the stacks, i.e.
     * 3 - 20 / 5 / 2 evaluates just 20 / 5 / 2 = 2.
     * P.S. I couldn't think of a good name for this method.
     */
    private static float fakeStkEval(Stack<Float> values, Stack<String> ops) {
    		float num2 = values.pop(); float num1;
    		String op = ops.pop();
    		if(ops.isEmpty()) {   num1 = values.pop();   }
    		else if(ops.peek().equals("/") || ops.peek().equals("*") ) {
    			num1 = fakeStkEval(values, ops);
    		} else {
    			num1 = values.pop();
    		}
    		if(op.equals("*")) {   return num1 * num2;   }
    		else {   return num1 / num2;   }
    }
    
}
