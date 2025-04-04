
/**
 * Service for file access operations. 
 * Handles reading and writing.
 */
package fr.cytech.projetdevwebbackend.util.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import fr.cytech.projetdevwebbackend.errors.types.FileError;
import fr.cytech.projetdevwebbackend.util.Either;

@Service
public class ProfilePictureFileAccessService {
    private final Path path;
    @Autowired
    private ResourceLoader resourceLoader;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ProfilePictureFileAccessService.class);

    /**
     * Constructor for FileAccessService.
     * 
     * @param rootPath The root path for file access, below the root path
     */
    public ProfilePictureFileAccessService(@Value("${app.database.path.root}") String rootPath,
            @Value("${app.database.path.profile-pictures}") String path) {
        this.path = Path.of(rootPath + path);
        // Create the directory if it doesn't exist
        try {
            Files.createDirectories(this.path);
        } catch (IOException e) {
            logger.error("Failed to create directory: " + this.path, e);
        }
    }

    /**
     * Gets the file from the specified path.
     */
    public Either<FileError, Optional<byte[]>> read(Long id) {
        try {
            Path file = path.resolve(id.toString());
            if (Files.exists(file)) {
                return Either.right(Optional.of(Files.readAllBytes(file)));
            } else {
                return Either.right(Optional.empty());
            }
        } catch (IOException e) {
            return Either.left(FileError.GENERAL_IO_ERROR);
        }
    }

    /**
     * Stores the file in the specified path.
     */
    public Optional<FileError> store(Long id, byte[] bytes) {
        try {
            Path destinationFile = this.path.resolve(id.toString())
                    .normalize().toAbsolutePath();
            logger.info("Storing file at: " + destinationFile);
            Files.copy(new ByteArrayInputStream(bytes), destinationFile,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(FileError.GENERAL_IO_ERROR);
        }
    }

    /**
     * Gets the default profile picture (stored under resources/default_user.png);
     */
    public byte[] getDefaultImage() {
        try {
            Resource resource = resourceLoader.getResource("classpath:default_user.png");
            return resource.getInputStream().readAllBytes();
        } catch (IOException e) {
            System.err.println("Error reading default image file: " + e.getMessage());
            throw new RuntimeException("Failed to read default image file", e);
        }
    }
}
