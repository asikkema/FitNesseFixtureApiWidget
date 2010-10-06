package org.fitnesse.widgets.fixtureapi;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import fitnesse.wikitext.widgets.MockWidgetRoot;
import fitnesse.wikitext.widgets.ParentWidget;


public class FixtureApiWidgetTest {

	private FixtureApiWidget fixtureApiWidget;

	@Before
	public void setup() throws Exception {
		final String text = FixtureApiWidget.FIXTURE_API +" "+ getThisProjectsPom().getAbsolutePath();
		final ParentWidget parent = new MockWidgetRoot();
		fixtureApiWidget = new FixtureApiWidget(parent, text);
	}

	@Test
	public void shouldGetAsWikiText() throws Exception {
		final String asWikiText = fixtureApiWidget.asWikiText();
		assertThat(asWikiText, containsString(FixtureApiWidget.FIXTURE_API));
	}

	@Test
	public void shouldRender() throws Exception {
		final String renderedHtml = fixtureApiWidget.render();
		System.out.println(renderedHtml);
		assertThat(renderedHtml, containsString("Package"));
	}

	@Test
	public void shouldGetAsText() throws Exception {
		final String text = fixtureApiWidget.getText();
		assertThat(text, is(notNullValue()));
	}

	private File getThisProjectsPom() {
		final String path = this.getClass().getResource(this.getClass().getSimpleName()+".class").getPath();
		final String pomFileStr = StringUtils.substringBefore(path, "target") + "pom.xml";
		final File pom = new File(pomFileStr);
		return pom;
	}
}
