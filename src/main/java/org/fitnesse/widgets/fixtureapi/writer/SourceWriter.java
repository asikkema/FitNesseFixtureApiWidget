package org.fitnesse.widgets.fixtureapi.writer;


import java.util.List;

import org.fitnesse.widgets.fixtureapi.scanner.Parameter;

public interface SourceWriter {

	void addClass(String name);

	void addMethod(final String name, final String javadoc, final String returns, List<Parameter> params);

	void addPackage(final String packageName);

	@Override
	public String toString();

}