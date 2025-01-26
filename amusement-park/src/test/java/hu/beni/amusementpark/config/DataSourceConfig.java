package hu.beni.amusementpark.config;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class DataSourceConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        return ProxyDataSourceBuilder
                .create(properties.initializeDataSourceBuilder().type(HikariDataSource.class).build())
                .listener(new DataSourceQueryCountListener()).build();
    }

}
