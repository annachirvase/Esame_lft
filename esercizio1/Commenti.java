public class Commenti
{
    public static boolean scan(String s)
    {
	int state = 0;
	int i = 0;

	while (state >= 0 && i < s.length()) {
	    final char ch = s.charAt(i++);

	    switch (state) {
	    case 0: // aspetto '/' iniziale
		if (ch == '/')
		    state = 1;
		else
		    state = -1;
		break;

	    case 1: // visto '/', aspetto '*' per completare l'apertura "/*"
		if (ch == '*')
		    state = 2;
		else
		    state = -1;
		break;

	    case 2: // dentro il corpo, ultimo carattere NON era '*'
		if (ch == '*')
		    state = 3;
		else if (ch == '/' || ch == 'a')
		    state = 2;
		else
		    state = -1;
		break;

	    case 3: // dentro il corpo, ultimo carattere ERA '*' (possibile chiusura)
		if (ch == '/')
		    state = 4;
		else if (ch == '*')
		    state = 3;
		else if (ch == 'a')
		    state = 2;
		else
		    state = -1;
		break;

	    case 4: // commento gia' chiuso: nessun altro carattere ammesso
		state = -1;
		break;
	    }
	}
	return state == 4;
    }

    public static void main(String[] args)
    {
	String[] valid = {"/****/", "/*a*a*/", "/*a/**/", "/**a///a/a**/", "/**/", "/*/*/"};
	String[] invalid = {"/*/", "/**/***/", "", "/*", "a", "/**"};

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
