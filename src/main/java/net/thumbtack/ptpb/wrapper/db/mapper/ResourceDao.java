package net.thumbtack.ptpb.wrapper.db.mapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResourceDao {

    @NonNull
    private final ResourceMapper resourceMapper;

    public Optional<Resource> getResourceById(String id) {
        return resourceMapper.findById(id);
    }

    public void insertResource(Resource resource) {
        resourceMapper.save(resource);
    }

    public void insertResources(List<Resource> resources) {
        resourceMapper.saveAll(resources);
    }

    public void deleteAllResources() {
        resourceMapper.deleteAll();
    }

    public List<Resource> getResourcesByTodoistId(long id) {
        List<Resource> resources = new LinkedList<>();
        resourceMapper.findByTodoistId(id).forEach(resources::add);
        return resources;
    }
}
