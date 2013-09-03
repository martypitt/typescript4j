package com.mangofactory.typescript;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Slf4j
public class TypescriptCompiler {
	private static final String TYPESCRIPT_COMPILER = "typescript-0.9.1.1.js";
	private static final String COMPILER_WRAPPER = "typescript.compile-0.4.js";
	private static final String ENV_FILE = "env.rhino.js";

	@Getter @Setter
	private URL envJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/" + ENV_FILE);
	@Getter @Setter
	private URL typescriptJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/" + TYPESCRIPT_COMPILER);
	@Getter @Setter
	private URL typescriptCompilerJs = TypescriptCompiler.class.getClassLoader().getResource("META-INF/" + COMPILER_WRAPPER);

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
	@SneakyThrows
	public String compile(String input) throws TypescriptException {
		return compile(input, new File("."));
	}
	public String compile(String input, File basePath) throws TypescriptException {
		CompilationContext compilationContext = CompilationContextRegistry.getNew(basePath);
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
		File basePath = input.getParentFile();
		return compile(source,basePath);
	}
	public void compile(File input, File targetFile) throws TypescriptException, IOException {
		String result = compile(input);
		FileUtils.writeStringToFile(targetFile, result);
	}
	public void compile(String input, File targetFile, CompilationContext context) throws IOException, TypescriptException {
		String result = compile(input,context);
		FileUtils.writeStringToFile(targetFile, result);
	}
	public void compile(String input, File basePath, File targetFile) throws IOException, TypescriptException {
		String result = compile(input,basePath);
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

			cx.evaluateReader(scope, new InputStreamReader(envJs.openConnection().getInputStream()), ENV_FILE, 1, null);
			cx.evaluateReader(scope, new InputStreamReader(typescriptJs.openConnection().getInputStream()), TYPESCRIPT_COMPILER, 1, null);
			cx.evaluateReader(scope, new InputStreamReader(typescriptCompilerJs.openConnection().getInputStream()), COMPILER_WRAPPER, 1, null);
		}
		catch (EvaluatorException e) {
			StringBuilder message = new StringBuilder();
			message.append("Failed to initialize Typescript compiler\nerror on line " + e.lineNumber() + ": " +
					e.details() + "\n" + e.lineSource() + "\n");
			for (int x = 0; x < e.columnNumber() - 1; x++) {
				message.append(" ");
			}
			message.append("^");
			log.error(message.toString(), e);
			throw new IllegalStateException(message.toString(), e);
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
