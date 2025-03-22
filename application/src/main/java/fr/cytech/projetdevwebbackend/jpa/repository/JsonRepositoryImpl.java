package fr.cytech.projetdevwebbackend.jpa.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cytech.projetdevwebbackend.jpa.annotation.JsonColumn;
import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class JsonRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements JsonRepositoryFragment<T, ID> {

    private final EntityManager entityManager;
    private final Class<T> domainClass;
    private final Map<String, Field> jsonFields;
    private final ObjectMapper objectMapper;

    public JsonRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.domainClass = entityInformation.getJavaType();
        this.jsonFields = findJsonFields();
        this.objectMapper = new ObjectMapper();
    }

    private Map<String, Field> findJsonFields() {
        Map<String, Field> fields = new HashMap<>();
        ReflectionUtils.doWithFields(domainClass, field -> {
            if (field.isAnnotationPresent(JsonColumn.class)) {
                field.setAccessible(true);
                fields.put(field.getName(), field);
            }
        });
        return fields;
    }

    @Override
    public List<T> findByValues(String searchText) {
        if (jsonFields.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch all entities
        List<T> allEntities = findAll();
        String searchTextLower = searchText.toLowerCase();

        // Filter entities that contain the search text in any JSON field
        return allEntities.stream().filter(entity -> {
            for (Field field : jsonFields.values()) {
                try {
                    String jsonValue = (String) field.get(entity);
                    if (jsonValue != null && !jsonValue.isEmpty()) {
                        // Simple string match first (faster)
                        if (jsonValue.toLowerCase().contains(searchTextLower)) {
                            // Try with Jackson for more precise check
                            JsonNode rootNode = objectMapper.readTree(jsonValue);
                            return containsValueInJsonNode(rootNode, searchTextLower);
                        }
                    }
                } catch (Exception e) {
                    // Skip this field if there's an error
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private boolean containsValueInJsonNode(JsonNode node, String searchText) {
        if (node.isValueNode()) {
            return node.asText().toLowerCase().contains(searchText);
        } else if (node.isObject()) {
            // Check all fields of this object
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (containsValueInJsonNode(entry.getValue(), searchText)) {
                    return true;
                }
            }
        } else if (node.isArray()) {
            // Check all elements of the array
            for (JsonNode arrayItem : node) {
                if (containsValueInJsonNode(arrayItem, searchText)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<T> findByKey(String key) {
        if (jsonFields.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch all entities
        List<T> allEntities = findAll();

        // Filter entities that contain the key in any JSON field
        return allEntities.stream().filter(entity -> {
            for (Field field : jsonFields.values()) {
                try {
                    String jsonValue = (String) field.get(entity);
                    if (jsonValue != null && !jsonValue.isEmpty()) {
                        JsonNode rootNode = objectMapper.readTree(jsonValue);
                        return hasJsonKey(rootNode, key);
                    }
                } catch (Exception e) {
                    // Skip this field if there's an error
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private boolean hasJsonKey(JsonNode node, String key) {
        if (node.isObject()) {
            if (node.has(key)) {
                return true;
            }
            // Search recursively in nested objects
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (hasJsonKey(entry.getValue(), key)) {
                    return true;
                }
            }
        } else if (node.isArray()) {
            // Search recursively in array elements
            for (JsonNode arrayItem : node) {
                if (hasJsonKey(arrayItem, key)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<T> findByKeyAndValue(String key, Object value) {
        if (jsonFields.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch all entities
        List<T> allEntities = findAll();
        String stringValue = value != null ? value.toString() : null;

        // Filter entities where any JSON field has the key with the specified value
        return allEntities.stream().filter(entity -> {
            for (Field field : jsonFields.values()) {
                try {
                    String jsonValue = (String) field.get(entity);
                    if (jsonValue != null && !jsonValue.isEmpty()) {
                        JsonNode rootNode = objectMapper.readTree(jsonValue);
                        return hasKeyWithValue(rootNode, key, value, stringValue);
                    }
                } catch (Exception e) {
                    // Skip this field if there's an error
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private boolean hasKeyWithValue(JsonNode node, String key, Object value, String stringValue) {
        if (node.isObject()) {
            if (node.has(key)) {
                JsonNode valueNode = node.get(key);
                if (valueMatches(valueNode, value, stringValue)) {
                    return true;
                }
            }
            // Search recursively in nested objects
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                Map.Entry<String, JsonNode> entry = it.next();
                if (hasKeyWithValue(entry.getValue(), key, value, stringValue)) {
                    return true;
                }
            }
        } else if (node.isArray()) {
            // Search recursively in array elements
            for (JsonNode arrayItem : node) {
                if (hasKeyWithValue(arrayItem, key, value, stringValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean valueMatches(JsonNode valueNode, Object value, String stringValue) {
        if (value == null) {
            return valueNode.isNull();
        }
        if (valueNode.isTextual() && stringValue != null) {
            return valueNode.asText().equals(stringValue);
        }
        if (valueNode.isNumber() && value instanceof Number) {
            if (value instanceof Integer || value instanceof Long) {
                return valueNode.asLong() == ((Number) value).longValue();
            }
            if (value instanceof Float || value instanceof Double) {
                return valueNode.asDouble() == ((Number) value).doubleValue();
            }
        }
        if (valueNode.isBoolean() && value instanceof Boolean) {
            return valueNode.asBoolean() == ((Boolean) value);
        }
        // As a fallback, compare string representations
        return valueNode.asText().equals(stringValue);
    }

    @Override
    public Optional<T> findFirstByKeyAndValue(String key, Object value) {
        List<T> results = findByKeyAndValue(key, value);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public boolean existsByKeyAndValue(String key, Object value) {
        List<T> results = findByKeyAndValue(key, value);
        return !results.isEmpty();
    }

    @Override
    public long countByKey(String key) {
        List<T> results = findByKey(key);
        return results.size();
    }
}
