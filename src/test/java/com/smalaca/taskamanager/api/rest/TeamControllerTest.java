package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.dto.TeamDto;
import com.smalaca.taskamanager.dto.TeamMembersDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class TeamControllerTest {
    private static final Long EXISTING_TEAM_ID = 1L;
    private static final String EXISTING_TEAM_NAME = "Avengers";
    private static final Long NOT_EXISTING_TEAM_ID = 101L;
    private static final TeamDto NO_TEAM_DATA = null;
    private static final Long NOT_EXISTING_USER_ID = 113L;

    private final TeamController controller = new TeamController(new InMemoryTeamRepository(), new InMemoryUserRepository());

    @Test
    void shouldReturnAllTeams() {
        ResponseEntity<List<TeamDto>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(OK);

        assertThat(response.getBody()).hasSize(5)
                .anySatisfy(dto -> assertTeam(dto, 1L, "Avengers"))
                .anySatisfy(dto -> assertTeam(dto, 2L, "Fantastic Four"))
                .anySatisfy(dto -> assertTeam(dto, 3L, "X-Men"))
                .anySatisfy(dto -> assertTeam(dto, 4L, "X Force"))
                .anySatisfy(dto -> assertTeam(dto, 5L, "Champions"));
    }

    private void assertTeam(TeamDto dto, long id, String name) {
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo(name);
    }

    @Test
    void shouldReturnNotFoundIfRetrievedTeamDoesNotExist() {
        ResponseEntity<TeamDto> response = controller.findById(NOT_EXISTING_TEAM_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldReturnExistingTeam() {
        ResponseEntity<TeamDto> response = controller.findById(EXISTING_TEAM_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertTeam(response.getBody());
    }

    @Test
    void shouldInformAboutConflictWhenCreatedTeamAlreadyExists() {
        UriComponentsBuilder uriComponentsBuilder = null;
        TeamDto existing = new TeamDto();
        existing.setName(EXISTING_TEAM_NAME);

        ResponseEntity<Void> response = controller.createTeam(existing, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
    }

    @Test
    void shouldCreateTeam() {
        TeamDto teamDto = new TeamDto();
        teamDto.setName("Invaders");
        UriComponentsBuilder uriComponentsBuilder = fromUriString("/");

        ResponseEntity<Void> response = controller.createTeam(teamDto, uriComponentsBuilder);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getHeaders().getLocation().getPath()).matches("/team/[0-9a-z\\-]+");
        assertThatTeamWasCreated("Invaders", response.getHeaders());
    }

    private void assertThatTeamWasCreated(String name,HttpHeaders headers) {
        String userId = headers.getLocation().getPath().replace("/team/", "");
        TeamDto found = controller.findById(Long.valueOf(userId)).getBody();

        assertThat(found.getName()).isEqualTo(name);
    }

    @Test
    void shouldReturnNotFoundIfUpdatedTeamDoesNotExist() {
        ResponseEntity<TeamDto> response = controller.updateTeam(NOT_EXISTING_TEAM_ID, NO_TEAM_DATA);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldNotUpdateAnythingWhenNoChangesSend() {
        ResponseEntity<TeamDto> response = controller.updateTeam(EXISTING_TEAM_ID, new TeamDto());

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertTeam(controller.findById(EXISTING_TEAM_ID).getBody());
    }

    private void assertTeam(TeamDto teamDto) {
        assertThat(teamDto.getId()).isEqualTo(EXISTING_TEAM_ID);
        assertThat(teamDto.getName()).isEqualTo(EXISTING_TEAM_NAME);
        assertThat(teamDto.getUserIds()).isEmpty();
    }

    @Test
    void shouldUpdateAboutSuccessIfUpdatingExistingTeam() {
        String newName = randomString();
        TeamDto dto = new TeamDto();
        dto.setName(newName);

        ResponseEntity<TeamDto> response = controller.updateTeam(EXISTING_TEAM_ID, dto);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        TeamDto actualDto = response.getBody();
        assertThat(actualDto.getId()).isEqualTo(EXISTING_TEAM_ID);
        assertThat(actualDto.getName()).isEqualTo(newName);
        TeamDto updated = controller.findById(EXISTING_TEAM_ID).getBody();
        assertThat(updated.getName()).isEqualTo(newName);
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    @Test
    void shouldReturnNotFoundIfDeletedTeamDoesNotExist() {
        ResponseEntity<Void> response = controller.deleteTeam(NOT_EXISTING_TEAM_ID);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldDeleteExistingTeam() {
        ResponseEntity<Void> response = controller.deleteTeam(EXISTING_TEAM_ID);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(controller.findById(EXISTING_TEAM_ID).getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldReturnNotFoundWhenAddingUsersToNonExistingTeam() {
        ResponseEntity<Void> response = controller.addTeamMembers(NOT_EXISTING_TEAM_ID, teamMembersDto(1L, 2L, 5L));

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldAddUsersToTeam() {
        ResponseEntity<Void> response = controller.addTeamMembers(EXISTING_TEAM_ID, teamMembersDto(1L, 2L, 5L));

        assertThat(response.getStatusCode()).isEqualTo(OK);
        TeamDto teamDto = controller.findById(EXISTING_TEAM_ID).getBody();
        assertThat(teamDto.getUserIds()).containsExactlyInAnyOrder(1L, 2L, 5L);
    }

    @Test
    void shouldAddUsersToTeamWhitTeamMembers() {
        controller.addTeamMembers(EXISTING_TEAM_ID, teamMembersDto(1L, 2L, 5L));

        controller.addTeamMembers(EXISTING_TEAM_ID, teamMembersDto(4L));

        TeamDto teamDto = controller.findById(EXISTING_TEAM_ID).getBody();
        assertThat(teamDto.getUserIds()).containsExactlyInAnyOrder(1L, 2L, 4L, 5L);
    }

    @Test
    void shouldReturnNotFoundWhenAddingNonExistingUsersToTeam() {
        TeamMembersDto dto = teamMembersDto(1L, 2L, NOT_EXISTING_USER_ID);

        ResponseEntity<Void> response = controller.addTeamMembers(EXISTING_TEAM_ID, dto);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    private TeamMembersDto teamMembersDto(Long... userIds) {
        TeamMembersDto dto = new TeamMembersDto();
        dto.setUserIds(asList(userIds));
        return dto;
    }
}