package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.domain.EmailAddress;
import com.smalaca.taskamanager.domain.PhoneNumber;
import com.smalaca.taskamanager.domain.ProductOwner;
import com.smalaca.taskamanager.domain.Project;
import com.smalaca.taskamanager.dto.ProductOwnerDto;
import com.smalaca.taskamanager.repository.ProductOwnerRepository;
import com.smalaca.taskamanager.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static com.google.common.primitives.Longs.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class ProductOwnerControllerTest {
    private static final String FIRST_NAME = "Steve";
    private static final String LAST_NAME = "Rogers";
    private static final String PHONE_PREFIX = "0000";
    private static final String PHONE_NUMBER = "123456789";
    private static final String EMAIL_ADDRESS = "steve.rogers@avengers.com";
    private static final long PROJECT_ID_1 = 13;
    private static final long PROJECT_ID_2 = 42;
    private static final long NEW_PROJECT_ID = 69;
    private static final long PRODUCT_OWNER_ID = 13L;

    private final ArgumentCaptor<ProductOwner> productOwnerCaptor = ArgumentCaptor.forClass(ProductOwner.class);
    private final ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);

    private final ProductOwnerRepository productOwnerRepository = mock(ProductOwnerRepository.class);
    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ProductOwnerController controller = new ProductOwnerController(productOwnerRepository, projectRepository);

    @Test
    void shouldRecognizeTheProductOwnerToFindDoesNotExist() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.empty());

        ResponseEntity<ProductOwnerDto> actual = controller.findById(PRODUCT_OWNER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFindProductOwner() {
        ProductOwner productOwner = productOwnerWithId();
        productOwner.setFirstName(FIRST_NAME);
        productOwner.setLastName(LAST_NAME);
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.of(productOwner));

        ResponseEntity<ProductOwnerDto> actual = controller.findById(PRODUCT_OWNER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductOwnerDto dto = actual.getBody();
        assertThat(dto.getId()).isEqualTo(PRODUCT_OWNER_ID);
        assertThat(dto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(dto.getLastName()).isEqualTo(LAST_NAME);
        assertThat(dto.getPhonePrefix()).isNull();
        assertThat(dto.getPhoneNumber()).isNull();
        assertThat(dto.getEmailAddress()).isNull();
        assertThat(dto.getProjectIds()).isEmpty();
    }

    @Test
    void shouldFindProductOwnerWithAllInformation() {
        ProductOwner productOwner = productOwner();
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.of(productOwner));

        ResponseEntity<ProductOwnerDto> actual = controller.findById(PRODUCT_OWNER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductOwnerDto dto = actual.getBody();
        assertThat(dto.getId()).isEqualTo(PRODUCT_OWNER_ID);
        assertThat(dto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(dto.getLastName()).isEqualTo(LAST_NAME);
        assertThat(dto.getPhonePrefix()).isEqualTo(PHONE_PREFIX);
        assertThat(dto.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(dto.getEmailAddress()).isEqualTo(EMAIL_ADDRESS);
        assertThat(dto.getProjectIds()).isEqualTo(asList(PROJECT_ID_1, PROJECT_ID_2));
    }

    @Test
    void shouldRecognizeProductOwnerToCreateAlreadyExists() {
        given(productOwnerRepository.findByFirstNameAndLastName(FIRST_NAME, LAST_NAME)).willReturn(Optional.of(productOwner()));
        ProductOwnerDto dto = new ProductOwnerDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);

        ResponseEntity<Void> actual = controller.create(dto, null);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldCreateProductOwner() {
        UriComponentsBuilder uriComponentsBuilder = fromUriString("/");
        given(productOwnerRepository.findByFirstNameAndLastName(FIRST_NAME, LAST_NAME)).willReturn(Optional.empty());
        given(productOwnerRepository.save(any())).willReturn(productOwnerWithId());
        ProductOwnerDto dto = new ProductOwnerDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);

        ResponseEntity<Void> actual = controller.create(dto, uriComponentsBuilder);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getHeaders().getLocation().getPath()).isEqualTo("/product-owner/13");
        then(productOwnerRepository).should().save(productOwnerCaptor.capture());
        ProductOwner productOwner = productOwnerCaptor.getValue();
        assertThat(productOwner.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(productOwner.getLastName()).isEqualTo(LAST_NAME);
        assertThat(productOwner.getPhoneNumber()).isNull();
        assertThat(productOwner.getEmailAddress()).isNull();
        assertThat(productOwner.getProjects()).isEmpty();
    }

    @Test
    void shouldRecognizeThereIsNoProductOwnerToUpdate() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.empty());

        ResponseEntity<ProductOwnerDto> actual = controller.update(PRODUCT_OWNER_ID, new ProductOwnerDto());

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateProductOwner() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.of(productOwner()));
        given(productOwnerRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        ProductOwnerDto dto = new ProductOwnerDto();
        dto.setPhonePrefix("9876");
        dto.setPhoneNumber("0987654321");
        dto.setEmailAddress("new.mail@address.com");

        ResponseEntity<ProductOwnerDto> actual = controller.update(PRODUCT_OWNER_ID, dto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductOwnerDto productOwnerDto = actual.getBody();
        assertThat(productOwnerDto.getId()).isEqualTo(PRODUCT_OWNER_ID);
        assertThat(productOwnerDto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(productOwnerDto.getLastName()).isEqualTo(LAST_NAME);
        assertThat(productOwnerDto.getPhonePrefix()).isEqualTo("9876");
        assertThat(productOwnerDto.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(productOwnerDto.getEmailAddress()).isEqualTo("new.mail@address.com");
    }

    @Test
    void shouldRecognizeThereIsNoProductOwnerToDelete() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.delete(PRODUCT_OWNER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldDeleteProductOwner() {
        ProductOwner productOwner = productOwner();
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.of(productOwner));

        ResponseEntity<Void> actual = controller.delete(PRODUCT_OWNER_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(productOwnerRepository).should().delete(productOwner);
    }

    @Test
    void shouldRecognizeThereIsNoProductOwnerToAddProject() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addProject(PRODUCT_OWNER_ID, NEW_PROJECT_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRecognizeProjectToAddDoesNotExist() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.of(productOwner()));
        given(projectRepository.findById(NEW_PROJECT_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.addProject(PRODUCT_OWNER_ID, NEW_PROJECT_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldAssignProjectToProductOwner() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.of(productOwner()));
        given(projectRepository.findById(NEW_PROJECT_ID)).willReturn(Optional.of(project(NEW_PROJECT_ID)));

        ResponseEntity<Void> actual = controller.addProject(PRODUCT_OWNER_ID, NEW_PROJECT_ID);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(productOwnerRepository).should().save(productOwnerCaptor.capture());
        assertThat(asProjectIds(productOwnerCaptor.getValue())).containsExactlyInAnyOrder(PROJECT_ID_1, PROJECT_ID_2, NEW_PROJECT_ID);
        then(projectRepository).should().save(projectCaptor.capture());
        ProductOwner projectProductOwner = projectCaptor.getValue().getProductOwner();
        assertThat(projectProductOwner.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(projectProductOwner.getLastName()).isEqualTo(LAST_NAME);
    }

    private List<Long> asProjectIds(ProductOwner actualProductOwner) {
        List<Long> projectIds = actualProductOwner.getProjects().stream().map(Project::getId).collect(toList());
        return projectIds;
    }

    @Test
    void shouldRecognizeThereIsNoProductOwnerToRemoveProject() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeProject(PRODUCT_OWNER_ID, PROJECT_ID_2);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRecognizeProjectToRemoveDoesNotExist() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.of(productOwner()));
        given(projectRepository.findById(PROJECT_ID_2)).willReturn(Optional.empty());

        ResponseEntity<Void> actual = controller.removeProject(PRODUCT_OWNER_ID, PROJECT_ID_2);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRemoveProjectToProductOwner() {
        given(productOwnerRepository.findById(PRODUCT_OWNER_ID)).willReturn(Optional.of(productOwner()));
        given(projectRepository.findById(PROJECT_ID_2)).willReturn(Optional.of(project(PROJECT_ID_2)));

        ResponseEntity<Void> actual = controller.removeProject(PRODUCT_OWNER_ID, PROJECT_ID_2);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(productOwnerRepository).should().save(productOwnerCaptor.capture());
        assertThat(asProjectIds(productOwnerCaptor.getValue())).containsExactlyInAnyOrder(PROJECT_ID_1);
        then(projectRepository).should().save(projectCaptor.capture());
        assertThat(projectCaptor.getValue().getProductOwner()).isNull();
    }

    private ProductOwner productOwner() {
        ProductOwner productOwner = productOwnerWithId();
        productOwner.setFirstName(FIRST_NAME);
        productOwner.setLastName(LAST_NAME);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPrefix(PHONE_PREFIX);
        phoneNumber.setNumber(PHONE_NUMBER);
        productOwner.setPhoneNumber(phoneNumber);
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress(EMAIL_ADDRESS);
        productOwner.setEmailAddress(emailAddress);
        productOwner.addProject(project(PROJECT_ID_1));
        productOwner.addProject(project(PROJECT_ID_2));
        return productOwner;
    }

    private ProductOwner productOwnerWithId() {
        try {
            ProductOwner productOwner = new ProductOwner();
            Field fieldId = ProductOwner.class.getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(productOwner, PRODUCT_OWNER_ID);
            return productOwner;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Project project(long id) {
        return setId(id, new Project());
    }

    private static Project setId(long id, Project project) {
        try {
            Field fieldId = Project.class.getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(project, id);
            return project;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}