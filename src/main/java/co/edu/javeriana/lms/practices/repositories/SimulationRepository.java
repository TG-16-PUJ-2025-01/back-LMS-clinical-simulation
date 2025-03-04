package co.edu.javeriana.lms.practices.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.javeriana.lms.practices.models.Simulation;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {
}
