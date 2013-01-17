package com.mangofactory.typescript;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class TypescriptCompilerTests extends AbstractFileManipulationTests {

	private TypescriptCompiler compiler;
	@Before
	public void setup()
	{
		this.compiler = new TypescriptCompiler();
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
}
