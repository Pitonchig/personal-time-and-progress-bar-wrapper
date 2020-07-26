package net.thumbtack.ptpb.wrapper.db.sync;

import net.thumbtack.ptpb.wrapper.db.DbConfiguration;
import net.thumbtack.ptpb.wrapper.db.DbProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {
        DbConfiguration.class,
        DbProperties.class,
        SyncDao.class
})
public class SyncDaoTest {

    @Autowired
    private SyncDao syncDao;

    @BeforeEach
    void setup() {
        syncDao.deleteAllSyncs();
    }

    @Test
    void testUpdateSyncAndGet() {
        String userUuid = UUID.randomUUID().toString();
        String syncToken = UUID.randomUUID().toString();

        Sync sync = Sync.builder()
                .userId(userUuid)
                .syncToken(syncToken)
                .build();
        assertFalse(syncDao.getSyncTokenByUserId(userUuid).isPresent());
        syncDao.updateSync(sync);

        Optional<Sync> resultOptional = syncDao.getSyncTokenByUserId(userUuid);
        assertTrue(resultOptional.isPresent());

        Sync result = resultOptional.get();
        assertNotNull(result);
        assertEquals(userUuid, result.getUserId());
        assertEquals(syncToken, result.getSyncToken());
    }

    @Test
    void testDoubleUpdate() {
        String userUuid = UUID.randomUUID().toString();
        String syncToken1 = UUID.randomUUID().toString();
        String syncToken2 = UUID.randomUUID().toString();

        Sync sync1 = Sync.builder()
                .userId(userUuid)
                .syncToken(syncToken1)
                .build();
        assertFalse(syncDao.getSyncTokenByUserId(userUuid).isPresent());
        syncDao.updateSync(sync1);

        Sync sync2 = Sync.builder()
                .userId(userUuid)
                .syncToken(syncToken2)
                .build();

        syncDao.updateSync(sync2);

        Optional<Sync> resultOptional = syncDao.getSyncTokenByUserId(userUuid);
        assertTrue(resultOptional.isPresent());

        Sync result = resultOptional.get();
        assertNotNull(result);
        assertEquals(userUuid, result.getUserId());
        assertNotEquals(syncToken1, result.getSyncToken());
        assertEquals(syncToken2, result.getSyncToken());
    }
}
