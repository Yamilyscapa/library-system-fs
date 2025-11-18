import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FileStorage<T extends SerializableEntity> {
    private final Path filePath;
    private final Function<String, Optional<T>> parser;

    public FileStorage(Path filePath, Function<String, Optional<T>> parser) {
        this.filePath = filePath;
        this.parser = parser;
    }

    public List<T> loadAll() {
        ensureDirectory();
        List<T> items = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return items;
        }
        try {
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                parser.apply(line).ifPresent(items::add);
            }
        } catch (IOException e) {
            System.err.println("No se pudieron leer datos desde " + filePath + ": " + e.getMessage());
        }
        return items;
    }

    public void saveAll(Collection<T> items) {
        ensureDirectory();
        List<String> lines = new ArrayList<>();
        for (T item : items) {
            lines.add(item.toRecord());
        }
        try {
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("No se pudieron almacenar datos en " + filePath + ": " + e.getMessage());
        }
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            System.err.println("No se pudo crear el directorio para " + filePath + ": " + e.getMessage());
        }
    }
}
