package net.thumbtack.ptpb.wrapper.db.project;

import org.springframework.data.aerospike.repository.AerospikeRepository;

interface ProjectMapper extends AerospikeRepository<Project, Long> {
    Iterable<Project> findByUserId(long userId);
}