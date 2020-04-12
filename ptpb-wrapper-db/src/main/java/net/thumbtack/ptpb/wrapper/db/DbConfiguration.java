package net.thumbtack.ptpb.wrapper.db;

import com.aerospike.client.Host;
import lombok.RequiredArgsConstructor;
import net.thumbtack.ptpb.wrapper.db.item.ItemMapper;
import net.thumbtack.ptpb.wrapper.db.project.ProjectMapper;
import net.thumbtack.ptpb.wrapper.db.sync.SyncMapper;
import net.thumbtack.ptpb.wrapper.db.user.UserDao;
import net.thumbtack.ptpb.wrapper.db.user.UserDaoImpl;
import net.thumbtack.ptpb.wrapper.db.user.UserMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableAutoConfiguration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableAerospikeRepositories(basePackages = {"net.thumbtack.ptpb.wrapper.db"})
@Import(DbProperties.class)
@ComponentScan(basePackages = {
        "net.thumbtack.ptpb.wrapper.db"
})
public class DbConfiguration extends AbstractAerospikeDataConfiguration {

    private final DbProperties dbProperties;

    @Bean
    public UserDao userDao(UserMapper userMapper) {
        return new UserDaoImpl(userMapper);
    }

    @Override
    protected Collection<Host> getHosts() {
        return Collections.singleton(new Host(dbProperties.getHost(), dbProperties.getPort()));
    }

    @Override
    protected String nameSpace() {
        return dbProperties.getNamespace();
    }
}