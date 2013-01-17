package com.mangofactory.typescript;

import lombok.Getter;

public enum EcmaScriptVersion {

	ES3("TypeScript.CodeGenTarget.ES3"),
	ES5("TypeScript.CodeGenTarget.ES5");
	
	@Getter
	private String js;
	private EcmaScriptVersion(String js)
	{
		this.js = js;
	}
}
