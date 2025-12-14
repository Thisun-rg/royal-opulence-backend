
package com.royalopulence.dto.room;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {
    private String id;
    private String roomNumber;
    private String roomTypeId;
    private String status;
}
