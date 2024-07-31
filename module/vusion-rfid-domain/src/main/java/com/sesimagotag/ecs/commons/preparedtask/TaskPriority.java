package com.sesimagotag.ecs.commons.preparedtask;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.strip;

@RequiredArgsConstructor
@Getter
public enum TaskPriority {
	@JsonProperty("PING")
	PING(0, "Reserved for ping tasks"),
	@JsonProperty("LOW")
	LOW(1, "Low priority (task only processed if no higher priority tasks are scheduled)"),
	@JsonProperty("NORMAL")
	NORMAL(2, "Normal priority (normal task processing)"),
	@JsonProperty("HIGH")
	HIGH(3, "High priority (task processing is preferred to lower priority tasks)"),
	@JsonProperty("JOIN")
	JOIN(4, "Reserved for network joining"),
	@JsonProperty("HIGHEST")
	HIGHEST(5, "Highest priority");

    public static final TaskPriority DEFAULT = NORMAL;

    private final int priorityCode;
    private final String description;

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public boolean isHighest() {
        return this == HIGHEST;
    }

    public boolean isHigh() {
        return this == HIGH;
    }

    public boolean isNormal() {
        return this == NORMAL;
    }

    public boolean isLow() {
        return this == LOW;
    }

    public static Stream<TaskPriority> stream() {
        return Stream.of(TaskPriority.values());
    }

	@Override
	public String toString() { return this.name();}

    public static TaskPriority fromInt(int code) {
        return stream().filter(p -> p.priorityCode == code).findFirst()
                       .orElseThrow(
                               () -> new IllegalArgumentException("""
                                                                  Unable to find a matching TaskPriority \
                                                                  for code: %d""".formatted(code)));
    }

	@JsonCreator
	public static TaskPriority fromString(String priorityCodeOrName) {
		if (isBlank(priorityCodeOrName)) {
            return null;
        }

		final String trimmed = strip(priorityCodeOrName);
		if (trimmed.matches("\\d+")) {
			try {
                final int parsedPriorityCode = Integer.parseInt(trimmed);
                return fromInt(parsedPriorityCode);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Unable to find a matching TaskType for: %s",
                                                                 priorityCodeOrName));
            }
		}

        return TaskPriority.stream()
                           .filter(priority -> equalsAnyIgnoreCase(trimmed,
						                                           priority.name(),
						                                           priority.getDescription()))
                           .findFirst()
                           .orElseThrow(() ->
                               new IllegalArgumentException("Unable to find a matching TaskType for: %s"
                                            .formatted(priorityCodeOrName)));
	}


    /**
     * Fixes an issue with Java serialization that maintains an enum's singleton state in the JVM.
     *
     * @return singleton enum
     * @throws ObjectStreamException if something goes wrong while deserializing the enum
     */
    @SuppressWarnings({"java:s2221","java:S1162","java:S1108"})
    private Object readResolve() throws ObjectStreamException {
        try {
            return fromString(name());
        } catch (final IllegalArgumentException e) {
            throw new NotSerializableException(ExceptionUtils.getMessage(e));
        }
    }
}
