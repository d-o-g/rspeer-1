package org.rspeer.runetek.api.query.results;

import org.rspeer.runetek.providers.RSGrandExchangeOffer;

import java.util.Collection;

public final class GrandExchangeOfferQueryResults extends QueryResults<RSGrandExchangeOffer, GrandExchangeOfferQueryResults> {

    public GrandExchangeOfferQueryResults(Collection<? extends RSGrandExchangeOffer> results) {
        super(results);
    }
}