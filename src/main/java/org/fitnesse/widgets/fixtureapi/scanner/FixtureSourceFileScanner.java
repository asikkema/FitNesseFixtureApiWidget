package org.fitnesse.widgets.fixtureapi.scanner;

import static org.apache.commons.lang.StringUtils.*;

import java.io.File;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Scans the given directory (pom root) recursively and collects all files which
 * are fixtures. A file is a fixture when it's contains the word 'fixture' or
 * one of the parent directories contains the word 'fixture' or 'fixtures' The
 * fixture file should also end with '.java'
 * 
 * @author albertsikkema
 * 
 */
public class FixtureSourceFileScanner {

	/**
	 * Scans the {@link root} for fixture source files.
	 * 
	 * @param rootDir
	 *            the directory to start searching (must exist, ofcourse)
	 * @return a list mulitmap containing fixture sources per directory (to keep
	 *         nesting in place).
	 */
	public ListMultimap<File, File> scanRecursive(final File rootDir) {
		checkIfRootDirExists(rootDir);
		final ListMultimap<File, File> filesMap = new ArrayListMultimap<File, File>();
		scanForFiles(filesMap, rootDir);

		return filesMap;
	}

	/**
	 * Scans all the directoriesToScan recurively and adds it to a single
	 * listMultiMap.
	 * 
	 * @param directoriesToScan
	 *            the directories to scan.
	 * @return a list of all fixtures from all the directories which were
	 *         scanned.
	 */
	public ListMultimap<File, File> scanRecursive(final List<File> directoriesToScan) {
		final ListMultimap<File, File> allFixtures = new ArrayListMultimap<File, File>();
		for (final File dir : directoriesToScan) {
			allFixtures.putAll(scanRecursive(dir));
		}
		return allFixtures;
	}

	/**
	 * Recursively runs through the dirToScan and creates a listMultimap of
	 * files per directory which are fixture sources.
	 * 
	 */
	private void scanForFiles(final ListMultimap<File, File> filesMap, final File dirToScan) {
		for (final File file : dirToScan.listFiles()) {
			if (file.isFile()) {
				if (isFixtureFile(file)) {
					filesMap.put(dirToScan, file);
				}
			} else {
				scanForFiles(filesMap, file);
			}
		}

	}

	/**
	 * Checks if the File is a fixture Rules: If the package contains "fixture"
	 * or "fixtures" or the fileName contains "fixture" then the file is mared
	 * as a fixture.
	 * TODO: Make these rules less hardcoded, by letting them
	 * being passed in for example.
	 */
	private boolean isFixtureFile(final File file) {
		final String path = file.getAbsolutePath();
		return (containsIgnoreCase(path, "fixture/") || containsIgnoreCase(file.getPath(), "fixtures/") || containsIgnoreCase(file.getName(), "fixture"))
		&& endsWithIgnoreCase(file.getName(), ".java");
	}

	private void checkIfRootDirExists(final File rootDir) {
		if (rootDir == null || !rootDir.exists() || !rootDir.isDirectory()) {
			throw new IllegalArgumentException("Root Dir does not exist or is not a directory!, rootDir: " + rootDir);
		}
	}

}
