package at.ftmahringer.tabsystem.repositories;

import at.ftmahringer.tabsystem.model.Tab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TabRepository extends JpaRepository<Tab, Long> {
    @Query("SELECT t FROM Tab t WHERE t.isActive = true ORDER BY t.priorityOrder")
    List<Tab> findAllActive();
}
