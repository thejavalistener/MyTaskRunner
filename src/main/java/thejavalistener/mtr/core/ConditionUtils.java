package thejavalistener.mtr.core;

public class ConditionUtils
{
	public static void validate(String s)
	{
		s = normalize(s);

		if(s.contains("==")) return;
		if(s.contains("!=")) return;

		throw new RuntimeException(
			"Condición inválida: " + s + ". Se esperaba '==' o '!='."
		);
	}

	public static boolean eval(String s)
	{
		s = normalize(s);

		if(s.contains("=="))
		{
			String[] p = s.split("==", 2);
			return clean(p[0]).equals(clean(p[1]));
		}

		if(s.contains("!="))
		{
			String[] p = s.split("!=", 2);
			return !clean(p[0]).equals(clean(p[1]));
		}

		throw new RuntimeException("Condición inválida: " + s);
	}

	private static String normalize(String s)
	{
		if(s == null)
			throw new RuntimeException("Condición nula");

		return s.trim();
	}

	private static String clean(String s)
	{
		s = s.trim();

		if(s.startsWith("'") && s.endsWith("'") && s.length() >= 2)
			return s.substring(1, s.length() - 1);

		if(s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2)
			return s.substring(1, s.length() - 1);

		return s;
	}
}