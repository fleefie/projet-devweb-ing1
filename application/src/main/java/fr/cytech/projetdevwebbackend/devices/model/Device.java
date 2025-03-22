package fr.cytech.projetdevwebbackend.devices.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cytech.projetdevwebbackend.jpa.annotation.JsonColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Entity representing a device for the platform.
 * <p>
 * Devices hold a name identifier and a set of arbitrary properties stored as
 * JSON.
 * The properties can be any key-value pairs that describe or configure the
 * device.
 *
 * @author fleefie
 * @since 2025-03-22
 */
@Entity
@Table(name = "devices")
@EqualsAndHashCode(of = { "id" })
@NoArgsConstructor
@Slf4j
public class Device {
    /** Shared ObjectMapper instance for JSON conversion */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Device must have a name")
    @Getter
    @Setter
    private String name;

    @NonNull
    @Column(nullable = false, columnDefinition = "TEXT")
    @JsonColumn
    private String properties = "{}";

    /**
     * Gets the device properties as a Map.
     * <p>
     * Deserializes the JSON properties string into a Map of String keys
     * and Object values. Returns an empty map if properties is null or
     * if deserialization fails.
     *
     * @return Map of property key-value pairs
     */
    public Map<String, Object> getProperties() {
        if (properties == null || properties.isEmpty()) {
            return new HashMap<>();
        }

        try {
            return OBJECT_MAPPER.readValue(properties, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize properties JSON for device {}: {}", name, e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Sets the device properties from a Map.
     * <p>
     * Serializes the given Map to a JSON string and stores it in the properties
     * field.
     * If serialization fails, the properties remain unchanged.
     *
     * @param properties Map of property key-value pairs to set
     */
    public void setProperties(Map<String, Object> properties) {
        if (properties == null) {
            this.properties = "{}";
            return;
        }

        try {
            this.properties = OBJECT_MAPPER.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize properties for device {}: {}", name, e.getMessage());
            // Keep existing properties in case of error
        }
    }

    /**
     * Gets a single property value by key.
     * 
     * @param key The property key
     * @return The property value, or null if the key doesn't exist
     */
    public Object getProperty(String key) {
        return getProperties().get(key);
    }

    /**
     * Sets a single property value.
     * 
     * @param key   The property key
     * @param value The property value
     */
    public void setProperty(String key, Object value) {
        Map<String, Object> props = getProperties();
        props.put(key, value);
        setProperties(props);
    }

    /**
     * Removes a property.
     * 
     * @param key The property key to remove
     * @return The previous value, or null if the key didn't exist
     */
    public Object removeProperty(String key) {
        Map<String, Object> props = getProperties();
        Object previous = props.remove(key);
        setProperties(props);
        return previous;
    }

    /**
     * Creates a new Device with the given name and empty properties.
     *
     * @param name The device name
     */
    public Device(String name) {
        this(name, "{}");
    }

    /**
     * Creates a new Device with the given name and properties JSON string.
     *
     * @param name       The device name
     * @param properties The properties as a JSON string
     */
    public Device(String name, String properties) {
        this.name = name;
        this.properties = properties == null || properties.isEmpty() ? "{}" : properties;
    }

    /**
     * Creates a new Device with the given name and properties Map.
     *
     * @param name       The device name
     * @param properties The properties as a Map
     */
    public Device(String name, Map<String, Object> properties) {
        this.name = name;
        setProperties(properties);
    }
}
