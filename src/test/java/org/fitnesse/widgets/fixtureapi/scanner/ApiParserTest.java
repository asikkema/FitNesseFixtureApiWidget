package org.fitnesse.widgets.fixtureapi.scanner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.fitnesse.widgets.fixtureapi.exception.FixtureApiPluginException;
import org.fitnesse.widgets.fixtureapi.writer.HtmlWriter;
import org.fitnesse.widgets.fixtureapi.writer.SourceWriter;
import org.junit.Test;

public class ApiParserTest {

	private ApiParser apiParser;

	@Test
	public void getTestJavaSource() throws Exception {
		final File file = getTestSourceFile();
		assertThat(file.exists(), is(true));
	}

	@Test
	public void shouldParseTestClassAndRenderHtml() throws Exception {
		apiParser = new ApiParser(getTestSourceFile());
		final HtmlWriter writer = new HtmlWriter();
		apiParser.execute(writer);

		final String html = writer.toString();
		assertThat(html, org.hamcrest.Matchers.startsWith("<h4>"));
		assertThat(html, containsString("DummyJavaSourceFile"));
	}

	@Test
	public void shouldNotWritePrivateAndProtectedMethods() throws Exception {
		apiParser = new ApiParser(getTestSourceFile());

		final StubSourceWriter writer = new StubSourceWriter();
		apiParser.execute(writer);
		assertThat(writer.getMethods().size(), is(2));
	}

	@Test(expected=FixtureApiPluginException.class)
	public void shouldThrowIOException() throws Exception {
		final File tempFile = File.createTempFile("TempFile", ".java");
		final FileWriter fileWriter = new FileWriter(tempFile);
		fileWriter.write("This should be a valid java class, but it isn't");
		fileWriter.close();
		new ApiParser(tempFile);
	}

	private File getTestSourceFile() {
		final String className = this.getClass().getSimpleName() +".class";
		final URL resource = this.getClass().getResource(className);
		final String path = resource.getPath();
		final String sourcePath = path.replace("target/test-classes", "src/test/java").replace("scanner", "fixture") .replace(className, "DummyJavaSourceFile.java");
		final File file = new File(sourcePath);
		return file;
	}


	private class StubSourceWriter implements SourceWriter {
		public List<String> methods = new ArrayList<String>();
		@Override
		public void addClass(final String name) {
		}

		@Override
		public void addMethod(final String name, final String javadoc, final String returns, final List<Parameter> params) {
			methods.add(name);
		}

		public List<String> getMethods() {
			return methods;
		}

		@Override
		public void addPackage(final String packageName) {
		}
	}
}
