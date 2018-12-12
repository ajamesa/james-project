/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mailbox.events.delivery;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PreDestroy;

import org.apache.james.mailbox.Event;
import org.apache.james.mailbox.MailboxListener;
import org.apache.james.util.concurrent.NamedThreadFactory;

public class AsynchronousEventDelivery implements EventDelivery {
    private final ExecutorService threadPoolExecutor;
    private final SynchronousEventDelivery synchronousEventDelivery;

    public AsynchronousEventDelivery(int threadPoolSize, SynchronousEventDelivery synchronousEventDelivery) {
        ThreadFactory threadFactory = NamedThreadFactory.withClassName(getClass());
        this.threadPoolExecutor = Executors.newFixedThreadPool(threadPoolSize, threadFactory);
        this.synchronousEventDelivery = synchronousEventDelivery;
    }

    @Override
    public void deliver(MailboxListener mailboxListener, Event event) {
        threadPoolExecutor.submit(() -> synchronousEventDelivery.deliver(mailboxListener, event));
    }

    @PreDestroy
    public void stop() {
        threadPoolExecutor.shutdownNow();
    }
}