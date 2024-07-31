package io.vusion.vtransmit.v2.commons.servicebus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.vusion.gson.utils.GsonHelper;
import io.vusion.secure.logs.ILogger;
import io.vusion.secure.logs.VusionLogger;
import io.vusion.servicebus.search.AbstractSearchServiceBusSender;
//import io.vusion.vtransmit.v2.commons.mapping.TransmissionSystemRegistryIndexMapper;
import io.vusion.vtransmit.v2.commons.model.EnumEventType;
//import io.vusion.vtransmit.v2.commons.model.indexation.LabelIndexation;
//import io.vusion.vtransmit.v2.commons.model.indexation.LabelTimeline;
//import io.vusion.vtransmit.v2.commons.model.indexation.RailIndexation;
//import io.vusion.vtransmit.v2.commons.model.indexation.StoreHealthEvent;
//import io.vusion.vtransmit.v2.commons.model.indexation.transmitter.SearchTransmitterChangeEvent;
import io.vusion.vtransmit.v2.commons.utils.BaseApplicationConfig;
import io.vusion.vtransmit.v2.commons.utils.CollectionUtil;
import io.vusion.vtransmit.v2.commons.utils.JsonNullPatterUtils;

@Service
@Retryable(
		maxAttemptsExpression = "${retry.indexationService.maxAttempts:3}",
		backoff = @Backoff(
				delayExpression = "${retry.indexationService.delay:100}",
				maxDelayExpression = "${retry.indexationService.maxDelay:120000}",
				multiplierExpression = "${retry.indexationService.multiplier:1.25}"
				)
		)
public class BaseIndexationService extends AbstractSearchServiceBusSender {

	@Autowired
	protected BaseApplicationConfig baseApplicationConfig;

	protected final VusionLogger logger = VusionLogger.getLogger(this.getClass());

//	public void forceLabelTimelines(String storeId, List<LabelTimeline> timelines) {
//		if (timelines.isEmpty()) {
//			return;
//		}
//
//		sendMessage(EnumIndexationType.LABEL_TIMELINE, EnumIndexationPipeline.CREATE_OR_MERGE_FORCE, timelines.get(0).getEventType().getSearchEventType(), GsonHelper.getGsonNull(), new HashMap<>(), storeId, baseApplicationConfig.getVtransmitTransmissionUnitId(), timelines);
//
//
//		if (baseApplicationConfig.isVerboseLabelTimelineIndexationDetails(storeId)) {
//			logger.info("Indexing label timelines (" + timelines.get(0).getTimelineType() + ")",
//					timelines.stream().map(timeline -> GsonHelper.getGson().toJson(timeline)));
//		} else if (baseApplicationConfig.isVerboseLabelTimelineIndexation(storeId)) {
//			logger.info("Indexing label timelines (" + timelines.get(0).getTimelineType() + ")",
//					timelines.stream().map(LabelTimeline::getLabelId));
//		}
//
//	}

	@Override
	protected void sendMessage(EnumIndexationType indexationType, EnumIndexationPipeline pipeline,
			String eventType, Gson gson, Map<String, Object> properties,
			String storeId, String source, List<?> messagesContent) {
		final List<JsonObject> messageContentObjects = new ArrayList<>();
		messagesContent.forEach(message -> {
			final JsonObject json = gson.toJsonTree(message).getAsJsonObject();
			final JsonObject cleanedJson = JsonNullPatterUtils.replaceImpossibleValuesWithNullFromJsonObject(json);
			messageContentObjects.add(cleanedJson);
		});

		super.sendMessage(indexationType, pipeline, eventType, gson, properties, storeId, source, messageContentObjects);
	}

//	public void sendLabelTimelines(String storeId, List<LabelTimeline> timelines) {
//		if (timelines.isEmpty()) {
//			return;
//		};
//		sendMessage(EnumIndexationType.LABEL_TIMELINE, EnumIndexationPipeline.CREATE_OR_MERGE,
//				null /* mixed event type is possible, so we don't set it for search */,
//				GsonHelper.getGsonNull(), new HashMap<>(), storeId,
//				baseApplicationConfig.getVtransmitTransmissionUnitId(), timelines);
//
//		if (baseApplicationConfig.isVerboseLabelTimelineIndexationDetails(storeId)) {
//			final Map<String, Long> countTimelinesByEventTypeStatus = CollectionUtil.groupCountBy(timelines, t -> t.getEventType() + ":" + t.getStatus());
//			logger.info("Indexing label timelines (" + GsonHelper.getGson().toJson(countTimelinesByEventTypeStatus) + ")",
//					timelines.stream().map(timeline -> GsonHelper.getGson().toJson(timeline)));
//		} else if (baseApplicationConfig.isVerboseLabelTimelineIndexation(storeId)) {
//			final Map<String, Set<String>> labelIdsByEventTypeAndStatus = new HashMap<>();
//			timelines.forEach(timeline -> CollectionUtil.addToSetMap(labelIdsByEventTypeAndStatus, timeline.getEventType() + ":"+ timeline.getStatus(), timeline.getLabelId()));
//			labelIdsByEventTypeAndStatus
//			.forEach(
//					(evenTypeStatus, labelIds) -> logger.info("Indexing label timelines" + (evenTypeStatus == null ? "" : " with: " + evenTypeStatus), labelIds));
//		}
//
//	}


//	public void sendStoreHealthEvents(final String storeId, final List<StoreHealthEvent> events) {
//
//		sendMessage(EnumIndexationType.STORE, EnumIndexationPipeline.CREATE_OR_MERGE, EnumEventType.STORE_STATUS_MODIFICATION.getSearchEventType(), GsonHelper.getGsonNull(), new HashMap<>(), storeId, baseApplicationConfig.getVtransmitTransmissionUnitId(), events);
//
//		if (baseApplicationConfig.isVerboseEventIndexation(storeId)) {
//			logger.info("Indexing store health", events.stream().map(event -> GsonHelper.getGson().toJson(event)));
//		} else {
//			logger.info("Indexing store health", events.stream().map(StoreHealthEvent::getStoreId));
//		}
//	}

	@Override
	protected String getSessionId(String storeId) {
		return storeId.substring(0, storeId.indexOf(".") - 1);
	}

//	public void sendLabels(String storeId, List<LabelIndexation> labels) {
//		sendLabels(storeId, labels, EnumIndexationPipeline.CREATE_OR_MERGE);
//	}

//	public void sendRails(String storeId, List<RailIndexation> rails) {
//		sendRails(storeId, rails, EnumIndexationPipeline.CREATE_OR_MERGE);
//	}

//	public void sendRails(String storeId, List<RailIndexation> rails, EnumIndexationPipeline pipeline) {
//		if (rails.isEmpty()) {
//			return;
//		}
//		sendMessage(EnumIndexationType.RAIL, pipeline, EnumEventType.RAILS_STATUS.getSearchEventType(), GsonHelper.getGsonNull(), new HashMap<>(), storeId, baseApplicationConfig.getVtransmitTransmissionUnitId(), rails);
//		if (baseApplicationConfig.isVerboseLabelIndexationDetails(storeId)) {
//			logger.info("Indexing rails", rails.stream().map(label -> GsonHelper.getGson().toJson(label)));
//		}
//
//	}

//	public void sendLabels(String storeId, List<LabelIndexation> labels, EnumIndexationPipeline pipeline) {
//		if (labels.isEmpty()) {
//			return;
//		}
//		sendMessage(EnumIndexationType.LABEL, pipeline, EnumEventType.LABELS_DETAILS.getSearchEventType(), GsonHelper.getGsonNull(), new HashMap<>(), storeId, baseApplicationConfig.getVtransmitTransmissionUnitId(), labels);
//
//		if (baseApplicationConfig.isVerboseLabelIndexationDetails(storeId)) {
//			logger.info("Indexing labels", labels.stream().map(label -> GsonHelper.getGson().toJson(label)));
//		} else if (baseApplicationConfig.isVerboseLabelIndexation(storeId)) {
//			final Map<String, List<String>> labelIdsByStatus = new HashMap<>();
//			labels.forEach(
//					label -> CollectionUtil.addToListMap(labelIdsByStatus,
//							label.getStatus() != null
//									? label.getStatus() + ":"
//											+ (label.getConnectivity() != null
//													&& label.getConnectivity().getStatus() != null
//															? label.getConnectivity().getStatus()
//															: "UNKNOWN")
//									: null,
//							label.getLabelId()));
//			labelIdsByStatus.forEach((status, labelIds) -> logger.info("Indexing labels" + (status == null ? "" : " with status: " + status), labelIds));
//		}
//	}

	@Override
	protected String getSearchServiceBusPrimaryConnectionString() {
		return "";
	}

	@Override
	protected String getSearchServiceBusSecondaryConnectionString() {
		return "";
	}

	@Override
	protected ILogger getLogger() {
		return logger.getSecureLogger();
	}

//	public void sendTransmitterChangeEvent(final String storeId, final String eventType,
//										   final List<SearchTransmitterChangeEvent> events) {
//		if(events.isEmpty()) {
//			return;
//		}
//		sendMessage(EnumIndexationType.EVENT,
//				EnumIndexationPipeline.CREATE_OR_MERGE,
//				eventType,
//				GsonHelper.getGsonNull(),
//				new HashMap<>(),
//				storeId,
//				baseApplicationConfig.getVtransmitTransmissionUnitId(),
//				events
//		);
//
//		if (baseApplicationConfig.isVerboseTransmitterIndexationDetails(storeId)) {
//			logger.info("Indexing transmission systems", events.stream()
//					.map(device -> GsonHelper.getGson().toJson(device)));
//		} else if (baseApplicationConfig.isVerboseTransmitterIndexation(storeId)) {
//			logger.info("Indexing transmission systems", events.stream()
//					.map(SearchTransmitterChangeEvent::getTransmitterId));
//		}
//	}
}
