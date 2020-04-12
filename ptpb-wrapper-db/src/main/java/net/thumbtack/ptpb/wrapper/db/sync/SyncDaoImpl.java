package net.thumbtack.ptpb.wrapper.db.sync;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SyncDaoImpl implements SyncDao {

    @NonNull
    private final SyncMapper syncMapper;

    @Override
    public void updateSync(Sync sync) {
        syncMapper.save(sync);
    }

    @Override
    public Optional<Sync> getSyncTokenByUserId(long userId) {
        return syncMapper.findById(userId);
    }
}
