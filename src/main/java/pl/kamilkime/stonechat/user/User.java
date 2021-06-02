/*
 * Copyright 2021 Kamil Trysiński <kamilkime@pm.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.kamilkime.stonechat.user;

import java.util.UUID;

public class User {

    private final UUID uuid;

    private int stoneMined;
    private long nextMessageTime;

    public User(final UUID uuid, final int stoneMined, final long nextMessageTime) {
        this.uuid = uuid;
        this.stoneMined = stoneMined;
        this.nextMessageTime = nextMessageTime;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getStoneMined() {
        return this.stoneMined;
    }

    public int addStoneMined() {
        return  ++this.stoneMined;
    }

    public long getNextMessageTime() {
        return this.nextMessageTime;
    }

    public void cooldown(final long cooldown) {
        if (cooldown <= 0L) {
            return;
        }

        this.nextMessageTime = System.currentTimeMillis() + cooldown * 1000L;
    }

    public boolean canSendMessage() {
        return this.nextMessageTime <= 0L || this.nextMessageTime <= System.currentTimeMillis();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        return this.uuid.equals(((User) o).uuid);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

}
