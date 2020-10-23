package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.domain.ProductOwner;
import com.smalaca.taskamanager.domain.Project;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProjectProductOwnerRelationTest {
    private final Map<String, Long> projects = new HashMap<>();
    private final Map<String, Long> productOwners = new HashMap<>();

    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProductOwnerRepository productOwnerRepository;

    @BeforeEach
    void projectsAndProductOwners() {
        givenProject("Avengers vs. X-Men");
        givenProject("Secret Wars");
        givenProject("Civil War");
        givenProject("Phoenix Saga");
        givenProject("Thanos Imperative");

        givenProductOwner("Gwen", "Stacy");
        givenProductOwner("Miles", "Morales");
        givenProductOwner("Miguel", "O'Hara");
    }

    private void givenProject(String name) {
        Project project = project(name);
        Long id = projectRepository.save(project).getId();

        projects.put(name, id);
    }

    private Project project(String name) {
        Project project = new Project();
        project.setName(name);
        return project;
    }

    private void givenProductOwner(String firstName, String lastName) {
        ProductOwner productOwner = productOwner(firstName, lastName);
        Long id = productOwnerRepository.save(productOwner).getId();

        productOwners.put(firstName + lastName, id);
    }

    private ProductOwner productOwner(String firstName, String lastName) {
        ProductOwner productOwner = new ProductOwner();
        productOwner.setFirstName(firstName);
        productOwner.setLastName(lastName);
        return productOwner;
    }

    @AfterEach
    void removeAll() {
        projectRepository.deleteAll();
        productOwnerRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAssignProductOwnerToProject() {
        Project avengersVsXmen = findProjectByName("Avengers vs. X-Men");
        Project secretWars = findProjectByName("Secret Wars");
        Project civilWar = findProjectByName("Civil War");
        Project phoenixSaga = findProjectByName("Phoenix Saga");
        Project thanosImperative = findProjectByName("Thanos Imperative");

        ProductOwner gwenStacy = findProductOwnerByName("Gwen", "Stacy");
        ProductOwner milesMorales = findProductOwnerByName("Miles", "Morales");
        ProductOwner miguelOHara = findProductOwnerByName("Miguel", "O'Hara");

        avengersVsXmen.setProductOwner(gwenStacy);
        secretWars.setProductOwner(gwenStacy);
        gwenStacy.setProjects(asList(avengersVsXmen, secretWars));

        civilWar.setProductOwner(milesMorales);
        milesMorales.setProjects(asList(civilWar));

        phoenixSaga.setProductOwner(miguelOHara);
        thanosImperative.setProductOwner(miguelOHara);
        miguelOHara.setProjects(asList(phoenixSaga, thanosImperative));

        projectRepository.saveAll(asList(avengersVsXmen, secretWars, civilWar, phoenixSaga, thanosImperative));
        productOwnerRepository.saveAll(asList(gwenStacy, milesMorales, miguelOHara));

        assertThat(findProjectByName("Avengers vs. X-Men").getProductOwner().getId()).isEqualTo(gwenStacy.getId());
        assertThat(findProjectByName("Secret Wars").getProductOwner().getId()).isEqualTo(gwenStacy.getId());
        assertThat(findProjectByName("Civil War").getProductOwner().getId()).isEqualTo(milesMorales.getId());
        assertThat(findProjectByName("Phoenix Saga").getProductOwner().getId()).isEqualTo(miguelOHara.getId());
        assertThat(findProjectByName("Thanos Imperative").getProductOwner().getId()).isEqualTo(miguelOHara.getId());

        assertThat(asIds(findProductOwnerByName("Gwen", "Stacy").getProjects()))
                .containsExactlyInAnyOrder(avengersVsXmen.getId(), secretWars.getId());
        assertThat(asIds(findProductOwnerByName("Miles", "Morales").getProjects()))
                .containsExactlyInAnyOrder(civilWar.getId());
        assertThat(asIds(findProductOwnerByName("Miguel", "O'Hara").getProjects()))
                .containsExactlyInAnyOrder(phoenixSaga.getId(), thanosImperative.getId());
    }

    private List<Long> asIds(List<Project> projects) {
        return projects.stream().map(Project::getId).collect(toList());
    }

    private Project findProjectByName(String name) {
        return projectRepository.findById(projects.get(name)).get();
    }

    private ProductOwner findProductOwnerByName(String firstName, String lastName) {
        return productOwnerRepository.findById(productOwners.get(firstName + lastName)).get();
    }
}
