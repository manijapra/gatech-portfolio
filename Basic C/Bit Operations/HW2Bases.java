/**
 * CS 2110 Fall 2015 HW2
 * Part 1 - Coding with bases
 * 
 * @author Mani Japra
 *
 * Global rules for this file:
 * - You may not use more than 2 conditionals per method. Conditionals are
 *   if-statements, if-else statements, or ternary expressions. The else block
 *   associated with an if-statement does not count toward this sum.
 * - You may not use more than 2 looping constructs per method. Looping
 *   constructs include for loops, while loops and do-while loops.
 * - You may not use nested loops.
 * - You may not declare any file-level variables.
 * - You may not declare any objects, other than String in select methods.
 * - You may not use switch statements.
 * - You may not use the unsigned right shift operator (>>>)
 * - You may not write any helper methods, or call any other method from this or
 *   another file to implement any method.
 * - The only Java API methods you are allowed to invoke are:
 *     String.length()
 *     String.charAt()
 *     String.equals()
 * - You may not invoke the above methods from string literals.
 *     Example: "12345".length()
 * - When concatenating numbers with Strings, you may only do so if the number
 *   is a single digit.
 *
 * Method-specific rules for this file:
 * - You may not use multiplication, division or modulus in any method, EXCEPT
 *   strdtoi.
 * - You may declare exactly one String variable each in itostrb, and itostrx.
 */
public class HW2Bases
{
	/**
	 * strdtoi - Decimal String to int
	 *
	 * Convert a string containing ASCII characters (in decimal) to an int.
	 * You do not have to handle negative numbers. The Strings we will pass in will be
	 * valid decimal numbers, and able to fit in a 32-bit signed integer.
	 * 
	 * Example: strdtoi("123"); // => 123
	 */
	public static int strdtoi(String decimal)
	{
        int i = 0;
        int ans = 0;

        while (i < decimal.length()) {
            ans *= 10;
            ans += decimal.charAt(i++) - '0';
        }

        return ans;
	}

	/**
	 * strbtoi - Binary String to int
	 *
	 * Convert a string containing ASCII characters (in binary) to an int.
	 * You do not have to handle negative numbers. The Strings we will pass in will be
	 * valid binary numbers, and able to fit in a 32-bit signed integer.
	 * 
	 * Example: strbtoi("111"); // => 7
	 */
	public static int strbtoi(String binary)
	{
        int curLoc = 1;
        int i = binary.length() - 1;
        int ans = 0;

        while (i >= 0) {
            if (binary.charAt(i) == '1') {
                ans += curLoc;
            }
            curLoc += curLoc;
            i--;
        }

        return ans;
	}

	/**
	 * strxtoi - Hexadecimal String to int
	 *
	 * Convert a string containing ASCII characters (in hex) to an int.
	 * The input string will only contain numbers and uppercase letters A-F.
	 * You do not have to handle negative numbers. The Strings we will pass in will be
	 * valid hexadecimal numbers, and able to fit in a 32-bit signed integer.
	 * 
	 * Example: strxtoi("A6"); // => 166
	 */
	public static int strxtoi(String hex)
	{
        int num = 0;
        int shift = 0;

        for (int q = hex.length() - 1; q >= 0; q--) {
            int val = 0;
            if ((int) hex.charAt(q) >= 65) {
                val = (int) hex.charAt(q) - 55;
            } else {
                val = (int) hex.charAt(q) - 48;
            }

            val = val << shift;
            shift += 4;
            num += val;
        }

        return num;
	}

	/**
	 * itostrb - int to Binary String
	 *
	 * Convert a int into a String containing ASCII characters (in binary).
	 * You do not have to handle negative numbers.
	 * The String returned should contain the minimum number of characters necessary to
	 * represent the number that was passed in.
	 * 
	 * Example: itostrb(7); // => "111"
	 */
	public static String itostrb(int binary)
	{
        int s = 0;
        int test = 0;
        String result = "";

        while ((binary >> s) > 0) {
            test = (binary >> s) & 1;
            if (test > 0) {
                result = "1" + result;
            } else {
                result = "0" + result;
            }
            s++;
        }

        if (result.length() == 0) {
            return "0";
        }

        return result;
	}

	/**
	 * itostrx - int to Hexadecimal String
	 *
	 * Convert a int into a String containing ASCII characters (in hexadecimal).
	 * The output string should only contain numbers and uppercase letters A-F.
	 * You do not have to handle negative numbers.
	 * The String returned should contain the minimum number of characters necessary to
	 * represent the number that was passed in.
	 * 
	 * Example: itostrx(166); // => "A6"
	 */
	public static String itostrx(int hex)
	{
        int m = 0xF;
        String sol = "";
        if (hex == 0) {
            return "0";
        }
        while (hex != 0) {
            int quad = hex & m;
            if (quad > 9) {
                sol = ((char) (quad + 55)) + sol;
            } else {
                sol = ((char) (quad + 48)) + sol;
            }
            hex = hex >> 4;
        }

        return sol;
	}
}

