package br.com.argonavis.si.examples.tdc;

public class SelectUtil {

	private static String[] java = { "java", "ejb", "jms", "spring",
			"jsf", "cdi", "servlet", "mvc"};
	private static String[] web = { "html", "html5", "css", "css3",
			"javascript", "svg", "jquery", "less", "sass", "w3c"};

	public static boolean contains(String text, String[] words) {
		for (String word : words) {
			if (text.toLowerCase().indexOf(word.toLowerCase()) >= 0) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAboutJava(String message) {
		return contains(message, java);
	}

	public static boolean isAboutWeb(String message) {
		return contains(message, web);
	}
}
