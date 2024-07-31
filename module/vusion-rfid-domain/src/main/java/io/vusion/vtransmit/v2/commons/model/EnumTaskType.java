package io.vusion.vtransmit.v2.commons.model;

//import io.vusion.vtransmit.v2.commons.model.dao.Task;
//import io.vusion.vtransmit.v2.commons.model.indexation.EnumLabelTimelineType;
//import io.vusion.vtransmit.v2.commons.model.reply.BaseTransmissionSystemReply;
//import io.vusion.vtransmit.v2.commons.model.reply.DiscoveredLabelReply;
//import io.vusion.vtransmit.v2.commons.model.reply.FlashLabelReply;
//import io.vusion.vtransmit.v2.commons.model.reply.HealthLifeCycleReply;
//import io.vusion.vtransmit.v2.commons.model.reply.ImageTransmissionReply;
//import io.vusion.vtransmit.v2.commons.model.reply.LabelDetailReply;
//import io.vusion.vtransmit.v2.commons.model.reply.PingDeviceReply;
//import io.vusion.vtransmit.v2.commons.model.reply.RegisterLabelReply;
//import io.vusion.vtransmit.v2.commons.model.reply.StartLifeCycleReply;
//import io.vusion.vtransmit.v2.commons.model.reply.SwitchPageLabelsReply;
//import io.vusion.vtransmit.v2.commons.model.reply.TransmitterDetailReply;
//import io.vusion.vtransmit.v2.commons.model.reply.UnregisterLabelReply;
import lombok.Getter;

import java.util.Locale;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.strip;

@Getter
public enum EnumTaskType {
//
//
//
//	IMAGE_TRANSMISSION(EnumTaskCategory.BY_LABEL, ImageTransmissionReply.class, Task.class, null,
//                       EnumLabelTimelineType.IMAGE_TRANSMISSION, true, "/transmissions",
//                       EnumTaskIndexationStrategy.LABEL_AND_TIMELINE),
//	RESET_PAGE_TRANSMISSION_TASK(EnumTaskCategory.BY_LABEL, ImageTransmissionReply.class,
//                                 Task.class,
//                                 null, EnumLabelTimelineType.RESET_PAGE_TRANSMISSION_TASK, true, "/transmissions",
//                                 EnumTaskIndexationStrategy.LABEL_AND_TIMELINE),
//	IMAGE_SYNCHRONIZATION(EnumTaskCategory.BY_LABEL, ImageTransmissionReply.class, Task.class, null,
//                          EnumLabelTimelineType.IMAGE_TRANSMISSION, true, "/transmissions",
//                          EnumTaskIndexationStrategy.LABEL_AND_TIMELINE),
//	IMAGE_REFRESH(EnumTaskCategory.BY_LABEL, ImageTransmissionReply.class, Task.class, null,
//                  EnumLabelTimelineType.IMAGE_TRANSMISSION, true, "/transmissions",
//                  EnumTaskIndexationStrategy.LABEL_AND_TIMELINE),
//
//	LABEL_CREATION(EnumTaskCategory.BY_LABEL, RegisterLabelReply.class, null, null,
//                   EnumLabelTimelineType.LABEL_CREATION, false, "/todo", EnumTaskIndexationStrategy.TIMELINE),
//	LABEL_DELETE(EnumTaskCategory.BY_LABEL, UnregisterLabelReply.class, null, null,
//                 EnumLabelTimelineType.DELETED, false, "/todo", EnumTaskIndexationStrategy.TIMELINE),
//
//	LABELS_FLASH(EnumTaskCategory.BY_LABEL, FlashLabelReply.class, null, null,
//                 EnumLabelTimelineType.FLASH, false, "/flashing", EnumTaskIndexationStrategy.TIMELINE),
//	LABELS_FLASH_STOP(EnumTaskCategory.BY_LABEL, FlashLabelReply.class, null, null,
//                      EnumLabelTimelineType.FLASH_STOP, false, "/flashing", EnumTaskIndexationStrategy.TIMELINE),
//
//	SWITCH_PAGE(EnumTaskCategory.BY_LABEL, SwitchPageLabelsReply.class, null, null,
//                EnumLabelTimelineType.SWITCH_PAGE, false, "/transmissions", EnumTaskIndexationStrategy.TIMELINE),
//	ROLLBACK_PAGE_LABELS(EnumTaskCategory.BY_LABEL, SwitchPageLabelsReply.class, null,
//                         EnumEventType.SWITCH_PAGE, EnumLabelTimelineType.SWITCH_PAGE, false, "/transmissions",
//                         EnumTaskIndexationStrategy.TIMELINE),
//
//	LABELS_DETAILS(EnumTaskCategory.PERIODIC_QUERY, LabelDetailReply.class, null, null, null, false, "/labels",
//                   EnumTaskIndexationStrategy.NONE),
//	LIFECYCLE_LABELS_DETAILS(EnumTaskCategory.LIFECYCLE, LabelDetailReply.class, null, null, null, false,
//                             "/labels", EnumTaskIndexationStrategy.NONE),
//	LIFECYCLE_TRANSMITTERS_DETAILS(EnumTaskCategory.LIFECYCLE, TransmissionSystemReply.class, null, null, null,
//                                   false, "/transmissionSystems/highFrequency", EnumTaskIndexationStrategy.NONE),
//
//	TRANSMITTER_CONFIGURATION(),
//	TRANSMITTER_ADD(),
//	TRANSMITTER_REMOVE(),
//	TRANSMITTER_DESCRIPTION(),
//
//	TRANSMITTERS_DETAILS(EnumTaskCategory.PERIODIC_QUERY, TransmitterDetailReply.class, null, null, null, false,
//                         "/transmissionSystems/highFrequency", EnumTaskIndexationStrategy.NONE),
//	STORE_OPERATION(EnumTaskCategory.LIFECYCLE, null, null, null, null, false, "/todo",
//                    EnumTaskIndexationStrategy.NONE),
//	LIFECYCLE_START(EnumTaskCategory.LIFECYCLE, StartLifeCycleReply.class, null, null, null, false, "/start",
//                    EnumTaskIndexationStrategy.NONE),
//	LIFECYCLE_HEALTH(EnumTaskCategory.LIFECYCLE, HealthLifeCycleReply.class, null, null, null, false, "/health",
//                     EnumTaskIndexationStrategy.NONE),
//
//	TRANSMISSION_SYSTEM_CONFIGURATION(),
//	TRANSMISSION_SYSTEM_READ(),
//	PING(EnumTaskCategory.BY_LABEL, PingDeviceReply.class, null, null, EnumLabelTimelineType.PING, false,
//         "/labels/ping",
//         EnumTaskIndexationStrategy.NONE),
//
//	DISCOVERED_LABELS(EnumTaskCategory.PERIODIC_QUERY, DiscoveredLabelReply.class, null, null, null, false,
//                      "/discoveredLabel",
//                      EnumTaskIndexationStrategy.NONE),
	FIRMWARE, //(EnumTaskCategory.BY_LABEL, FirmwareUpdateDeviceReply.class, null, null, EnumLabelTimelineType.FIRMWARE, false,
			  //"/firmware", EnumTaskIndexationStrategy.TIMELINE),
	ITEM_INPUT,

    EPC_SENSOR_READING,
    EPC_SENSOR_QUERY,

	MATCHING,
	STORE_MODIFICATION,
	SMART_REFRESH,
	DISPLAY_REFRESH,
	STORE_CREATION,
	STORE_SWARM_MAESTRO_CREATION,
	STORE_SWARM_MAESTRO_MODIFICATION,
	STORE_INSTALLATION_STATUS,
	STORE_STATUS_MODIFICATION,
	STORE_INSIGHT_MODIFICATION,
	INSIGHT_PROVISIONING_CREATION,
	INSIGHT_PROVISIONING_MODIFICATION,
	STORE_REFRSH_DATA_FREQUENCY,
	STORE_REFRESH_DATA_FREQUENCY,
	LABEL_REFRESH,
	RAILS_STATUS,
	TRANSMITTER_CONNECTIVITY,
	LABEL_DISSOCIATION,
	LABELS_REGISTRATION,
	NEW_SETTING,
	INSIGHT_MIGRATION,
	LABEL_UNMATCH,
	DELETE_ITEMS,
	UNDEFINED,
	NFC_UPDATE,
	GET_IMAGE,
	NONE();
	
	//private final Class<? extends BaseTransmissionSystemReply> replyClass;
	//private final Class<? extends Task> taskClass;
	private final EnumTaskCategory category;
	private final EnumTaskType parentTask;
//	private final EnumLabelTimelineType timelineType;
	private final boolean perPage;

	private final String swType;


    EnumTaskType() {
        this.category = null;
        this.parentTask = null;
        this.perPage = false;
        this.swType = null;
    }




//	EnumTaskType(final EnumTaskCategory category, final Class<? extends BaseTransmissionSystemReply> replyClass,
//			final Class<? extends Task> taskClass, final EnumTaskType subTaskOf, final EnumLabelTimelineType timelineType,
//			final boolean perPage, final String swType) {
//
//        //this.replyClass = replyClass;
//        //this.taskClass = taskClass;
//		this.category = category;
//		this.parentTask = subTaskOf;
//        //this.timelineType = timelineType;
//        this.perPage = false;
//        this.swType = null;
//	}
	
	public boolean isLifeCycle() {
		return category == EnumTaskCategory.LIFECYCLE;
	}
	
	public boolean hasTask() {
		return false;
	}
	
	public enum EnumTaskCategory {
		LIFECYCLE, BY_LABEL, PERIODIC_QUERY, SENSOR_READING;
		
		public static Stream<EnumTaskCategory> stream() {
			return Stream.of(EnumTaskCategory.values());
		}

		public static EnumTaskCategory fromString(String text) {
			if (isBlank(text)) {
				return null;
			}
			
			final String cleanedUp = strip(text).toUpperCase(Locale.getDefault());
			return EnumTaskCategory.stream()
					.filter(x -> equalsIgnoreCase(cleanedUp, x.name()))
					.findFirst()
					.orElseThrow(
							() -> new IllegalArgumentException("Unable to find a matching TaskCategory for: %s"
									.formatted(text)));
		}
	}
	
	
//	public String generateTimelineId(final Task task) {
//		return String.format("%s.%s.%s%s", this.name(), task.getMetadata().getCorrelationId(), task.getLabelId(),
//				task.getPage() != null ? ".PAGE_" + task.getPage() : "");
//	}
	
}
