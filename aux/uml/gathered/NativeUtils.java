package jRAPL;
import java.io.*;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.StandardCopyOption;
class NativeUtils {
private static final int MIN_PREFIX_LENGTH = 3;
public static final String NATIVE_FOLDER_PATH_PREFIX = "nativeutils";
private static File temporaryDir;
private NativeUtils()
public static void loadLibraryFromJar(String path) throws IOException
private static boolean isPosixCompliant()
private static File createTempDirectory(String prefix) throws IOException
}
