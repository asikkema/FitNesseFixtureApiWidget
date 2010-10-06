package org.fitnesse.widgets.fixtureapi.maven;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.fitnesse.widgets.fixtureapi.exception.FixtureApiPluginException;
import org.fitnesse.widgets.fixtureapi.maven.MavenPomExtractor;
import org.junit.Before;
import org.junit.Test;

public class MavenPomExtractorTest {

	private MavenPomExtractor mavenPomExtractor;
	private File pom;

	@Before
	public void setup() throws Exception {
		pom = getThisProjectsPom();
		mavenPomExtractor = new MavenPomExtractor(pom);
	}

	@Test
	public void shouldReturnTestClassPathsFromPom() throws Exception {
		final List<File> roots = mavenPomExtractor.getTestRoots();
		assertThat(roots.size(), is(1));
		assertThat(roots.get(0).getAbsolutePath(), containsString("src/test/java"));
	}

	@Test
	public void shouldGetFullPathOfPom() throws Exception {
		final File fullPathOfBaseDir = mavenPomExtractor.getFullPathOfBaseDir();
		assertThat(fullPathOfBaseDir.getPath(), is(pom.getParentFile().getPath()));
	}

	@Test(expected=FixtureApiPluginException.class)
	public void shouldThrowExceptionWhenPomDoesNotExist() throws Exception {
		new MavenPomExtractor(new File("notExistingPom.xml"));
	}

	@Test(expected=FixtureApiPluginException.class)
	public void shouldThrowExceptionWhenPassingInNull() throws Exception {
		new MavenPomExtractor(null);
	}

	@Test(expected=FixtureApiPluginException.class)
	public void shouldThrowExceptionWhenBadPomIsPassedIn() throws Exception {
		final File badPom = createBadPom();
		final MavenPomExtractor mavenPomExtractor2 = new MavenPomExtractor(badPom);
		mavenPomExtractor2.getTestRoots();
	}

	private File createBadPom() throws IOException {
		final File badPom = File.createTempFile("pom", ".xml");
		final FileWriter fileWriter = new FileWriter(badPom);
		fileWriter.write("<pom>this is not a valid pom</pom>");
		fileWriter.close();
		return badPom;
	}

	private File getThisProjectsPom() {
		final String path = this.getClass().getResource(this.getClass().getSimpleName()+".class").getPath();
		final String pomFileStr = StringUtils.substringBefore(path, "target") + "pom.xml";
		final File pom = new File(pomFileStr);
		return pom;
	}
}
