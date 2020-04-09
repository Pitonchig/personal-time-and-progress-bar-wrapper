package net.thumbtack.ptpb.wrapper.db.sync;

import org.springframework.data.aerospike.repository.AerospikeRepository;

interface SyncMapper extends AerospikeRepository<Sync, Long> {

}
