package esercizio1;
public class Identificatori
{
    public static boolean scan(String s)
    {
	int state = 0;
	int i = 0;

	while (state >= 0 && i < s.length()) {
	    final char ch = s.charAt(i++);
	    final boolean isLetter = (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
	    final boolean isDigit = (ch >= '0' && ch <= '9');
	    final boolean isUnderscore = (ch == '_');

	    switch (state) {
	    case 0:
		if (isLetter)
		    state = 1;
		else if (isUnderscore)
		    state = 2;
		else
		    state = -1;
		break;

	    case 1:
		if (isLetter || isDigit || isUnderscore)
		    state = 1;
		else
		    state = -1;
		break;

	    case 2:
		if (isLetter || isDigit)
		    state = 1;
		else if (isUnderscore)
		    state = 2;
		else
		    state = -1;
		break;
	    }
	}
	return state == 1;
    }





	//test
    public static void main(String[] args)
    {
	String[] valid = {"x", "flag1", "x2y2", "x_1", "lft_lab", "_temp", "x_1_y_2", "x___", "__5"};
	String[] invalid = {"5", "221B", "123", "9_to_5", "___", "", "_"};

	System.out.println("Stringhe che devono essere ACCETTATE:");
	for (String s : valid)
	    System.out.println("  \"" + s + "\" -> " + (scan(s) ? "OK" : "NOPE  <-- ERRORE!"));

	System.out.println("\nStringhe che devono essere RIFIUTATE:");
	for (String s : invalid)
	    System.out.println("  \"" + s + "\" -> " + (scan(s) ? "OK  <-- ERRORE!" : "NOPE"));

	if (args.length > 0)
	    System.out.println("\n" + args[0] + " -> " + (scan(args[0]) ? "OK" : "NOPE"));
    }
}
