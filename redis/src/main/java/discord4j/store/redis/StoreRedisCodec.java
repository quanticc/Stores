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

package discord4j.store.redis;

import io.lettuce.core.codec.RedisCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 * A {@link RedisCodec} implementation tailored for {@link RedisStore} instances, allowing separate serialization
 * strategies for keys and values.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class StoreRedisCodec<K, V> implements RedisCodec<K, V> {

    private final RedisSerializer<K> keySerializer;
    private final RedisSerializer<V> valueSerializer;

    /**
     * Create a new {@link StoreRedisCodec} using the given {@link RedisSerializer} instances for keys and values.
     *
     * @param keySerializer serializer for keys
     * @param valueSerializer serializer for values
     */
    public StoreRedisCodec(RedisSerializer<K> keySerializer, RedisSerializer<V> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    @Override
    public K decodeKey(ByteBuffer bytes) {
        return keySerializer.deserialize(decodeBuffer(bytes));
    }

    @Override
    public V decodeValue(ByteBuffer bytes) {
        return valueSerializer.deserialize(decodeBuffer(bytes));
    }

    @Override
    public ByteBuffer encodeKey(K key) {
        return encodeBuffer(keySerializer.serialize(key));
    }

    @Override
    public ByteBuffer encodeValue(V value) {
        return encodeBuffer(valueSerializer.serialize(value));
    }

    private byte[] decodeBuffer(ByteBuffer bytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        byte[] array = new byte[buffer.readableBytes()];
        buffer.duplicate().readBytes(array);
        return array;
    }

    private ByteBuffer encodeBuffer(byte[] array) {
        if (array == null) {
            return ByteBuffer.wrap(new byte[0]);
        }
        return Unpooled.wrappedBuffer(array).nioBuffer();
    }
}
