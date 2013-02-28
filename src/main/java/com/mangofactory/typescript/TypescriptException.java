package com.mangofactory.typescript;

import java.util.List;

import lombok.Getter;

/**
 * Compilation exception thrown by the typescript compiler
 * @author martypitt
 *
 */
public class TypescriptException extends RuntimeException {

	@Getter
	private final List<CompilationProblem> compilationProblems;
	public TypescriptException(List<CompilationProblem> compilationProblems)
	{
		super(CompilationProblem.getErrorMessage(compilationProblems));
		this.compilationProblems = compilationProblems;
	}
}
