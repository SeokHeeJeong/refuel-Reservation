package refuel;

import javax.persistence.*;

import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String fuelType;
    private int qty;
    private String customerId;
    private String stationId;
    private String reservationStatus;
    private Long price;
    private Long refuelId;


    @PostPersist
    public void onPostPersist(){

        if(reservationStatus.equals("UNRESERVED")){

            UnReserved unReserved = new UnReserved();
            BeanUtils.copyProperties(this, unReserved);
            unReserved.publishAfterCommit();

        } else {

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

    }

    @PostRemove
    public void onPostRemove(){
        ReservationCanceled reservationCanceled = new ReservationCanceled();
        BeanUtils.copyProperties(this, reservationCanceled);
        // add
        reservationCanceled.setReservationStatus("CANCELLED");
        reservationCanceled.publishAfterCommit();


    }

}
