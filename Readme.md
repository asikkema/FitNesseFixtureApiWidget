FitNesse - FixtureAPI
=====================

*"A fitNesse widget which prints the fixture api in html into the fitNesse wiki so that tester can see 
which fixure methods are available to them without having to go and ask the developer / read javadoc."*

**WHY**  
When you have existing code where you want to write fitNesse test for you need to know what methods are available to use in setting up tests. This plugin makes it easy to see what's available.

**Plugin installation**  
  - copy the fitnesse-fixtureapi-widget-xxx-SNAPSHOT-jar-with-dependencies.jar into the directory where fitnesse is located under /plugins (create if it does not exist)  
  - create/edit plugins.properties (also where fitnesse is located) and add:  

    WikiWidgets = org.fitnesse.widgets.fixtureapi.FixtureApiWidget

**Usage**  
After installation of the plugin add the following to a wiki page:

    !fixtureApi path-to/pom.xml
Make sure your fixture code has at least the word 'fixture' or 'fixtures' in the package or className and it should appear automatically.

**How it works**  
It scans the maven test roots for packages/classes with the name **Fixture** in it. These sources are then rendered as html in the wiki.

**Improvements/TODO**  
  - The filter for fixtures is hardcoded. Could make this configurable  
  - It only works for maven projects right now.  
  - Javadoc is not rendered particularly nice.