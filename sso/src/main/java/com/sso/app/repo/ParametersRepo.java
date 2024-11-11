package com.sso.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sso.app.entity.Parameters;


@Repository
public interface ParametersRepo extends JpaRepository<Parameters, Long>{
	
	Parameters findFirstByKeyAndActive(String key, boolean active);
	

}
