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
//import io.vusion.vtransmit.v2.commons.model.reply.TransmissionSystemReply;
//import io.vusion.vtransmit.v2.commons.model.reply.TransmitterDetailReply;
//import io.vusion.vtransmit.v2.commons.model.reply.UnregisterLabelReply;
import lombok.Getter;

@Getter
public enum EnumEventType {
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

    	IMAGE_TRANSMISSION,
	RESET_PAGE_TRANSMISSION_TASK,
	IMAGE_SYNCHRONIZATION,
	IMAGE_REFRESH,
	LABEL_CREATION,
	LABEL_DELETE,
	LABELS_FLASH,
	LABELS_FLASH_STOP,
	SWITCH_PAGE,
	ROLLBACK_PAGE_LABELS,
	LABELS_DETAILS,
	LIFECYCLE_LABELS_DETAILS,
	LIFECYCLE_TRANSMITTERS_DETAILS,

	TRANSMITTER_CONFIGURATION,
	TRANSMITTER_ADD,
	TRANSMITTER_REMOVE,
	TRANSMITTER_DESCRIPTION,

	TRANSMITTERS_DETAILS,

	STORE_OPERATION,
	LIFECYCLE_START,

	LIFECYCLE_HEALTH,
	TRANSMISSION_SYSTEM_CONFIGURATION,
	TRANSMISSION_SYSTEM_READ,
    PING,
	DISCOVERED_LABELS,


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
	private final EnumEventType parentTask;
	//private final EnumLabelTimelineType timelineType;
	private final boolean perPage;
	private final EnumTaskIndexationStrategy taskIndexationStrategy;
	private final String swType;
	private final String searchEventType;
	
	EnumEventType() {
		//this(null, null, null, null, null, false, "/not-defined", null, null);
        this(null, null, null);
	}


//	EnumEventType(final EnumTaskCategory category,
//			final Class<? extends BaseTransmissionSystemReply> replyClass,
//			final Class<? extends Task> taskClass, final EnumEventType subTaskOf,
//			final EnumLabelTimelineType timelineType,
//			final boolean perPage, final String swType, EnumTaskIndexationStrategy taskIndexationStrategy) {
//
//		this(category, replyClass, taskClass, subTaskOf, timelineType, perPage, swType, taskIndexationStrategy, null);
//	}

//	EnumEventType(final EnumTaskCategory category,
//			final Class<? extends BaseTransmissionSystemReply> replyClass,
//			final Class<? extends Task> taskClass, final EnumEventType subTaskOf,
//			final EnumLabelTimelineType timelineType,
//			final boolean perPage, final String swType, EnumTaskIndexationStrategy taskIndexationStrategy, String searchEventType) {
//
//		this.replyClass = replyClass;
//		this.taskClass = taskClass;
//		this.category = category;
//		this.parentTask = subTaskOf;
//		this.timelineType = timelineType;
//		this.perPage = perPage;
//		this.swType = swType;
//		this.taskIndexationStrategy = taskIndexationStrategy;
//		this.searchEventType = searchEventType == null ? this.name() : searchEventType;
//	}

    EnumEventType(final EnumTaskCategory category,
                  final EnumEventType subTaskOf,
                  EnumTaskIndexationStrategy taskIndexationStrategy) {

        //this.replyClass = replyClass;
        //this.taskClass = taskClass;
        this.category = category;
        this.parentTask = subTaskOf;
        //this.timelineType = timelineType;
        this.perPage = false;
        this.swType = null;
        this.taskIndexationStrategy = taskIndexationStrategy;
        this.searchEventType = null;
    }
	
	public boolean isLifeCycle() {
		return category == EnumTaskCategory.LIFECYCLE;
	}
	
	public boolean hasTask() {
		return false;
	}
	
	public enum EnumTaskCategory {
		LIFECYCLE, BY_LABEL, PERIODIC_QUERY, SENSOR_READING;
	}
	
	public enum EnumTaskIndexationStrategy {
		LABEL_AND_TIMELINE, TIMELINE, NONE;
	}
	
	public boolean isTask() {
		return false;
	}
	
//	public static EnumEventType fromSwType(final String swType) {
//		for (final EnumEventType eventType : EnumEventType.values()) {
//			if (eventType.getSwType().equals(swType)) {
//				return eventType;
//			}
//		}
//		return null;
//	}
}
