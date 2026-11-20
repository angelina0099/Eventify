package org.example.files;

import org.example.model.Event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventFileManager {

    public void saveEvents(List<Event> events, Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (Event event : events) {
                writer.write(encode(event));
                writer.newLine();
            }
        }
    }

    public ArrayList<Event> loadEvents(Path filePath) throws IOException {
        ArrayList<Event> events = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Event event = decode(line, lineNumber);
                if (!ids.add(event.getId())) {
                    throw new IOException("Identifiant en double à la ligne " + lineNumber + ".");
                }
                events.add(event);
            }
        }

        return events;
    }

    private String encode(Event event) {
        return event.getId() + "\t"
                + escape(event.getName()) + "\t"
                + escape(event.getDescription()) + "\t"
                + event.getDate() + "\t"
                + escape(event.getLocation());
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private Event decode(String line, int lineNumber) throws IOException {
        List<String> fields = splitFields(line, lineNumber);
        if (fields.size() != 5) {
            throw new IOException("Format invalide à la ligne " + lineNumber
                    + " : cinq champs sont attendus.");
        }

        try {
            int id = Integer.parseInt(fields.get(0));
            if (id <= 0) {
                throw new IOException("Identifiant invalide à la ligne " + lineNumber + ".");
            }
            LocalDate date = LocalDate.parse(fields.get(3));
            return new Event(id, fields.get(1), fields.get(2), date, fields.get(4));
        } catch (NumberFormatException | DateTimeParseException exception) {
            throw new IOException("Identifiant ou date invalide à la ligne " + lineNumber + ".", exception);
        }
    }

    private List<String> splitFields(String line, int lineNumber) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaping = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (escaping) {
                switch (character) {
                    case '\\':
                        field.append('\\');
                        break;
                    case 't':
                        field.append('\t');
                        break;
                    case 'n':
                        field.append('\n');
                        break;
                    case 'r':
                        field.append('\r');
                        break;
                    default:
                        throw new IOException("Caractère d'échappement invalide à la ligne " + lineNumber + ".");
                }
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else if (character == '\t') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (escaping) {
            throw new IOException("Séquence d'échappement incomplète à la ligne " + lineNumber + ".");
        }
        fields.add(field.toString());
        return fields;
    }
}
