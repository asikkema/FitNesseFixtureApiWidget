package org.fitnesse.widgets.fixtureapi.scanner;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fitnesse.widgets.fixtureapi.exception.FixtureApiPluginException;
import org.fitnesse.widgets.fixtureapi.writer.SourceWriter;

/**
 * Parses a java source file and gathers information which can be printed by
 * passing in a {@link SourceWriter}.
 * 
 * @author albertsikkema
 * 
 */
@SuppressWarnings("unchecked")
public class ApiParser {
	private CompilationUnit cu;

	/**
	 * Constructor with the absolute path of the directory to scan for source
	 * files
	 * 
	 * @param directory
	 */
	public ApiParser(final File fixtureSourceFile) {
		assert fixtureSourceFile != null && fixtureSourceFile.exists();
		try {
			if (isJavaSourceFile(fixtureSourceFile)) {
				cu = JavaParser.parse(fixtureSourceFile);
			}
		} catch (final Exception e) {
			throw new FixtureApiPluginException(e);
		}
	}

	/**
	 * Write the collected information to the writer.
	 * 
	 * @param writer
	 *            an implementation of {@link SourceWriter}
	 */
	public void execute(final SourceWriter writer) {
		writer.addPackage(cu.getPackage().getName().toString());
		final MethodVisitor methodVisitor = new MethodVisitor(writer);
		methodVisitor.visit(cu, null);
	}

	private boolean isJavaSourceFile(final File f) {
		return StringUtils.endsWithIgnoreCase(f.getName(), ".java");
	}

	@SuppressWarnings("rawtypes")
	private class MethodVisitor extends VoidVisitorAdapter {

		private final SourceWriter writer;

		public MethodVisitor(final SourceWriter wikiWriter) {
			this.writer = wikiWriter;
		}

		private String getJavaDoc(final MethodDeclaration n) {
			return n.getJavaDoc() != null ? n.getJavaDoc().toString() : "";
		}

		@Override
		public void visit(final ClassOrInterfaceDeclaration n, final Object arg) {
			super.visit(n, arg);
			writer.addClass(n.getName());
		}

		@Override
		public void visit(final MethodDeclaration methodDeclaration, final Object arg) {
			super.visit(methodDeclaration, arg);
			try {
				if (methodDeclaration.getModifiers() == ModifierSet.PUBLIC) {
					writer.addMethod(methodDeclaration.getName().toString(), getJavaDoc(methodDeclaration), methodDeclaration.getType().toString(),
							convertParams(methodDeclaration.getParameters()));
				}
			} catch (final Exception e) {
				throw new FixtureApiPluginException("MethodVisitor cannot write to writer", e);
			}
		}

		private List<Parameter> convertParams(final List<japa.parser.ast.body.Parameter> parameters) {
			if (parameters == null) {
				return Collections.emptyList();
			}
			final List<Parameter> convertedParams = new ArrayList<Parameter>();
			for (final japa.parser.ast.body.Parameter p : parameters) {
				convertedParams.add(new Parameter(p.getId().toString(), p.getType().toString()));
			}
			return convertedParams;
		}
	}

}
