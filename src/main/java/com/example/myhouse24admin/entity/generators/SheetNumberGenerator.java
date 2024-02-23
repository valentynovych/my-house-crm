package com.example.myhouse24admin.entity.generators;

import com.example.myhouse24admin.entity.CashSheet;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.Properties;
import java.util.stream.Stream;

public class SheetNumberGenerator implements IdentifierGenerator, Configurable {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object o) {

        String query = "select id from CashSheet ";
        Stream<Long> ids = session.createNamedQuery(query, Long.class).stream();

        Long maxId = ids.mapToLong(Long::longValue).max().orElse(0L);

        String newSheetNumber = StringUtils.leftPad(maxId.toString(), 10, "0");
        return newSheetNumber;
    }

    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) {
        IdentifierGenerator.super.configure(type, parameters, serviceRegistry);
    }
}
