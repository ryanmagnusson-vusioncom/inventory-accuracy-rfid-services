package io.vusion.vtransmit.v2.commons.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.vtransmit.v2.commons.model.EnumEventType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Configuration
@Getter
@Data
public class BaseApplicationConfig {
	
//	@Value("${search.service.bus.primary.connection.string}")
//	private String searchServiceBusPrimaryConnectionString;
//
//	@Value("${search.service.bus.secondary.connection.string}")
//	private String searchServiceBusSecondaryConnectionString;
//
//	@Value("${internal.service.bus.timer.triggered.topic.name:internal-timer-triggered}")
//	private String timerTriggeredTopicName;
//
//	@Value("${vtransmit.service.bus.manage.subscription.name:manage}")
//	private String manageSubscriptionName;
	
	// Limit is 256 K
	// can be override to change SB max size
	@Value("${internal.service.bus.max.message.size:240000}")
	private int maxMessageSize;
	
	@Value("${max.messages.concurrent.modification.retry.count:5}")
	private int maxConcurrentModificationRetryCount;
	
	@Value("${max.messages.count.before.logging.warn:100}")
	private int maxMessagesCountBeforeLoggingWarn;
	
	@Value("${max.dlq.count.before.logging.warn:100}")
	private int maxDlqCountBeforeLoggingWarn;
	
	// Used for devs
	@Value("${reply.store.prefix.to.listen:}")
	private String replyStorePrefixTolisten;
	
	// Used for devs
	@Value("${verbose.all:}")
	private String globalAll;
	
	// Used for devs
	@Value("${verbose.label.timeline.indexation:false}")
	private String verboseLabelTimelineIndexation;
	
	
	// Used for devs
	@Value("${verbose.label.timeline.indexation.details:false}")
	private String verboseLabelTimelineIndexationDetails;
	
	// Used for devs
	@Value("${verbose.label.indexation:false}")
	private String verboseLabelIndexation;
	
	// Used for devs
	@Value("${verbose.label.indexation.details:false}")
	private String verboseLabelIndexationDetails;
	
	// Used for devs
	@Value("${verbose.task.management.details:false}")
	private String verboseTaskManagementDetails;
	
	// Used for devs
	@Value("${verbose.task.management:false}")
	private String verboseTaskManagement;
	
	// Used for devs
	@Value("${verbose.transmission:false}")
	private String verboseTransmission;
	
	@Value("${verbose.label.server.request:false}")
	private boolean verboseLabelServerRequest;
	
	// Used for devs
	@Value("${verbose.task.reply:false}")
	private String verboseTaskReply;
	
	// Used for devs
	@Value("${verbose.periodic.notification:false}")
	private String verbosePeriodicNotification;
	
	// Used for devs
	@Value("${verbose.all.reply:false}")
	private String verboseAllReply;
	
	// Used for devs
	@Value("${verbose.transmitter.timeline.indexation:false}")
	private String verboseTransmitterTimelineIndexation;
	
	// Used for devs
	@Value("${verbose.transmitter.indexation:false}")
	private String verboseTransmitterIndexation;
	
	// Used for devs
	@Value("${verbose.transmitter.indexation.details:false}")
	private String verboseTransmitterIndexationDetails;
	
	// Used for devs
	@Value("${verbose.incoming.request:false}")
	private boolean verboseIncomingRequest;
	
	// Used for devs
	@Value("${verbose.event.indexation:false}")
	private String verboseEventIndexation;
	
	@Value("${verbose.store.config.allowed:false}")
	private String verboseStoreConfigAllowed;
	
	// Used for devs / validate partners devs
	@Value("${verbose.reply.types:{'defaultValue':[]}}")
	@Getter(AccessLevel.NONE) // To not generate a getter
	private String verboseReplyTypes;
	
	@Getter(AccessLevel.NONE) // To not generate a getter
	private JsonObject verboseReplyTypesAsjsonJsonObject;
	
	@Getter(AccessLevel.NONE)
	@Value("${vtransmit.redis.port:6379}")
	private int openEslRedisPort;
	
	@Getter(AccessLevel.NONE)
	@Value("${vtransmit.redis.forward.read.port:6379}")
	private int openEslRedisForwardReadPort;
	
	@Getter(AccessLevel.NONE)
	@Value("${vtransmit.redis.forward.write.port:6379}")
	private int openEslRedisForwardWritePort;
	
	@Value("${vtransmit.redis.timeout:10000}")
	private int openEslRedisTimeout;
	
	@Value("${vtransmit.redis.ssl:true}")
	private boolean openEslRedisSsl;
	
	@Value("${vtransmit.service.request.all.stores.transmitter.details.in.hour:6}")
	private int requestAllStoresTransmitterDetailsInHour;
	
	@Value("${vtransmit.service.request.all.stores.labels.details.in.hour:6}")
	private int requestAllStoresLabelsDetailsInHour;
	
	@Value("${vtransmit.service.request.timeout.in.hour:6}")
	private int requestTimeoutInHour;
	
	@Value("${simulation.force.upsert.config.store.regex:}")
	private String simulationForceUpsertConfigStoreRegex;
	
	@Value("${simulation.ecs.uri:}")
	private String simulationEcsUri;
	
	@Value("${simulation.ecs.username:}")
	private String simulationEcsUsername;
	
	@Value("${simulation.ecs.password:}")
	private String simulationEcsPassword;
	
	@Value("${vtransmit.transmission.unit.id:NOT_DEFINED}")
	private String vtransmitTransmissionUnitId;
	
	// period while all stores has to be processed by the delete duplicate tasks requests, in hours
	@Value("${vtransmit.service.delete.duplicate.tasks.process.all.stores.period.in.hours:8}")
	private int deleteDuplicateTasksAllStoresPeriodProcessingHours;

    @Value("${vtransmit.switch.page.rollback.delay:5}")
    private int rollbackDelayMinutes;

	public int getOpenEslRedisReadPort() {
		if (openEslRedisPort != 6379) {
			return openEslRedisPort;
		}
		
		return openEslRedisForwardReadPort;
	}
	
	public int getOpenEslRedisWritePort() {
		if (openEslRedisPort != 6379) {
			return openEslRedisPort;
		}
		
		return openEslRedisForwardWritePort;
	}
	
	public List<EnumEventType> getVerboseReplyTypes(final String storeId) {
		
		if (verboseReplyTypesAsjsonJsonObject == null) {
			verboseReplyTypesAsjsonJsonObject = GsonHelper.fromJson(verboseReplyTypes.replace("'", "\""),
					JsonObject.class);
		}
		
		final JsonArray configurations = VtransmitV2Utils.getConfiguration(verboseReplyTypesAsjsonJsonObject, storeId);
		
		return GsonHelper.fromJsonToList(GsonHelper.getGson(), configurations, EnumEventType.class);
	}
	
	public boolean isSimulatedEnvironement() {
		return StringUtils.isNotEmpty(simulationForceUpsertConfigStoreRegex);
	}
	
	private boolean isVerbose(final String verbose, final String storeId) {
		boolean isGlobalVerbose = false;
		boolean isVerbose = false;
		
		if (!StringUtils.isBlank(globalAll)) {
			if (globalAll.matches("true|false")) {
				isGlobalVerbose = Boolean.parseBoolean(globalAll);
			} else {
				isGlobalVerbose = storeId.matches(globalAll);
			}
		}
		if (!StringUtils.isBlank(verbose)) {
			if (verbose.matches("true|false")) {
				isVerbose = Boolean.parseBoolean(verbose);
			} else {
				isVerbose = storeId.matches(verbose);
			}
		}
		
		return isGlobalVerbose || isVerbose;
	}
	
	public boolean isVerboseLabelTimelineIndexationDetails(final String storeId) {
		return isVerbose(verboseLabelTimelineIndexationDetails, storeId);
	}
	
	public boolean isVerboseLabelTimelineIndexation(final String storeId) {
		return isVerbose(verboseLabelTimelineIndexation, storeId);
	}
	
	public boolean isVerboseLabelIndexationDetails(final String storeId) {
		return isVerbose(verboseLabelIndexationDetails, storeId);
	}
	
	public boolean isVerboseLabelIndexation(final String storeId) {
		return isVerbose(verboseLabelIndexation, storeId);
	}
	
	public boolean isVerboseTransmitterIndexationDetails(final String storeId) {
		return isVerbose(verboseTransmitterIndexationDetails, storeId);
	}
	
	public boolean isVerboseTransmitterIndexation(final String storeId) {
		return isVerbose(verboseTransmitterIndexation, storeId);
	}
	
	public boolean isVerboseEventIndexation(final String storeId) {
		return isVerbose(verboseEventIndexation, storeId);
	}
	
	public boolean isVerboseTaskManagementDetails(final String storeId) {
		return isVerbose(verboseTaskManagementDetails, storeId);
	}
	
	public boolean isVerbosePeriodicNotification(final String storeId) {
		return isVerbose(verbosePeriodicNotification, storeId);
	}
	
	public boolean isVerboseTaskReply(final String storeId) {
		return isVerbose(verboseTaskReply, storeId);
	}
	
	public boolean isVerboseAllReply(final String storeId) {
		return isVerbose(verboseAllReply, storeId);
	}
	
	public boolean isVerboseTaskManagement(final String storeId) {
		return isVerbose(verboseTaskManagement, storeId);
	}
}
