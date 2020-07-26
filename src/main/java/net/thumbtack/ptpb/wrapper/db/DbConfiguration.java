package net.thumbtack.ptpb.wrapper.db;

import com.aerospike.client.Host;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableAerospikeRepositories
public class DbConfiguration extends AbstractAerospikeDataConfiguration {

    private final DbProperties dbProperties;

    @Override
    protected Collection<Host> getHosts() {
        return Collections.singleton(new Host(dbProperties.getHost(), dbProperties.getPort()));
    }

    @Override
    protected String nameSpace() {
        return dbProperties.getNamespace();
    }
}