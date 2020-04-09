package net.thumbtack.ptpb.wrapper.db.project;

import java.util.List;
import java.util.Optional;

public interface ProjectDao {
    Optional<Project> getProjectById(long id);

    List<Project> getAllProjects();

    void insertProject(Project project);

    void insertProjects(List<Project> projects);

    void deleteAllProjects();

    List<Project> getProjectsByUserId(long userId);

    void deleteProjectById(long projectId);
}
