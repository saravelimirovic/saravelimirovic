package com.example.Backend.repository;

import com.example.Backend.dto.CustomerMapDTO;
import com.example.Backend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT new com.example.Backend.dto.CustomerMapDTO(u.firstName, u.lastName, u.longitude, u.latitude) " +
            "FROM User u " +
            "WHERE u.longitude > 0 AND u.latitude > 0")
    List<CustomerMapDTO> findAllUsersWithLocation();

    Optional<User> findUserById(Long id);

    Long deleteUserById(Long id);
    User findUserByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.companies c WHERE c.id = :companyId")
    User findByCompanyId(@Param("companyId") Long companyId);
}
