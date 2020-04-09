package net.thumbtack.ptpb.wrapper.db.sync;

import java.util.Optional;

public interface SyncDao {

    void updateSync(Sync sync);

    Optional<Sync> getSyncTokenByUserId(long userId);
}
