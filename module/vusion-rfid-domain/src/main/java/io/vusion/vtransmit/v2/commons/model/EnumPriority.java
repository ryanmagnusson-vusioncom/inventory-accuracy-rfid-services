package io.vusion.vtransmit.v2.commons.model;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.strip;

import java.util.Locale;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sesimagotag.ecs.commons.preparedtask.TaskPriority;

import lombok.Getter;

@Getter
public enum EnumPriority {
	
	LOW(1, TaskPriority.LOW), MEDIUM(2, TaskPriority.NORMAL), HIGH(3, TaskPriority.HIGH), HIGHEST(4, TaskPriority.HIGHEST);
	private final int level;
	private final TaskPriority taskPriority;
	
	EnumPriority(int level, TaskPriority taskPriority) {
		this.level = level;
		this.taskPriority = taskPriority;
	}

	public static Stream<EnumPriority> stream() {
		return Stream.of(EnumPriority.values());
	}
	
	@JsonCreator
	public static EnumPriority fromString(String text) {
		if (isBlank(text)) {
			return null;
		}
		
		final String cleanedUp = strip(text).toUpperCase(Locale.getDefault())
				.replace("^task-", "");
		return EnumPriority.stream()
				.filter(x -> equalsIgnoreCase(cleanedUp, x.name()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unable to find a matching Priority for: %s"
						.formatted(text)));
	}
}
