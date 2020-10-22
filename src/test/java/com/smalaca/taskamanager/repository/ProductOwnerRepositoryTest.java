package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.domain.EmailAddress;
import com.smalaca.taskamanager.domain.PhoneNumber;
import com.smalaca.taskamanager.domain.ProductOwner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductOwnerRepositoryTest {
    @Autowired private ProductOwnerRepository repository;

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateProductOwner() {
        ProductOwner productOwner = new ProductOwner();
        productOwner.setFirstName("Johnny");
        productOwner.setLastName("Blaze");
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("ghost.rider@hell.com");
        productOwner.setEmailAddress(emailAddress);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix("123");
        phoneNumber.setNumber("1223334444");
        productOwner.setPhoneNumber(phoneNumber);

        ProductOwner saved = repository.save(productOwner);
        ProductOwner actual = repository.findById(saved.getId()).get();

        assertThat(actual.getFirstName()).isEqualTo("Johnny");
        assertThat(actual.getLastName()).isEqualTo("Blaze");
        assertThat(actual.getEmailAddress()).isEqualTo(emailAddress);
        assertThat(actual.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    void shouldFindSpecificProductOwner() {
        repository.save(productOwner("Steve", "Rogers"));
        Long id = repository.save(productOwner("Wanda", "Maximoff")).getId();
        repository.save(productOwner("Kitty", "Pryde"));

        ProductOwner actual = repository.findById(id).get();

        assertThat(actual.getFirstName()).isEqualTo("Wanda");
        assertThat(actual.getLastName()).isEqualTo("Maximoff");
    }

    private ProductOwner productOwner(String firstName, String lastName) {
        ProductOwner productOwner = new ProductOwner();
        productOwner.setFirstName(firstName);
        productOwner.setLastName(lastName);
        return productOwner;
    }
}