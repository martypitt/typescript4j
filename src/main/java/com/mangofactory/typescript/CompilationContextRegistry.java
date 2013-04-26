package com.mangofactory.typescript;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

public class CompilationContextRegistry {

	private static final Map<String, CompilationContext> contexts = Maps.newConcurrentMap();
	public static CompilationContext getNew(File basePath)
	{
		String name = UUID.randomUUID().toString();
		CompilationContext compilationContext = new CompilationContext(name,basePath);
		contexts.put(name,compilationContext);
		return compilationContext;
	}
	
	public static int getCount()
	{
		return contexts.size();
	}
	public static CompilationContext get(String name)
	{
		return contexts.get(name);
	}
	public static void destroy(CompilationContext compilationContext) {
		destroy(compilationContext.getName());
	}
	public static void destroy(String name)
	{
		contexts.remove(name);
	}
	public static boolean contains(String name)
	{
		return contexts.containsKey(name);
	}
}
