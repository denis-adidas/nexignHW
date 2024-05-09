package ru.bootcamp.brt.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallsRepo extends JpaRepository<Calls, Integer> {
}
