package io.vusion.rfid.services.front.model;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor @AllArgsConstructor
public class MessageResponse implements Serializable {
    @Setter(AccessLevel.PROTECTED)
    private String message;
}
