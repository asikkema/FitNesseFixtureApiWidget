package org.fitnesse.widgets.fixtureapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fitnesse.widgets.fixtureapi.maven.MavenPomExtractor;
import org.fitnesse.widgets.fixtureapi.scanner.ApiParser;
import org.fitnesse.widgets.fixtureapi.scanner.FixtureSourceFileScanner;
import org.fitnesse.widgets.fixtureapi.writer.HtmlWriter;

import com.google.common.collect.ListMultimap;

import fitnesse.wikitext.widgets.ParentWidget;
import fitnesse.wikitext.widgets.WidgetWithTextArgument;

/**
 * Scans a project for fixture sources based upon the given pom.xml file and
 * renders the fixture's API in html Useful for testers who are searching for
 * fixture code within a project.
 * 
 * @author albertsikkema
 * 
 */
public class FixtureApiWidget extends ParentWidget implements WidgetWithTextArgument {
	public static final String FIXTURE_API = "fixtureApi";
	public static final String REGEXP = "^!" + FIXTURE_API + " [^\r\n]*";
	private static final Pattern pattern = Pattern.compile(FIXTURE_API + " (.*)");
	private String pomfile;
	boolean sourceFileExists = false;

	public FixtureApiWidget(final ParentWidget parent, final String text) throws Exception {
		super(parent);
		extractPomFromText(text);
	}

	private void extractPomFromText(final String text) throws Exception {
		final Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			final String matchedGroup = matcher.group(1);
			pomfile = matchedGroup;
			addChildWidgets(matchedGroup);
			verifySourceFileExists();
		}
	}

	@Override
	public String asWikiText() throws Exception {
		return FIXTURE_API +" " + pomfile;
	}

	@Override
	public String getText() throws Exception {
		//TODO: Figure out what to return here (where and how is it used?)
		return "as text";
	}

	@Override
	public String render() throws Exception {
		if (!sourceFileExists) {
			return renderNotValidSourceResponse();
		}
		return renderMethodSignatures();
	}

	private String renderMethodSignatures() throws FileNotFoundException {
		final ListMultimap<File, File> allFixtures = getAllFixtures();
		final StringBuilder sb = new StringBuilder();
		for (final File fixtureDir : allFixtures.keySet()) {
			for (final File fixtureFile : allFixtures.get(fixtureDir)) {
				final HtmlWriter writer = new HtmlWriter();
				new ApiParser(fixtureFile).execute(writer);
				sb.append(writer.toString());
			}
		}
		return sb.toString();
	}

	private ListMultimap<File, File> getAllFixtures() {
		return new FixtureSourceFileScanner().scanRecursive(new MavenPomExtractor(new File(pomfile)).getTestRoots());
	}

	private String renderNotValidSourceResponse() {
		return "<span style='color:red'><b>Error: POM file " + pomfile + " does not exist!</b></span> ";
	}

	private void verifySourceFileExists() {
		sourceFileExists = new File(pomfile).exists();
	}
}
