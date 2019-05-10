import java.util.ArrayList;
import java.util.Scanner;

public class Calculator {

	public String calculate(String answer) {

		// Fixes spacing
		String input = format(answer);
		String[] splitArray = input.split(" ");

		// Checks for bad input
		checkInput(splitArray);

		// Creates ArrayList with values from splitArray
		ArrayList<String> expression = new ArrayList<String>();
		for (int i = 0; i < splitArray.length; i++) {
			expression.add(splitArray[i]);
		}

		solve(expression);
		return expression.get(0);
	}

	// Calculates the expression
	public static int solve(ArrayList<String> expression) {

		// Handles parenthesis and then does calculation for all operators
		expression = paranthesis(expression);

		expression = calculate(expression, "^");
		expression = calculate(expression, "*");
		expression = calculate(expression, "/");
		expression = calculate(expression, "+");
		expression = calculate(expression, "-");

		return Integer.parseInt(expression.get(0));
	}

	// Handles parenthesis
	public static ArrayList<String> paranthesis(ArrayList<String> expression) {

		// Finds all sets of parenthesis
		for (int i = 0; i < expression.size(); i++) {
			if (expression.get(i).equals("(")) {

				int open = 1;
				int closed = 0;

				for (int k = i + 1; k < expression.size(); k++) {

					if (expression.get(k).equals("(")) {
						open++;

					} else if (expression.get(k).equals(")")) {
						closed++;

						if (open == closed) {
							ArrayList<String> newinput = new ArrayList<String>();

							// Creates new ArrayList with the inside of the parenthesis
							for (int j = i + 1; j < k; j++) {
								newinput.add(expression.get(j));
							}
							// Runs the solve method on the inside of the parenthesis
							expression.set(i, Integer.toString(solve(newinput)));

							// Deletes the contents from the main ArrayList so only the calculation is left
							for (int j = i + 1; j <= k; j++) {
								expression.remove(i + 1);
							}

							break;
						}
					}
				}
			}
		}

		return expression;
	}

	// Performs all of a certain operation on an ArrayList and returns the new
	// ArrayList
	public static ArrayList<String> calculate(ArrayList<String> expression, String operator) {

		for (int i = 1; i < expression.size(); i++) {

			if (expression.get(i).equals(operator)) {

				int a = Integer.parseInt(expression.get(i - 1));
				int b = Integer.parseInt(expression.get(i + 1));

				expression.set(i - 1, Integer.toString(operation(a, b, operator)));
				expression.remove(i);
				expression.remove(i);
				i--;
			}
		}
		return expression;
	}

	// Performs an operation on two numbers
	public static int operation(int a, int b, String operator) {

		if (operator.equals("^"))
			return (int) Math.pow(a, b);
		else if (operator.equals("*"))
			return a * b;
		else if (operator.equals("/"))
			return a / b;
		else if (operator.equals("+"))
			return a + b;
		else
			return a - b;
	}

	// checks if a String is an operator
	public static boolean isOperator(String character) {
		return "+-*/^()".contains(character);
	}

	// Formats the input by adding and removing spaces
	public static String format(String input) {

		for (int i = 0; i < input.length() - 1; i++) {
			if (input.charAt(i) != ' ' && input.charAt(i + 1) != ' '
					&& (isOperator(input.substring(i, i + 1)) || isOperator(input.substring(i + 1, i + 2)))) {
				input = input.substring(0, i + 1) + " " + input.substring(i + 1, input.length());

			} else if (input.charAt(i) == ' ' && input.charAt(i + 1) == ' ') {
				input = input.substring(0, i) + input.substring(i + 1, input.length());
				i--;
			}
		}
		return input;
	}

	// Checks for input that isn't an integer or operator
	public static void checkInput(String[] expression) throws IllegalArgumentException {
		for (int i = 0; i < expression.length; i++) {
			if (isOperator(expression[i])) {

			} else
				try {
					Integer.parseInt(expression[i]);

				} catch (NumberFormatException ex) {
					throw new IllegalArgumentException("You many only have integers, operators, and spaces");
				}
		}
	}
}