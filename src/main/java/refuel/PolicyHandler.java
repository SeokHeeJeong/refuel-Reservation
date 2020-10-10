package refuel;

import refuel.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    ReservationRepository ReservationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRefuelled_UpdateStatus(@Payload Refuelled refuelled){

        if(refuelled.isMe()){
            // 주유 완료시 상태 변경
            System.out.println("##### listener UpdateStatus : " + refuelled.toJson());

            Optional<Reservation> reservationOptional = ReservationRepository.findById(refuelled.getReservationId());
            Reservation reservation = reservationOptional.get();
            reservation.setReservationStatus("REFUELLED");

            ReservationRepository.save(reservation);
        }
    }

}
