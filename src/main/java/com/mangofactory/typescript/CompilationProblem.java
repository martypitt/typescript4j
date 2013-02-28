package com.mangofactory.typescript;

import java.util.List;

import lombok.Data;

import org.mozilla.javascript.NativeObject;

@Data
public class CompilationProblem {

	private final Integer line;
	private final Integer column;
	private final String message;
	private final Integer block;
	public static CompilationProblem fromNativeObject(NativeObject jsProblem) {
		Double line = (Double) jsProblem.get("line");
		Double column = (Double) jsProblem.get("col");
		String message = (String) jsProblem.get("message");
		Double compilationUnitIndex = (Double) jsProblem.get("compilationUnitIndex");
		
		return new CompilationProblem(line.intValue(),column.intValue(),message,compilationUnitIndex.intValue());
	}
	
	@Override
	public String toString()
	{
		return message  + " (" + line + " , " + column + ")";
	}
	
	
	public static String getErrorMessage(List<CompilationProblem> problems)
	{
		StringBuilder sb = new StringBuilder();
		for (CompilationProblem problem : problems)
		{
			if (sb.length() > 0)
				sb.append("\n");
			
			sb.append(problem.toString());
		}
		return sb.toString();
	}

}
