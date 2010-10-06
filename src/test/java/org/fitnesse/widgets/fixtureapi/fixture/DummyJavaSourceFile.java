package org.fitnesse.widgets.fixtureapi.fixture;

public class DummyJavaSourceFile {

	/**
	 * Prints whatever is said as <i>anyThing</i>
	 * 
	 * @param anyThing any string
	 * @param aNumber any number
	 */
	public void saySomething(final String anyThing, final Integer aNumber) {
	}

	/**
	 * Dummy method for testing (not compiled).
	 * 
	 * @return always true.
	 */
	public boolean willBeTrue() {
		return true;
	}

	private void aPrivateMethodWhichShouldNotBeRendered() {
	}

	protected void aProtectedMethodWhichShouldNotBeRendered() {

	}
}
