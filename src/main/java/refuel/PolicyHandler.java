package refuel;

import org.springframework.beans.BeanUtils;
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
            Reservation reservation = null ;

            try {
                reservation = reservationOptional.get();

            } catch (Exception e) {
                // 예약이 취소 되었거나 미등록된 예약 정보인 경우 - 보상트랜젝션
                reservation = new Reservation();
                BeanUtils.copyProperties(refuelled, reservation);
                reservation.setReservationStatus("UNRESERVED");
                reservation.setRefuelId(refuelled.getId());

                ReservationRepository.save(reservation);
                return;
            }

            reservation.setReservationStatus("REFUELLED");
            reservation.setRefuelId(refuelled.getId());

            ReservationRepository.save(reservation);
        }
    }

}
