package com.smalaca.taskamanager.repository;

import com.smalaca.taskamanager.model.entities.Epic;
import com.smalaca.taskamanager.model.entities.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EpicStoryRelationTest {
    private final Map<String, Long> epics = new HashMap<>();
    private final Map<String, Long> storys = new HashMap<>();

    @Autowired private EpicRepository epicRepository;
    @Autowired private StoryRepository storyRepository;

    @BeforeEach
    void epicsAndProductOwners() {
        givenEpic("Avengers vs. X-Men");
        givenEpic("Secret Wars");
        givenEpic("Civil War");

        givenStory("AVX 1");
        givenStory("AVX 2");
        givenStory("AVX 3");
        givenStory("AVX 4");
        givenStory("Secret Wars 1");
        givenStory("Secret Wars 2");
        givenStory("Civil War 1");
    }

    private void givenEpic(String name) {
        Epic epic = epic(name);
        Long id = epicRepository.save(epic).getId();

        epics.put(name, id);
    }

    private Epic epic(String name) {
        Epic epic = new Epic();
        epic.setTitle(name);
        return epic;
    }

    private void givenStory(String name) {
        Story story = story(name);
        Long id = storyRepository.save(story).getId();
        storys.put(name, id);
    }

    private Story story(String title) {
        Story story = new Story();
        story.setTitle(title);
        return story;
    }

    @AfterEach
    void removeAll() {
        epicRepository.deleteAll();
        storyRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldAssignProductOwnerToEpic() {
        Epic avengersVsXmen = findEpicByName("Avengers vs. X-Men");
        Epic secretWars = findEpicByName("Secret Wars");
        Epic civilWar = findEpicByName("Civil War");

        Story avx1 = findStoryByTitle("AVX 1");
        Story avx2 = findStoryByTitle("AVX 2");
        Story avx3 = findStoryByTitle("AVX 3");
        Story avx4 = findStoryByTitle("AVX 4");
        Story secretWars1 = findStoryByTitle("Secret Wars 1");
        Story secretWars2 = findStoryByTitle("Secret Wars 2");
        Story civilWar1 = findStoryByTitle("Civil War 1");

        avengersVsXmen.addStory(avx1);
        avengersVsXmen.addStory(avx2);
        avengersVsXmen.addStory(avx3);
        avengersVsXmen.addStory(avx4);
        avx1.setEpic(avengersVsXmen);
        avx2.setEpic(avengersVsXmen);
        avx3.setEpic(avengersVsXmen);
        avx4.setEpic(avengersVsXmen);

        secretWars.addStory(secretWars1);
        secretWars.addStory(secretWars2);
        secretWars1.setEpic(secretWars);
        secretWars2.setEpic(secretWars);

        civilWar.addStory(civilWar1);
        civilWar1.setEpic(civilWar);

        epicRepository.saveAll(asList(avengersVsXmen, secretWars, civilWar));
        storyRepository.saveAll(asList(avx1, avx2, avx3, avx4, secretWars1, secretWars2, civilWar1));

        assertThat(asIds(findEpicByName("Avengers vs. X-Men").getStories()))
                .containsExactlyInAnyOrder(avx1.getId(), avx2.getId(), avx3.getId(), avx4.getId());
        assertThat(asIds(findEpicByName("Secret Wars").getStories()))
                .containsExactlyInAnyOrder(secretWars1.getId(), secretWars2.getId());
        assertThat(asIds(findEpicByName("Civil War").getStories()))
                .containsExactlyInAnyOrder(civilWar1.getId());

        assertThat(findStoryByTitle("AVX 1").getEpic().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findStoryByTitle("AVX 2").getEpic().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findStoryByTitle("AVX 3").getEpic().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findStoryByTitle("AVX 4").getEpic().getId()).isEqualTo(avengersVsXmen.getId());
        assertThat(findStoryByTitle("Secret Wars 1").getEpic().getId()).isEqualTo(secretWars.getId());
        assertThat(findStoryByTitle("Secret Wars 2").getEpic().getId()).isEqualTo(secretWars.getId());
        assertThat(findStoryByTitle("Civil War 1").getEpic().getId()).isEqualTo(civilWar.getId());
    }

    private List<Long> asIds(List<Story> storys) {
        return storys.stream().map(Story::getId).collect(toList());
    }

    private Epic findEpicByName(String name) {
        return epicRepository.findById(epics.get(name)).get();
    }

    private Story findStoryByTitle(String name) {
        return storyRepository.findById(storys.get(name)).get();
    }
}
