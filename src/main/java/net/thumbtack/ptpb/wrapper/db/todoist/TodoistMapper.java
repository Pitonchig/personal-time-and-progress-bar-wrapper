package net.thumbtack.ptpb.wrapper.db.todoist;

import org.springframework.data.aerospike.repository.AerospikeRepository;

interface TodoistMapper  extends AerospikeRepository<Todoist, String> {

}
