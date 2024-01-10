package com.example.Backend.repository;

import com.example.Backend.entity.Company;
import com.example.Backend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends ListCrudRepository<Company, Long> {
    // OVO JE SAMO ZA TESTIRANJE DA LI MI RADI CUSTOMIZOVANA ANOTACIJA
    Optional<Company> findByName(String name);

    Optional<Company> findCompanyByUserId(Long userId);

    Optional<Company> findCompanyById(Long id);

    List<Company> findCompaniesByUserId(Long userId);

    List<Company> findCompaniesByUserIn(List<User> users);

    List<Company> findByStreetId(Long streetId);

    List<Company> findAllByIdNot(Long id);

    List<Company> findCompaniesByNameContainingIgnoreCase(String search);
}
