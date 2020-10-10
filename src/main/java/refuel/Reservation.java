package refuel;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String fuelType;
    private Integer qty;
    private String customerId;
    private String stationId;
    private String reservationStatus;
    private Long price;

    @PostPersist
    public void onPostPersist(){
        // PrePersist 에서는 reservation id 값 미생성
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        refuel.external.Payment payment = new refuel.external.Payment();
        // mappings goes here
        BeanUtils.copyProperties(this, payment);
        payment.setReservationId(this.id);
        payment.setPaymentStatus("PAID");
        payment.setPaymentType("CARD");

        ReservationApplication.applicationContext.getBean(refuel.external.PaymentService.class)
            .pay(payment);


    }

    @PostRemove
    public void onPostRemove(){
        ReservationCanceled reservationCanceled = new ReservationCanceled();
        BeanUtils.copyProperties(this, reservationCanceled);
        // add
        reservationCanceled.setReservationStatus("CANCELLED");
        reservationCanceled.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }




}
