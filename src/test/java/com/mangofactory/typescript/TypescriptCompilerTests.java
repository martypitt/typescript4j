package com.mangofactory.typescript;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.contains;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TypescriptCompilerTests extends AbstractFileManipulationTests {

	@Rule public ExpectedException exception = ExpectedException.none();
	private TypescriptCompiler compiler = new TypescriptCompiler();
	
	@Before
	public void setup()
	{
//		this.compiler = new TypescriptCompiler();
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

	@Test
	@SneakyThrows
	public void es5InputShouldFailWithoutParameter()
	{
		exception.expect(TypescriptException.class);
		exception.expectMessage(contains("Compilation error: Property accessors are only available when targeting ES5 or greater"));
		compiler.compile(testResource("ES5-example.ts"));
	}
	@Test
	@SneakyThrows
	public void es5InputShouldCompileWithParameter()
	{
		compiler.setEcmaScriptVersion(EcmaScriptVersion.ES5);
		String output = compiler.compile(testResource("ES5-example.ts"));
		String expected = FileUtils.readFileToString(testResource("ES5-expected.js"));

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
		CompilationContext context = CompilationContextRegistry.getNew();
		context.setThrowExceptionOnCompilationFailure(false);
		compiler.compile(testResource("exampleWithError.ts"), context);
		assertThat(context.getErrorCount(), equalTo(1));
		CompilationProblem problem = context.getProblem(0);
		assertThat(problem.getLine(),equalTo(6));
		assertThat(problem.getColumn(),equalTo(2));
		assertThat(problem.getMessage(),equalTo("Function declared a non-void return type, but has no return expression"));
	}

	@Test
	public void generatesCorrectEcmaScriptCommand()
	{
		compiler.setEcmaScriptVersion(EcmaScriptVersion.ES3);
		assertThat(compiler.getCompilationCommand(), equalTo("var compilationResult; compilationResult = compilerWrapper.compile(input, TypeScript.CodeGenTarget.ES3, contextName)"));
		compiler.setEcmaScriptVersion(EcmaScriptVersion.ES5);
		assertThat(compiler.getCompilationCommand(), equalTo("var compilationResult; compilationResult = compilerWrapper.compile(input, TypeScript.CodeGenTarget.ES5, contextName)"));
	}
}
