<<<<<<< HEAD

package com.royalopulence.repository;



=======
package com.royalopulence.repository;

>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
import com.royalopulence.model.core.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {

    List<Reservation> findByUserId(String userId);
<<<<<<< HEAD
    List<Reservation> findByRoomId(String roomId);
=======
    //List<Reservation> findByRoomId(String roomId);
    List<Reservation> findByRoomIdsContaining(String roomId);


    
>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
}
