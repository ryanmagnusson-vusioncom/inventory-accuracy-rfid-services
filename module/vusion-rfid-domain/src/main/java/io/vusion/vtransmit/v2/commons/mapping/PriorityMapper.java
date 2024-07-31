package io.vusion.vtransmit.v2.commons.mapping;

import com.sesimagotag.ecs.commons.preparedtask.TaskPriority;

import io.vusion.vtransmit.v2.commons.model.EnumPriority;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = IGNORE)
public interface PriorityMapper {

	@ValueMapping(target = "NORMAL", source = "MEDIUM")
    TaskPriority toTaskPriority(final EnumPriority priority);

    @ValueMapping(source = "PING", target = "LOW")
    @ValueMapping(source = "JOIN", target = "HIGHEST")
    @ValueMapping(source = "NORMAL", target = "MEDIUM")
    EnumPriority toPriority(TaskPriority priority);

    default TaskPriority fromRedisPriority(final Integer code) {
        if (code == null) {
            return TaskPriority.DEFAULT;
        }

        return switch (code) {
            case 0 -> TaskPriority.HIGHEST;
            case 1 -> TaskPriority.HIGH;
            case 2 -> TaskPriority.NORMAL;
            case 3 -> TaskPriority.LOW;
            default -> TaskPriority.DEFAULT;
        };
    }

}
