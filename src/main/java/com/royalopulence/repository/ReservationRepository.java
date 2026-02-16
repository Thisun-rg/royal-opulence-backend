
package com.royalopulence.repository;


import com.royalopulence.model.core.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {

    List<Reservation> findByUserId(String userId);

    List<Reservation> findByRoomId(String roomId);

    //List<Reservation> findByRoomId(String roomId);
    List<Reservation> findByRoomIdsContaining(String roomId);


    

}
