package com.mangofactory.typescript;

import lombok.Getter;

public enum EcmaScriptVersion {

	ES3("TypeScript.LanguageVersion.EcmaScript3"),
	ES5("TypeScript.LanguageVersion.EcmaScript5");

	@Getter
	private String js;
	private EcmaScriptVersion(String js)
	{
		this.js = js;
	}
}
