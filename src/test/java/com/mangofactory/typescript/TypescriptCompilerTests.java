package com.mangofactory.typescript;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.contains;

import java.io.File;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TypescriptCompilerTests extends AbstractFileManipulationTests {

	@Rule public ExpectedException exception = ExpectedException.none();
	private static TypescriptCompiler compiler = new TypescriptCompiler();
	
	@Before
	public void setup()
	{
	}

	@Test @SneakyThrows
	public void compileTypescript()
	{
		String output = compiler.compile(testResource("example.ts"));
		String expected = FileUtils.readFileToString(testResource("expected.js"));

		assertThat(output,equalTo(expected));
	}

	@SneakyThrows
	@Test
	public void compilesInputText()
	{
		String output = compiler.compile("class Greeter { greeting: string; }");
		String expected = FileUtils.readFileToString(testResource("expected-inline.js"));

		assertThat(output,equalTo(expected));
	}
	
	@SneakyThrows
	@Test
	public void compilesFollowingFileReferences()
	{
		String output = normalizeLineEndings(compiler.compile(testResource("human.ts")));
		String expected = normalizeLineEndings(FileUtils.readFileToString(testResource("human.js")));

		assertThat(output,equalTo(expected));
	}

	@Test
	@SneakyThrows
	public void es5InputShouldFailWithoutParameter()
	{
		/*exception.expect(TypescriptException.class);
		exception.expectMessage(contains("error TS1056: Accessors are only available when targeting ECMAScript 5 and higher."));
		compiler.compile(testResource("ES5-example.ts"));*/
	}
	@Test
	@SneakyThrows
	public void es5InputShouldCompileWithParameter()
	{
		compiler.setEcmaScriptVersion(EcmaScriptVersion.ES5);
		String output = normalizeLineEndings(compiler.compile(testResource("ES5-example.ts")));
		String expected = normalizeLineEndings(FileUtils.readFileToString(testResource("ES5-expected.js")));

		assertThat(output,equalTo(expected));
	}
	
	
	@Test
	public void afterFailedCompilation_that_compilationContextIsUnregistered()
	{
		int registryCount = CompilationContextRegistry.getCount();
		try {
			compiler.compile("asdfasfd");
		} catch (Exception e) {}
		assertThat(CompilationContextRegistry.getCount() , equalTo( registryCount ));
	}
	@SneakyThrows
	@Test
	public void afterSuccessfulCompilation_that_compilationContextIsUnregistered()
	{
		int registryCount = CompilationContextRegistry.getCount();
		compiler.compile("class Greeter { greeting: string; }");
		assertThat(CompilationContextRegistry.getCount() , equalTo( registryCount ));
	}
	
	@Test @SneakyThrows
	public void givenError_that_detailedErrorObjectIsAvailable()
	{
		CompilationContext context = CompilationContextRegistry.getNew(new File("."));
		context.setThrowExceptionOnCompilationFailure(false);
		compiler.compile(testResource("exampleWithError.ts"), context);
		assertThat(context.getErrorCount(), equalTo(1));
		TypescriptCompilationProblem problem = context.getProblem(0);
		assertThat(problem.getLine(),equalTo(6));
		assertThat(problem.getColumn(),equalTo(10));
		assertThat(problem.getMessage(),equalTo("error TS2131: Function declared a non-void return type, but has no return expression."));
	}

	@Test
	public void generatesCorrectEcmaScriptCommand()
	{
		compiler.setEcmaScriptVersion(EcmaScriptVersion.ES3);
		assertThat(compiler.getCompilationCommand(), equalTo("var compilationResult; compilationResult = compilerWrapper.compile(input, TypeScript.LanguageVersion.EcmaScript3, contextName, defaultLibTs)"));
		compiler.setEcmaScriptVersion(EcmaScriptVersion.ES5);
		assertThat(compiler.getCompilationCommand(), equalTo("var compilationResult; compilationResult = compilerWrapper.compile(input, TypeScript.LanguageVersion.EcmaScript5, contextName, defaultLibTs)"));
	}
}
