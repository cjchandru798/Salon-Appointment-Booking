package com.project.ProjectSalon.repo;

import com.project.ProjectSalon.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /*  Detect overlaps – used while booking  */
    @Query("""
            SELECT a FROM Appointment a
             WHERE a.staff.staffId = :staffId
               AND a.appointmentDate = :date
               AND (:start < a.endTime)
               AND (:end   > a.startTime)
           """)
    List<Appointment> findConflicts(@Param("staffId") Long staffId,
                                    @Param("date")     LocalDate date,
                                    @Param("start")    LocalTime start,
                                    @Param("end")      LocalTime end);

    /*  Queries used by the dashboard / list pages  */
    List<Appointment> findByCustomers_CustomerId(Long customerId);
    List<Appointment> findByStaff_StaffId(Long staffId);
}
