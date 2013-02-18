package com.mangofactory.typescript;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.google.common.collect.Lists;

@RequiredArgsConstructor(access=AccessLevel.PACKAGE)
public class CompilationContext {

	@Getter
	private final String name;
	@Setter
	private boolean throwExceptionOnCompilationFailure = true;
	
	@Getter
	private final List<Problem> problems = Lists.newArrayList();
	
	public void addError(Double start, Double len, String message, Double block)
	{
		problems.add(new Problem(start.intValue(),len.intValue(),message,block.intValue()));
	}

	public Integer getErrorCount() {
		return problems.size();
	}
	
	public boolean getThrowExceptionOnCompilationFailure()
	{
		return throwExceptionOnCompilationFailure;
	}
}
