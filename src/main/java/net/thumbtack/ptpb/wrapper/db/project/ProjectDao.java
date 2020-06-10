package net.thumbtack.ptpb.wrapper.db.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectDao {

    private final ProjectMapper projectMapper;

    public Optional<Project> getProjectById(long id) {
        return projectMapper.findById(id);
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new LinkedList<>();
        projectMapper.findAll().forEach(projects::add);
        return projects;
    }

    public void insertProject(Project project) {
        projectMapper.save(project);
    }

    public void insertProjects(List<Project> projects) {
        projectMapper.saveAll(projects);
    }

    public void deleteAllProjects() {
        projectMapper.deleteAll();
    }

    public List<Project> getProjectsByUserId(long userId) {
        List<Project> projects = new LinkedList<>();
        projectMapper.findByUserId(userId).forEach(projects::add);
        return projects;
    }

    public void deleteProjectById(long projectId) {
        projectMapper.deleteById(projectId);
    }

}
