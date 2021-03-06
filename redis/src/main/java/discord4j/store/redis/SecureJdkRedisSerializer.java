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

import discord4j.store.crypto.AES;
import discord4j.store.crypto.SecretKeyGenerator;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.GeneralSecurityException;

/**
 * A {@link RedisSerializer} that uses JDK serialization along with a {@link SecretKey} to encrypt and decrypt
 * resulting objects while encoding/decoding them. This implementation currently uses AES cipher outlined by the
 * {@link AES} class.
 * <p>
 * Simplest way to create a {@code SecretKey} object for usage in this serializer is to run the
 * {@link SecretKeyGenerator} main method.
 */
public class SecureJdkRedisSerializer implements RedisSerializer<Object> {

    private final SecretKey secretKey;

    /**
     * Create a new serializer that also encrypts data using AES with the given {@link SecretKey}. You can generate
     * one by running {@link SecretKeyGenerator} class.
     *
     * @param secretKey the key used to encrypt/decrypt objects
     */
    public SecureJdkRedisSerializer(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bytes);
            os.writeObject(o);
            return AES.encrypt(bytes.toByteArray(), secretKey);
        } catch (IOException | GeneralSecurityException e) {
            throw new SerializationException("Could not write object: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        try {
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(AES.decrypt(bytes, secretKey)));
            return is.readObject();
        } catch (Exception e) {
            throw new SerializationException("Could not read object: " + e.getMessage(), e);
        }
    }
}
