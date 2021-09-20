/*
 * Apache License
 * 
 * Copyright (c) 2020 HuahuiData
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.datasphere.datasource.connections.jdbc.accessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datasphere.datasource.connections.jdbc.exception.JdbcDataConnectionErrorCodes;
import com.datasphere.datasource.connections.jdbc.exception.JdbcDataConnectionException;


public class TDSQLDataAccessor extends AbstractJdbcDataAccessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(TDSQLDataAccessor.class);

  @Override
  public Map<String, Object> getDatabases(String catalog, String schemaPattern, Integer pageSize, Integer pageNumber) {
    Map<String, Object> databaseMap = new LinkedHashMap<>();

    String schemaListQuery = dialect.getDataBaseQuery(connectionInfo, catalog, schemaPattern, getExcludeSchemas(), pageSize, pageNumber);

    LOGGER.debug("Execute Schema List query : {}", schemaListQuery);

    int databaseCount = 0;
    List<String> databaseNames = null;
    try {
      databaseNames = this.executeQueryForList(this.getConnection(), schemaListQuery, (resultSet, i) -> resultSet.getString(1));
    } catch (Exception e) {
      LOGGER.error("Fail to get list of database : {}", e.getMessage());
      new JdbcDataConnectionException(JdbcDataConnectionErrorCodes.INVALID_QUERY_ERROR_CODE,
                                            "Fail to get list of database : " + e.getMessage());
    }

    List<String> excludeSchemas = getExcludeSchemas();
    if (excludeSchemas != null) {
      //filter after query execute for tdsql
      databaseNames = databaseNames.stream()
                                   .filter(databaseName -> excludeSchemas.indexOf(databaseName) < 0)
                                   .collect(Collectors.toList());
    }

    databaseCount = databaseNames.size();

    databaseMap.put("databases", databaseNames);
    databaseMap.put("page", createPageInfoMap(databaseCount, databaseCount, 0));
    return databaseMap;
  }

  @Override
  public Map<String, Object> getTables(String catalog, String schemaPattern, String tableNamePattern, Integer pageSize, Integer pageNumber) {
    int size = pageSize == null ? 20 : pageSize;
    int page = pageNumber == null ? 0 : pageNumber;

    String tableCountQuery = dialect.getTableCountQuery(connectionInfo, catalog, schemaPattern, tableNamePattern, getExcludeTables());
    String tableListQuery = dialect.getTableQuery(connectionInfo, catalog, schemaPattern, tableNamePattern, getExcludeTables(), size, page);

    LOGGER.debug("Execute Table Count query : {}", tableCountQuery);
    LOGGER.debug("Execute Table List query : {}", tableListQuery);

    int tableCount = 0;
    List<Map<String, Object>> tableNames = null;
    try {
      tableCount = this.executeQueryForObject(this.getConnection(), tableCountQuery, Integer.class);
      tableNames = this.executeQueryForList(this.getConnection(), tableListQuery);
    } catch (Exception e) {
      LOGGER.error("Fail to get list of table : {}", e.getMessage());
      new JdbcDataConnectionException(JdbcDataConnectionErrorCodes.INVALID_QUERY_ERROR_CODE,
                                            "Fail to get list of database : " + e.getMessage());
    }

    Map<String, Object> databaseMap = new LinkedHashMap<>();
    databaseMap.put("tables", tableNames);
    databaseMap.put("page", createPageInfoMap(size, tableCount, page));
    return databaseMap;
  }
}
