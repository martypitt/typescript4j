package com.mangofactory.typescript;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.mozilla.javascript.NativeObject;

import com.google.common.collect.Lists;

@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
public class CompilationContext {

	@Getter
	private final String name;
	@Setter
	private boolean throwExceptionOnCompilationFailure = true;
	
	@Getter
	private final List<CompilationProblem> problems = Lists.newArrayList();
	
	public void addError(NativeObject error)
	{
		problems.add(CompilationProblem.fromNativeObject(error));
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

	public CompilationProblem getProblem(int i) {
		return getProblems().get(i);
	}
}
