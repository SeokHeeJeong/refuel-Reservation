package refuel.external;

import lombok.Data;

@Data
public class Payment {

    private Long id;
    private Long reservationId;
    private String fuelType;
    private int qty;
    private String customerId;
    private String stationId;
    private String reservationStatus;
    private Long price;
    private String paymentType;
    private String paymentStatus;

}
