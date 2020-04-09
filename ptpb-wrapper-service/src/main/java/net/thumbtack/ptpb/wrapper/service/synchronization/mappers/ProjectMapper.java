package net.thumbtack.ptpb.wrapper.service.synchronization.mappers;

import net.thumbtack.ptpb.wrapper.client.project.ProjectDto;
import net.thumbtack.ptpb.wrapper.db.project.Project;

import java.util.LinkedList;
import java.util.List;

public class ProjectMapper {

    public static Project toProject(ProjectDto dto, long userId) {
        return Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .userId(userId)
                .color(dto.getColor())
                .build();
    }

    public static List<Project> toProjects(List<ProjectDto> projectsDto, long userId) {
        List<Project> list = new LinkedList<>();
        projectsDto.forEach(p -> list.add(toProject(p, userId)));
        return list;
    }
}
