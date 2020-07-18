package com.gihub.ndiaz.jdbc.dao;

import com.gihub.ndiaz.jdbc.exception.DaoException;
import com.gihub.ndiaz.jdbc.mapper.RowUnmapper;
import com.gihub.ndiaz.jdbc.mapper.impl.DefaultResultSetExtractor;
import com.gihub.ndiaz.jdbc.mapper.impl.DefaultRowMapper;
import com.gihub.ndiaz.jdbc.mapper.impl.DefaultRowUnmapper;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;

@Slf4j
public abstract class JdbcEntityDao<T> {

  private final String className;
  @Autowired
  private JdbcBaseDao dao;
  @Autowired
  private RowMapper<T> rowMapper;
  @Autowired
  private RowUnmapper<T> rowUnmapper;
  @Autowired
  private ResultSetExtractor<T> resultSetExtractor;

  public JdbcEntityDao() {
    className = getClassSimpleName();
  }

  @SuppressWarnings("unchecked")
  private String getClassSimpleName() {
    return ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
        .getActualTypeArguments()[0]).getSimpleName();
  }

  @PostConstruct
  private void checkMappers() {
    if (rowMapper instanceof DefaultRowMapper<?>) {
      log.info("No implementation of RowMapper<{}> found. Using DefaultRowMapper instead.",
          className);
    }
    if (rowUnmapper instanceof DefaultRowUnmapper<?>) {
      log.info("No implementation of RowUnmapper<{}> found. Using DefaultRowUnmapper instead.",
          className);
    }
    if (resultSetExtractor instanceof DefaultResultSetExtractor<?>) {
      log.info(
          "No implementation of ResultSetExtractor<{}> found. "
              + "Using DefaultResultSetExtractor instead.", className);
    }
  }

  protected T query(final String sql) {
    return dao.query(sql, resultSetExtractor);
  }

  protected T query(final String sql, final AbstractSqlParameterSource params) {
    return dao.query(sql, params, resultSetExtractor);
  }

  protected T query(final String sql, final T entity) {
    return dao.query(sql, rowUnmapper.getSqlParameters(entity), resultSetExtractor);
  }

  protected T queryForObject(final String sql) {
    return dao.queryForObject(sql, rowMapper);
  }

  protected T queryForObject(final String sql, final AbstractSqlParameterSource params) {
    return dao.queryForObject(sql, params, rowMapper);
  }

  protected T queryForObject(final String sql, final T entity) {
    return dao.queryForObject(sql, rowUnmapper.getSqlParameters(entity), rowMapper);
  }

  protected List<T> queryForList(final String sql) {
    return dao.queryForList(sql, rowMapper);
  }

  protected List<T> queryForList(final String sql, final AbstractSqlParameterSource params) {
    return dao.queryForList(sql, params, rowMapper);
  }

  protected List<T> queryForList(final String sql, final T entity) {
    return dao.queryForList(sql, rowUnmapper.getSqlParameters(entity), rowMapper);
  }

  protected Integer insert(final String sql, final T entity) {
    return dao.insert(sql, rowUnmapper.getSqlParameters(entity));
  }

  protected Number insert(final String sql, final T entity, final List<String> keyColumnNames)
      throws DaoException {
    return dao.insert(sql, rowUnmapper.getSqlParameters(entity), keyColumnNames);
  }

  protected Integer update(final String sql) {
    return dao.update(sql);
  }

  protected Integer update(final String sql, final T entity) {
    return dao.update(sql, rowUnmapper.getSqlParameters(entity));
  }

  protected Integer update(final String sql, final AbstractSqlParameterSource params) {
    return dao.update(sql, params);
  }

  protected List<Integer> batchUpdate(final String sql, final List<T> entities) {
    return dao.batchUpdate(sql, rowUnmapper.getSqlParameters(entities));
  }

  protected List<Integer> batchUpdate(final String sql, final AbstractSqlParameterSource[] params) {
    return dao.batchUpdate(sql, Arrays.asList(params));
  }

}
