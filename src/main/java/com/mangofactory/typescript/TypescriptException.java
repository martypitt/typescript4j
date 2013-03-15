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
	private final List<TypescriptCompilationProblem> compilationProblems;
	public TypescriptException(List<TypescriptCompilationProblem> compilationProblems)
	{
		super(TypescriptCompilationProblem.getErrorMessage(compilationProblems));
		this.compilationProblems = compilationProblems;
	}
}
