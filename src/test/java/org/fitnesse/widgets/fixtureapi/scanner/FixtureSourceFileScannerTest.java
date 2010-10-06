package org.fitnesse.widgets.fixtureapi.scanner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fitnesse.widgets.fixtureapi.maven.MavenPomExtractor;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ListMultimap;

public class FixtureSourceFileScannerTest {


	private FixtureSourceFileScanner fixtureSourceFileScanner;

	@Before
	public void setup() throws Exception {
		fixtureSourceFileScanner = new FixtureSourceFileScanner();
	}

	@Test
	public void shouldCollectListOfFixtureSourcesAndFindThisTestBecauseItHasTheWordFixtureInTheName() throws Exception {
		final File pom = getThisProjectsPom();
		final MavenPomExtractor mavenPomExtractor = new MavenPomExtractor(pom);
		final List<File> testRoots = mavenPomExtractor.getTestRoots();

		final ListMultimap<File, File> fixtureMultimap = fixtureSourceFileScanner.scanRecursive(testRoots);
		assertThat(fixtureMultimap.keySet().size(), is(3));
		final File fixtureDir = fixtureMultimap.keySet().iterator().next();
		assertThat(fixtureDir.isDirectory(), is(true));

		final List<File> fixtures = fixtureMultimap.get(fixtureDir);
		assertThat(fixtures.size(), is(1));
		assertThat(fixtures.get(0).getName(), containsString("FixtureApiWidgetTest.java"));
	}

	private File getThisProjectsPom() {
		final String path = this.getClass().getResource(this.getClass().getSimpleName()+".class").getPath();
		final String pomFileStr = StringUtils.substringBefore(path, "target") + "pom.xml";
		final File pom = new File(pomFileStr);
		return pom;
	}

}
