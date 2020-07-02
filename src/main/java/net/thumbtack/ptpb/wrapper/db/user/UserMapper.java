package net.thumbtack.ptpb.wrapper.db.user;

import net.thumbtack.ptpb.wrapper.db.sync.Sync;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface UserMapper extends AerospikeRepository<User, String> {
    Iterable<Sync> findByUserId(String userId);
}
