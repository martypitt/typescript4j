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
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;

@Slf4j
public class TypescriptCompiler {

	@Getter @Setter
	private URL envJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/env.rhino.js");
	@Getter @Setter
	private URL typescriptJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/typescript-0.8.js");
	@Getter @Setter
	private URL typescriptCompilerJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/typescript.compile-0.3.js");

	private Scriptable scope;

	@Getter @Setter
	private EcmaScriptVersion ecmaScriptVersion = EcmaScriptVersion.ES3;

	@Getter @Setter
	private ModuleKind moduleKind = ModuleKind.CommonJS;

	String getCompilationCommand()
	{
		return String.format("var compilationResult; compilationResult = compilerWrapper.compile(input, %s, contextName)",ecmaScriptVersion.getJs());
	}

	public String compile(String input, CompilationContext compilationContext)  throws TypescriptException
	{
		synchronized (this) {
			if (scope == null) {
				init();
			}
		}

		long start = System.currentTimeMillis();

		try {
			Context cx = Context.enter();

			NativeObject compilationResult = new NativeObject();
			scope.put("input", scope, input);
			scope.put("contextName", scope, compilationContext.getName());
			scope.put("compilationResult", scope, compilationResult);
			cx.evaluateString(scope, getCompilationCommand(), "compile.js", 1, null);
			compilationResult = (NativeObject) scope.get("compilationResult", scope);

			String compiledSource = compilationResult.get("source").toString();
//			NativeArray problems = (NativeArray) compilationResult.get("problems");

			log.debug("Finished compilation of Typescript source in " + (System.currentTimeMillis() - start) + " ms.");

			compilationContext.throwIfCompilationFailed();

			return compiledSource;
		}
		catch (JavaScriptException e) {
			Scriptable value = (Scriptable)((JavaScriptException)e).getValue();
			if (value != null && ScriptableObject.hasProperty(value, "message")) 
			{
				String message = (String)ScriptableObject.getProperty(value, "message");
				throw new RuntimeException(message);
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			Context.exit();
		}
	}
	public String compile(String input) throws TypescriptException {
		CompilationContext compilationContext = CompilationContextRegistry.getNew();
		try {
			String result = compile(input,compilationContext);
			return result;
		} finally {
			CompilationContextRegistry.destroy(compilationContext);
		}
	}

	
	public String compile(File input, CompilationContext context) throws TypescriptException, IOException {
		String source = FileUtils.readFileToString(input);
		return compile(source, context);
	}
	public String compile(File input) throws TypescriptException, IOException {
		String source = FileUtils.readFileToString(input);
		return compile(source);
	}
	public void compile(File input, File targetFile) throws TypescriptException, IOException {
		String result = compile(input);
		FileUtils.writeStringToFile(targetFile, result);
	}
	public void compile(String input, File targetFile, CompilationContext context) throws IOException, TypescriptException {
		String result = compile(input,context);
		FileUtils.writeStringToFile(targetFile, result);
	}
	public void compile(String input, File targetFile) throws IOException, TypescriptException {
		String result = compile(input);
		FileUtils.writeStringToFile(targetFile, result);
	}

	public synchronized void init()
	{
		long start = System.currentTimeMillis();

		try {

			Context cx = Context.enter();
			cx.setOptimizationLevel(-1); 
			cx.setLanguageVersion(Context.VERSION_1_7);

			Global global = new Global(); 
			global.init(cx); 

			scope = cx.initStandardObjects(global);

			cx.evaluateReader(scope, new InputStreamReader(envJs.openConnection().getInputStream()), "env.rhino.js", 1, null);
			cx.evaluateReader(scope, new InputStreamReader(typescriptJs.openConnection().getInputStream()), "typescript-0.8.js", 1, null);
			cx.evaluateReader(scope, new InputStreamReader(typescriptCompilerJs.openConnection().getInputStream()), "typescript.compile-0.3.js", 1, null);
		}
		catch (Exception e) {
			String message = "Failed to initialize Typescript compiler.";
			log.error(message, e);
			throw new IllegalStateException(message, e);
		} finally {
			Context.exit();
		}

		log.debug("Finished initialization of typescript compiler in " + (System.currentTimeMillis() - start) + " ms.");
	}
}
