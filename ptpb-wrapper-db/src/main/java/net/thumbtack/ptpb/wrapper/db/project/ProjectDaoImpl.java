package net.thumbtack.ptpb.wrapper.db.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectDaoImpl implements ProjectDao {
    private final ProjectMapper projectMapper;

    @Override
    public Optional<Project> getProjectById(long id) {
        return projectMapper.findById(id);
    }

    @Override
    public List<Project> getAllProjects() {
        List<Project> projects = new LinkedList<>();
        projectMapper.findAll().forEach(projects::add);
        return projects;
    }

    @Override
    public void insertProject(Project project) {
        projectMapper.save(project);
    }

    @Override
    public void insertProjects(List<Project> projects) {
        projectMapper.saveAll(projects);
    }

    @Override
    public void deleteAllProjects() {
        projectMapper.deleteAll();
    }

    @Override
    public List<Project> getProjectsByUserId(long userId) {
        List<Project> projects = new LinkedList<>();
        projectMapper.findByUserId(userId).forEach(projects::add);
        return projects;
    }

    @Override
    public void deleteProjectById(long projectId) {
        projectMapper.deleteById(projectId);
    }

}
