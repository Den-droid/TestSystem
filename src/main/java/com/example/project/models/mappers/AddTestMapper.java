package com.example.project.models.mappers;

import com.example.project.dto.test.AddTestDto;
import com.example.project.models.entities.Test;
import com.example.project.models.enums.TestDifficulty;

import java.time.LocalDateTime;

public class AddTestMapper {
    private AddTestMapper() {
    }

    public static Test map(AddTestDto addTestDto) {
        Test test = new Test();
        test.setName(addTestDto.getName());
        test.setTestDifficulty(TestDifficulty
                .getByText(addTestDto.getDifficulty()));
        test.setStartDate(LocalDateTime.parse(addTestDto.getStartDate()));
        test.setFinishDate(LocalDateTime.parse(addTestDto.getFinishDate()));
        test.setTimeLimit(addTestDto.getTimeLimit());
        test.setQuestionsCount(addTestDto.getQuestionsCount());
        return test;
    }
}
