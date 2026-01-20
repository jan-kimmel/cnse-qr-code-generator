package de.hskl.cnseqrcode.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.hskl.cnseqrcode.model.UserHistoryEntity;

public class InMemoryUserHistoryRepositoryTest {
InMemoryUserHistoryRepository repo = new InMemoryUserHistoryRepository();

    @Test
    void save_shouldReplaceSameQrCode() {
        UserHistoryEntity h1 = new UserHistoryEntity("u", "id1", Instant.now().minusSeconds(10));
        UserHistoryEntity h2 = new UserHistoryEntity("u", "id1", Instant.now());

        repo.save(h1);
        repo.save(h2);

        List<UserHistoryEntity> list = repo.findAllByUserId("u");

        assertEquals(1, list.size());
        assertEquals(h2.getLastUsedAt(), list.get(0).getLastUsedAt());
    }

    @Test
    void findAll_shouldReturnSortedByDate() {
        UserHistoryEntity older = new UserHistoryEntity("u", "a", Instant.now().minusSeconds(20));
        UserHistoryEntity newer = new UserHistoryEntity("u", "b", Instant.now());

        repo.save(older);
        repo.save(newer);

        List<UserHistoryEntity> list = repo.findAllByUserId("u");

        assertEquals("b", list.get(0).getQrCodeId());
    }
}
