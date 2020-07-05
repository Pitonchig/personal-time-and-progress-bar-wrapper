package net.thumbtack.ptpb.wrapper.db.mapper;

import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface ResourceMapper extends AerospikeRepository<Resource, String> {
    Iterable<Resource> findByTodoistId(long todoistId);
}