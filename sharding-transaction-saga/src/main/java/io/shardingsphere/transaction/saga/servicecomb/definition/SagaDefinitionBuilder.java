/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.transaction.saga.servicecomb.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Saga definition builder.
 *
 * @author yangyi
 */
@RequiredArgsConstructor
public final class SagaDefinitionBuilder {
    
    private static final String TYPE = "sql";
    
    private final String recoveryPolicy;
    
    private final int transactionMaxRetries;
    
    private final int compensationMaxRetries;
    
    private final int transactionRetryDelayMilliseconds;
    
    private final ConcurrentLinkedQueue<SagaRequest> requests = new ConcurrentLinkedQueue<>();
    
    private String[] parents = new String[]{};
    
    private ConcurrentLinkedQueue<String> newRequestIds = new ConcurrentLinkedQueue<>();
    
    /**
     * Add child request node to definition graph.
     *
     * @param id request ID
     * @param datasourceName data source name
     * @param sql transaction SQL
     * @param parameters transaction SQL parameters
     * @param compensationSQL compensation SQL
     * @param compensationParams compensation SQL parameters
     */
    public void addChildRequest(final String id, final String datasourceName, final String sql, final List<List<Object>> parameters,
                                final String compensationSQL, final List<Collection<Object>> compensationParams) {
        Transaction transaction = new Transaction(sql, parameters, transactionMaxRetries);
        Compensation compensation = new Compensation(compensationSQL, compensationParams, compensationMaxRetries);
        requests.add(new SagaRequest(id, datasourceName, TYPE, transaction, compensation, parents, transactionRetryDelayMilliseconds));
        newRequestIds.add(id);
    }
    
    /**
     * Switch to next logic SQL.
     */
    public void switchParents() {
        parents = newRequestIds.toArray(new String[]{});
        newRequestIds = new ConcurrentLinkedQueue<>();
    }
    
    /**
     * Build saga definition json string.
     *
     * @return saga json string
     * @throws JsonProcessingException json process exception
     */
    public String build() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(new SagaDefinition(recoveryPolicy, requests));
    }
    
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class SagaDefinition {
        
        private final String policy;
        
        private final Collection<SagaRequest> requests;
    }
    
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class SagaRequest {
        
        private final String id;
        
        private final String datasource;
        
        private final String type;
        
        private final Transaction transaction;
        
        private final Compensation compensation;
        
        private final String[] parents;
        
        private final int failRetryDelayMilliseconds;
    }
    
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class Transaction {
        
        private final String sql;
        
        private final List<List<Object>> params;
        
        private final int retries;
    }
    
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class Compensation {
        
        private final String sql;
        
        private final List<Collection<Object>> params;
        
        private final int retries;
    }
}
