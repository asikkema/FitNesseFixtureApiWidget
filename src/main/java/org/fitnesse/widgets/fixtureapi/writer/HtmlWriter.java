package org.fitnesse.widgets.fixtureapi.writer;


import static org.apache.commons.lang.StringUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.IOUtils;
import org.fitnesse.widgets.fixtureapi.scanner.Parameter;

public class HtmlWriter implements SourceWriter {

	@SuppressWarnings("unused")
	private class Method {
		private final String javadoc;
		private final String name;
		private final List<Parameter> params;
		private final String returns;

		public Method(final String name, final String javadoc, final String returns, final List<Parameter> params) {
			this.name = name;
			this.javadoc = cleanupJavaDoc(javadoc);
			this.returns = returns;
			this.params = params;
		}

		public String getJavadoc() {
			return javadoc;
		}

		public String getName() {
			return name;
		}

		public String getReadableName() {
			return CamelCaseSplitter.splitString(name);
		}

		public List<Parameter> getParams() {
			return params;
		}

		public String getReturns() {
			return returns;
		}

		public boolean getHasParams() {
			return params != null && params.size() > 0;
		}

		private String cleanupJavaDoc(final String javadoc) {
			return trim(remove(remove(remove(javadoc, "/"), "*"), "\t"));
		}
	}

	private String className;
	private final List<Method> methods = new ArrayList<Method>();
	private String packageName;

	@Override
	public void addClass(final String className) {
		this.className = className;

	}

	@Override
	public void addMethod(final String name, final String javadoc, final String returns, final List<Parameter> params) {
		methods.add(new Method(name, javadoc, returns, params));
	}

	@Override
	public void addPackage(final String packageName) {
		this.packageName = packageName;
	}

	private String cleanUp(final String formattedHtml) {
		return trim(formattedHtml);
	}

	@Override
	public String toString() {
		String template;
		try {
			template = IOUtils.toString(this.getClass().getResourceAsStream("/htmlTemplate.st"));
			final StringTemplate st = new StringTemplate(template);
			st.setAttribute("package", packageName);
			st.setAttribute("methods", methods);
			st.setAttribute("className", className);
			return cleanUp(st.toString());
		} catch (final IOException e) {
			throw new RuntimeException("Error occured while creating html output", e);
		}
	}

	static class CamelCaseSplitter {

		public static String splitString(final String name) {
			return capitalize(join(splitIntoWords(name), " "));
		}

		private static List<String> splitIntoWords(final String string) {
			final List<String> words = new ArrayList<String>();
			String word = "";
			for (int i=0; i < string.length(); i++) {
				final Character c = string.charAt(i);
				if (i>0 && isUpper(c) && !isUpper(string.charAt(i-1))) {
					words.add(word);
					word = "" + Character.toLowerCase(c);
				} else {
					word += c;
				}
			}
			words.add(word);
			return words;
		}

		private static boolean isUpper(final char c) {
			return Character.isUpperCase(c);
		}

	}
}
