package com.example.Backend.validation;

import com.example.Backend.dto.CompanyDTO;
import com.example.Backend.entity.Company;
import com.example.Backend.repository.CompanyRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CompanyNameAvailableValidator implements ConstraintValidator<CompanyNameAvailable, CompanyDTO> {

    private final CompanyRepository companyRepository;

    // vraca se true ako treba da prodje dalje do endpointa tj ako ne nadje ni jednu kompaniju
    @Override
    public boolean isValid(CompanyDTO companyDTO, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Company> maybeCompany = companyRepository.findByName(companyDTO.getName());

        return maybeCompany.isEmpty();
    }

}
