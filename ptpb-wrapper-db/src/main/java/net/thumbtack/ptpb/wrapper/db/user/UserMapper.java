package net.thumbtack.ptpb.wrapper.db.user;

import org.springframework.data.aerospike.repository.AerospikeRepository;

interface UserMapper extends AerospikeRepository<User, Long> {

}
