# Typescript 4J
This project Java library to compile Typescript files to Javascript files

The below gives a simple example that compiles a very simple snippet:

	// Instantiate the compiler:
	TypescriptCompiler compiler = new TypescriptCompiler();
	
	// Compile a string:
	String output = compiler.compile("class Greeter { greeting: string; }");
	
	// Or, compile and output to a file:
	compiler.compile(new File("example.ts"), new File('output.js'));
	
To learn more about Typescript, please visit http://www.typescriptlang.org

## I'm not that smart
This project is **HEAVILY** borrowed from other really really smart people and projects.

Specifically, thanks to:
 * [LessCss-Java](https://github.com/marceloverdijk/lesscss-java)
 * [Typescript Compile](https://github.com/niutech/typescript-compile)
 
These projects made implementing this compiler very simple.