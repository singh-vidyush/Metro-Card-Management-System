package org.vidyush.service;

import org.vidyush.exception.MetroException;
import org.vidyush.model.SmartCard;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class CardService {

    private static final double MIN_RECHARGE_AMOUNT = 50.0;
    private static final AtomicLong CARD_ID_GENERATOR = new AtomicLong(1000);

    private final Map<Long, SmartCard> cardStore = new HashMap<>();

    // ===================== CREATE CARD =====================

    public SmartCard createCard(double initialBalance) throws MetroException {

        if (initialBalance < 0) {
            throw new MetroException("Initial balance cannot be negative");
        }

        long cardId = CARD_ID_GENERATOR.incrementAndGet();
        SmartCard card = new SmartCard(cardId, initialBalance);

        cardStore.put(cardId, card);
        return card;
    }

    // ===================== RECHARGE CARD =====================

    public void rechargeCard(long cardId, double amount) throws MetroException {

        if (amount < MIN_RECHARGE_AMOUNT) {
            throw new MetroException(
                    "Minimum recharge amount is Rs " + MIN_RECHARGE_AMOUNT
            );
        }

        SmartCard card = getCard(cardId);
        card.setBalance(card.getBalance() + amount);
    }

    // ===================== FETCH CARD =====================

    public SmartCard getCard(long cardId) throws MetroException {

        SmartCard card = cardStore.get(cardId);

        if (card == null) {
            throw new MetroException("Invalid card ID");
        }
        return card;
    }
}
