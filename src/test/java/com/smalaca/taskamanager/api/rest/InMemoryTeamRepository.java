package com.smalaca.taskamanager.api.rest;

import com.smalaca.taskamanager.domain.Team;
import com.smalaca.taskamanager.domain.TeamRepository;
import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.List.copyOf;

class InMemoryTeamRepository implements TeamRepository {
    private final Map<Long, Team> teams = new HashMap<>();

    public InMemoryTeamRepository() {
        teams.put(1L, createTeam(1L, "Avengers"));
        teams.put(2L, createTeam(2L, "Fantastic Four"));
        teams.put(3L, createTeam(3L, "X-Men"));
        teams.put(4L, createTeam(4L, "X Force"));
        teams.put(5L, createTeam(5L, "Champions"));
    }

    private Team createTeam(long id, String name) {
        Team team = new Team();
        team.setName(name);
        setId(id, team);
        return team;
    }

    @Override
    public Team save(Team team) {
        if (team.getId() == null) {
            setId(RandomUtils.nextLong(), team);
        }

        teams.put(team.getId(), team);

        return team;
    }

    private void setId(long id, Team team) {
        try {
            Field fieldId = Team.class.getDeclaredField("id");
            fieldId.setAccessible(true);
            fieldId.set(team, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <S extends Team> Iterable<S> saveAll(Iterable<S> teams) {
        return null;
    }

    @Override
    public Optional<Team> findByName(String name) {
        for (Team team : findAll()) {
            if (team.getName().equals(name)) {
                return Optional.of(team);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Team> findById(Long id) {
        if (teams.containsKey(id)) {
            return Optional.of(teams.get(id));
        }

        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Team> findAll() {
        return copyOf(teams.values());
    }

    @Override
    public Iterable<Team> findAllById(Iterable<Long> ids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void delete(Team team) {
        teams.remove(team.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends Team> iterable) {

    }

    @Override
    public void deleteAll() {

    }
}
