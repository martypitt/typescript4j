package com.mangofactory.typescript;

import java.io.File;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
public class CompilationContext {

	@Getter
	private final String name;
	
	private final File basePath;
	
	private final Map<String, NativeObject> generatedFileReferences = Maps.newHashMap();
	@Setter
	private boolean throwExceptionOnCompilationFailure = true;
	
	@Getter
	private final List<TypescriptCompilationProblem> problems = Lists.newArrayList();
	
	public void addError(NativeObject error)
	{
		problems.add(TypescriptCompilationProblem.fromNativeObject(error));
	}

	public Integer getErrorCount() {
		return problems.size();
	}
	
	public void throwIfCompilationFailed()
	{
		if (!throwExceptionOnCompilationFailure)
			return;
		if (problems.isEmpty())
			return;
		throw new TypescriptException(problems);
		
	}
	public boolean getThrowExceptionOnCompilationFailure()
	{
		return throwExceptionOnCompilationFailure;
	}

	@SneakyThrows
	public NativeArray resolveFiles(NativeArray referencedFiles)
	{
		List<NativeObject> codeUnits = Lists.newArrayList();
		for (int i = 0; i < referencedFiles.size(); i++)
		{
			NativeObject referencedFile = (NativeObject) referencedFiles.get(i);
			String path = (String) referencedFile.get("path");
			String fullPath = FilenameUtils.concat(basePath.getCanonicalPath(), path);
//			if (generatedFileReferences.containsKey(fullPath))
////			{
//				continue;
//			}
			
			String source = FileUtils.readFileToString(new File(fullPath));
			
			val codeUnit = new NativeObject();
			NativeObject.putProperty(codeUnit, "content", source);
			NativeObject.putProperty(codeUnit, "path", path);
			codeUnits.add(codeUnit);
			generatedFileReferences.put(fullPath, codeUnit);
		}
		NativeObject[] nativeObjects = codeUnits.toArray(new NativeObject[]{});
		return new NativeArray(nativeObjects);
	}
	public TypescriptCompilationProblem getProblem(int i) {
		return getProblems().get(i);
	}

	public boolean hasProblems() {
		return !problems.isEmpty();
	}
}
