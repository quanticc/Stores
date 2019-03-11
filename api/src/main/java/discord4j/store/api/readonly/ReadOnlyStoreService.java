/*
 * This file is part of Discord4J.
 *
 * Discord4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Discord4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Discord4J. If not, see <http://www.gnu.org/licenses/>.
 */

package discord4j.store.api.readonly;

import discord4j.store.api.Store;
import discord4j.store.api.primitive.LongObjStore;
import discord4j.store.api.readonly.primitive.ReadOnlyLongObjStore;
import discord4j.store.api.service.StoreService;
import discord4j.store.api.util.StoreContext;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * {@link StoreService} decorator disabling all mutative operations, effectively working as a read-only store.
 */
public class ReadOnlyStoreService implements StoreService {

    private final StoreService delegate;

    /**
     * Create a new {@link ReadOnlyStoreService} for the given {@link StoreService}.
     *
     * @param delegate the delegate service
     */
    public ReadOnlyStoreService(StoreService delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasGenericStores() {
        return delegate.hasGenericStores();
    }

    @Override
    public <K extends Comparable<K>, V extends Serializable> Store<K, V> provideGenericStore(Class<K> keyClass,
                                                                                             Class<V> valueClass) {
        return new ReadOnlyStore<>(delegate.provideGenericStore(keyClass, valueClass));
    }

    @Override
    public boolean hasLongObjStores() {
        return delegate.hasLongObjStores();
    }

    @Override
    public <V extends Serializable> LongObjStore<V> provideLongObjStore(Class<V> valueClass) {
        return new ReadOnlyLongObjStore<>(delegate.provideLongObjStore(valueClass));
    }

    @Override
    public void init(StoreContext context) {
        delegate.init(context);
    }

    @Override
    public Mono<Void> dispose() {
        return delegate.dispose();
    }
}
