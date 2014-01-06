package org.rpi.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;

import org.apache.log4j.Logger;


public class ConfigureLibraries {
	
	private static Logger log = Logger.getLogger(ConfigureLibraries.class);
	
	public ConfigureLibraries()
	{
		SetJavaPath();
	}
	
	public void addLibraryPath(String pathToAdd) throws Exception {
		Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		String[] paths = (String[]) usrPathsField.get(null);

		for (String path : paths)
			if (path.equals(pathToAdd))
				return;

		String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = pathToAdd;
		usrPathsField.set(null, newPaths);
	}
	
	/**
	 * Set the Path to the ohNetxx.so files
	 */
	private void SetJavaPath() {
		try

		{
			String class_name = this.getClass().getName();
			log.debug("Find Class, ClassName: " + class_name);
			String path = getFilePath(class_name);
			log.debug("Path of this File is: " + path);
			String os = System.getProperty("os.name").toUpperCase();
			log.debug("OS Name: " + os);
			if (os.startsWith("WINDOWS")) {
				log.debug("Windows OS");
				// System.setProperty("java.library.path", path +
				// "/mediaplayer_lib/ohNet/win32");
				addToLibPath(path + "/mediaplayer_lib/ohNet/win32");
			} else if (os.startsWith("LINUX")) {
				String arch = System.getProperty("os.arch").toUpperCase();
				if (arch.startsWith("ARM")) {
					log.debug("Its a Raspi, check for HardFloat or SoftFloat");
					// readelf -a /usr/bin/readelf | grep armhf
					boolean hard_float = true;
					String command = "dpkg -l | grep 'armhf\\|armel'";
					String full_path = path + "/mediaplayer_lib/ohNet/raspi/hard_float";
					try {
						Process pa = Runtime.getRuntime().exec(command);
						pa.waitFor();
						BufferedReader reader = new BufferedReader(new InputStreamReader(pa.getInputStream()));
						String line;
						while ((line = reader.readLine()) != null) {
							log.debug("Result of " + command + " : " + line);
							if (line.toUpperCase().contains("ARMHF")) {
								log.debug("HardFloat Raspi Set java.library.path to be: " + path);

								hard_float = true;
								break;
							} else if (line.toUpperCase().contains("ARMEL")) {
								full_path = path + "/mediaplayer_lib/ohNet/raspi/soft_float";
								log.debug("SoftFloat Raspi Set java.library.path to be: " + path);
								hard_float = false;
								break;
							}

						}
					} catch (Exception e) {
						log.debug("Error Determining Raspi OS Type: ", e);
					}
					addLibraryPath(full_path);
				}

			}
			// Field fieldSysPath =
			// ClassLoader.class.getDeclaredField("sys_paths");
			// fieldSysPath.setAccessible(true);
			// fieldSysPath.set(null, null);
		} catch (Exception e) {
			log.error(e);
		}

	}

	/**
	 * Not clever enough to work out how to override ClassLoader functionality,
	 * so using this nice trick instead..
	 * 
	 * @param path
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	static void addToLibPath(String path) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (System.getProperty("java.library.path") != null) {
			// If java.library.path is not empty, we will prepend our path Note
			// that path.separator is ; on Windows and : on Unix-like, so we
			// can't hard code it.
			System.setProperty("java.library.path", path + System.getProperty("path.separator") + System.getProperty("java.library.path"));
		} else {
			System.setProperty("java.library.path", path);
		}

		// Important: java.library.path is cached We will be using reflection to
		// clear the cache
		Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
		fieldSysPath.setAccessible(true);
		fieldSysPath.set(null, null);
	}

	/***
	 * Get the Path of this ClassFile Must be easier ways to do this!!!!
	 * 
	 * @param className
	 * @return
	 */
	private String getFilePath(String className) {
		if (!className.startsWith("/")) {
			className = "/" + className;
		}
		className = className.replace('.', '/');
		className = className + ".class";
		log.debug("Find Class, Full ClassName: " + className);
		String[] splits = className.split("/");
		String properName = splits[splits.length - 1];
		log.debug("Find Class, ClassName: " + properName);
		URL classUrl = new MainFilePath().getClass().getResource(className);
		if (classUrl != null) {
			String temp = classUrl.getFile();
			log.debug("Find Class, ClassURL: " + temp);
			if (temp.startsWith("file:")) {
				temp = temp.substring(5);
			}

			if (temp.toUpperCase().contains(".JAR!")) {
				log.debug("Find Class, This is a JarFile: " + temp);
				String[] parts = temp.split("/");
				String jar_path = "";
				for (String part : parts) {
					if (!part.toUpperCase().endsWith(".JAR!")) {
						jar_path += part + "/";
					} else {
						log.debug("Find File: Returning JarPath: " + jar_path);
						return jar_path;
					}
				}
			} else {
				log.debug("Find Class, This is NOT a Jar File: " + temp);
				if (temp.endsWith(className)) {
					temp = temp.substring(0, (temp.length() - className.length()));
				}
			}
			log.debug("Find File: Returning FilePath: " + temp);
			return temp;
		} else {
			log.debug("Find Class, URL Not Found");
			return "\nClass '" + className + "' not found in \n'" + System.getProperty("java.class.path") + "'";
		}
	}

}
