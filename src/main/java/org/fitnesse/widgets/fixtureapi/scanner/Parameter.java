package org.fitnesse.widgets.fixtureapi.scanner;

public class Parameter {

	private final String name;
	private final String type;

	public Parameter(final String name, final String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
}
