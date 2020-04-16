package net.thumbtack.ptpb.wrapper.db.sync;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class SyncDaoTest {
    @Autowired
    private SyncDao syncDao;

    @BeforeEach
    void setup() {
        syncDao.deleteAllSyncs();
    }

    @Test
    void testInsertAndGetSync() {
        Sync sync = Sync.builder()
                .userId(System.nanoTime())
                .syncToken(UUID.randomUUID().toString())
                .build();
        syncDao.updateSync(sync);


    }
}
