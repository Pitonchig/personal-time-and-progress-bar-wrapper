package net.thumbtack.ptpb.wrapper.db.item;

import org.springframework.data.aerospike.repository.AerospikeRepository;

interface ItemMapper extends AerospikeRepository<Item, Long> {
    Iterable<Item> findByProjectId(long userId);
}