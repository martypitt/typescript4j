package com.mangofactory.typescript;

import java.util.List;

import lombok.Data;

import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.NativeObject;

@Data
public class TypescriptCompilationProblem {

	private final Integer line;
	private final Integer column;
	private final String message;
	private final Integer block;
	public static TypescriptCompilationProblem fromNativeObject(NativeObject jsProblem) {
		Double line = (Double) jsProblem.get("line");
		Double column = (Double) jsProblem.get("col");
		String message = extractMessage(jsProblem);
		Double compilationUnitIndex = (Double) jsProblem.get("compilationUnitIndex");
		
		return new TypescriptCompilationProblem(line.intValue(),column.intValue(),message,compilationUnitIndex.intValue());
	}
	
	private static String extractMessage(NativeObject jsProblem) {
		 Object rawMessage = jsProblem.get("message");
		 if (rawMessage instanceof ConsString)
		 {
			 return ((ConsString) rawMessage).toString();
		 } else if (rawMessage instanceof String) {
			 return (String) rawMessage;
		 } else {
			 throw new IllegalStateException("Unable to extract an error message from type " + rawMessage.getClass().getCanonicalName());
		 }
	}

	@Override
	public String toString()
	{
		return message  + " (" + line + " , " + column + ")";
	}
	
	
	public static String getErrorMessage(List<TypescriptCompilationProblem> problems)
	{
		StringBuilder sb = new StringBuilder();
		for (TypescriptCompilationProblem problem : problems)
		{
			if (sb.length() > 0)
				sb.append("\n");
			
			sb.append(problem.toString());
		}
		return sb.toString();
	}

}
