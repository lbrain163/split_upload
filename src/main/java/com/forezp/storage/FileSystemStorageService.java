package com.forezp.storage;

import com.forezp.utils.DefineUtils;
import com.forezp.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public boolean exists(String filename) {
        return Files.exists(this.rootLocation.resolve(filename));
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }

            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void deleteFiles(String[] files) {
        for(String file : files) {
            FileSystemUtils.deleteRecursively(rootLocation.resolve(file).toFile());
        }
    }

    @Override
    public void init() {
        try {
            if(!Files.exists(rootLocation))
                Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public Map<String, String> doMergeFile(String fileName) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("code", "0");
        map.put("desc", "OK");

        List<String> fileList = new ArrayList<String>();
        List<String> filePathList = new ArrayList<String>();
        Files.walkFileTree(rootLocation, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if(file.toString().endsWith(DefineUtils.FileTypes.MP4)
                        && file.getFileName().toString().indexOf("_data") > 0){
                        // if( file.getFileName().toString().indexOf("_data") > 0){
                    fileList.add(file.getFileName().toString());
                }
                return super.visitFile(file, attrs);
            }
        });

        if (fileList != null && fileList.size() > 0) {
            for (String file : fileList) {
                if(exists(file)) {
                    filePathList.add(rootLocation.resolve(file).toString());
                }
                else {
                    map.put("code", "-1");
                    map.put("desc", "The File " + file + " is not exists!");
                    return map;
                }
            }
        }
        else{
            map.put("code", "-1");
            map.put("desc", "The file which is include keywords is not exists!");
            return map;
        }

// FileUtil.union("upload-dir","upload-dir/new.mp4");
        FileUtil.joinFileDemo(filePathList.toArray(new String[filePathList.size()]));
        deleteFiles(fileList.toArray(new String[fileList.size()]));

        return map;
    }
}
