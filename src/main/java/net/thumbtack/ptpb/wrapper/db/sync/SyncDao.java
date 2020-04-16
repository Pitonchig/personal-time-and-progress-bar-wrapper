package net.thumbtack.ptpb.wrapper.db.sync;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SyncDao {

    private final SyncMapper syncMapper;

    public void updateSync(Sync sync) {
        syncMapper.save(sync);
    }

    public Optional<Sync> getSyncTokenByUserId(long userId) {
        return syncMapper.findById(userId);
    }

    public void updateSyncs(List<Sync> syncs) {
        syncMapper.saveAll(syncs);
    }

    public void deleteAllSyncs() {
        syncMapper.deleteAll();
    }
}
