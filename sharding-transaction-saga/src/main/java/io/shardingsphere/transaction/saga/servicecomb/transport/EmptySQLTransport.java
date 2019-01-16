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

package io.shardingsphere.transaction.saga.servicecomb.transport;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.servicecomb.saga.core.SagaResponse;
import org.apache.servicecomb.saga.format.JsonSuccessfulSagaResponse;
import org.apache.servicecomb.saga.transports.SQLTransport;

import java.util.List;

/**
 * Empty SQL transport default implements.
 * 
 * <p>used when no SPI found.</p>
 *
 * @author yangyi
 */
@Slf4j
public class EmptySQLTransport implements SQLTransport {
    
    /**
     * empty SQL transport in transaction.
     * Just print log.
     *
     * @param datasourceName data source name
     * @param sql SQL in transaction
     * @param parameters parameters for SQL
     * @return saga execute response
     */
    @Override
    public SagaResponse with(final String datasourceName, final String sql, final List<List<String>> parameters) {
        log.warn("This is empty SQLTransport, SQL will not be executed actually.");
        log.info("SQL: " + sql + " param: " + Joiner.on(',').join(parameters) + " for datasource:" + datasourceName);
        return new JsonSuccessfulSagaResponse("{}");
    }
}
