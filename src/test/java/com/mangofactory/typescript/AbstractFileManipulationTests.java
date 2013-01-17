package com.mangofactory.typescript;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;

/**
 * Base class for tests that need to manipulate files.
 * Provides easy access for generating temp copies of the source
 * file for use in the test, and cleans up afterwards.
 * 
 * @author martypitt
 *
 */
public abstract class AbstractFileManipulationTests {

	private Set<File> filesToCleanUp;
	@Getter
	private File generatedAssetsFolder;
	@Before @SneakyThrows
	public void prepareTempFiles()
	{
		filesToCleanUp = new HashSet<File>();
		generatedAssetsFolder = new File("src/test/resources/generated");
		// Clean up in case any previous tests failed to do so
		FileUtils.deleteDirectory(generatedAssetsFolder);
		FileUtils.forceMkdir(generatedAssetsFolder);
	}
	@SneakyThrows
	private File asGeneratedAsset(String fileName)
	{
		String newPath = FilenameUtils.concat(generatedAssetsFolder.getCanonicalPath(), fileName);
		File file = new File(newPath);
		filesToCleanUp.add(file);
		return file;
	}
	
	@After @SneakyThrows
	public void cleanUpTempFiles()
	{
		for (File tempFile : filesToCleanUp)
		{
			tempFile.delete();
		}
		FileUtils.deleteDirectory(generatedAssetsFolder);
	}
	
	protected File testResource(String filename)
	{
		return new File("src/test/resources/" + filename);
	}
	
	protected void delete(Iterable<File> files)
	{
		for (File file : files) {
			file.delete();
		}
	}
	protected File getNewTempFile(String suffix)
	{
		String fileName = UUID.randomUUID().toString() + "." + suffix;
		return asGeneratedAsset(fileName);
	}
	@SneakyThrows
	protected File getTempCopyOf(File file)
	{
		return getTempCopyOf(file.getCanonicalPath());
	}
	@SneakyThrows
	protected File getTempCopyOf(String sourcePath) {
		File source = new File(sourcePath);
		File tempFile = new File(UUID.randomUUID().toString());
		FileUtils.copyFile(source, tempFile);
		filesToCleanUp.add(tempFile);
		return tempFile;
	}
	protected String normalizeLineEndings(String input) {
		input = input.replaceAll("\\r\\n", "\n");
		input = input.replaceAll("\\r", "\n");
		input = StringUtils.trim(input);
		return input;
	}
}
