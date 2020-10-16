package refuel;

import lombok.Data;

@Data
public class Refuelled extends AbstractEvent {

    private Long id;
    private Long reservationId;
    private String fuelType;
    private int qty;
    private String customerId;
    private String stationId;
    private String reservationStatus;

}