package org.rspeer.runetek.api.query;

import org.rspeer.runetek.api.commons.ArrayUtils;
import org.rspeer.runetek.api.commons.Range;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.query.results.GrandExchangeOfferQueryResults;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class GrandExchangeOfferQueryBuilder
        extends QueryBuilder<RSGrandExchangeOffer, GrandExchangeOfferQueryBuilder, GrandExchangeOfferQueryResults> {

    private final Supplier<List<? extends RSGrandExchangeOffer>> provider;


    private int[] ids = null;

    private Range amount = null;
    private Range pricePerItem = null;

    private RSGrandExchangeOffer.Progress progress = null;
    private RSGrandExchangeOffer.Type type = null;

    private String[] names = null;
    private String[] nameContains = null;

    public GrandExchangeOfferQueryBuilder(Supplier<List<? extends RSGrandExchangeOffer>> provider) {
        this.provider = provider;
    }

    public GrandExchangeOfferQueryBuilder() {
        this(() -> Arrays.asList(GrandExchange.getOffers()));
    }

    @Override
    public Supplier<List<? extends RSGrandExchangeOffer>> getDefaultProvider() {
        return provider;
    }

    @Override
    protected GrandExchangeOfferQueryResults createQueryResults(Collection<? extends RSGrandExchangeOffer> raw) {
        return new GrandExchangeOfferQueryResults(raw);
    }

    public GrandExchangeOfferQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public GrandExchangeOfferQueryBuilder amount(int minInclusive) {
        return amount(minInclusive, Integer.MAX_VALUE);
    }

    public GrandExchangeOfferQueryBuilder amount(int minInclusive, int maxInclusive) {
        amount = Range.of(minInclusive, maxInclusive);
        return self();
    }

    public GrandExchangeOfferQueryBuilder progress(RSGrandExchangeOffer.Progress progress) {
        this.progress = progress;
        return self();
    }

    public GrandExchangeOfferQueryBuilder type(RSGrandExchangeOffer.Type type) {
        this.type = type;
        return self();
    }

    public GrandExchangeOfferQueryBuilder names(String... names) {
        this.names = names;
        return self();
    }

    public GrandExchangeOfferQueryBuilder nameContains(String... names) {
        this.nameContains = names;
        return self();
    }

    @Override
    public boolean test(RSGrandExchangeOffer offer) {
        if (type != null && offer.getType() != type) {
            return false;
        }

        if (progress != null && offer.getProgress() != progress) {
            return false;
        }

        if (ids != null && !ArrayUtils.contains(ids, offer.getItemId())) {
            return false;
        }

        if (names != null && !ArrayUtils.containsExactInsensitive(names, offer.getItemName())) {
            return false;
        }

        if (nameContains != null) {
            boolean match = false;
            String item = offer.getItemName().toLowerCase();
            for (String name : nameContains) {
                if (name.toLowerCase().contains(item)) {
                    match = true;
                }
            }

            if (!match) {
                return false;
            }
        }

        if (pricePerItem != null && !pricePerItem.within(offer.getItemPrice())) {
            return false;
        }

        if (amount != null && !amount.within(offer.getItemQuantity())) {
            return false;
        }

        return super.test(offer);
    }
}
