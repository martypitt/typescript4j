/* *****************************************************************************
Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0 

THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION ANY IMPLIED
WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A PARTICULAR PURPOSE, 
MERCHANTABLITY OR NON-INFRINGEMENT. 

See the Apache Version 2.0 License for specific language governing permissions
and limitations under the License.
 ***************************************************************************** */
var compilerWrapper = {
	compile : function(src,codeGenTarget,contextName,defaultLibString) {
		//Packed declaration file lib.d.ts
		var compilationResult = { source: ''};
		var compilationContext = Packages.com.mangofactory.typescript.CompilationContextRegistry.get(contextName);

		TypeScript.Environment = {newLine: "\n"};

		var compSettings = new TypeScript.CompilationSettings();
		compSettings.removeComments = true;
		compSettings.codeGenTarget = codeGenTarget;
		var immutableCompSettings = TypeScript.ImmutableCompilationSettings.fromCompilationSettings(compSettings);
		var compiler = new TypeScript.TypeScriptCompiler(new TypeScript.NullLogger(), immutableCompSettings);

		if(src.length == 0) {
			return '';
		}

		var fl = TypeScript.ScriptSnapshot.fromString(defaultLibString);
		compiler.addFile('lib.d.ts', fl, TypeScript.ByteOrderMark.None, 0,false, []);

		var sourceUnitsToParse = [ { content: src, path: 'code.ts' } ];
		var parsedUnits = {};

		var recordDiagnostic = function(d){
			var lineMap = d.lineMap();
			var lineCol = { line: -1, character: -1 };
			lineMap.fillLineAndCharacterFromPosition(d.start(), lineCol);
			var problem = {
				line: d.line() + 1,
				col: d.character() + 1,
				message: d.message(),
				compilationUnitIndex: 0
			};
			compilationContext.addError(problem);
		};

		//Walk over all the source units, adding references to the end
		while (sourceUnitsToParse.length > 0) {
			var sourceToParse = sourceUnitsToParse.shift();
			parsedUnits[sourceToParse.path] = sourceToParse.content;

			var code = TypeScript.ScriptSnapshot.fromString(sourceToParse.content);
			var fileReferencesInSource = TypeScript.getReferencedFiles(sourceToParse.path, code);

			//Use our context to resolve the dependencies/references
			var referencedSourceUnits = compilationContext.resolveFiles(fileReferencesInSource, sourceToParse);

			//Add the source file to the compiler with the list of its references
			compiler.addFile(sourceToParse.path, code, TypeScript.ByteOrderMark.None, 0, false, referencedSourceUnits);

			//Parse the source file
			var syntacticDiagnostics = compiler.getSyntacticDiagnostics(sourceToParse.path);
			syntacticDiagnostics.forEach(function(element) {
				recordDiagnostic(element);
			});

			//Add the references to the "to parse" list
			for( var i = 0; i < referencedSourceUnits.length; i++ )
			{
				var sourceUnit = referencedSourceUnits[i];
				//Make sure we haven't already parsed this file before
				if (!parsedUnits[sourceUnit.path])
				{
					sourceUnitsToParse.push(sourceUnit);
				}
			}
		}

		//Now that we've parsed all the files, we can do the semantic analysis and code emission
		var files = compiler.fileNames();

		var emitted = {};
		files.forEach(function (element, index, array){
			var semanticDiagnostics = compiler.getSemanticDiagnostics(element);
			semanticDiagnostics.forEach(function(element) {
				recordDiagnostic(element);
			});
		});

		var emitOutput = compiler.emitAll(compilationContext.basePath);
		emitOutput.outputFiles.forEach(function(f) {
			if (emitted[f.name] === undefined) {
				compilationResult.source += f.text;
				emitted[f.name] = f.text;
			}
		});

		return compilationResult;
	}
};


