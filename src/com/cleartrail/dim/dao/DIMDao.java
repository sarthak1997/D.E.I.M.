package com.cleartrail.dim.dao;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.ServletContextAware;

import com.cleartrail.dim.model.FileModel;
import com.cleartrail.dim.model.JSONFileModelMapResponse;
import com.cleartrail.dim.model.JSONNotificationResponse;
import com.cleartrail.dim.model.JSONProgressResponse;
import com.cleartrail.dim.model.JSONSearchResultResponse;
import com.cleartrail.dim.model.JSONWatcherEventResponse;
import com.cleartrail.dim.websocket.SocketHandler;

@Repository
public class DIMDao extends SimpleFileVisitor<Path> implements Runnable, ServletContextAware {

	@Override
	public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
		// TODO Auto-generated method stub
		return CONTINUE;
	}

	private WatchService watcher;
	private Map<WatchKey, Path> keys;
	private Map<String, FileModel> filesList;
	private Map<String, String> allpaths;
	private Map<String, Map<String, Integer>> finalTokens;
	int i;
	private String dir;
	private long processedCount;
	private long totalBeneathFiles;

	@Autowired
	private ServletContext context;

	private SocketHandler handler = new SocketHandler();

	DIMDao() {
		filesList = new Hashtable<String, FileModel>();
		allpaths = new HashMap<String, String>();
		i = 0;
		finalTokens = new Hashtable<>();
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		keys = new HashMap<WatchKey, Path>();
		// dir=DIMDao.class.getProtectionDomain().getCodeSource().getLocation().toString();
		// dir=dir.substring(0,
		// dir.indexOf("DirectoryIntegrityManager")+("DirectoryIntegrityManager").length());
		// System.out.println(dir+"----");
		dir = "D:\\DirectoryIntegrityManager";
		processedCount = 0;
		totalBeneathFiles = 0;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
		totalBeneathFiles++;
		return CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		totalBeneathFiles++;
		return CONTINUE;
	}

	public int addPath(File f, Map<String, String> map) {

		Date date = new Date();
		// This method returns the time in millis
		long timeMilli = date.getTime();
		// System.out.println();

		if (!this.validatePath(f)) {
			return -2;
		}

		else if (this.isIndexed(f.getAbsolutePath().intern())) {
			try {
				Properties properties = new Properties();
				File prop = new File("C:\\Users\\sarthak.jain\\Desktop\\DirectoryIntegrityManager\\custom.properties");
				FileInputStream fileInput = new FileInputStream(prop);

				properties.load(fileInput);
				fileInput.close();

				if (!properties.containsKey("path-" + f.getAbsolutePath().intern())) {
					properties.clear();
					properties.setProperty("path-" + f.getAbsolutePath().intern(), f.getAbsolutePath().intern());

					FileOutputStream fout = new FileOutputStream(prop, true);
					properties.store(fout, null);
					map.put(f.getAbsolutePath().intern(), f.getAbsolutePath().intern());
					fout.close();
					return 5;
				} else
					return 4;
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		Path file;
		BasicFileAttributes attrs;
		FileModel fm;

		try {
			Properties properties = new Properties();
			File prop = new File("C:\\Users\\sarthak.jain\\Desktop\\DirectoryIntegrityManager\\custom.properties");
			FileInputStream fileInput = new FileInputStream(prop);

			properties.load(fileInput);
			fileInput.close();

			if (!properties.containsKey("path-" + f.getAbsolutePath().intern())) {
				properties.clear();
				properties.setProperty("path-" + f.getAbsolutePath().intern(), f.getAbsolutePath().intern());

				FileOutputStream fout = new FileOutputStream(prop, true);
				properties.store(fout, null);
				fout.close();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		file = f.toPath();
		totalBeneathFiles = 0;
		try {
			Files.walkFileTree(file, this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Problem while walking tree");
			e1.printStackTrace();
		}
		processedCount = 0;
		handler.setAction(new JSONProgressResponse(totalBeneathFiles, processedCount));

		fm = new FileModel();
		fm.setLength(f.length());
		fm.setName(f.getName().intern());
		fm.setPath(f.getAbsolutePath().intern());

		if (f.getParent() != null)
			fm.setParent(f.getParent());
		else
			fm.setParent("No parent Exists");

		fm.setWordCount(-1);
		fm.setLineCount(-1);
		fm.setExtension("NA");

		try {
			attrs = Files.readAttributes(file, BasicFileAttributes.class);
			fm.setCreationTime(attrs.creationTime().toMillis());
			fm.setLastAccessTime(attrs.lastAccessTime().toMillis());
			fm.setLastModifiedTime(attrs.lastModifiedTime().toMillis());
		} catch (IOException e) {
			e.printStackTrace();
		}
		fm.setDirectory(true);
		try {
			WatchKey key1 = file.register(watcher, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY);
			// System.out.println("registered with key: " + key1 + "for file : " +
			// fm.getPath());
			keys.put(key1, file);
		} catch (IOException e) {
			System.out.println("Error in registering watcher");
			e.printStackTrace();

		}

		fm.setFilesList(this.initialize(f));
		filesList.put(f.getAbsolutePath().intern(), fm);
		map.put(f.getAbsolutePath().intern(), f.getAbsolutePath().intern());

		handler.setAction(new JSONProgressResponse(++processedCount, fm.getPath()));

		// this.saveSerializedObject();
		System.out.println("Time in milliseconds using Date class: " + timeMilli);
		timeMilli = date.getTime();
		System.out.println("Time in milliseconds using Date class: " + timeMilli);
		return 2;
	}

	private boolean isIndexed(String path) {
		// TODO Auto-generated method stub
		if (filesList.containsKey(path))
			return true;
		return false;
	}

	private Map<String, FileModel> initialize(File fobj) {

		Path file;
		BasicFileAttributes attrs;
		FileModel fm;
		File allFiles[] = fobj.listFiles();
		Map<String, FileModel> tempList = new Hashtable<String, FileModel>();
		if (allFiles != null) {
			// System.out.println("allFiles -" + allFiles.length);
			for (File f : allFiles) {

				if (f.isDirectory() && this.isIndexed(f.getAbsolutePath().intern())) {
					fm = filesList.get(f.getAbsolutePath().intern());
					tempList.put(f.getAbsolutePath().intern(), fm);
					handler.setAction(new JSONProgressResponse(++processedCount, fm.getPath()));
					continue;
				}

				fm = new FileModel();

				fm.setLength(f.length());

				fm.setName(f.getName().intern());

				fm.setPath(f.getAbsolutePath().intern());

				if (f.getParent() != null)
					fm.setParent(f.getParent());
				else
					fm.setParent("No parent Exists");

				if (f.canRead() && !f.isDirectory()) {
					fm.setWordCount(this.getWordCount(f));
					fm.setLineCount(this.getLineCount(f));
					fm.setExtension(this.getExtension(f).intern());
					fm.setTokenList(this.getTokenMap(f));
					// this.serializeTokenList();
				} else {
					fm.setWordCount(-1);
					fm.setLineCount(-1);
					fm.setExtension("NA");
				}

				file = f.toPath();
				try {
					attrs = Files.readAttributes(file, BasicFileAttributes.class);
					fm.setCreationTime(attrs.creationTime().toMillis());

					fm.setLastAccessTime(attrs.lastAccessTime().toMillis());

					fm.setLastModifiedTime(attrs.lastModifiedTime().toMillis());

				} catch (IOException e) {
					e.printStackTrace();
				}
				if (f.isDirectory()) {
					fm.setDirectory(true);

					try {
						WatchKey key1 = file.register(watcher, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY);
						// System.out.println("registered with key: " + key1 + " for file : " +
						// fm.getPath());
						keys.put(key1, file);

					} catch (IOException e) {
						e.printStackTrace();
					}
					fm.setFilesList(this.initialize(f));
					filesList.put(f.getAbsolutePath().intern(), fm);
				} else {
					fm.setDirectory(false);
				}
				tempList.put(f.getAbsolutePath().intern(), fm);
				handler.setAction(new JSONProgressResponse(++processedCount, fm.getPath()));
			}
		}

		return tempList;
	}

	private Map<String, Integer> getTokenMap(File f) {

		Map<String, Integer> tempTokenMap = new Hashtable<>();
		@SuppressWarnings("unchecked")
		Set<String> stopList = (Set<String>) context.getAttribute("stoplist");
		try {

			FileInputStream input = new FileInputStream(f);
			CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
			decoder.onMalformedInput(CodingErrorAction.REPLACE);
			InputStreamReader reader = new InputStreamReader(input, decoder);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();

			StringTokenizer st;
			String token;
			String path = f.getAbsolutePath().intern();
			while (line != null) {
				if (!line.contains(decoder.replacement())) {
					st = new StringTokenizer(line, " `~!@#$%^&*()_+-=/.?<>,\'\";:[\\]}{\t0987654321");
					while (st.hasMoreElements()) {
						token = st.nextToken();
						// System.out.println(token);
						token = token.toUpperCase();
						if (!stopList.contains(token)) {
							if (tempTokenMap.containsKey(token)) {
								tempTokenMap.replace(token, tempTokenMap.get(token) + 1);
							} else {
								tempTokenMap.put(token, 1);
							}

							if (finalTokens.containsKey(token)) {
								if (finalTokens.get(token).containsKey(path)) {
									finalTokens.get(token).replace(path, finalTokens.get(token).get(path) + 1);
								} else {
									finalTokens.get(token).put(path, 1);
								}
							} else {
								Map<String, Integer> temp = new Hashtable<String, Integer>();
								temp.put(path, 1);
								finalTokens.put(token, temp);
							}
						}
					}
					line = bufferedReader.readLine();
				} else {
					bufferedReader.close();
					return null;
				}

			}
			bufferedReader.close();
			return tempTokenMap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("io problem in getTokenMap");
		}
		return null;
	}

	private int getLineCount(File f) {

		int count = 0;
		FileInputStream input;
		try {
			input = new FileInputStream(f);
			CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
			decoder.onMalformedInput(CodingErrorAction.REPLACE);
			InputStreamReader reader = new InputStreamReader(input, decoder);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();

			while (line != null) {
				if (!line.contains(decoder.replacement())) {

					count++;

					line = bufferedReader.readLine();
				} else {
					bufferedReader.close();
					return -1;
				}
			}
			bufferedReader.close();
			return count;

		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// e.printStackTrace();
			return -1;
		}
	}

	private String getExtension(File f2) {
		String name = f2.getName();
		int index = name.lastIndexOf('.');
		if (index != -1)
			name = name.substring(index + 1);
		else
			return "NA";
		if (name == null)
			return "NA";
		return name;
	}

	private int getWordCount(File f2) {

		int count = 0;

		FileInputStream input;
		StringTokenizer st;
		try {
			input = new FileInputStream(f2);
			CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
			decoder.onMalformedInput(CodingErrorAction.REPLACE);
			InputStreamReader reader = new InputStreamReader(input, decoder);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();

			while (line != null) {
				if (!line.contains(decoder.replacement())) {
					st = new StringTokenizer(line, " ");
					// System.out.println(currentLine);
					while (st.hasMoreTokens()) {
						count++;
						st.nextToken();
					}
					line = bufferedReader.readLine();
				} else {
					bufferedReader.close();
					return -1;
				}
			}
			bufferedReader.close();
			return count;

		} catch (FileNotFoundException e) {
			System.out.println("file not found in wordcount");
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("io in wordcount");
			e.printStackTrace();
			return -1;
		}
	}

	public Map<String, FileModel> getAll() {
		return filesList;
	}

	public boolean validatePath(File f) {

		if (f.exists() && f.isDirectory())
			return true;
		return false;
	}

	public void test() {
		System.out.println("from listener");
	}

	public int saveIndexFilePath(String path) {
		// TODO Auto-generated method stub

		if (!this.validatePath(new File(path))) {
			return -1;
		}

		System.out.println("inside dao");
		try {

			Properties properties = new Properties();
			File prop = new File("C:\\Users\\sarthak.jain\\Desktop\\DirectoryIntegrityManager\\custom.properties");
			FileInputStream fileInput = new FileInputStream(prop);

			properties.load(fileInput);
			fileInput.close();

			if (!properties.containsKey("indexfilepath-" + path)) {
				properties.clear();
				properties.setProperty("indexfilepath-" + path, path);
				System.out.println(
						prop.exists() + " " + prop.getAbsolutePath() + " " + prop.getName() + " " + prop.length());
				FileOutputStream fout = new FileOutputStream(prop, true);
				properties.store(fout, null);
				System.out.println(
						prop.exists() + " " + prop.getAbsolutePath() + " " + prop.getName() + " " + prop.length());
				System.out.println("stored");
				fout.close();
			} else {
				return 3;
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	@PreDestroy
	public void saveSerializedObject() {
		System.out.println("runsMainPre");
		Properties properties = new Properties();
		File prop = new File("C:\\Users\\sarthak.jain\\Desktop\\DirectoryIntegrityManager\\custom.properties");

		try {
			FileInputStream fileInput = new FileInputStream(prop);
			properties.load(fileInput);
			fileInput.close();

			Enumeration<?> en = properties.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();

				if ((key.contains("indexfilepath-"))) {
					String path = properties.getProperty(key);
					path += "\\DIMindex.ser";

					// Saving of object in a file
					FileOutputStream file = new FileOutputStream(path);
					ObjectOutputStream out = new ObjectOutputStream(file);

					// Method for serialization of object
					out.writeObject(filesList);
					out.close();
					file.close();

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@PreDestroy
	private void serializeTokenList() {
		// TODO Auto-generated method stub

		System.out.println("runsTokenPre");
		Properties properties = new Properties();
		File prop = new File("C:\\Users\\sarthak.jain\\Desktop\\DirectoryIntegrityManager\\custom.properties");

		try {
			FileInputStream fileInput = new FileInputStream(prop);
			properties.load(fileInput);
			fileInput.close();

			Enumeration<?> en = properties.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();

				if ((key.contains("indexfilepath-"))) {
					String path = properties.getProperty(key);
					path += "\\DIMindex_tokens.ser";

					// Saving of object in a file
					FileOutputStream file = new FileOutputStream(path);
					ObjectOutputStream out = new ObjectOutputStream(file);

					// Method for serialization of object
					out.writeObject(finalTokens);

					out.close();
					file.close();

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void loadSerializedObjectState() {
		System.out.println("runsMainPost");

		Properties properties = new Properties();
		File prop = new File("C:\\Users\\sarthak.jain\\Desktop\\DirectoryIntegrityManager\\custom.properties");

		try {
			FileInputStream fileInput = new FileInputStream(prop);
			properties.load(fileInput);
			fileInput.close();

			Enumeration<?> en = properties.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String path = properties.getProperty(key);
				if ((key.contains("indexfilepath-"))) {

					path += "\\DIMindex.ser";
					Path checkPath = Paths.get(path);
					if (!Files.exists(checkPath)) {
						continue;
					}

					// Reading the object from a file
					FileInputStream file = new FileInputStream(path);
					ObjectInputStream in = new ObjectInputStream(file);

					// Method for deserialization of object
					Map<String, FileModel> temp = (Map<String, FileModel>) in.readObject();
					filesList.putAll(temp);

					for (FileModel fm : temp.values()) {
						Path file1 = Paths.get(fm.getPath());
						WatchKey key1 = file1.register(watcher, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY);
						// System.out.println("registered with key: " + key1 + "for file : " +
						// fm.getPath());
						keys.put(key1, file1);
					}

					in.close();
					file.close();

				} else {
					allpaths.put(path, path);

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: I/O Exception in Loading Searialized Object");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: ClassNotFound Exception in Loading Searialized Object");
			e.printStackTrace();

		}
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void deserializeTokenList() {
		System.out.println("runsTokenPost");

		Properties properties = new Properties();
		File prop = new File("C:\\Users\\sarthak.jain\\Desktop\\DirectoryIntegrityManager\\custom.properties");

		try {
			FileInputStream fileInput = new FileInputStream(prop);
			properties.load(fileInput);
			fileInput.close();

			Enumeration<?> en = properties.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String path = properties.getProperty(key);
				if ((key.contains("indexfilepath-"))) {

					path += "\\DIMindex_tokens.ser";
					Path checkPath = Paths.get(path);
					if (!Files.exists(checkPath)) {
						continue;
					}

					// Reading the object from a file
					FileInputStream file = new FileInputStream(path);
					ObjectInputStream in = new ObjectInputStream(file);

					// Method for deserialization of object
					Map<String, Map<String, Integer>> temp = (Map<String, Map<String, Integer>>) in.readObject();
					finalTokens.putAll(temp);

					in.close();
					file.close();

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: I/O Exception in Unloading Searialized Object");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: ClassNotFound Exception in Unloading Searialized Object");
			e.printStackTrace();
		}
	}

	public Map<String, String> loadPaths() {
		// TODO Auto-generated method stub

		this.loadSerializedObjectPaths();
		return allpaths;
	}

	private void loadSerializedObjectPaths() {

		Properties properties = new Properties();
		File prop = new File("C:\\Users\\sarthak.jain\\Desktop\\DirectoryIntegrityManager\\custom.properties");

		try {
			FileInputStream fileInput = new FileInputStream(prop);
			properties.load(fileInput);
			fileInput.close();

			Enumeration<?> en = properties.keys();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String path = properties.getProperty(key);
				if (!(key.contains("indexfilepath-"))) {

					allpaths.put(path, path);

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: I/O Exception in Loading Paths in Searialized Object");
			e.printStackTrace();
		}

	}

	public JSONFileModelMapResponse getMetaForPaths(List<String> paths) {

		JSONFileModelMapResponse res = new JSONFileModelMapResponse();
		List<FileModel> result = new ArrayList<>();

		System.out.println(filesList.size() + " " + paths.size() + " " + paths.get(0));

		for (String p : paths) {
			// System.out.println(p+" "+filesList.get(p).getFilesList().size());
			// System.out.println(filesList.get(p));
			result.add(filesList.get(p));
			System.out.println(p);
			if (filesList.get(p).getFilesList() != null) {
				for (FileModel fm : filesList.get(p).getFilesList().values()) {
					result.add(fm);
				}
			}
		}
		if (result.isEmpty())
			res.setCode(-1);
		else
			res.setCode(1);
		res.setResult(result);
		return res;
	}

	public JSONFileModelMapResponse getMetaForSubPaths(String path) {
		JSONFileModelMapResponse res = new JSONFileModelMapResponse();
		List<FileModel> result = new ArrayList<>();

		if (filesList.get(path).getFilesList() != null) {
			for (FileModel fm : filesList.get(path).getFilesList().values()) {
				result.add(fm);
			}
		}
		if (result.isEmpty())
			res.setCode(-1);
		else
			res.setCode(1);
		res.setResult(result);
		return res;
	}

	@Override
	public void run() {
		WatchKey key = null;
		System.out.println("inside thread");
		while (true) {

			try {
				// key = watcher.poll(2, TimeUnit.SECONDS);
				key = watcher.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR: Interrupted Excpetion in polling watcher");
				e.printStackTrace();
			}

			// key=watcher.poll();
			// System.out.println("got the key" + keys.get(key));

			Path dir = keys.get(key);
			if (dir == null) {
				// throw custom exception that key is not recognized
				// System.out.println("null coming on dir");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				@SuppressWarnings("rawtypes")
				WatchEvent.Kind kind = event.kind();

				if (kind == OVERFLOW) {
					continue;
				}

				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path name = ev.context();
				Path child = dir.resolve(name);

				System.out.println("event : " + kind.name() + " file: " + name);
				// FileModel fm = getAll().values().iterator().next();
				if (kind == ENTRY_CREATE) {

					System.out.println("going to enter new");
					this.addEntry(child);

				} else if (kind == ENTRY_DELETE) {
					System.out.println("going to delete");
					this.deleteEntry(child);
					if (Files.isDirectory(child)) {
						keys.remove(key);
					}
				} else {
					System.out.println("going to update");
					this.modifyEntry(child);
				}

			}

			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);
				if (keys.isEmpty()) {
					filesList.clear();
					break;
				}
			}

		}
	}

	private void modifyEntry(Path child) {

		File f = new File(child.toString());
		Path file;
		BasicFileAttributes attrs;

		if (f.isDirectory())
			return;
		if (!filesList.containsKey(child.getParent().toString()))
			return;
		else {
			FileModel fm1 = filesList.get(child.getParent().toString());
			for (String token : fm1.getFilesList().get(child.toString()).getTokenList().keySet()) {
				finalTokens.get(token).remove(child.toString());
			}

			FileModel fm = new FileModel();
			fm.setLength(f.length());

			fm.setName(f.getName().intern());

			fm.setPath(f.getAbsolutePath().intern());

			fm.setParent(f.getParentFile().getName().intern());
			if (f.canRead()) {
				fm.setWordCount(this.getWordCount(f));
				fm.setLineCount(this.getLineCount(f));
				fm.setExtension(this.getExtension(f).intern());
				fm.setTokenList(this.getTokenMap(f));
				// this.serializeTokenList();
			} else {
				fm.setWordCount(-1);
				fm.setLineCount(-1);
				fm.setExtension("NA");
			}

			file = f.toPath();
			try {
				attrs = Files.readAttributes(file, BasicFileAttributes.class);
				fm.setCreationTime(attrs.creationTime().toMillis());

				fm.setLastAccessTime(attrs.lastAccessTime().toMillis());

				fm.setLastModifiedTime(attrs.lastModifiedTime().toMillis());

			} catch (IOException e) {
				System.out.println("ERROR: I/O Excpetion in modify entry");
				e.printStackTrace();
			}

			fm.setDirectory(false);

			fm1.getFilesList().replace(child.toString(), fm);
			handler.setAction(new JSONWatcherEventResponse(1, "mod", fm.getPath(), fm));
		}

	}

	private void deleteEntry(Path child) {

		FileModel fm2 = filesList.get(child.toString());

		if (fm2 != null && fm2.isDirectory()) {
			System.out.println("inside deleteing dir");
			for (FileModel fm : fm2.getFilesList().values()) {
				if (fm.isDirectory()) {
					this.deleteEntry(fm.getPath());
				} else {
					for (String token : fm.getTokenList().keySet()) {
						finalTokens.get(token).remove(fm.getPath());
					}
					fm2.getFilesList().remove(fm.getPath());
					handler.setAction(new JSONWatcherEventResponse(1, "del", fm.getPath()));
				}
			}
			if (filesList.containsKey(child.getParent().toString())) {
				FileModel fm1 = filesList.get(child.getParent().toString());
				filesList.remove(child.toString());
				fm1.getFilesList().remove(child.toString());
				handler.setAction(new JSONWatcherEventResponse(1, "del", child.toString()));
			} else {
				filesList.remove(child.toString());
				handler.setAction(new JSONWatcherEventResponse(1, "del", child.toString()));
			}
		} else {
			System.out.println("inside deleteing file");
			FileModel fm1 = filesList.get(child.getParent().toString());

			for (String token : fm1.getFilesList().get(child.toString()).getTokenList().keySet()) {
				finalTokens.get(token).remove(child.toString());
			}

			fm1.getFilesList().remove(child.toString());
			handler.setAction(new JSONWatcherEventResponse(1, "del", child.toString()));
		}
		// this.serializeTokenList();
		// this.saveSerializedObject();
	}

	private void deleteEntry(String path) {

		for (FileModel fm : filesList.get(path).getFilesList().values()) {
			if (fm.isDirectory()) {
				this.deleteEntry(fm.getPath());
			} else {
				for (String token : fm.getTokenList().keySet()) {
					finalTokens.get(token).remove(fm.getPath());
				}
				filesList.get(path).getFilesList().remove(fm.getPath());
				handler.setAction(new JSONWatcherEventResponse(1, "del", fm.getPath()));
			}
		}
		filesList.remove(path);
		handler.setAction(new JSONWatcherEventResponse(1, "del", path));
	}

	private void addEntry(Path child) {
		// TODO Auto-generated method stub

		Path file;
		BasicFileAttributes attrs;

		File f = new File(child.toString());
		FileModel fm1 = filesList.get(child.getParent().toString());
		FileModel fm = new FileModel();
		if (f.isDirectory()) {
			fm.setLength(f.length());
			fm.setName(f.getName().intern());
			fm.setPath(f.getAbsolutePath().intern());
			fm.setParent(f.getParentFile().getName().intern());

			fm.setWordCount(-1);
			fm.setLineCount(-1);
			fm.setExtension("NA");

			file = f.toPath();
			try {
				attrs = Files.readAttributes(file, BasicFileAttributes.class);
				fm.setCreationTime(attrs.creationTime().toMillis());
				fm.setLastAccessTime(attrs.lastAccessTime().toMillis());
				fm.setLastModifiedTime(attrs.lastModifiedTime().toMillis());
			} catch (IOException e) {
				System.out.println("ERROR: I/O Excpetion in ading entry");
				e.printStackTrace();

			}
			fm.setDirectory(true);
			try {
				WatchKey key1 = file.register(watcher, ENTRY_DELETE, ENTRY_CREATE, ENTRY_MODIFY);
				// System.out.println("registered with key: " + key1 + "for file : " +
				// fm.getPath());
				keys.put(key1, file);
			} catch (IOException e) {
				System.out.println("ERROR: I/O Excpetion in registering watcher");
				e.printStackTrace();
			}

			fm.setFilesList(this.initialize(f));
			fm1.getFilesList().put(child.toString(), fm);
			filesList.put(f.getAbsolutePath().intern(), fm);
			handler.setAction(new JSONWatcherEventResponse(1, "add", fm.getPath(), fm));
		} else {
			fm.setLength(f.length());

			fm.setName(f.getName().intern());

			fm.setPath(f.getAbsolutePath().intern());

			fm.setParent(f.getParentFile().getName().intern());
			if (f.canRead()) {
				fm.setWordCount(this.getWordCount(f));
				fm.setLineCount(this.getLineCount(f));
				fm.setExtension(this.getExtension(f).intern());
				fm.setTokenList(this.getTokenMap(f));
				// this.serializeTokenList();
			} else {
				fm.setWordCount(-1);
				fm.setLineCount(-1);
				fm.setExtension("NA");
			}

			file = f.toPath();
			try {
				attrs = Files.readAttributes(file, BasicFileAttributes.class);
				fm.setCreationTime(attrs.creationTime().toMillis());

				fm.setLastAccessTime(attrs.lastAccessTime().toMillis());

				fm.setLastModifiedTime(attrs.lastModifiedTime().toMillis());

			} catch (IOException e) {
				System.out.println("ERROR: I/O Excpetion in add entry in reading attributes");
				e.printStackTrace();
			}

			fm.setDirectory(false);

			fm1.getFilesList().put(child.toString(), fm);
			handler.setAction(new JSONWatcherEventResponse(1, "add", fm.getPath(), fm));
		}

		// this.saveSerializedObject();
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.context = servletContext;
	}

	public JSONNotificationResponse addStopWord(String word) {

		try {
			word = word.toUpperCase();
			Connection con = (Connection) context.getAttribute("datacon");
			PreparedStatement ps;
			ResultSet rs;
			String check = "select words from stopwords where words=?";
			ps = con.prepareStatement(check);
			ps.setString(1, word);
			rs = ps.executeQuery();
			if (rs.next())
				return new JSONNotificationResponse(2);
			String query = "insert into stopwords values(?)";
			ps = con.prepareStatement(query);
			ps.setString(1, word);
			if (ps.executeUpdate() > 0)
				return new JSONNotificationResponse(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new JSONNotificationResponse(-1);
	}

	public List<JSONSearchResultResponse> searchToken(String word) {
		// TODO Auto-generated method stub
		word = word.toUpperCase();
		List<JSONSearchResultResponse> result = new ArrayList<>();

		if (finalTokens.containsKey(word)) {
			for (String path : finalTokens.get(word).keySet()) {
				FileModel fm = this.getPathFM(path).getFilesList().get(path);
				// res.put(fm, finalTokens.get(word).get(path));
				result.add(new JSONSearchResultResponse(1, fm, finalTokens.get(word).get(path)));
			}
			return result;
		} else {
			result.add(new JSONSearchResultResponse(-1));
			return result;
		}

	}

	private FileModel getPathFM(String path) {
		int index = path.lastIndexOf('\\');
		if (index == -1 || index == (path.length() - 1))
			return null;
		String parent = path.substring(0, index);

		return filesList.get(parent);
	}

	public String getContent(String path) {
		// TODO Auto-generated method stub

		File f = new File(path);

		FileInputStream input;
		try {
			input = new FileInputStream(f);
			CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
			decoder.onMalformedInput(CodingErrorAction.REPLACE);
			InputStreamReader reader = new InputStreamReader(input, decoder);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();
			String result = "";

			while (line != null) {
				result += line + "\n";
				line = bufferedReader.readLine();

			}

			bufferedReader.close();
			return result;
		} catch (FileNotFoundException e) {
			System.out.println("file not found in get content");
			e.printStackTrace();
			return "!File can not be read!";
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("io in get content");
			e.printStackTrace();
			return "!File can not be read!";
		}
	}

	public Map<String, Map<String, Integer>> getAllTokens() {
		// TODO Auto-generated method stub
		return finalTokens;
	}

	public void download(HttpServletRequest request, HttpServletResponse response, String path) {
		// TODO Auto-generated method stub
		//System.out.println("inside download dao");
		File downloadFile = new File(path);
		try {
			FileInputStream inputStream = new FileInputStream(downloadFile);
			String mimeType = request.getServletContext().getMimeType(path);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			OutputStream outStream = response.getOutputStream();
			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot download. " + path + " not found.");
		} catch (IOException e) {
			System.out.println("ERROR: Cannot download. " + path + " not found.");
		}

	}
}