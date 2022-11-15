package com.example.project.dto.topic;

import com.example.project.dto.page.PageDto;
import com.example.project.models.entities.Topic;

import java.util.List;

public class TopicPageDto extends PageDto {
    private List<Topic> topics;

    public TopicPageDto(int currentPage, int totalPages, List<Topic> topics) {
        super(currentPage, totalPages);
        this.topics = topics;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
