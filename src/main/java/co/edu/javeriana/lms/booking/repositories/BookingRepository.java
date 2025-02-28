package co.edu.javeriana.lms.booking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.booking.models.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
}
