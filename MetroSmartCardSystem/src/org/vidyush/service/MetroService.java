package org.vidyush.service;

import org.vidyush.dao.InMemoryCardTrxRepository;
import org.vidyush.exception.InsufficientCardBalanceException;
import org.vidyush.exception.MetroException;
import org.vidyush.exception.MinimumCardBalanceException;
import org.vidyush.model.CardTransaction;
import org.vidyush.model.SmartCard;
import org.vidyush.model.Station;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetroService {

    private static final double MIN_BALANCE_REQUIRED = 10.0;

    private final InMemoryCardTrxRepository trxRepository = new InMemoryCardTrxRepository();
    private final FareCalculator fareCalculator = new FareCalculator();

    private final Map<Station, Integer> stationFootFall = new HashMap<>();

    // ===================== SWIPE IN =====================

    public void swipeIn(SmartCard card, Station source, LocalDateTime dateTime)
            throws MetroException {

        if (card.getBalance() < MIN_BALANCE_REQUIRED) {
            throw new MinimumCardBalanceException(
                    "Minimum balance of Rs " + MIN_BALANCE_REQUIRED + " is required at swipe-in"
            );
        }

        if (trxRepository.hasActiveJourney(card.getId())) {
            throw new MetroException("Card already has an active journey");
        }

        incrementFootFall(source);

        CardTransaction trx = new CardTransaction();
        trx.setCard(card);
        trx.setSource(source);
        trx.setStartDateTime(dateTime);

        trxRepository.addTransientTrx(card.getId(), trx);
    }

    // ===================== SWIPE OUT =====================

    public void swipeOut(SmartCard card, Station destination, LocalDateTime dateTime)
            throws MetroException {

        CardTransaction trx = trxRepository.getTransientTrx(card.getId());

        if (trx == null) {
            throw new MetroException("No active journey found for this card");
        }

        incrementFootFall(destination);

        trx.setDestination(destination);
        trx.setEndDateTime(dateTime);

        double fare = fareCalculator.getFare(
                trx.getSource(),
                destination,
                dateTime
        );

        if (fare > card.getBalance()) {
            throw new InsufficientCardBalanceException(
                    "Insufficient balance at swipe-out"
            );
        }

        trx.setFare(fare);
        trx.setBalance(card.getBalance() - fare);
        card.setBalance(card.getBalance() - fare);

        trxRepository.addCompletedTrx(card.getId(), trx);
    }

    // ===================== REPORTS =====================

    public int calculateFootFall(Station station) {
        return stationFootFall.getOrDefault(station, 0);
    }

    public List<CardTransaction> cardReport(SmartCard card) {
        return trxRepository.getCompletedTrxs(card.getId());
    }

    // ===================== HELPER =====================

    private void incrementFootFall(Station station) {
        stationFootFall.put(
                station,
                stationFootFall.getOrDefault(station, 0) + 1
        );
    }
}
