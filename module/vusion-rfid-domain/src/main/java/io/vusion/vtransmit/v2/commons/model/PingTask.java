package io.vusion.vtransmit.v2.commons.model;

import com.sesimagotag.ecs.commons.preparedtask.TaskPriority;

import io.vusion.gson.model.BaseModel;
import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// @SuperBuilder(toBuilder = true, setterPrefix = "with")
public class PingTask extends BaseModel {
	
	private String store;
	private String deviceId;
	private String correlationId;
	private String externalId;
	private String extClientId;
	private String taskPriority = TaskPriority.PING.name();
	
	@Override
	public String toString() {
		return GsonHelper.toJson(this);
	}
}
