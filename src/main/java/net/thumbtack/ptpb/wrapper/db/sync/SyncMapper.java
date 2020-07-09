package net.thumbtack.ptpb.wrapper.db.sync;

import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface SyncMapper extends AerospikeRepository<Sync, String> {

}
