package org.vidyush.dao;

import org.vidyush.model.CardTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryCardTrxRepository {

    private ConcurrentMap<Long, CardTransaction> transientTrxStore = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, List<CardTransaction>> completedTrxStore = new ConcurrentHashMap<>();

    // ---------- TRANSIENT (ACTIVE JOURNEY) ----------

    public void addTransientTrx(long cardId, CardTransaction trx) {
        transientTrxStore.put(cardId, trx);
    }

    public boolean hasActiveJourney(long cardId) {
        return transientTrxStore.containsKey(cardId);
    }

    public CardTransaction getTransientTrx(long cardId) {
        return transientTrxStore.remove(cardId);
    }

    // ---------- COMPLETED JOURNEYS ----------

    public void addCompletedTrx(long cardId, CardTransaction trx) {
        completedTrxStore.putIfAbsent(cardId, new ArrayList<>());
        completedTrxStore.get(cardId).add(trx);
    }

    public List<CardTransaction> getCompletedTrxs(long cardId) {
        return completedTrxStore.getOrDefault(cardId, Collections.emptyList());
    }
}
