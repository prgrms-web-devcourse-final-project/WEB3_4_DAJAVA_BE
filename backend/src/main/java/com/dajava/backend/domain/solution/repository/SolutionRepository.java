package com.dajava.backend.domain.solution.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.solution.entity.SolutionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SolutionRepository extends JpaRepository<SolutionEntity, Long>{
	Optional<SolutionEntity> findByRegister(Register register);
}