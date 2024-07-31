package io.vusion.vtransmit.v2.commons.model.request;

import io.vusion.gson.utils.GsonHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PingRequest implements Serializable, RequestWithLabelId {
    private String labelId;

    private String taskPriority;

    @Override
    public String toString() {
        return GsonHelper.getGsonNull().toJson(this);
    }
}
