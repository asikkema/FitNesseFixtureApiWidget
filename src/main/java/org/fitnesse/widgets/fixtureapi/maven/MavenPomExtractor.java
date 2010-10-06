package org.fitnesse.widgets.fixtureapi.maven;

import static org.apache.maven.embedder.MavenEmbedder.validateConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.apache.maven.embedder.Configuration;
import org.apache.maven.embedder.ConfigurationValidationResult;
import org.apache.maven.embedder.DefaultConfiguration;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.settings.Settings;
import org.fitnesse.widgets.fixtureapi.exception.FixtureApiPluginException;

/**
 * Decorates the mavenEmbedder with methods to get to the testroot(s) and the
 * baseDir of a project.
 * 
 * @author albertsikkema
 * 
 */
public class MavenPomExtractor {
	private final File userSettingsFile = MavenEmbedder.DEFAULT_USER_SETTINGS_FILE;
	private final File globalSettingsFile = MavenEmbedder.DEFAULT_GLOBAL_SETTINGS_FILE;
	private final File pom;

	public MavenPomExtractor(final File pom) {
		checkIfPomExists(pom);
		this.pom = pom;
	}

	/**
	 * Returns a list of testRoots (${project.basedir}/src/test/java) etc which
	 * should contain the test sources.
	 * 
	 * @return a list of roots which contain tests.
	 */
	public List<File> getTestRoots() {
		final Configuration configuration = mavenConfiguration();
		ensureMavenConfigurationIsValid(configuration);
		final MavenExecutionRequest request = createExecutionRequest(pom);
		List<String> testRoots = new ArrayList<String>();
		try {
			testRoots = extractTestRoots(configuration, request);
		} catch (final MavenEmbedderException e) {
			throw new FixtureApiPluginException("unable to extract test roots from pom", e);
		}
		return makeAbsolute(testRoots);
	}

	private void checkIfPomExists(final File pom) {
		if (pom == null || !pom.exists() || !pom.isFile()) {
			throw new FixtureApiPluginException("Not a valid Pom: " + pom);
		}
	}

	/**
	 * Replaces the ${project.basedir} variable in the path with the full path
	 * of the pom so that the file paths are absolute.
	 * 
	 * @param testRoots
	 *            the testRoots as they are returned by the maven embedder
	 * 
	 * @return an list of absolute testPaths.
	 */
	private List<File> makeAbsolute(final List<String> testRoots) {
		final List<File> absoluteRoots = new ArrayList<File>();
		final String baseDir = pom.getParentFile().getAbsolutePath();
		for (final String root : testRoots) {
			absoluteRoots.add(new File(root.replace("${project.basedir}", baseDir)));
		}
		return absoluteRoots;
	}

	/**
	 * Get the absolute path of the given pom.
	 * 
	 * @return the absolute path of the given pom.
	 */
	public File getFullPathOfBaseDir() {
		return pom.getParentFile();
	}

	private List<String> extractTestRoots(final Configuration configuration, final MavenExecutionRequest request) throws MavenEmbedderException {
		final MavenEmbedder embedder = new MavenEmbedder(configuration);
		final MavenExecutionResult executionResult = embedder.readProjectWithDependencies(request);
		checkIfExecutionIsValid(executionResult);

		@SuppressWarnings("unchecked")
		final List<String> testRoots = executionResult.getProject().getTestCompileSourceRoots();
		return testRoots;
	}

	private void checkIfExecutionIsValid(final MavenExecutionResult executionResult) {
		if (executionResult == null || executionResult.getProject() == null) {
			throw new FixtureApiPluginException("Unable to create executionResult for the given maven context");
		}
	}

	private MavenExecutionRequest createExecutionRequest(final File pom) {
		final MavenExecutionRequest pomFile = new DefaultMavenExecutionRequest().setBaseDirectory(pom.getParentFile()).setPomFile(pom.getName());
		return pomFile;
	}

	private void ensureMavenConfigurationIsValid(final Configuration configuration) {
		final ConfigurationValidationResult validationResult = validateMavenConfiguration(configuration);
		if (!validationResult.isValid()) {
			throw new IllegalArgumentException("Unable to create valid Maven Configuration.");
		}
	}

	protected File getLocalRepository(final Configuration configuration) {
		String localRepositoryPath = null;

		final ConfigurationValidationResult validateMavenConfiguration = validateMavenConfiguration(configuration);
		final Settings userSettings = validateMavenConfiguration.getUserSettings();

		if (userSettings != null) {
			localRepositoryPath = userSettings.getLocalRepository();
		} else if (validateMavenConfiguration.getGlobalSettings() != null) {
			final Settings globalSettings = validateMavenConfiguration.getGlobalSettings();
			localRepositoryPath = globalSettings.getLocalRepository();
		} else {
			return MavenEmbedder.defaultUserLocalRepository;
		}

		return getLocalRepositoryLocation(localRepositoryPath);
	}

	private File getLocalRepositoryLocation(final String localRepositoryPath) {
		if (localRepositoryPath == null) {
			return MavenEmbedder.defaultUserLocalRepository;
		}

		return new File(localRepositoryPath);
	}

	private ConfigurationValidationResult validateMavenConfiguration(final Configuration configuration) {
		return validateConfiguration(configuration);
	}

	protected Configuration mavenConfiguration() {
		final Configuration configuration = new DefaultConfiguration().setClassLoader(Thread.currentThread().getContextClassLoader()).setMavenEmbedderLogger(
				new MavenEmbedderConsoleLogger());

		if (userSettingsFile != null && userSettingsFile.exists()) {
			configuration.setUserSettingsFile(userSettingsFile);
		}
		if (globalSettingsFile != null && globalSettingsFile.exists()) {
			configuration.setGlobalSettingsFile(globalSettingsFile);
		}

		if (hasNonDefaultLocalRepository(configuration)) {
			configuration.setLocalRepository(getLocalRepository(configuration));
		}
		return configuration;
	}

	private boolean hasNonDefaultLocalRepository(final Configuration configuration) {
		return getLocalRepository(configuration) != null;
	}
}
