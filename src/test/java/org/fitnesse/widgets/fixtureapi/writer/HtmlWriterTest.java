package org.fitnesse.widgets.fixtureapi.writer;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.fitnesse.widgets.fixtureapi.scanner.Parameter;
import org.junit.Before;
import org.junit.Test;


public class HtmlWriterTest {

	private HtmlWriter htmlWriter;


	@Before
	public void setup() throws Exception {
		htmlWriter = new HtmlWriter();
	}

	@Test
	public void shouldRenderAsHtml() throws Exception {
		htmlWriter.addPackage("nl.sikkema.plugins.fitnesse.writer");
		htmlWriter.addMethod("someDummyMethod","** Some javadoc **", "boolean", Arrays.asList(new Parameter("isDummy", "boolean")));
		final String html = htmlWriter.toString();
		assertThat(html, containsString("<h4>Package:"));
	}

	@Test
	public void shouldFormatJavaDocProperly() throws Exception {
		final String javadoc = "/**\n" +
		"	 * Prints whatever is said as <i>anyThing</i>\n" +
		"	 * \n" +
		"	 * @param anyThing any string\n" +
		"	 * @param aNumber any number\n" +
		"	 */";
		htmlWriter.addMethod("someMethodWithJavadoc", javadoc, "boolean", null);
		final String html = htmlWriter.toString();
		assertThat(html, containsString("Some method with javadoc"));
		System.out.println(html);
	}
}
