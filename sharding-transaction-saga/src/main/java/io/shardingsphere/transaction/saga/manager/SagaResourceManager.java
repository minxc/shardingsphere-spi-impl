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

package io.shardingsphere.transaction.saga.manager;

import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.transaction.saga.config.SagaConfiguration;
import io.shardingsphere.transaction.saga.persistence.SagaPersistence;
import io.shardingsphere.transaction.saga.persistence.SagaPersistenceLoader;
import io.shardingsphere.transaction.saga.servicecomb.SagaExecutionComponentFactory;
import lombok.Getter;
import org.apache.servicecomb.saga.core.application.SagaExecutionComponent;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Saga resource manager.
 *
 * @author yangyi
 */
@Getter
public final class SagaResourceManager {
    
    private final SagaPersistence sagaPersistence;
    
    private final SagaExecutionComponent sagaExecutionComponent;
    
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();
    
    public SagaResourceManager(final SagaConfiguration sagaConfiguration) {
        sagaPersistence = SagaPersistenceLoader.load(sagaConfiguration.isEnablePersistence());
        sagaExecutionComponent = SagaExecutionComponentFactory.createSagaExecutionComponent(sagaConfiguration, sagaPersistence);
    }
    
    /**
     * Register data source map.
     *
     * @param dataSourceMap data source map
     */
    public void registerDataSourceMap(final Map<String, DataSource> dataSourceMap) {
        validateDataSourceName(dataSourceMap);
        this.dataSourceMap.putAll(dataSourceMap);
    }
    
    private void validateDataSourceName(final Map<String, DataSource> dataSourceMap) {
        for (String each : dataSourceMap.keySet()) {
            if (this.dataSourceMap.containsKey(each)) {
                throw new ShardingException("datasource {} has registered", each);
            }
        }
    }
    
    /**
     * Release data source map.
     */
    public void releaseDataSourceMap() {
        dataSourceMap.clear();
    }
}
