# Typescript 4J
This project Java library to compile Typescript files to Javascript files

## Dependency
Grab it from maven:

     <dependency>
          <groupId>com.mangofactory</groupId>
          <artifactId>typescript4j</artifactId>
          <version>0.4.0</version>
     </dependency>
     
     
The below gives a simple example that compiles a very simple snippet:

	// Instantiate the compiler:
	TypescriptCompiler compiler = new TypescriptCompiler();
	
	// Compile a string:
	String output = compiler.compile("class Greeter { greeting: string; }");
	
	// Or, compile and output to a file:
	compiler.compile(new File("example.ts"), new File('output.js'));
	
	
	// Compile using ES5 features:
	TypescriptCompiler compiler = new TypescriptCompiler();
	compiler.setEcmaScriptVersion(EcmaScriptVersion.ES5);
	... etc ...
	
To learn more about Typescript, please visit http://www.typescriptlang.org

## Credit
This project is **HEAVILY** borrowed from other really really smart people and projects.

Specifically, thanks to:
 * [LessCss-Java](https://github.com/marceloverdijk/lesscss-java)
 * [Typescript Compile](https://github.com/niutech/typescript-compile)
 
These projects made implementing this compiler very simple.

## Intent
The goal of this project is to make Typescript compilation possible from Java,
without a dependency on `npm` or `node`.

It's used in [Bakehouse](https://github.com/martypitt/bakehouse) to facilitate server-side on-demand compilation of Typescript.

This project doesn't intend to be a replacement for the typescript compiler.
Also, the primary focus of this project is *not* performance.

