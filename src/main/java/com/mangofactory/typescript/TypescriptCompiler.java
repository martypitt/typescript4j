package com.mangofactory.typescript;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;

@Slf4j
public class TypescriptCompiler {

    private static final String COMPILE_STRING = "var result; result = compilerWrapper.compile(input)";

	@Getter @Setter
	private URL envJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/env.rhino.js");
	@Getter @Setter
	private URL typescriptJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/typescript-0.8.js");
	@Getter @Setter
	private URL typescriptCompilerJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/typescript.compile-0.3.js");

	private Context cx;
	private Scriptable scope;


	public String compile(String input) throws TypescriptException {
		if (cx == null) {
			init();
		}

		long start = System.currentTimeMillis();

		try {
			scope.put("input", scope, input);
			scope.put("result", scope, "");

			cx.evaluateString(scope, COMPILE_STRING, "compile.js", 1, null);
			Object result = scope.get("result", scope);

			log.debug("Finished compilation of Typescript source in " + (System.currentTimeMillis() - start) + " ms.");

			return result.toString();
		}
		catch (Exception e) {
			if (e instanceof JavaScriptException) {
				Scriptable value = (Scriptable)((JavaScriptException)e).getValue();
				if (value != null && ScriptableObject.hasProperty(value, "message")) {
					String message = (String)ScriptableObject.getProperty(value, "message");
					throw new TypescriptException(message, e);
				}
			}
			throw new TypescriptException(e);
		}
	}
	public String compile(File input) throws TypescriptException, IOException {
		String source = FileUtils.readFileToString(input);
		return compile(source);
	}
	public void compile(File input, File targetFile) throws TypescriptException, IOException {
		String result = compile(input);
		FileUtils.writeStringToFile(targetFile, result);
	}
	public void compile(String input, File targetFile) throws IOException, TypescriptException {
		String result = compile(input);
		FileUtils.writeStringToFile(targetFile, result);
	}

	private void init()
	{
		long start = System.currentTimeMillis();

		cx = Context.enter();
		cx.setOptimizationLevel(-1); 
		cx.setLanguageVersion(Context.VERSION_1_7);

		Global global = new Global(); 
		global.init(cx); 

		scope = cx.initStandardObjects(global);

		try {
			cx.evaluateReader(scope, new InputStreamReader(envJs.openConnection().getInputStream()), "env.rhino.js", 1, null);
			cx.evaluateReader(scope, new InputStreamReader(typescriptJs.openConnection().getInputStream()), "typescript-0.8.js", 1, null);
			cx.evaluateReader(scope, new InputStreamReader(typescriptCompilerJs.openConnection().getInputStream()), "typescript.compile-0.3.js", 1, null);
		}
		catch (Exception e) {
			String message = "Failed to initialize Typescript compiler.";
			log.error(message, e);
			throw new IllegalStateException(message, e);
		}

		log.debug("Finished initialization of typescript compiler in " + (System.currentTimeMillis() - start) + " ms.");
	}
}
